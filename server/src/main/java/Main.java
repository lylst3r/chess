
import dataaccess.memory.MemoryDataAccessDAO;
import server.Server;
import dataaccess.DataAccessDAO;
import dataaccess.sql.SQLDataAccessDAO;
import server.service.Service;


public class Main {
    public static void main(String[] args) {

        /*Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");*/

        try {
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

            DataAccessDAO dataAccess = new MemoryDataAccessDAO();
            if (args.length >= 2 && args[1].equals("sql")) {
                dataAccess = new SQLDataAccessDAO();
            }

            var service = new Service(dataAccess);
            var server = new Server().run(port);
            //port = server.port();
            System.out.printf("Server started on port %d with %s%n", port, dataAccess.getClass());
            return;
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
        System.out.println("""
                ♕ 240 Chess Server:
                java ServerMain <port> [sql]
                """);
    }
}
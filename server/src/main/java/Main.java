
import dataaccess.memory.MemoryDataAccessDAO;
import server.Server;
import dataaccess.DataAccessDAO;
import dataaccess.sql.SQLDataAccessDAO;
import server.service.Service;


public class Main {
    public static void main(String[] args) {

        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");

    }
}
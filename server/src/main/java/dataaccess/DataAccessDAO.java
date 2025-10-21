package dataaccess;

//DAO classes mostly CRUD (create, read, update, delete) operations
public class DataAccessDAO{

    UserDAO userDAO;
    GameDAO gameDAO;
    AuthDAO authDAO;

    public DataAccessDAO(){
        userDAO = new UserDAO();
        gameDAO = new GameDAO();
        authDAO = new AuthDAO();

    }

    public void clear() {
        userDAO.clearUsers();
        gameDAO.clearGames();
        authDAO.clearAuths();
    }
}

package dataaccess;


import exception.ResponseException;

public class SQLDataAccessDAO implements DataAccessDAO {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public SQLDataAccessDAO() throws ResponseException, DataAccessException {

        this.userDAO = new SQLUserDAO();
        this.gameDAO = new SQLGameDAO();
        this.authDAO = new SQLAuthDAO();
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public GameDAO getGameDAO() {
        return gameDAO;
    }

    public AuthDAO getAuthDAO() {
        return authDAO;
    }

    public void clearAll() throws DataAccessException {
        try {
            userDAO.clearUsers();
            gameDAO.clearGames();
            authDAO.clearAuths();
        } catch (Exception e) {
            throw new DataAccessException("Failed to clear database");
        }

    }
}

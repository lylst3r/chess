package dataaccess;


import exception.ResponseException;

import java.util.HashMap;
import java.util.Map;

public class SQLDataAccessDAO implements DataAccessDAO {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public SQLDataAccessDAO() throws ResponseException, DataAccessException {
        this.userDAO = new SQLUserDAO();
        this.gameDAO = new SQLGameDAO();
        this.authDAO = new SQLAuthDAO();
    }

    public UserDAO getUserDAO() throws DataAccessException {
        return userDAO;
    }

    public GameDAO getGameDAO() throws DataAccessException {
        return gameDAO;
    }

    public AuthDAO getAuthDAO() throws DataAccessException {
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

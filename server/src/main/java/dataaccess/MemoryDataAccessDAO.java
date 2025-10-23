package dataaccess;


import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccessDAO implements DataAccessDAO {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public MemoryDataAccessDAO() {
        this.userDAO = new MemoryUserDAO();
        this.gameDAO = new MemoryGameDAO();
        this.authDAO = new MemoryAuthDAO();
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
        userDAO.clearUsers();
        gameDAO.clearGames();
        authDAO.clearAuths();

    }
}

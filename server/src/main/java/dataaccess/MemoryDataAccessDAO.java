package dataaccess;

public class MemoryDataAccessDAO implements DataAccessDAO {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public MemoryDataAccessDAO() {
        this.userDAO = new MemoryUserDAO();
        this.gameDAO = new MemoryGameDAO();
        this.authDAO = new MemoryAuthDAO();
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

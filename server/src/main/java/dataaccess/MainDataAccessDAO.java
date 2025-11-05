package dataaccess;

import dataaccess.sql.DatabaseManager;

public class MainDataAccessDAO implements DataAccessDAO {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    protected MainDataAccessDAO(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @Override
    public UserDAO getUserDAO() {
        return userDAO;
    }

    @Override
    public GameDAO getGameDAO() {
        return gameDAO;
    }

    @Override
    public AuthDAO getAuthDAO() {
        return authDAO;
    }

    @Override
    public void clearAll() throws DataAccessException {
        try {
            userDAO.clearUsers();
            gameDAO.clearGames();
            authDAO.clearAuths();
        } catch (Exception e) {
            throw new DataAccessException("Failed to clear database", e);
        }
    }

}

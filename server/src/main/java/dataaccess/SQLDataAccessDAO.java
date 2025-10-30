package dataaccess;

public class SQLDataAccessDAO implements DataAccessDAO{

    public SQLDataAccessDAO(){}

    public UserDAO getUserDAO() throws DataAccessException {
        return null;
    }

    public GameDAO getGameDAO() throws DataAccessException {
        return null;
    }

    public AuthDAO getAuthDAO() throws DataAccessException {
        return null;
    }

    public void clearAll() throws DataAccessException {

    }

}

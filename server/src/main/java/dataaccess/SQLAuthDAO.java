package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() {

    }

    public void createAuth(AuthData auth) throws DataAccessException {

    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {

    }

    public void clearAuths() throws DataAccessException {

    }

    public String getUsername(String authToken) throws DataAccessException {
        return null;
    }

}

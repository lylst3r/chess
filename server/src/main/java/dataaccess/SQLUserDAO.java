package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() {

    }

    public void createUser(UserData user) throws DataAccessException {

    }

    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    public void clearUsers() throws DataAccessException {

    }

    public boolean usernameTaken(String username) throws DataAccessException {
        return false;
    }

    public ArrayList<UserData> listUsers() throws DataAccessException {
        return null;
    }
}

package dataaccess;

import exception.ResponseException;
import model.UserData;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void clearUsers() throws DataAccessException;
    boolean usernameTaken(String username) throws DataAccessException;
}

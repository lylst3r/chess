package dataaccess;

import exception.ResponseException;
import model.UserData;

import java.util.ArrayList;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException, ResponseException;
    UserData getUser(String username) throws DataAccessException;
    void clearUsers() throws DataAccessException;
    boolean usernameTaken(String username) throws DataAccessException;
    ArrayList<UserData> listUsers() throws DataAccessException;
}

package dataaccess;

import exception.ResponseException;
import model.UserData;

import java.util.ArrayList;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException, ResponseException;
    UserData getUser(String username) throws DataAccessException, ResponseException;
    void clearUsers() throws DataAccessException, ResponseException;
    boolean usernameTaken(String username) throws DataAccessException,  ResponseException;
    ArrayList<UserData> listUsers() throws DataAccessException, ResponseException;
}

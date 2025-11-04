package dataaccess;

import exception.ResponseException;

//DAO classes mostly CRUD (create, read, update, delete) operations
public interface DataAccessDAO {

    public UserDAO getUserDAO() throws DataAccessException, ResponseException;
    public GameDAO getGameDAO() throws DataAccessException,  ResponseException;
    public AuthDAO getAuthDAO() throws DataAccessException, ResponseException;
    public void clearAll() throws DataAccessException, ResponseException;

}

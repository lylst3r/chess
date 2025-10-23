package dataaccess;

import model.UserData;
import model.GameData;
import model.AuthData;

//DAO classes mostly CRUD (create, read, update, delete) operations
public interface DataAccessDAO {

    UserDAO getUserDAO() throws DataAccessException;
    GameDAO getGameDAO() throws DataAccessException;
    AuthDAO getAuthDAO() throws DataAccessException;
    void clearAll() throws DataAccessException;

}

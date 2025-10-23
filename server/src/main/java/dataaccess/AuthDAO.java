package dataaccess;
import java.util.ArrayList;
import java.util.UUID;

import model.AuthData;
import model.UserData;

public interface AuthDAO {

    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clearAuths() throws DataAccessException;

}

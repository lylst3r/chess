package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {

    private final ArrayList<AuthData> auths;

    public MemoryAuthDAO(){
        auths = new ArrayList<>();
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        auths.add(auth);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData auth : auths) {
            if (auth.authToken().equals(authToken)) {
                return auth;
            }
        }
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(getAuth(authToken));
    }

    public void clearAuths() throws DataAccessException {
        auths.clear();
    }

    public String getUsername(String authToken) throws DataAccessException {
        for (AuthData auth : auths) {
            if (auth.authToken().equals(authToken)) {
                return auth.username();
            }
        }
        return null;
    }
}

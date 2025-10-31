package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public class MemoryAuthDAO implements AuthDAO {

    private final ArrayList<AuthData> auths;

    public MemoryAuthDAO(){
        auths = new ArrayList<>();
    }

    public void createAuth(AuthData auth) {
        auths.add(auth);
    }

    public AuthData getAuth(String authToken) {
        for (AuthData auth : auths) {
            if (auth.authToken().equals(authToken)) {
                return auth;
            }
        }
        return null;
    }

    public void deleteAuth(String authToken) {
        auths.remove(getAuth(authToken));
    }

    public void clearAuths() {
        auths.clear();
    }

    public String getUsername(String authToken) {
        for (AuthData auth : auths) {
            if (auth.authToken().equals(authToken)) {
                return auth.username();
            }
        }
        return null;
    }
}

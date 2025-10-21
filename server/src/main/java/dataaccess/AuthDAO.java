package dataaccess;
import java.util.ArrayList;
import java.util.UUID;

import model.UserData;

public class AuthDAO {

    private ArrayList<Auth> authTokens;

    public AuthDAO(){
        authTokens = new ArrayList<>();
    }

    public void createAuth(String authToken, String username) {
        Auth auth = new Auth(authToken, username);
        authTokens.add(auth);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void clearAuths() {
        authTokens.clear();
    }


}

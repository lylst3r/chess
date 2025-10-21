package dataaccess;

import model.AuthData;

public class Auth {

    private String authToken;
    private String username;

    public Auth(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    public void setUserData(String authToken, String username) {
        AuthData authData = new AuthData(authToken, username);
    }
}

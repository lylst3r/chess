package dataaccess;

import model.UserData;

public class User {

    private String username;
    private String password;
    private String email;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void setUserData(String username, String password, String email) {
        UserData userData = new UserData(username, password, email);
    }
}

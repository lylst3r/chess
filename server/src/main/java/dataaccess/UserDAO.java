package dataaccess;

import java.util.ArrayList;

public class UserDAO {
    private ArrayList<User> usernames;

    public UserDAO(){
        usernames = new ArrayList<>();
    }

    public void createUser(String username, String password, String email) {
        User user = new User(username, password, email);
        usernames.add(user);
    }

    public void clearUsers() {
        usernames.clear();
    }
}

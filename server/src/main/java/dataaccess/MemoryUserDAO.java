package dataaccess;

import java.util.ArrayList;
import model.UserData;

public class MemoryUserDAO implements UserDAO {
    private final ArrayList<UserData> users;

    public MemoryUserDAO(){
        users = new ArrayList<>();

    }

    public void createUser(UserData user) throws DataAccessException {
        if (users.contains(user)) {
            throw new DataAccessException("User already exists");
        }

        users.add(user);
    }

    public void clearUsers() throws DataAccessException {
        users.clear();
    }

    public UserData getUser(String username) throws DataAccessException {
        for(UserData user : users){
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public boolean usernameTaken(String username) throws DataAccessException {
        for(UserData user : users){
            if (user.username().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<UserData> listUsers() throws DataAccessException {
        return users;
    }
}
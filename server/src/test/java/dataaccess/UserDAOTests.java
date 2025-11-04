package dataaccess;

import dataaccess.sql.SQLUserDAO;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import model.UserData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {

    private final SQLUserDAO userDAO;

    public UserDAOTests() throws ResponseException, DataAccessException {
        userDAO = new SQLUserDAO();
    }

    @BeforeEach
    void clearAll() throws ResponseException, DataAccessException {
        userDAO.clearUsers();
    }

    @Test
    void createUserPositive() throws ResponseException, DataAccessException {
        UserData user = new UserData("lily", "password", "lily@email.com");
        userDAO.createUser(user);
        assertTrue(userDAO.usernameTaken("lily"));
    }

    @Test
    void createUserNegativeDuplicate() throws ResponseException, DataAccessException {
        UserData user = new UserData("unicorn", "password", "unicorn@email.com");
        userDAO.createUser(user);
        assertThrows(ResponseException.class, () -> userDAO.createUser(user));
    }

    @Test
    void getUserPositive() throws ResponseException, DataAccessException {
        UserData user = new UserData("goat", "pass123", "goat@email.com");
        userDAO.createUser(user);
        UserData retrieved = userDAO.getUser("goat");
        assertEquals("goat", retrieved.username());
    }

    @Test
    void getUserNegativeNotExist() throws ResponseException {
        assertNull(userDAO.getUser("nonexistent"));
    }

    @Test
    void usernameTakenPositive() throws ResponseException, DataAccessException {
        UserData user = new UserData("lion", "pass", "kingofthejungle@email.com");
        userDAO.createUser(user);
        assertTrue(userDAO.usernameTaken("lion"));
    }

    @Test
    void usernameTakenNegative() throws DataAccessException {
        assertFalse(userDAO.usernameTaken("ghost"));
    }

    @Test
    void listUsersPositive() throws ResponseException, DataAccessException {
        userDAO.createUser(new UserData("panda", "123", "bamboo@email.com"));
        ArrayList<UserData> users = userDAO.listUsers();
        assertEquals(1, users.size());
        assertEquals("panda", users.get(0).username());
    }

    @Test
    void clearUsersPositive() throws ResponseException, DataAccessException {
        userDAO.createUser(new UserData("frank", "123", "frank@email.com"));
        userDAO.clearUsers();
        assertFalse(userDAO.usernameTaken("frank"));
    }
}

package dataaccess;

import dataaccess.sql.SQLAuthDAO;
import exception.ResponseException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTests {

    private final SQLAuthDAO authDAO;

    public AuthDAOTests() throws ResponseException, DataAccessException {
        authDAO = new SQLAuthDAO();
    }

    @BeforeEach
    void clearAll() throws ResponseException, DataAccessException {
        authDAO.clearAuths();
    }

    @Test
    void createAuthPositive() throws ResponseException, DataAccessException {
        AuthData auth = new AuthData("token", "lily");
        authDAO.createAuth(auth);
        assertEquals("lily", authDAO.getUsername("token"));
    }

    @Test
    void createAuthNegativeDuplicate() throws ResponseException, DataAccessException {
        AuthData auth = new AuthData("anotherToken", "unicorn");
        authDAO.createAuth(auth);
        assertThrows(ResponseException.class, () -> authDAO.createAuth(auth));
    }

    @Test
    void getAuthPositive() throws ResponseException, DataAccessException {
        AuthData auth = new AuthData("angryToken", "cheesitz");
        authDAO.createAuth(auth);
        assertEquals("cheesitz", authDAO.getAuth("angryToken").username());
    }

    @Test
    void getAuthNegative() throws ResponseException, DataAccessException {
        assertNull(authDAO.getAuth("invalidToken"));
    }

    @Test
    void deleteAuthPositive() throws ResponseException, DataAccessException {
        AuthData auth = new AuthData("auth", "monday");
        authDAO.createAuth(auth);
        authDAO.deleteAuth("auth");
        assertNull(authDAO.getAuth("auth"));
    }

    @Test
    void clearAuthsPositive() throws ResponseException, DataAccessException {
        authDAO.createAuth(new AuthData("nextToken", "kalea"));
        authDAO.clearAuths();
        assertNull(authDAO.getAuth("nextToken"));
    }

    @Test
    void getUsernamePositive() throws ResponseException, DataAccessException {
        AuthData auth = new AuthData("tooManyTokens", "hank");
        authDAO.createAuth(auth);
        assertEquals("hank", authDAO.getUsername("tooManyTokens"));
    }

    @Test
    void getUsernameNegative() throws ResponseException, DataAccessException {
        assertNull(authDAO.getUsername("nonexistent"));
    }
}

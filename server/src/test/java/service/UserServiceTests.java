package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccessDAO;
import exception.ResponseException;
import org.junit.jupiter.api.Test;
import server.service.Service;
import server.service.UserService;
import server.service.request.LoginRequest;
import server.service.request.LogoutRequest;
import server.service.request.RegisterRequest;
import server.service.result.LoginResult;
import server.service.result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    MemoryDataAccessDAO dao;
    Service service;

    public UserServiceTests() {
        dao = new MemoryDataAccessDAO();
        service = new Service(dao);
    }

    void register_succeedsForNewUser() throws ResponseException, DataAccessException {
        RegisterResult result = service.register(new RegisterRequest("lily", "password", "lily@email.com"));
        assertNotNull(result.authToken());
        assertEquals("lily", result.username());
    }

    @Test
    void register_failsIfUsernameTaken() throws ResponseException, DataAccessException {
        service.register(new RegisterRequest("lily", "password", "lily@email.com"));
        assertThrows(ResponseException.class, () -> {
            service.register(new RegisterRequest("kalea", "1234", "kalea@email.com"));
        });
    }

    @Test
    void login_succeedsForExistingUser() throws ResponseException, DataAccessException {
        service.register(new RegisterRequest("unicorn", "5678", "unicorn@email.com"));
        LoginResult result = service.login(new LoginRequest("unicorn", "5678"));

        assertEquals("unicorn", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void login_failsForWrongPassword() throws ResponseException, DataAccessException {
        service.register(new RegisterRequest("toast", "1111", "toast@email.com"));
        assertThrows(ResponseException.class, () -> {
            service.login(new LoginRequest("toast", "0000"));
        });
    }

    @Test
    void logout_removesAuthToken() throws ResponseException, DataAccessException {
        RegisterResult r = service.register(new RegisterRequest("potatoes", "ilovepotatoes", "potatoes@email.com"));
        service.logout(new LogoutRequest(r.authToken()));

        // Logging out again should fail
        assertThrows(ResponseException.class, () -> {
            service.logout(new LogoutRequest(r.authToken()));
        });
    }

}

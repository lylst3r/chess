package service;

import dataaccess.DataAccessException;
import dataaccess.memory.MemoryDataAccessDAO;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.service.Service;
import server.service.request.CreateGameRequest;
import server.service.request.RegisterRequest;
import server.service.result.RegisterResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClearServiceTests {

    private MemoryDataAccessDAO dao;
    private Service service;
    private RegisterResult user;

    @BeforeEach
    void setup() throws ResponseException, DataAccessException {
        dao = new MemoryDataAccessDAO();
        service = new Service(dao);

        user = service.register(new RegisterRequest("lily", "password", "lily@email.com"));
        service.createGame(user.authToken(), new CreateGameRequest("LilyGame"));
    }

    @Test
    void testClearAll() throws ResponseException, DataAccessException {
        service.clear();

        assertEquals(0, dao.getUserDAO().listUsers().size(), "Users should be cleared");
        assertEquals(0, dao.getGameDAO().listGames().size(), "Games should be cleared");
    }
}

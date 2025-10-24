package service;

import dataaccess.MemoryDataAccessDAO;
import org.junit.jupiter.api.Test;
import server.service.ClearService;
import server.service.GameService;
import server.service.Service;
import server.service.UserService;
import server.service.request.RegisterRequest;
import dataaccess.DataAccessException;
import exception.ResponseException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClearServiceTests {

    MemoryDataAccessDAO dao;
    Service service;

    public ClearServiceTests() {

        dao = new MemoryDataAccessDAO();
        service = new Service(dao);

    }

    @Test
    void testClearAll() throws ResponseException, DataAccessException {
        service.register(new server.service.request.RegisterRequest("lily", "password", "lily@email.com"));
        service.createGame(new server.service.request.CreateGameRequest("lily'sGame"));

        // Clear everything
        service.clear();

        assertEquals(0, dao.getUserDAO().listUsers().size());
        assertEquals(0, dao.getGameDAO().listGames().size());
    }

}

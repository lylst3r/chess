package service;

import dataaccess.DataAccessException;
import dataaccess.memory.MemoryDataAccessDAO;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.service.Service;
import server.service.request.CreateGameRequest;
import server.service.request.JoinGameRequest;
import server.service.request.RegisterRequest;
import server.service.result.CreateGameResult;
import server.service.result.ListGamesResult;
import server.service.result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    private MemoryDataAccessDAO dao;
    private Service service;
    private RegisterResult user1;
    private RegisterResult user2;

    @BeforeEach
    void setup() throws ResponseException, DataAccessException {
        dao = new MemoryDataAccessDAO();
        service = new Service(dao);

        user1 = service.register(new RegisterRequest("alice", "password", "alice@email.com"));
        user2 = service.register(new RegisterRequest("bob", "password", "bob@email.com"));
    }

    @Test
    void createGameSucceeds() throws ResponseException, DataAccessException {
        CreateGameResult game = service.createGame(user1.authToken(), new CreateGameRequest("BestGame"));

        assertTrue(game.gameID() > 0, "Game ID should be greater than 0");
    }

    @Test
    void createGameFailsForEmptyName() {
        assertThrows(ResponseException.class, () -> {
            service.createGame(user1.authToken(), new CreateGameRequest(""));
        });
    }

    @Test
    void joinGameSucceeds() throws ResponseException, DataAccessException {
        CreateGameResult game = service.createGame(user1.authToken(), new CreateGameRequest("ChessBattle"));

        service.joinGame(new JoinGameRequest("BLACK", game.gameID()), user2.username());

        assertEquals("bob", dao.getGameDAO().getGame(game.gameID()).blackUsername());
    }

    @Test
    void joinGameFailsIfSpotTaken() throws ResponseException, DataAccessException {
        CreateGameResult game = service.createGame(user1.authToken(), new CreateGameRequest("EpicGame"));

        service.joinGame(new JoinGameRequest("WHITE", game.gameID()), user2.username());

        assertThrows(ResponseException.class, () -> {
            service.joinGame(new JoinGameRequest("WHITE", game.gameID()), user1.username());
        });
    }

    @Test
    void listGamesReturnsAllGames() throws ResponseException, DataAccessException {
        service.createGame(user1.authToken(), new CreateGameRequest("GameOne"));
        service.createGame(user1.authToken(), new CreateGameRequest("GameTwo"));

        ListGamesResult result = service.listGames(user1.authToken());
        assertEquals(2, result.games().size(), "Should return all created games");
    }

    @Test
    void listGamesFailsForInvalidAuth() {
        assertThrows(ResponseException.class, () -> {
            service.listGames("invalid-token");
        });
    }
}
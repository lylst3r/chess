package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccessDAO;
import exception.ResponseException;
import org.junit.jupiter.api.Test;
import server.service.GameService;
import server.service.Service;
import server.service.request.CreateGameRequest;
import server.service.request.JoinGameRequest;
import server.service.request.RegisterRequest;
import server.service.result.CreateGameResult;
import server.service.result.ListGamesResult;
import server.service.result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

     MemoryDataAccessDAO dao;
     Service service;

    public GameServiceTests() {

        dao = new MemoryDataAccessDAO();
        service = new Service(dao);

    }

    @Test
    void createGameSucceeds() throws ResponseException, DataAccessException {
        RegisterResult r = service.register(new RegisterRequest("kalea", "1234", "kalea@email.com"));
        CreateGameResult game = service.createGame(new CreateGameRequest("bestGameEver"));

        assertTrue(game.gameID() > 0);
    }

    @Test
    void createGameFailsForNullName() {
        assertThrows(ResponseException.class, () -> {
            service.createGame(new CreateGameRequest(""));
        });
    }

    @Test
    void joinGameSucceeds() throws ResponseException, DataAccessException {
        RegisterResult lily = service.register(new RegisterRequest("lily", "password", "lily@email.com"));
        RegisterResult unicorns = service.register(new RegisterRequest("unicorns", "pwd", "unicorns@email.com"));

        CreateGameResult game = service.createGame(new CreateGameRequest("myGame"));
        service.joinGame(new JoinGameRequest("BLACK", game.gameID()), "unicorns");

        assertEquals("unicorns", dao.getGameDAO().getGame(game.gameID()).blackUsername());
    }

    @Test
    void joinGameFailsIfSpotTaken() throws ResponseException, DataAccessException {
        RegisterResult lily = service.register(new RegisterRequest("lily", "pwd", "lily@email.com"));
        RegisterResult potato = service.register(new RegisterRequest("potato", "pwd", "potato@email.com"));
        RegisterResult charlie = service.register(new RegisterRequest("charlie", "pwd", "charlie@email.com"));

        CreateGameResult game = service.createGame(new CreateGameRequest("aGame"));
        service.joinGame(new JoinGameRequest("BLACK", game.gameID()), "potato");

        assertThrows(ResponseException.class, () -> {
            service.joinGame(new JoinGameRequest("BLACK", game.gameID()), "charlie");
        });
    }

    @Test
    void listGamesReturnsAllGames() throws ResponseException, DataAccessException {
        RegisterResult lily = service.register(new RegisterRequest("lily", "pwd", "lily@email.com"));
        service.createGame(new CreateGameRequest("yay"));

        ListGamesResult result = service.listGames(lily.authToken());
        assertEquals(1, result.games().size());
    }

    @Test
    void listGamesFailsForBadAuth() throws ResponseException, DataAccessException {
        assertThrows(ResponseException.class, () -> {
            service.listGames(null);
        });
    }

}

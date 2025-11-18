package client;

import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clear() throws Exception {
        facade.clear();
    }

    @Test
    public void registerPositive() throws Exception {
        AuthData auth = facade.register(new UserData("u1", "pass", "email"));
        assertNotNull(auth);
        assertEquals("u1", auth.username());
        assertTrue(auth.authToken().length() > 5);
    }

    @Test
    public void registerNegativeDuplicateUser() throws Exception {
        facade.register(new UserData("u1", "pass", "email"));
        assertThrows(ResponseException.class, () ->
                facade.register(new UserData("u1", "pass", "email")));
    }

    @Test
    public void loginPositive() throws Exception {
        facade.register(new UserData("u1", "pass", "email"));
        AuthData auth = facade.login("u1", "pass");
        assertNotNull(auth);
        assertEquals("u1", auth.username());
    }

    @Test
    public void loginNegativeWrongPassword() throws Exception {
        facade.register(new UserData("u1", "pass", "email"));
        assertThrows(ResponseException.class, () ->
                facade.login("u1", "BADPASS"));
    }

    @Test
    public void logoutPositive() throws Exception {
        AuthData auth = facade.register(new UserData("u1", "pass", "email"));
        assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }

    @Test
    public void logoutNegativeInvalidToken() throws Exception {
        assertThrows(ResponseException.class, () ->
                facade.logout(null));
    }

    @Test
    public void createGamePositive() throws Exception {
        AuthData auth = facade.register(new UserData("u1", "pass", "email"));

        GameData game = facade.createGame(auth.authToken(), "Chess");

        assertNotNull(game);
        assertTrue(game.gameID() > 0, "Game ID should be positive");
    }

    @Test
    public void createGameNegativeInvalidAuth() {
        assertThrows(ResponseException.class, () ->
                facade.createGame("BAD_TOKEN", "Chess"));
    }

    @Test
    public void listGamesPositive() throws Exception {
        AuthData auth = facade.register(new UserData("u1", "pass", "email"));
        facade.createGame(auth.authToken(), "Game A");
        facade.createGame(auth.authToken(), "Game B");

        GameData[] games = facade.listGames(auth.authToken());

        assertNotNull(games);
        assertEquals(2, games.length);
        assertTrue(Arrays.stream(games).anyMatch(g -> g.gameName().equals("Game A")));
        assertTrue(Arrays.stream(games).anyMatch(g -> g.gameName().equals("Game B")));
    }

    @Test
    public void listGamesNegativeInvalidAuth() {
        assertThrows(ResponseException.class, () ->
                facade.listGames("BAD_TOKEN"));
    }

    @Test
    public void joinGamePositive() throws Exception {
        AuthData auth = facade.register(new UserData("u1", "pass", "email"));
        GameData created = facade.createGame(auth.authToken(), "Chess");

        GameData joined = facade.joinGame(auth.authToken(), created.gameID(), "LIGHT");

        assertNotNull(joined);
        assertEquals(created.gameID(), joined.gameID());
    }

    @Test
    public void joinGameNegativeInvalidGameID() throws Exception {
        AuthData auth = facade.register(new UserData("u1", "pass", "email"));
        assertThrows(ResponseException.class, () ->
                facade.joinGame(auth.authToken(), 9999, "LIGHT"));
    }

    @Test
    public void clearPositive() throws Exception {
        facade.register(new UserData("u1", "pass", "email"));
        facade.clear();
        assertThrows(ResponseException.class, () ->
                facade.login("u1", "pass"));
    }

    @Test
    public void clearNegativeRemoteCallFails() {
        ServerFacade badFacade = new ServerFacade("http://localhost:9999");
        assertThrows(ResponseException.class, badFacade::clear);
    }
}

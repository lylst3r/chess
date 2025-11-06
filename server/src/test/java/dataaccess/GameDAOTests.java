package dataaccess;

import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import dataaccess.sql.SQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {

    private final SQLGameDAO gameDAO;

    public GameDAOTests() throws ResponseException, DataAccessException{
        gameDAO = new SQLGameDAO();
    }

    @BeforeEach
    void clearAll() throws ResponseException, DataAccessException {
        gameDAO.clearGames();
    }

    @Test
    void createGamePositive() throws ResponseException, DataAccessException {
        GameData game = new GameData(0, "lily", "gwen", "familyGame", null);
        int id = gameDAO.createGame(game);
        assertTrue(id > 0);
    }

    @Test
    void createGameNegative() {
        GameData invalidGame = new GameData(0, null, null, null, null);
        assertThrows(ResponseException.class, () -> gameDAO.createGame(invalidGame));
    }

    @Test
    void getGamePositive() throws ResponseException, DataAccessException {
        GameData game = new GameData(0, "noah", "annie", "bestGame", null);
        int id = gameDAO.createGame(game);
        GameData retrieved = gameDAO.getGame(id);
        assertEquals("bestGame", retrieved.gameName());
    }

    @Test
    void getGameNegative() throws ResponseException, DataAccessException {
        assertNull(gameDAO.getGame(99999));
    }

    @Test
    void listGamesPositive() throws ResponseException, DataAccessException {
        gameDAO.createGame(new GameData(0, "maui", "pippi", "Game1", null));
        gameDAO.createGame(new GameData(0, "maui", "pippi", "Game2", null));
        ArrayList<GameData> games = gameDAO.listGames();
        assertEquals(2, games.size());
    }

    @Test
    void updateGamePositive() throws ResponseException, DataAccessException {
        GameData game = new GameData(0, "unicorn", "narwhal", "OldName", null);
        int id = gameDAO.createGame(game);
        GameData updated = new GameData(id, "unicorn", "narwhal", "NewName", null);
        gameDAO.updateGame(id, updated);
        assertEquals("NewName", gameDAO.getGame(id).gameName());
    }

    @Test
    void clearGamesPositive() throws ResponseException, DataAccessException {
        gameDAO.createGame(new GameData(0, "unicorn", "narwhal", "ToClear", null));
        gameDAO.clearGames();
        assertEquals(0, gameDAO.listGames().size());
    }

}

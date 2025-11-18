package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;

public class SQLGameDAO implements GameDAO {

    private final SQLHelper sqlHelper;

    public SQLGameDAO() throws ResponseException, DataAccessException {
        sqlHelper = new SQLHelper();
        configureDatabase();
    }

    public int createGame(GameData game) throws DataAccessException, ResponseException {
        String statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        return executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
    }

    public GameData getGame(int gameID) throws DataAccessException, ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, " +
                    "gameName, game FROM game WHERE gameID = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public ArrayList<GameData> listGames() throws DataAccessException, ResponseException {
        var result = new ArrayList<GameData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException, ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE game SET whiteUsername = ?, blackUsername = ?, " +
                    "gameName = ?, game = ? WHERE gameID = ?";

            String json = new Gson().toJson(updatedGame.game());

            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, updatedGame.whiteUsername());
                ps.setString(2, updatedGame.blackUsername());
                ps.setString(3, updatedGame.gameName());
                ps.setString(4, json);
                ps.setInt(5, gameID);

                ps.executeUpdate();
            }

        } catch (SQLException e) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    "Error updating game: " + e.getMessage());
        }
    }

    public void clearGames() throws DataAccessException, ResponseException {
        var statement = "DELETE FROM game";
        executeUpdate(statement);
        //executeUpdate("ALTER TABLE game AUTO_INCREMENT = 1");
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int id = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        String json = rs.getString("game");
        ChessGame game = new Gson().fromJson(json, ChessGame.class);
        return new GameData(id, whiteUsername, blackUsername, gameName, game);
    }

    private int executeUpdate(String statement, Object... params) throws ResponseException, DataAccessException {
        return sqlHelper.executeUpdate(statement, params);
    }


    private void configureDatabase() throws ResponseException, DataAccessException {
        assert sqlHelper != null;
        sqlHelper.configureDatabase();
        }
}

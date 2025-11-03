package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    public int createGame(GameData game) throws DataAccessException, ResponseException {
        var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        String json = new Gson().toJson(game);
        int id = executeUpdate(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        return id;
    }

    public GameData getGame(int gameID) throws DataAccessException, ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game, json FROM game WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public ArrayList<GameData> listGames() throws DataAccessException, ResponseException {
        var result = new ArrayList<GameData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, json FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException, ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE game SET json=? WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                executeUpdate(statement);
            }

        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public void clearGames() throws DataAccessException, ResponseException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var id = rs.getString("gameID");
        var json = rs.getString("json");
        GameData game = new Gson().fromJson(json, GameData.class);
        return game;
    }



    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` INT NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(255) NOT NULL,
              `blackUsername` varchar(255) NOT NULL,
              `gameName` varchar(255) NOT NULL,
              `game` JSON DEFAULT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(whiteUsername),
              INDEX(blackUsername)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private int executeUpdate(String statement, Object... params) throws ResponseException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                Object param = params[i];

                if (param instanceof String s) ps.setString(i + 1, s);
                else if (param instanceof Integer n) ps.setInt(i + 1, n);
                else if (param instanceof ChessGame game) {
                    String json = new Gson().toJson(game);
                    ps.setString(i + 1, json);
                } else if (param == null) ps.setNull(i + 1, Types.NULL);
                else ps.setString(i + 1, param.toString());
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // returns the generated gameID
                }
            }

            return 0; // no key generated

        } catch (SQLException e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to update database: %s, %s", statement, e.getMessage()));
        }
    }


    private void configureDatabase() throws ResponseException, DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

}

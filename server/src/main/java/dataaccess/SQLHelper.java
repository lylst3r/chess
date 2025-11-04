package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;

import java.sql.*;

public class SQLHelper {

    public SQLHelper() throws ResponseException, DataAccessException {

    }

    private final String[] createUserStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(255) NOT NULL UNIQUE,
              `password` varchar(255) NOT NULL,
              `email` varchar(255) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(password),
              INDEX(email)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public final String[] createGameStatements = {
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

    public final String[] createAuthStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` varchar(255) NOT NULL,
              `username` varchar(255) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public int executeUpdate(String statement, Object... params) throws ResponseException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                switch (param) {
                    case String s -> ps.setString(i + 1, s);
                    case Integer n -> ps.setInt(i + 1, n);
                    case ChessGame game -> {
                        String json = new Gson().toJson(game);
                        ps.setString(i + 1, json);
                    }
                    case null -> ps.setNull(i + 1, Types.NULL);
                    default ->
                            ps.setString(i + 1, param.toString());
                }
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            return 0;
        } catch (SQLException e) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    String.format("Unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    public void configureDatabase(String table) throws ResponseException, DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            String[] statements;
            switch (table) {
                case "user" -> {
                    statements = createUserStatements;
                    for (String statement : statements) {
                        try (var preparedStatement = conn.prepareStatement(statement)) {
                            preparedStatement.executeUpdate();
                        }
                    }
                }
                case "game" -> {
                    statements = createGameStatements;
                    for (String statement : statements) {
                        try (var preparedStatement = conn.prepareStatement(statement)) {
                            preparedStatement.executeUpdate();
                        }
                    }
                }
                case "auth" -> {
                    statements = createAuthStatements;
                    for (String statement : statements) {
                        try (var preparedStatement = conn.prepareStatement(statement)) {
                            preparedStatement.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

}

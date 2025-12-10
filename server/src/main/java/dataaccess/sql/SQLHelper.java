package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;

import java.sql.*;

public class SQLHelper {

    public SQLHelper() throws ResponseException, DataAccessException {}

    private final String createUserStatements = """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(255) NOT NULL UNIQUE,
              `password` varchar(255) NOT NULL,
              `email` varchar(255) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(password),
              INDEX(email)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """;

    public final String createGameStatements = """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` INT NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(255) DEFAULT NULL,
              `blackUsername` varchar(255) DEFAULT NULL,
              `gameName` varchar(255) NOT NULL,
              `game` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(whiteUsername),
              INDEX(blackUsername)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """;

    public final String createAuthStatements = """
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` varchar(255) NOT NULL,
              `username` varchar(255) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """;

    public int executeUpdate(String statement, Object... params) throws ResponseException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            setStatementParams(ps, params);
            int affectedRows = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return affectedRows;

        } catch (SQLException e) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    "Error: Unable to update database: " + e.getMessage());
        }
    }

    private void setStatementParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param == null) {
                ps.setNull(i + 1, Types.NULL);
            } else if (param instanceof String s) {
                ps.setString(i + 1, s);
            } else if (param instanceof Integer n) {
                ps.setInt(i + 1, n);
            } else if (param instanceof ChessGame game) {
                ps.setString(i + 1, new Gson().toJson(game));
            } else {
                ps.setString(i + 1, param.toString());
            }
        }
    }

    public void configureDatabase() throws ResponseException, DataAccessException {
        databaseExists();

        try (Connection conn = DatabaseManager.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createUserStatements);
                stmt.executeUpdate(createGameStatements);
                stmt.executeUpdate(createAuthStatements);
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    "Error: Unable to configure database: " + ex.getMessage());
        }
    }

    private void databaseExists() throws DataAccessException {
        String dbName = DatabaseManager.getDatabaseName();

        try {
            // Try connecting normally first
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://" + DatabaseManager.getHost() + ":" + DatabaseManager.getPort() + "/" + dbName,
                    DatabaseManager.getUsername(),
                    DatabaseManager.getPassword())) {
                return; // Database exists
            }
        } catch (SQLException e) {
            // If the error is not "unknown database", rethrow
            if (!e.getMessage().toLowerCase().contains("unknown database")) {
                throw new DataAccessException("Failed to check database existence: " + e.getMessage(), e);
            }

            // Connect without database to create it
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://" + DatabaseManager.getHost() + ":" + DatabaseManager.getPort() + "/",
                    DatabaseManager.getUsername(),
                    DatabaseManager.getPassword());
                 Statement stmt = conn.createStatement()) {

                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName + " CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci");

            } catch (SQLException ex) {
                throw new DataAccessException("Failed to create database: " + ex.getMessage(), ex);
            }
        }
    }
}

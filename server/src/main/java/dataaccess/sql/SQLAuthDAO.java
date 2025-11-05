package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    private final SQLHelper sqlHelper;

    public SQLAuthDAO() throws DataAccessException, ResponseException {
        sqlHelper = new SQLHelper();
        configureDatabase();
    }

    public void createAuth(AuthData auth) throws DataAccessException, ResponseException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, auth.authToken(), auth.username());
    }

    public AuthData getAuth(String authToken) throws DataAccessException, ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth WHERE authToken = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException, ResponseException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    public void clearAuths() throws DataAccessException, ResponseException {
        var statement = "DELETE FROM auth";
        executeUpdate(statement);
    }

    public String getUsername(String authToken) throws DataAccessException, ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM auth WHERE authToken = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("username");
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        return new AuthData(rs.getString("authToken"), rs.getString("username"));
    }

    private void executeUpdate(String statement, Object... params) throws ResponseException, DataAccessException {
        sqlHelper.executeUpdate(statement, params);
    }


    private void configureDatabase() throws ResponseException, DataAccessException {
        assert sqlHelper != null;
        sqlHelper.configureDatabase();
    }
}

package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;

import java.sql.*;

public class SQLHelper {

    public SQLHelper() {}

    public int executeUpdate(String statement, Object... params) throws ResponseException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof String s) {
                    ps.setString(i + 1, s);
                } else if (param instanceof Integer n) {
                    ps.setInt(i + 1, n);
                } else if (param instanceof ChessGame game) {
                    String json = new Gson().toJson(game);
                    ps.setString(i + 1, json);
                } else if (param == null) {
                    ps.setNull(i + 1, java.sql.Types.NULL);
                } else {
                    // fallback: convert to string
                    ps.setString(i + 1, param.toString());
                }
            }

            ps.executeUpdate();

            // return generated key if any
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }

            return 0; // no key generated
        } catch (SQLException e) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    String.format("Unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

}

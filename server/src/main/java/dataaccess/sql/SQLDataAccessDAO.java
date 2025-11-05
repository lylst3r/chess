package dataaccess.sql;


import dataaccess.DataAccessException;
import dataaccess.MainDataAccessDAO;
import exception.ResponseException;

import java.sql.SQLException;

import static dataaccess.sql.DatabaseManager.getConnection;

public class SQLDataAccessDAO extends MainDataAccessDAO {

    public SQLDataAccessDAO() throws ResponseException, DataAccessException {

        super(new SQLUserDAO(), new SQLGameDAO(), new SQLAuthDAO());
    }

    public boolean addTestRecord(String authToken, String username) throws SQLException, DataAccessException {
        String sql = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, authToken);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        }
    }

}

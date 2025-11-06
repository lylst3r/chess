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

}

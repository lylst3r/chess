package dataaccess.sql;


import dataaccess.DataAccessException;
import dataaccess.MainDataAccessDAO;
import exception.ResponseException;

public class SQLDataAccessDAO extends MainDataAccessDAO {

    public SQLDataAccessDAO() throws ResponseException, DataAccessException {

        super(new SQLUserDAO(), new SQLGameDAO(), new SQLAuthDAO());
    }

}

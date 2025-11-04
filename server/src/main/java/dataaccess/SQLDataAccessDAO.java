package dataaccess;


import exception.ResponseException;

public class SQLDataAccessDAO extends mainDataAccessDAO {

    public SQLDataAccessDAO() throws ResponseException, DataAccessException {

        super(new SQLUserDAO(), new SQLGameDAO(), new SQLAuthDAO());
    }
}

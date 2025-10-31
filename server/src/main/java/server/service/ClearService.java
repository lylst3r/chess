package server.service;

import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import exception.ResponseException;

public class ClearService {

    private final DataAccessDAO dao;

    public ClearService(DataAccessDAO dao) {

        this.dao = dao;

    }

    public void clearAll() throws DataAccessException, ResponseException {

        dao.clearAll();
    }

}

package server.service;

import dataaccess.MemoryDataAccessDAO;
import dataaccess.DataAccessException;
import exception.ResponseException;

public class ClearService {

    MemoryDataAccessDAO dao;

    public ClearService(MemoryDataAccessDAO dao) {

        this.dao = dao;

    }

    public void clearAll() throws DataAccessException, ResponseException {

        dao.clearAll();
    }

}

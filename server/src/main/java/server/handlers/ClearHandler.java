package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccessDAO;
import exception.ResponseException;
import server.service.Service;

public class ClearHandler {

    private final Service service;
    private final DataAccessDAO dao;
    private final Gson gson = new Gson();

    public ClearHandler(Service service, DataAccessDAO dao) {
        this.service = service;
        this.dao = dao;
    }

    public void clearAll() throws DataAccessException, ResponseException {
        service.clear();
    }
}

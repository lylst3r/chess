package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccessDAO;
import exception.ResponseException;
import server.service.Service;

public class ClearHandler {

    Service service;
    MemoryDataAccessDAO dao;
    private final Gson gson = new Gson();

    public ClearHandler(Service service, MemoryDataAccessDAO dao) {
        this.service = service;
        this.dao = dao;
    }

    public void clearAll(String authToken) throws DataAccessException, ResponseException {
        service.clear(authToken);
    }
}

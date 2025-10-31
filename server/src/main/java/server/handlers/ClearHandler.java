package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import exception.ResponseException;
import server.service.Service;

public class ClearHandler {

    private final Service service;
    private final Gson gson = new Gson();

    public ClearHandler(Service service) {
        this.service = service;
    }

    public void clearAll() throws DataAccessException, ResponseException {
        service.clear();
    }
}

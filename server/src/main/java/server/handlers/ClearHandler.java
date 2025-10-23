package server.handlers;

import dataaccess.DataAccessException;
import server.service.Service;

public class ClearHandler {

    public void clearAll(Service service) throws DataAccessException {
        service.clear();
    }
}

package server.handlers;

import dataaccess.DataAccessDAO;
import server.services.Service;

public class Handler {

    Service service = new Service();
    ClearHandler clearHandler = new ClearHandler();

    public void clear() {
        clearHandler.clearAll(service);
    }

}

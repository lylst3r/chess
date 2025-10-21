package server.handlers;

import server.services.Service;

public class ClearHandler {

    public void clearAll(Service service) {
        service.clear();
    }
}

package server.handlers;

import dataaccess.DataAccessException;
import exception.ResponseException;
import server.service.Service;

public class Handler {

    Service service;
    ClearHandler clearHandler;
    UserHandler userHandler;
    GameHandler gameHandler;

    public Handler(){
        service = new Service();
        clearHandler = new ClearHandler();
    }

    public void clear() throws DataAccessException {
        clearHandler.clearAll(service);
    }

    /*public String[] login(String username, String password) throws ResponseException {
        return userHandler.login(username, password, service);
    }

    public void register(String username, String password, String email) throws ResponseException {
        userHandler.register(username, password, email, service);
    }*/

}

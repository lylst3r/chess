package server.handlers;

import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import exception.ResponseException;
import server.service.Service;
import server.service.request.LoginRequest;
import server.service.request.LogoutRequest;
import server.service.request.RegisterRequest;
import server.service.result.LoginResult;
import server.service.result.RegisterResult;

//endpoints: register, login/logout
public class UserHandler {

    private final Service service;

    public UserHandler(Service service){

        this.service = service;

    }

    public LoginResult login(String username, String password) throws ResponseException, DataAccessException {
        LoginRequest loginRequest = new LoginRequest(username, password);
        return service.login(loginRequest);
    }

    public RegisterResult register(String username, String password, String email) throws ResponseException, DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        return service.register(registerRequest);
    }

    public void logout(String authToken) throws ResponseException, DataAccessException {
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        service.logout(logoutRequest);
    }
}

package server.service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccessDAO;
import exception.ResponseException;
import server.service.request.LoginRequest;
import server.service.request.LogoutRequest;
import server.service.request.RegisterRequest;
import server.service.result.ListGamesResult;
import server.service.result.LoginResult;
import server.service.result.LoginResult;
import server.service.result.RegisterResult;

public class Service {

    MemoryDataAccessDAO dao;
    ClearService clearService;
    UserService userService;
    GameService gameService;

    public Service(MemoryDataAccessDAO dao) {
        this.dao = dao;
        clearService = new ClearService(dao);
        userService = new UserService(dao);
        gameService = new GameService(dao);
    }

    public void clear(String authToken) throws DataAccessException, ResponseException {
        clearService.clearAll(authToken);

    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException, DataAccessException {
        return userService.login(loginRequest);
    }

    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException, DataAccessException {
        return userService.register(registerRequest);
    }

    public void logout(LogoutRequest logoutRequest) throws ResponseException, DataAccessException {
        userService.logout(logoutRequest);
    }

    public ListGamesResult listGames(String authToken) throws ResponseException, DataAccessException {
        return gameService.listGames(authToken);
    }

}

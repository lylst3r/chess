package server.handlers;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccessDAO;
import exception.ResponseException;
import server.service.Service;
import server.service.result.CreateGameResult;
import server.service.result.ListGamesResult;
import server.service.result.LoginResult;
import server.service.result.RegisterResult;

public class Handler {

    private final Service service;
    private final ClearHandler clearHandler;
    private final UserHandler userHandler;
    private final GameHandler gameHandler;
    private final MemoryDataAccessDAO dao;

    public Handler(Service service, MemoryDataAccessDAO dao){
        this.service = service;
        clearHandler = new ClearHandler(service, dao);
        userHandler = new UserHandler(service, dao);
        gameHandler = new GameHandler(service, dao);
        this.dao = dao;
    }

    public void clear(String authToken) throws DataAccessException, ResponseException {

        clearHandler.clearAll(authToken);
    }

    public LoginResult login(String username, String password) throws ResponseException, DataAccessException {
        return userHandler.login(username, password);
    }

    public RegisterResult register(String username, String password, String email) throws ResponseException, DataAccessException {
        return userHandler.register(username, password, email);
    }

    public void logout(String authToken) throws ResponseException, DataAccessException {
        userHandler.logout(authToken);
    }

    public ListGamesResult listGames(String authToken) throws ResponseException, DataAccessException {
        return gameHandler.listGames(authToken);
    }

    public CreateGameResult createGame(String authToken, String gameName) throws ResponseException, DataAccessException {
        return gameHandler.createGame(authToken, gameName);
    }

}

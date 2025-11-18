package server.handlers;

import dataaccess.DataAccessException;
import exception.ResponseException;
import server.service.Service;
import server.service.request.JoinGameRequest;
import server.service.result.CreateGameResult;
import server.service.result.ListGamesResult;
import server.service.result.LoginResult;
import server.service.result.RegisterResult;

public class Handler {

    private final ClearHandler clearHandler;
    private final UserHandler userHandler;
    private final GameHandler gameHandler;

    public Handler(Service service){
        this.clearHandler = new ClearHandler(service);
        this.userHandler = new UserHandler(service);
        this.gameHandler = new GameHandler(service);
    }

    public void clear() throws DataAccessException, ResponseException {

        clearHandler.clearAll();
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

    public void joinGame(String authToken, JoinGameRequest request) throws ResponseException, DataAccessException {
        gameHandler.joinGame(authToken, request);
    }
}

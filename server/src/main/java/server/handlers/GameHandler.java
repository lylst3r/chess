package server.handlers;

import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import server.service.Service;
import server.service.request.CreateGameRequest;
import server.service.request.JoinGameRequest;
import server.service.result.CreateGameResult;
import server.service.result.ListGamesResult;

//endpoints: listGames, createGame, joinGame
public class GameHandler {

    private final Service service;


    public GameHandler(Service service) {

        this.service = service;

    }

    public ListGamesResult listGames(String authToken) throws ResponseException, DataAccessException {
        AuthData auth = service.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");
        }

        return service.listGames(authToken);
    }

    public CreateGameResult createGame(String authToken, String gameName) throws ResponseException, DataAccessException {
        AuthData auth = service.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");
        }

        CreateGameRequest request = new CreateGameRequest(gameName);
        return service.createGame(request);
    }

    public void joinGame(String authToken, JoinGameRequest request) throws ResponseException, DataAccessException {
        AuthData auth = service.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");
        }

        String username = auth.username();

        service.joinGame(request, username);
    }
}

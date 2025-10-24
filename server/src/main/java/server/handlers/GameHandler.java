package server.handlers;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccessDAO;
import exception.ResponseException;
import server.service.Service;
import server.service.result.ListGamesResult;

//endpoints: listGames, createGame, joinGame
public class GameHandler {

    Service service;
    MemoryDataAccessDAO dao;


    public GameHandler(Service service, MemoryDataAccessDAO dao) {

        this.service = service;
        this.dao = dao;

    }

    public ListGamesResult listGames(String authToken) throws ResponseException, DataAccessException {
        return service.listGames(authToken);
    }
}

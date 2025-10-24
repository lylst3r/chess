package server.service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccessDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import server.service.result.ListGamesResult;

import java.util.ArrayList;
import java.util.List;

//endpoints: listGames, createGame, joinGame
public class GameService {

    MemoryDataAccessDAO dao = new MemoryDataAccessDAO();

    public GameService(MemoryDataAccessDAO dao){

        this.dao = dao;

    }

    public ListGamesResult listGames(String authToken) throws ResponseException, DataAccessException {
        AuthData auth = dao.getAuthDAO().getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");
        }
        String username = auth.username();
        ArrayList<GameData> games = dao.getGameDAO().listGames();
        ListGamesResult result = new ListGamesResult(games);
        return result;
    }
}

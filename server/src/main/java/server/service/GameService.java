package server.service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccessDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import server.service.request.CreateGameRequest;
import server.service.result.CreateGameResult;
import server.service.result.ListGamesResult;

import java.util.ArrayList;
import java.util.List;

//endpoints: listGames, createGame, joinGame
public class GameService {

    MemoryDataAccessDAO dao = new MemoryDataAccessDAO();

    public GameService(MemoryDataAccessDAO dao){

        this.dao = dao;

    }

    public ListGamesResult listGames() throws ResponseException, DataAccessException {
        ArrayList<GameData> games = dao.getGameDAO().listGames();
        ListGamesResult result = new ListGamesResult(games);
        return result;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ResponseException, DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(0, null, null, request.gameName(), chessGame);
        int gameID = dao.getGameDAO().createGame(game);;
        CreateGameResult result = new CreateGameResult(gameID);
        return result;
    }
}

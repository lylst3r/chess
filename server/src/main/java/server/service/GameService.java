package server.service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccessDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import server.service.request.CreateGameRequest;
import server.service.request.JoinGameRequest;
import server.service.result.CreateGameResult;
import server.service.result.ListGamesResult;

import java.util.ArrayList;
import java.util.List;

//endpoints: listGames, createGame, joinGame
public class GameService {

    private final MemoryDataAccessDAO dao;

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

    public void joinGame(JoinGameRequest request, String username) throws ResponseException, DataAccessException {
        GameData game = dao.getGameDAO().getGame(request.gameID());
        if (game == null) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Error: bad request");
        }
        String playerColor = request.playerColor();
        //spectator
        if (playerColor == null) {
            return;
        }

        if (playerColor.equals("WHITE") && game.whiteUsername() != null) {
            throw new ResponseException(ResponseException.Code.Conflict, "Error: color taken");
        }
        else if (playerColor.equals("BLACK") && game.blackUsername() != null) {
            throw new ResponseException(ResponseException.Code.Conflict, "Error: color taken");
        }

        GameData newGame;
        if (playerColor.equals("WHITE")) {
            newGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
            dao.getGameDAO().updateGame(request.gameID(), newGame);
        }
        else if (playerColor.equals("BLACK")) {
            newGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
            dao.getGameDAO().updateGame(request.gameID(), newGame);
        }
    }
}

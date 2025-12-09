package server.service;

import chess.ChessGame;
import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.GameData;
import server.service.request.CreateGameRequest;
import server.service.request.JoinGameRequest;
import server.service.result.CreateGameResult;
import server.service.result.ListGamesResult;

import java.util.ArrayList;

//endpoints: listGames, createGame, joinGame
public class GameService {

    private final DataAccessDAO dao;

    public GameService(DataAccessDAO dao){

        this.dao = dao;

    }

    public ListGamesResult listGames(String authToken) throws ResponseException, DataAccessException {
        try {
            if (authToken == null || authToken.isEmpty()|| dao.getAuthDAO().getAuth(authToken) == null) {
                throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");
            }
            ArrayList<GameData> games = dao.getGameDAO().listGames();
            return new ListGamesResult(games);
        } catch (DataAccessException e) {
            throw new  ResponseException(ResponseException.Code.ServerError, "Error: Internal Server Error " + e.getMessage());
        }
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws ResponseException, DataAccessException {
        var auth = dao.getAuthDAO().getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");
        }

        try {
            ChessGame chessGame = new ChessGame();
            String gameName = request.gameName();

            if (gameName == null || gameName.isEmpty()) {
                throw new ResponseException(ResponseException.Code.BadRequest, "Error: Game name cannot be empty");
            }

            GameData game = new GameData(0, null, null, gameName, chessGame);
            int gameID = dao.getGameDAO().createGame(game);
            return new CreateGameResult(gameID);
        } catch (DataAccessException e) {
            throw new  ResponseException(ResponseException.Code.ServerError, "Error: Internal Server Error " + e.getMessage());
        }
    }

    public void joinGame(JoinGameRequest request, String username) throws ResponseException, DataAccessException {
        try {
            GameData game = dao.getGameDAO().getGame(request.gameID());

            if (game == null) {
                throw new ResponseException(ResponseException.Code.BadRequest, "Error: bad request");
            }

            String gameName = game.gameName();
            if (gameName == null || gameName.isEmpty()) {
                throw new ResponseException(ResponseException.Code.BadRequest, "Error: Game name cannot be empty");
            }

            String playerColor = request.playerColor();
            //spectator
            if (playerColor == null) {
                throw new ResponseException(ResponseException.Code.BadRequest, "Error: no color given");
            }

            playerColor = playerColor.toUpperCase();

            if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
                throw new ResponseException(ResponseException.Code.BadRequest, "Error: invalid color");

            }

            if (playerColor.equals("WHITE")) {
                if (game.whiteUsername() != null && !game.whiteUsername().equals(username)) {
                    throw new ResponseException(ResponseException.Code.Conflict, "Error: color taken");
                }
            } else {
                if (game.blackUsername() != null && !game.blackUsername().equals(username)) {
                    throw new ResponseException(ResponseException.Code.Conflict, "Error: color taken");
                }
            }

            GameData newGame;
            if (playerColor.equals("WHITE")) {
                newGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
                dao.getGameDAO().updateGame(request.gameID(), newGame);
            }
            else {
                newGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
                dao.getGameDAO().updateGame(request.gameID(), newGame);
            }
        } catch (DataAccessException e) {
            throw new  ResponseException(ResponseException.Code.ServerError, "Error: Internal Server Error " + e.getMessage());
        }
    }

    public void leaveGame(int gameID, String username) throws ResponseException, DataAccessException {
        try {
            GameData game = dao.getGameDAO().getGame(gameID);

            if (game == null) {
                throw new ResponseException(ResponseException.Code.BadRequest, "Error: bad request");
            }

            String white = game.whiteUsername();
            String black = game.blackUsername();

            // Determine if the user is in the game
            boolean isWhite = username.equals(white);
            boolean isBlack = username.equals(black);

            if (!isWhite && !isBlack) {
                return;
            }

            GameData newGame;

            if (isWhite) {
                newGame = new GameData(game.gameID(), null, game.blackUsername(),
                        game.gameName(), game.game());
            } else {
                newGame = new GameData(game.gameID(), game.whiteUsername(), null,
                        game.gameName(), game.game());
            }

            dao.getGameDAO().updateGame(gameID, newGame);

        } catch (DataAccessException e) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    "Error: Internal Server Error " + e.getMessage());
        }
    }

}

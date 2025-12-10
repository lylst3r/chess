package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessDAO;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import java.io.IOException;
import dataaccess.AuthDAO;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();
    private final DataAccessDAO dao;

    public  WebSocketHandler(DataAccessDAO dao) {
        this.dao = dao;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            var root = new Gson().fromJson(ctx.message(), UserGameCommand.class);

            switch (root.getCommandType()) {
                case CONNECT -> {
                    UserGameCommand cmd = new Gson().fromJson(ctx.message(), UserGameCommand.class);
                    connect(cmd.getAuthToken(), cmd.getGameID(), (Session) ctx.session);
                }
                case LEAVE -> {
                    UserGameCommand cmd = new Gson().fromJson(ctx.message(), UserGameCommand.class);
                    leave(cmd.getAuthToken(), cmd.getGameID(), ctx.session);
                }
                case RESIGN -> {
                    UserGameCommand cmd = new Gson().fromJson(ctx.message(), UserGameCommand.class);
                    resign(cmd.getAuthToken(), cmd.getGameID(), ctx.session);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand cmd = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(cmd, ctx.session);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(String authToken, int gameID, Session session) throws IOException {
        try {
            var auth = dao.getAuthDAO().getAuth(authToken);
            if (auth == null) {
                sendError(session, "Invalid auth token");
                return;
            }

            String username = auth.username();

            var game = dao.getGameDAO().getGame(gameID);
            if (game == null) {
                sendError(session, "Game not found");
                return;
            }

            connections.add(gameID, session);

            var loadGame = new LoadGameMessage(dao.getGameDAO().getGame(gameID));
            session.getRemote().sendString(gson.toJson(loadGame));

            var notification = new NotificationMessage(username + " joined the game");
            connections.broadcast(gameID, session, notification);

        } catch (Exception e) {
            sendError(session, "Failed to connect");
        }
    }

    private void leave(String authToken, int gameID, Session session) throws IOException {
        try {
            var auth = dao.getAuthDAO().getAuth(authToken);
            if (auth == null) {
                sendError(session, "Invalid auth token");
                return;
            }

            var game = dao.getGameDAO().getGame(gameID);
            if (game == null) {
                sendError(session, "Game not found");
                return;
            }

            String username = auth.username();

            String newWhite = game.whiteUsername();
            String newBlack = game.blackUsername();

            if (username.equals(newWhite)) {
                newWhite = null;
            } else if (username.equals(newBlack)) {
                newBlack = null;
            }

            GameData updated = new GameData(
                    gameID,
                    newWhite,
                    newBlack,
                    game.gameName(),
                    game.game()
            );

            dao.getGameDAO().updateGame(gameID, updated);

            connections.remove(session);

            var note = new NotificationMessage(username + " left the game");
            connections.broadcast(gameID, session, note);

        } catch (Exception e) {
            sendError(session, "Failed to leave");
        }
    }

    private void makeMove(MakeMoveCommand action, Session session) throws IOException {
        String authToken = action.getAuthToken();
        int gameID = action.getGameID();

        try {
            var auth = dao.getAuthDAO().getAuth(authToken);
            if (auth == null) {
                sendError(session, "Invalid auth token");
                return;
            }
            String username = auth.username();

            var game = dao.getGameDAO().getGame(gameID);
            if (game == null) {
                sendError(session, "Invalid game ID");
                return;
            }

            var chessGame = game.game();

            if (chessGame.isGameOver()) {
                sendError(session, "Game is already over");
                return;
            }

            if (!username.equals(game.whiteUsername()) && !username.equals(game.blackUsername())) {
                sendError(session, "You are not a player in this game");
                return;
            }

            var move = action.getMove();

            if (!chessGame.isValidMove(move)) {
                sendError(session, "Illegal move");
                return;
            }

            boolean isWhite = username.equals(game.whiteUsername());
            boolean isBlack = username.equals(game.blackUsername());

            if ((chessGame.getTeamTurn() == ChessGame.TeamColor.WHITE && !isWhite) ||
                    (chessGame.getTeamTurn() == ChessGame.TeamColor.BLACK && !isBlack)) {
                sendError(session, "Not your turn");
                return;
            }

            chessGame.makeMove(move);

            GameData newGame = new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame);
            dao.getGameDAO().updateGame(gameID, newGame);

            var loadGame = new LoadGameMessage(newGame);
            session.getRemote().sendString(gson.toJson(loadGame));

            var note = new NotificationMessage(username + " made a move");
            connections.broadcast(gameID, session, note);

            var loadGameBroadcast = new LoadGameMessage(newGame);
            connections.broadcast(gameID, session, loadGameBroadcast);

        } catch (Exception e) {
            e.printStackTrace();
            sendError(session, "Failed to make move");
        }
    }

    private void resign(String authToken, int gameID, Session session) throws IOException {
        try {
            var auth = dao.getAuthDAO().getAuth(authToken);
            if (auth == null) {
                sendError(session, "Invalid auth token");
                return;
            }
            String username = auth.username();

            var game = dao.getGameDAO().getGame(gameID);
            if (game == null) {
                sendError(session, "Invalid game ID");
                return;
            }

            boolean isWhite = username.equals(game.whiteUsername());
            boolean isBlack = username.equals(game.blackUsername());

            if (!isWhite && !isBlack) {
                sendError(session, "Only players may resign");
                return;
            }

            if (game.game().isGameOver()) {
                sendError(session, "Game already over");
                return;
            }

            var chessGame = game.game();
            chessGame.resign(isWhite ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK);

            GameData updated = new GameData(gameID,
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName(),
                    chessGame);

            dao.getGameDAO().updateGame(gameID, updated);

            var loadGame = new LoadGameMessage(updated);

            session.getRemote().sendString(gson.toJson(
                    new NotificationMessage(username + " resigned")));

            String winner = isWhite ? game.blackUsername() : game.whiteUsername();

            String combined = username + " resigned, " + winner + " wins by resignation";

            connections.broadcast(gameID, session,
                    new NotificationMessage(combined));


            connections.remove(session);

        } catch (Exception e) {
            sendError(session, "Failed to resign");
        }
    }

    private void sendError(Session session, String errorMessage) throws IOException {
        var error = new ErrorMessage(errorMessage);
        session.getRemote().sendString(gson.toJson(error));
    }
}

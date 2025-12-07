package server.websocket;

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
import org.eclipse.jetty.websocket.api.Session;
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
            UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (action.getCommandType()) {
                case CONNECT -> connect(action.getAuthToken(), action.getGameID(), (Session) ctx.session);
                case MAKE_MOVE -> makeMove(action, ctx.session);
                case LEAVE -> leave(action.getAuthToken(), action.getGameID(), ctx.session);
                case RESIGN -> resign(action.getAuthToken(), action.getGameID(), ctx.session);
            }
        } catch (IOException ex) {
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
        var message = String.format("Leaving game: %s", authToken);
        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, session, notification);
        connections.remove(session);
    }

    private void makeMove(UserGameCommand action, Session session) throws IOException {
        int gameID = action.getGameID();
        String authToken = action.getAuthToken();

        var moveMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast(gameID, session, moveMessage);

        if (session.isOpen()) {
            session.getRemote().sendString(gson.toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION)));
        }
    }

    private void resign(String authToken, int gameID, Session session) throws IOException {
        var message = String.format("%s resigned", authToken);
        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, session, notification);
        connections.remove(session);
    }

    private void sendError(Session session, String errorMessage) throws IOException {
        var error = new ErrorMessage(errorMessage);
        session.getRemote().sendString(gson.toJson(error));
    }
}

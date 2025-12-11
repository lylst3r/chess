package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.*;
import websocket.messages.*;

import jakarta.websocket.*;
import java.io.IOException;
import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private final NotificationHandler handler;
    private static final Gson GSON = new Gson();

    public WebSocketFacade(String baseUrl, NotificationHandler handler) throws ResponseException {
        try {
            this.handler = handler;

            String wsUrl = baseUrl.replace("http", "ws") + "/ws";
            URI uri = new URI(wsUrl);

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                ServerMessage base = GSON.fromJson(message, ServerMessage.class);

                switch (base.getServerMessageType()) {
                    case LOAD_GAME -> handler.notify(
                            GSON.fromJson(message, LoadGameMessage.class));
                    case ERROR -> handler.notify(
                            GSON.fromJson(message, ErrorMessage.class));
                    case NOTIFICATION -> handler.notify(
                            GSON.fromJson(message, NotificationMessage.class));
                }
                }
            });

        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {}

    public void send(UserGameCommand command) throws ResponseException {
        try {
            session.getBasicRemote().sendText(GSON.toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void connect(String auth, int gameID) throws ResponseException {
        send(new ConnectCommand(auth, gameID));
    }

    public void makeMove(String auth, int gameID, ChessMove move) throws ResponseException {
        send(new MakeMoveCommand(auth, gameID, move));
    }

    public void resign(String auth, int gameID) throws ResponseException {
        send(new ResignCommand(auth, gameID));
    }

    public void leave(String auth, int gameID) throws ResponseException {
        send(new LeaveCommand(auth, gameID));
    }
}

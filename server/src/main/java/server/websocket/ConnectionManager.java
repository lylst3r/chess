package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final Map<Integer, Set<Session>> gameConnections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        gameConnections.putIfAbsent(gameID, ConcurrentHashMap.newKeySet());
        gameConnections.get(gameID).add(session);
    }

    public void remove(Session session) {
        for (var set : gameConnections.values()) {
            set.remove(session);
        }
    }

    public void broadcast(int gameID, Session exclude, ServerMessage message) throws IOException {
        var sessions = gameConnections.get(gameID);
        if (sessions == null) return;

        String json = new Gson().toJson(message);

        for (Session s : sessions) {
            if (s.isOpen() && !s.equals(exclude)) {
                s.getRemote().sendString(json);
            }
        }
    }
}
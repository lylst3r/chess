package server;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson;

    // Gson instance that ignores the "game" field in GameData
    private final Gson gsonIgnoreGame = new GsonBuilder()
            .addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getName().equals("game");
                }
                @Override
                public boolean shouldSkipClass(Class<?> clazz) { return false; }
            }).create();

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
        this.gson = new Gson();
    }

    // ---------------- PreLogin --------------------

    public AuthData register(UserData user) throws ResponseException {
        HttpRequest request = buildRequest("POST", "/user", user, null);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(String username, String password) throws ResponseException {
        UserData body = new UserData(username, password, null);
        HttpRequest request = buildRequest("POST", "/session", body, null);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    // ---------------- PostLogin --------------------

    public void logout(String authToken) throws ResponseException {
        HttpRequest request = buildRequest("DELETE", "/session", null, authToken);
        sendRequest(request);
    }

    public GameData[] listGames(String authToken) throws ResponseException {
        HttpRequest request = buildRequest("GET", "/game", null, authToken);
        HttpResponse<String> response = sendRequest(request);

        return gsonIgnoreGame.fromJson(response.body(), GameData[].class);
    }

    public GameData createGame(String authToken, String gameName) throws ResponseException {
        Map<String, String> body = new HashMap<>();
        body.put("gameName", gameName);
        HttpRequest request = buildRequest("POST", "/game", body, authToken);
        HttpResponse<String> response = sendRequest(request);

        return gsonIgnoreGame.fromJson(response.body(), GameData.class);
    }

    public GameData joinGame(String authToken, int gameID, String playerColor) throws ResponseException {
        Map<String, Object> body = new HashMap<>();
        body.put("gameID", gameID);
        body.put("playerColor", playerColor);
        HttpRequest request = buildRequest("PUT", "/game", body, authToken);
        HttpResponse<String> response = sendRequest(request);

        return gsonIgnoreGame.fromJson(response.body(), GameData.class);
    }

    public void clear() throws ResponseException {
        HttpRequest request = buildRequest("DELETE", "/db", null, null);
        sendRequest(request);
    }

    // ---------------- HELPERS --------------------

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, body != null
                        ? HttpRequest.BodyPublishers.ofString(gson.toJson(body))
                        : HttpRequest.BodyPublishers.noBody());

        if (body != null) builder.header("Content-Type", "application/json");
        if (authToken != null) builder.header("Authorization", authToken);

        return builder.build();
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, "Failed to connect to server");
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        int status = response.statusCode();
        String body = response.body();

        if (status / 100 != 2) {
            String message = "Server error: " + status;
            if (body != null && !body.isEmpty()) {
                try {
                    Map<?, ?> map = gson.fromJson(body, Map.class);
                    if (map != null && map.get("message") != null) {
                        message = map.get("message").toString();
                    }
                } catch (Exception ignored) {}
            }

            ResponseException.Code code = switch (status) {
                case 400 -> ResponseException.Code.BadRequest;
                case 401 -> ResponseException.Code.Unauthorized;
                case 403 -> ResponseException.Code.Conflict;
                default -> ResponseException.Code.ServerError;
            };

            throw new ResponseException(code, message);
        }

        if (responseClass == null) return null;
        return gson.fromJson(body, responseClass);
    }
}

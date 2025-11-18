package server;

import com.google.gson.*;
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
    private final Gson gson = new Gson();

    private String authToken;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    //preLogin

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

    //postLogin

    public void logout(String authToken) throws ResponseException {
        if (authToken == null || authToken.isBlank()) {
            throw new ResponseException(ResponseException.Code.Unauthorized,
                    "Error: Unauthorized");
        }

        HttpRequest request = buildRequest("DELETE", "/session", null, authToken);
        HttpResponse<String> response = sendRequest(request);

        if (response.statusCode() == 401) {
            throw new ResponseException(ResponseException.Code.Unauthorized,
                    "Error: Unauthorized");
        }

        authToken = null;
    }

    public GameData[] listGames(String authToken) throws ResponseException {
        if (authToken == null || authToken.isBlank()) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: Unauthorized");
        }

        HttpRequest request = buildRequest("GET", "/game", null, authToken);
        HttpResponse<String> response = sendRequest(request);

        if (response.statusCode() == 401) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: Unauthorized");
        }

        Map<String, Object> wrapper = gson.fromJson(response.body(), Map.class);

        var list = (java.util.List<Map<String, Object>>) wrapper.get("games");

        GameData[] result = new GameData[list.size()];

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> g = list.get(i);
            result[i] = new GameData(
                    ((Number) g.get("gameID")).intValue(),
                    (String) g.get("whiteUsername"),
                    (String) g.get("blackUsername"),
                    (String) g.get("gameName"),
                    null
            );
        }

        return result;
    }

    public GameData createGame(String authToken, String gameName) throws ResponseException {
        if (authToken == null || authToken.isBlank()) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: Unauthorized");
        }

        Map<String, String> body = new HashMap<>();
        body.put("gameName", gameName);

        HttpRequest request = buildRequest("POST", "/game", body, authToken);
        HttpResponse<String> response = sendRequest(request);

        if (response.statusCode() == 401) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: Unauthorized");
        }

        return gson.fromJson(response.body(), GameData.class);
    }

    public GameData joinGame(String authToken, int gameID, String playerColor) throws ResponseException {
        if (authToken == null || authToken.isBlank()) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: Unauthorized");
        }

        if (playerColor == null || playerColor.isBlank()) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Player color cannot be empty");
        }

        String color = switch (playerColor.trim().toUpperCase()) {
            case "LIGHT" -> "WHITE";
            case "DARK"  -> "BLACK";
            default -> throw new ResponseException(ResponseException.Code.BadRequest,
                    "Must enter 'LIGHT' or 'DARK' for team color.");
        };

        Map<String, Object> body = new HashMap<>();
        body.put("playerColor", color);
        body.put("gameID", gameID);

        HttpRequest request = buildRequest("PUT", "/game", body, authToken);
        HttpResponse<String> response = sendRequest(request);

        int status = response.statusCode();
        if (status / 100 != 2) {
            handleResponse(response, null);
        }

        Map<String, Object> wrapper = gson.fromJson(response.body(), Map.class);
        Object gamesObj = wrapper.get("games");
        if (gamesObj == null) {
            throw new ResponseException(ResponseException.Code.ServerError,
                    "Server returned no games after joining");
        }

        java.util.List<Map<String, Object>> list = (java.util.List<Map<String, Object>>) gamesObj;
        for (var g : list) {
            int id = ((Number) g.get("gameID")).intValue();
            if (id == gameID) {
                return new GameData(
                        id,
                        (String) g.get("whiteUsername"),
                        (String) g.get("blackUsername"),
                        (String) g.get("gameName"),
                        null
                );
            }
        }

        throw new ResponseException(ResponseException.Code.BadRequest, "Game not found after join");
    }

    public void clear() throws ResponseException {
        HttpRequest request = buildRequest("DELETE", "/db", null, null);
        sendRequest(request);
    }

    //HELPERS

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, body != null
                        ? HttpRequest.BodyPublishers.ofString(gson.toJson(body))
                        : HttpRequest.BodyPublishers.noBody());

        if (body != null) {
            builder.header("Content-Type", "application/json");
        }
        if (authToken != null) {
            builder.header("Authorization", authToken);
        }

        return builder.build();
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, "Failed to connect to server");
        }
    }

    private final Gson gsonIgnoreGame = new GsonBuilder()
            .addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getName().equals("game");
                }
                @Override
                public boolean shouldSkipClass(Class<?> clazz) { return false; }
            }).create();

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

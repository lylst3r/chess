package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccessDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import exception.ResponseException;

import model.AuthData;
import model.GameData;
import model.UserData;
import server.handlers.Handler;
import server.service.Service;
import server.service.result.LoginResult;
import server.service.result.RegisterResult;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final Handler handler;
    private final Service service;
    private final MemoryDataAccessDAO memoryDAO;

    public Server() {
        memoryDAO = new MemoryDataAccessDAO();
        service = new Service(memoryDAO);
        handler  = new Handler(service, memoryDAO);
        this.javalin = Javalin.create(config -> config.staticFiles.add("web"))

        // Register your endpoints and exception handlers here.
            .delete("/db", this::clear)
            .post("/user", this::register)
            .post("/session", this::login)
            .delete("/session", this::logout)
            .get("/game", this::listGames)
            .post("/game", this::createGame)
            .put("/game", this::joinGame)
            .exception(ResponseException.class, this::exceptionHandler);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public int port() {
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void exceptionHandler(ResponseException ex, Context ctx) {
        ctx.status(ex.toHttpStatusCode());
        ctx.json(ex.toJson());
    }

    private void register(Context ctx) throws ResponseException,  DataAccessException {
        Gson gson = new Gson();
        UserData user = gson.fromJson(ctx.body(), UserData.class);

        String username = user.username();
        String password = user.password();
        String email = user.email();

        RegisterResult result = handler.register(username, password, email);

        ctx.status(200);
    }

    private void login(Context ctx) throws ResponseException, DataAccessException {
        Gson gson = new Gson();
        UserData user = gson.fromJson(ctx.body(), UserData.class);

        String username = user.username();
        String password = user.password();

        LoginResult result = handler.login(username, password);
        if (result == null) {
            ctx.status(400);
            return;
        }

        ctx.json(Map.of(
                "username", result.username(),
                "authToken", result.authToken()
        ));

        ctx.status(200);
    }

    private void logout(Context ctx) throws ResponseException, DataAccessException {
        Gson gson = new Gson();
        AuthData auth = gson.fromJson(ctx.body(), AuthData.class);

        String authToken = auth.authToken();

        handler.logout(authToken);
        ctx.status(200);

    }

    private void listGames(Context ctx) throws ResponseException, DataAccessException {
        Gson gson = new Gson();
        AuthData auth = gson.fromJson(ctx.body(), AuthData.class);

        String authToken = auth.authToken();

        handler.listGames(authToken);
        ctx.status(200);

    }

    private void createGame(Context ctx) throws ResponseException, DataAccessException {
        Gson gson = new Gson();
        AuthData auth = gson.fromJson(ctx.body(), AuthData.class);
        GameData game = gson.fromJson(ctx.body(), GameData.class);

        String authToken = auth.authToken();
        String gameName = game.gameName();

        handler.createGame(authToken, gameName);
        ctx.status(200);
    }

    private void joinGame(Context ctx) throws ResponseException {}

    private void clear(Context ctx) throws DataAccessException, ResponseException {
        Gson gson = new Gson();
        String authToken = null;

        String headerToken = ctx.header("authorization");
        if (headerToken != null && !headerToken.isEmpty()) {
            authToken = headerToken;
        } else if (ctx.body() != null && !ctx.body().isEmpty()) {
            AuthData auth = gson.fromJson(ctx.body(), AuthData.class);
            if (auth != null) {
                authToken = auth.authToken();
            }
        }

        handler.clear(authToken);
        ctx.status(200);
        ctx.result("");
    }
}

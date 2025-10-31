package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessDAO;
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
import server.service.request.JoinGameRequest;
import server.service.result.CreateGameResult;
import server.service.result.ListGamesResult;
import server.service.result.LoginResult;
import server.service.result.RegisterResult;

import java.util.ArrayList;
import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final Handler handler;
    private final Service service;
    private final DataAccessDAO dao;
    private final Gson gson;

    public Server() {
        dao = new MemoryDataAccessDAO();
        service = new Service(dao);
        handler  = new Handler(service, dao);
        gson = new Gson();
        this.javalin = Javalin.create(config -> {
                    config.staticFiles.add("web");
                });

        // Register your endpoints and exception handlers here.
            javalin.delete("/db", this::clear)
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
        ctx.result(ex.toJson());
    }

    private void register(Context ctx) throws ResponseException,  DataAccessException {
        UserData user = gson.fromJson(ctx.body(), UserData.class);

        String username = user.username();
        String password = user.password();
        String email = user.email();

        RegisterResult result = handler.register(username, password, email);

        ctx.status(200);
        ctx.result(gson.toJson(result));
    }

    private void login(Context ctx) throws ResponseException, DataAccessException {
        UserData user = gson.fromJson(ctx.body(), UserData.class);

        String username = user.username();
        String password = user.password();

        LoginResult result = handler.login(username, password);

        ctx.status(200);
        ctx.result(gson.toJson(result));
    }

    private void logout(Context ctx) throws ResponseException, DataAccessException {
        String authToken = ctx.header("authorization");

        handler.logout(authToken);
        ctx.status(200);
        ctx.result("");

    }

    private void listGames(Context ctx) throws ResponseException, DataAccessException {
        String authToken = ctx.header("authorization");

        ListGamesResult result = handler.listGames(authToken);
        ctx.status(200);
        ctx.result(gson.toJson(result));

    }

    private void createGame(Context ctx) throws ResponseException, DataAccessException {
        String authToken = ctx.header("authorization");
        GameData game = gson.fromJson(ctx.body(), GameData.class);
        String gameName = game.gameName();

        CreateGameResult result = handler.createGame(authToken, gameName);
        ctx.status(200);
        ctx.result(gson.toJson(result));
    }

    private void joinGame(Context ctx) throws ResponseException, DataAccessException {
        String authToken = ctx.header("authorization");
        JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);

        handler.joinGame(authToken, request);
        ctx.status(200);
        ctx.result("");

    }

    private void clear(Context ctx) throws DataAccessException, ResponseException {

        handler.clear();
        ctx.status(200);
        ctx.result("");
    }
}

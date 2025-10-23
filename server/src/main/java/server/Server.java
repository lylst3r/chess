package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import exception.ResponseException;

import model.UserData;
import server.handlers.Handler;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final Handler handler;

    public Server() {
        this.handler  = new Handler();
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

    private void register(Context ctx) throws ResponseException {
        Gson gson = new Gson();
        UserData user = gson.fromJson(ctx.body(), UserData.class);

        String username = user.username();
        String password = user.password();
        String email = user.email();

        //handler.register(username, password, email);

        ctx.status(200);
    }

    private void login(Context ctx) throws ResponseException {
        /*Gson gson = new Gson();
        UserData user = gson.fromJson(ctx.body(), UserData.class);

        String username = user.username();
        String password = user.password();

        String[] authInfo = handler.login(username, password);
        if (authInfo.length != 2) {
            ctx.status(400);
            return;
        }

        ctx.json(Map.of(
                "username", authInfo[0],
                "authToken", authInfo[1]
        ));

        ctx.status(200);*/
    }

    private void logout(Context ctx) throws ResponseException {}

    private void listGames(Context ctx) throws ResponseException {}

    private void createGame(Context ctx) throws ResponseException {}

    private void joinGame(Context ctx) throws ResponseException {}

    private void clear(Context ctx) throws DataAccessException {
        handler.clear();
        ctx.status(200);
        ctx.result("");
    }
}

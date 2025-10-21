package server;

import io.javalin.*;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import exception.ResponseException;

import server.handlers.Handler;

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

    private void register(Context ctx) throws ResponseException {}

    private void login(Context ctx) throws ResponseException {}

    private void logout(Context ctx) throws ResponseException {}

    private void listGames(Context ctx) throws ResponseException {}

    private void createGame(Context ctx) throws ResponseException {}

    private void joinGame(Context ctx) throws ResponseException {}

    private void clear(Context ctx)  {
        handler.clear();
        ctx.status(200);
    }
}

package ui;

import exception.ResponseException;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public class UIHelper {
    private State state = State.LOGGEDOUT;
    private AuthData auth;
    private GameData game;
    private String gameName;
    private ArrayList<GameTracker> games;
    private String color;

    public UIHelper() {}

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public AuthData getAuth() {
        return auth;
    }

    public void setAuth(AuthData auth) {
        this.auth = auth;
    }

    public GameData getGame() {
        return game;
    }

    public void setGame(GameData game) {
        this.game = game;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void addGame(int gameID, String gameName) throws ResponseException {
        GameTracker game = new GameTracker(gameID, gameName);
        games.add(game);
    }

    public String getGameName(int gameID) {
        for (GameTracker g : games) {
            if (g.getId() == gameID) {
                return g.getName();
            }
        }
        return "No game with ID: " + gameID;
    }

    public String getColor() {
        if (color == null) {
            return "";
        }
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

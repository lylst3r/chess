package ui;

import chess.ChessPosition;
import exception.ResponseException;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public class UIHelper {
    private State state = State.LOGGEDOUT;
    private AuthData auth;
    private GameData game;
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

    public String getAuthToken() {
        return auth.authToken();
    }

    public GameData getGame() {
        return game;
    }

    public void setGame(GameData game) {
        this.game = game;
    }

    public String getGameName(int gameID) {
        for (GameTracker g : games) {
            if (g.getId() == gameID) {
                return g.getName();
            }
        }
        return "No game with ID: " + gameID;
    }

    public int getGameID() {
        return game.gameID();
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

    public ChessPosition toPosition(String coord) throws ResponseException {
        if (coord == null || coord.length() != 2) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Invalid coordinate: " + coord);
        }

        char file = coord.charAt(0);
        char rank = coord.charAt(1);

        int col = file - 'a' + 1;
        int row = rank - '0';

        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Coordinate out of range: " + coord);
        }

        return new ChessPosition(row, col);
    }
}

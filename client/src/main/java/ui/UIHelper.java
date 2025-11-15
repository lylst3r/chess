package ui;

import model.AuthData;
import model.GameData;

public class UIHelper {
    private State state = State.LOGGEDOUT;
    private AuthData auth;
    private GameData game;

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
}

package ui;

import model.AuthData;

public class UIHelper {
    private State state = State.LOGGEDOUT;
    private AuthData auth;

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
}

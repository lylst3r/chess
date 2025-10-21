package dataaccess;

import model.GameData;

public class Game {

    private int gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private chess.ChessGame game;

    public Game(int gameID, String whiteUsername, String blackUsername, String gameName, chess.ChessGame game) {

    }

    public void setUserData(int gameID, String whiteUsername, String blackUsername, String gameName, chess.ChessGame game) {
        GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }
}

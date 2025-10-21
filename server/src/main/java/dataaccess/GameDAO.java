package dataaccess;

import java.util.ArrayList;

public class GameDAO {
    private ArrayList<Game> gameList;

    public GameDAO(){
        gameList = new ArrayList<>();
    }

    public void createGame(int gameID, String whiteUsername, String blackUsername, String gameName, chess.ChessGame game) {
        Game gameNew = new Game(gameID, whiteUsername, blackUsername, gameName, game);
        gameList.add(gameNew);
    }

    public void clearGames() {
        gameList.clear();
    }
}

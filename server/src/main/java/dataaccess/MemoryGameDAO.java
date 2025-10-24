package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class MemoryGameDAO implements GameDAO {

    private final ArrayList<GameData> gameList;
    private int gameIDCounter = 0;

    public MemoryGameDAO(){
        gameList = new ArrayList<>();
    }

    public int createGame(GameData game) throws DataAccessException {
        gameIDCounter++;
        GameData newGame = new GameData(gameIDCounter, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        gameList.add(newGame);
        return gameIDCounter;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        for(GameData game: gameList){
            if(game.gameID() == gameID){
                return game;
            }
        }
        return null;
    }

    public ArrayList<GameData> listGames() throws DataAccessException {
        return gameList;
    }

    public void updateGame(int gameID, GameData updateGame) throws DataAccessException {
        if (!gameList.contains(gameID)) {
            throw new DataAccessException("Game not found");
        }

        for(int i = 0; i < gameList.size(); i++){
            GameData game = gameList.get(i);
            if(game.gameID() == gameID){
                gameList.set(i, updateGame);
            }
        }
    }

    public void clearGames() throws DataAccessException {
        gameList.clear();
        gameIDCounter = 0;
    }
}

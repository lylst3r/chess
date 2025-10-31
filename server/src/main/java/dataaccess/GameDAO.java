package dataaccess;

import java.util.ArrayList;

import model.GameData;

public interface GameDAO {

    int createGame(GameData game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    ArrayList<GameData> listGames() throws DataAccessException;
    void updateGame(int gameID, GameData updatedGame) throws DataAccessException;
    void clearGames() throws DataAccessException;

}

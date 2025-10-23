package dataaccess;

import java.util.List;

import model.GameData;

public interface GameDAO {

    void createGame(GameData game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    void updateGame(int gameID, GameData updatedGame) throws DataAccessException;
    void clearGames() throws DataAccessException;

}

package dataaccess;

import java.util.ArrayList;

import exception.ResponseException;
import model.GameData;

public interface GameDAO {

    int createGame(GameData game) throws DataAccessException, ResponseException;
    GameData getGame(int gameID) throws DataAccessException, ResponseException;
    ArrayList<GameData> listGames() throws DataAccessException, ResponseException;
    void updateGame(int gameID, GameData updatedGame) throws DataAccessException, ResponseException;
    void clearGames() throws DataAccessException, ResponseException;

}

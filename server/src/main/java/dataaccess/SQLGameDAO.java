package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() {

    }

    public int createGame(GameData game) throws DataAccessException {
        return 0;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    public ArrayList<GameData> listGames() throws DataAccessException {
        return null;
    }

    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {

    }

    public void clearGames() throws DataAccessException {

    }

}

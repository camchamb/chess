package dataAccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void createGame(GameData u) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    GameData updateGame(GameData u) throws DataAccessException;
}

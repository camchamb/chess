package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class GameSqlAccess implements GameDAO{
    @Override
    public int createGame(String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData u) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}

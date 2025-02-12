package dataAccess;

import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class GameMemoryAccess implements GameDAO{
    private int nextId = 1;
    final private HashMap<Integer, GameData> data = new HashMap<>();

    @Override
    public void createGame(GameData u) throws DataAccessException {
        return;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (!data.containsKey(gameID)) {
            return null;
        }
        return data.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(data.values());
    }


    @Override
    public GameData updateGame(GameData u) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        data.clear();
    }
}

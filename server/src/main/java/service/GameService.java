package service;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;


public class GameService {
    private final GameDAO gameAccess;

    public GameService(GameDAO gameAccess) {
        this.gameAccess = gameAccess;
    }

    public void clear() throws DataAccessException {
        gameAccess.clear();
    }
}

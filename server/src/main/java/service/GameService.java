package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import service.Requests.CreateGameRequest;
import service.Requests.CreateGameResult;
import service.Requests.ListGamesRequest;
import service.Requests.ListGamesResult;


public class GameService {
    private final GameDAO gameAccess;
    private final AuthDAO authAccess;

    public GameService(GameDAO gameAccess, AuthDAO authAccess) {
        this.gameAccess = gameAccess;
        this.authAccess = authAccess;
    }

    public void clear() throws DataAccessException {
        gameAccess.clear();
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException{
        if (listGamesRequest.authToken() == null) {
            throw new DataAccessException(400, "Error: invalid request");
        }
        var authData = authAccess.getAuth(listGamesRequest.authToken());
        if (authData == null) {
            throw new DataAccessException(401, "Error: unauthorized");
        }
        var data = gameAccess.listGames();
        return new ListGamesResult(data);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException{
        if (createGameRequest.authToken() == null) {
            throw new DataAccessException(400, "Error: invalid request");
        }
        var authData = authAccess.getAuth(createGameRequest.authToken());
        if (authData == null) {
            throw new DataAccessException(401, "Error: unauthorized");
        }
        var gameID = gameAccess.createGame(createGameRequest.gameName());
        return new CreateGameResult(gameID);
    }
}

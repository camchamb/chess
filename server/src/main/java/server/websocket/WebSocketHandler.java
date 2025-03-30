package server.websocket;


import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
//import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessages;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final UserDAO userAccess;
    private final GameDAO gameAccess;
    private final AuthDAO authAccess;


    public WebSocketHandler(UserDAO userAccess, GameDAO gameAccess, AuthDAO authAccess) {
        this.userAccess = userAccess;
        this.gameAccess = gameAccess;
        this.authAccess = authAccess;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.println(message);
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> connect(action.getAuthToken(), action.getGameID(), session);
//            case MAKE_MOVE -> make_move(action.visitorName());
            case LEAVE -> leave(action.getAuthToken(), action.getGameID());
//            case RESIGN -> resign(action.visitorName());
        }
    }

    private void connect(String authToken, int gameID, Session session) throws IOException {
        connections.add(authToken, gameID, session);
        String message;
        GameData gameData;
        try {
            var authData = authAccess.getAuth(authToken);
            if (authData == null) {
                var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Bad Request");
                connections.messageRoot(authToken, error);
                return;
            }
            message = String.format("%s connected to the game", authData.username());
            gameData = gameAccess.getGame(gameID);
            if (gameData == null) {
                var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "No Such Game");
                connections.messageRoot(authToken, error);
                return;
            }
        } catch (DataAccessException e) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Bad Request");
            connections.messageRoot(authToken, error);
            return;
        }
        var loadGame = new LoadGameMessages(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
        connections.messageRoot(authToken, loadGame);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(authToken, gameID, notification);
    }

    private void leave(String authToken, int gameID) throws IOException {
        connections.remove(authToken);
        String message;
        GameData gameData;
        AuthData authData;
        try {
            authData = authAccess.getAuth(authToken);
            message = String.format("%s left the game", authData.username());
            gameData = gameAccess.getGame(gameID);
            if (gameData == null) {
                var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "No Such Game");
                connections.messageRoot(authToken, error);
                return;
            }
            if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(authData.username())) {
                gameAccess.updateGame(new GameData(gameID, null, gameData.blackUsername(), gameData.gameName(), gameData.game()));
            }
            if (gameData.blackUsername() != null &&gameData.blackUsername().equals(authData.username())) {
                gameAccess.updateGame(new GameData(gameID, gameData.whiteUsername(), null, gameData.gameName(), gameData.game()));
            }
        } catch (DataAccessException e) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Bad Request");
            connections.messageRoot(authToken, error);
            return;
        }
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(authToken, gameID, notification);
    }
//
//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new ServerMessage(ServerMessage.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
}

package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import chess.ResponseException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessages;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    private final Gson serializer = new Gson();

    public WebSocketFacade(String url, NotificationHandler notificationHandler) {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = serializer.fromJson(message, ServerMessage.class);
                    if (notification.getServerMessageType().equals(ServerMessage.ServerMessageType.ERROR)) {
                        notificationHandler.notify(serializer.fromJson(message, ErrorMessage.class));
                    }
                    if (notification.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
                        notificationHandler.notify(serializer.fromJson(message, LoadGameMessages.class));
                    }
                    if (notification.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
                        notificationHandler.notify(serializer.fromJson(message, NotificationMessage.class));
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameID) {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(serializer.toJson(action));
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void leave(String authToken, int gameID) {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(serializer.toJson(action));
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void resign(String authToken, int gameID) {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(serializer.toJson(action));
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) {
        try {
            var action = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
            this.session.getBasicRemote().sendText(serializer.toJson(action));
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}

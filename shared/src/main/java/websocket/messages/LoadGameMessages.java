package websocket.messages;

import chess.ChessGame;

public class LoadGameMessages extends ServerMessage {
    private final ChessGame game;

    public LoadGameMessages(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
    }

    public ChessGame getGame() {
        return this.game;
    }
}

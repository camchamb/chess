package client;

//import com.sun.nio.sctp.Notification;
//import com.sun.nio.sctp.NotificationHandler;
import chess.ChessMove;
import chess.ChessPosition;
import client.websocket.NotificationHandler;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessages;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final ChessClient chessClient;

    public Repl(String serverUrl) {
        chessClient = new ChessClient(serverUrl, this);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        System.out.print("Welcome to the Chess Server." + "\n" + chessClient.eval("help") + "\n");
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = chessClient.eval(line);
                System.out.println(result);
            } catch (Throwable e) {
                var msg = e.getMessage();
                System.out.print(msg);
            }
        }
    }

    public void printPrompt() {
        var prompt = chessClient.printPrompt();
        System.out.print("\n" + prompt + " >>> ");
    }


    @Override
    public void notify(ServerMessage notification) {
        ServerMessage.ServerMessageType type = notification.getServerMessageType();
        if (type.equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
            NotificationMessage msg = (NotificationMessage) notification;
            System.out.print(msg.getMessage());
        }
        if (type.equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
            LoadGameMessages msg = (LoadGameMessages) notification;
            chessClient.game = msg.getGame();
            chessClient.redraw(new ArrayList<ChessMove>());
        }
        if (type.equals(ServerMessage.ServerMessageType.ERROR)) {
            ErrorMessage msg = (ErrorMessage) notification;
            System.out.println("Error: " + msg.getErrorMessage());
        }
        printPrompt();
    }
}

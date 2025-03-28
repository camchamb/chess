package client;

import com.sun.nio.sctp.HandlerResult;
//import com.sun.nio.sctp.Notification;
//import com.sun.nio.sctp.NotificationHandler;
import client.websocket.NotificationHandler;
import websocket.messages.ServerMessage;

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
                System.out.println("\n" + result);
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
        System.out.println(notification.getServerMessageType());
        printPrompt();
    }
}

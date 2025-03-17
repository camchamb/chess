package client;

import com.sun.nio.sctp.HandlerResult;
import com.sun.nio.sctp.Notification;
import com.sun.nio.sctp.NotificationHandler;
import dataaccess.DataAccessException;

import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final ChessClient chessClient;
    public State state = State.PreloginClient;

    public Repl(String serverUrl) {
        chessClient = new ChessClient(serverUrl, this);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        System.out.print("\n" + chessClient.eval("help") + "\n");
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
        System.out.print("\n" + "ChessClient >>> ");
    }

    @Override
    public HandlerResult handleNotification(Notification notification, Object attachment) {
        return null;
    }
}

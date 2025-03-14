package client;

import com.sun.nio.sctp.HandlerResult;
import com.sun.nio.sctp.Notification;
import com.sun.nio.sctp.NotificationHandler;

import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final PreloginClient preClient;
    private final PostloginClient postClient;
    private final GamePlayClient gameClient;
    public State state = State.PreloginClient;

    public Repl(String serverUrl) {
        preClient = new PreloginClient(serverUrl, this);
        postClient = new PostloginClient(serverUrl, this);
        gameClient = new GamePlayClient(serverUrl, this);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        System.out.print("\n" + preClient.eval("help") + "\n");
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = switch (state) {
                    case PreloginClient -> preClient.eval(line);
                    case PostloginClient -> postClient.eval(line);
                    case GamePlayClient -> gameClient.eval(line);
                };
                System.out.println("\n" + result);
            } catch (Throwable e) {
                var msg = e.toString();
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

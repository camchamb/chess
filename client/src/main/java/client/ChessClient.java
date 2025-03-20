package client;

import chess.ChessGame;
import com.sun.nio.sctp.NotificationHandler;
import model.GameData;
import serverfacade.ServerFacade;
import requests.*;
import model.UserData;
import ui.PrintBoard;
import java.util.ArrayList;
import java.util.Arrays;

public class ChessClient {
    private final NotificationHandler notificationHandler;
    private String authToken = null;
    private ArrayList<GameData> gameList = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.PreloginClient;
    private ChessGame.TeamColor playersColor = null;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (state) {
                case PreloginClient -> preEval(params, cmd);
                case PostloginClient -> postEval(params, cmd);
                case GamePlayClient -> gameEval(params, cmd);
            };
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }

    public String preEval(String[] params, String cmd) throws RuntimeException{
        return switch (cmd) {
            case "register" -> register(params);
            case "login" -> login(params);
            case "quit" -> "quit";
            default -> preHelp();
        };
    }

    public String postEval(String[] params, String cmd) throws RuntimeException{
        return switch (cmd) {
            case "create" -> create(params);
            case "list" -> list();
            case "join" -> join(params);
            case "observe" -> observe(params);
            case "logout" -> logout();
            case "quit" -> quit();
            default -> postHelp();
        };
    }

    public String gameEval(String[] params, String cmd) throws RuntimeException{
        return switch (cmd) {
            case "help" -> gameHelp();
            case "quit" -> quit();
            case "exit" -> exit();
            default -> printBoard();
        };
    }

    public String preHelp() {
        return """
                register <username> <password> <email> - to register
                login <username> <password> - to login
                quit - stop client
                help - all commands""";}

    public String postHelp() {
        return """
                create <GameName> - to create a game
                list - list all games
                join <ID> <WHITE|BLACK> - Join game as a color
                observe <ID> - observe game
                logout - logout of server
                quit - stop client
                help - all commands""";}

    public String gameHelp() {
        return """
                future work
                quit - stop client
                help - all commands""";}

    public String register(String... params) throws RuntimeException {
        if (params.length < 3) {
            throw new RuntimeException("Expected: <username> <password> <email>");
        }
        var username = params[0];
        var password = params[1];
        var email = params[2];
        var user = server.register(new UserData(username, password, email));
        state = State.PostloginClient;
        authToken = user.authToken();
        return postHelp();
    }

    public String login(String... params) throws RuntimeException {
        if (params.length < 2) {
            throw new RuntimeException("Expected: <username> <password>");
        }
        var username = params[0];
        var password = params[1];
        var user = server.login(new UserData(username, password, null));
        state = State.PostloginClient;
        authToken = user.authToken();
        return postHelp();
    }

    public String create(String... params) throws RuntimeException {
        if (params.length < 1) {
            throw new RuntimeException("Expected: <username> <password>");
        }
        var gameName = params[0];
        server.create(new CreateGameRequest(gameName, authToken));
        return "Created game: " + gameName;
    }

    public String list() throws RuntimeException {
        var result = server.list(authToken);
        gameList = (ArrayList<GameData>) result;
        StringBuilder games = new StringBuilder();
        int i = 1;
        for (var game : result) {
            games.append("Game ");
            games.append(i).append(":\n");
            games.append(game.toString());
            games.append("\n");
            i++;
        }
        return games.toString();
    }

    public String logout() throws RuntimeException {
        server.logout(new LogoutRequest(authToken));
        state = State.PreloginClient;
        return "Logged out";
    }

    public String quit() throws RuntimeException {
        server.logout(new LogoutRequest(authToken));
        state = State.PreloginClient;
        return "quit";
    }

    public String join(String... params) throws RuntimeException {
        if (params.length < 2) {
            throw new RuntimeException("Expected: <ID> <COLOR>");
        }
        int gameID;
        try {
            gameID = Integer.parseInt(params[0]);
        } catch (Exception e) {
            throw new RuntimeException("GameID not a number");
        }
        if (gameID < 1 || gameID > gameList.size()) {
            throw new RuntimeException("GameID not valid");
        }
        var color = params[1].toUpperCase();
        int realGameId = gameList.get(gameID - 1).gameID();
        server.join(new JoinGameRequest(color, realGameId, authToken));
        state = State.GamePlayClient;
        if (color.equals("WHITE")) {
            playersColor = ChessGame.TeamColor.WHITE;
        } else {
            playersColor = ChessGame.TeamColor.BLACK;
        }
        return printBoard();
    }

    public String observe(String... params) throws RuntimeException {
        if (params.length < 1) {
            throw new RuntimeException("Expected: <ID>");
        }
        int gameID;
        try {
            gameID = Integer.parseInt(params[0]);
        } catch (Exception e) {
            throw new RuntimeException("GameID not a number");
        }
        if (gameID < 1 || gameID > gameList.size()) {
            throw new RuntimeException("GameID not valid");
        }
        GameData obsGame = gameList.get(gameID - 1);

        state = State.GamePlayClient;
        playersColor = ChessGame.TeamColor.WHITE;
        return printBoard();
    }

    public String printPrompt() {
        return switch (state) {
            case PreloginClient -> "Logged Out";
            case PostloginClient -> "Logged In";
            case GamePlayClient -> "GamePlay";
        };
    }

    public String printBoard() {
        if (playersColor.equals(ChessGame.TeamColor.WHITE)) {
            PrintBoard.printBoard(ChessGame.TeamColor.WHITE);
        } else {
            PrintBoard.printBoard(ChessGame.TeamColor.BLACK);
        }
        return "";
    }

    public String exit() {
        state = State.PostloginClient;
        return "";
    }

}

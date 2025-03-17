package client;

import com.sun.nio.sctp.NotificationHandler;
import model.GameData;
import service.requests.*;
import model.UserData;
import server.ServerFacade;
import dataaccess.DataAccessException;

import java.util.Arrays;
import java.util.Collection;

public class ChessClient {
    private final NotificationHandler notificationHandler;
    private String authToken = null;
    private Collection<GameData> gameList = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.PreloginClient;

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
        } catch (DataAccessException ex) {
            return ex.getMessage();
        }
    }

    public String preEval(String[] params, String cmd) throws DataAccessException{
        return switch (cmd) {
            case "register" -> register(params);
            case "login" -> login(params);
            case "quit" -> "quit";
            default -> preHelp();
        };
    }

    public String postEval(String[] params, String cmd) throws DataAccessException{
        return switch (cmd) {
            case "create" -> create(params);
            case "list" -> list();
            case "join" -> join(params);
            case "observe" -> observe(params);
            case "quit" -> "quit";
            default -> postHelp();
        };
    }

    public String gameEval(String[] params, String cmd) throws DataAccessException{
        return switch (cmd) {
//            case "register" -> register(params);
            case "quit" -> "quit";
            default -> gameHelp();
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

    public String register(String... params) throws DataAccessException {
        if (params.length < 3) {
            throw new DataAccessException(400, "Expected: <username> <password> <email>");
        }
        var username = params[0];
        var password = params[1];
        var email = params[2];
        var user = server.addUser(new UserData(username, password, email));
        state = State.PostloginClient;
        authToken = user.authToken();
        return "Registered:" + user.username();
    }

    public String login(String... params) throws DataAccessException {
        if (params.length < 2) {
            throw new DataAccessException(400, "Expected: <username> <password>");
        }
        var username = params[0];
        var password = params[1];
        var user = server.login(new UserData(username, password, null));
        state = State.PostloginClient;
        authToken = user.authToken();
        return "Logged in: " + user.username();
    }

    public String create(String... params) throws DataAccessException {
        if (params.length < 1) {
            throw new DataAccessException(400, "Expected: <username> <password>");
        }
        var gameName = params[0];
        var gameResult = server.create(new CreateGameRequest(gameName, authToken));
        return "Created game: " + gameResult.gameID();
    }

    public String list() throws DataAccessException {
        var result = server.list(authToken);
        gameList = result;
        return "Games: " + result;
    }

    public String join(String... params) throws DataAccessException {
        if (params.length < 2) {
            throw new DataAccessException(400, "Expected: <ID> <COLOR>");
        }
        int gameID;
        try {
            gameID = Integer.parseInt(params[0]);
        } catch (Exception e) {
            throw new DataAccessException(400, "GameID not a number");
        }
        var color = params[1].toUpperCase();
        server.join(new JoinGameRequest(color, gameID, authToken));
        state = State.GamePlayClient;
        return "Joined game: " + gameID;
    }

    public String observe(String... params) throws DataAccessException {
        if (params.length < 1) {
            throw new DataAccessException(400, "Expected: <ID>");
        }
        int gameID;
        try {
            gameID = Integer.parseInt(params[0]);
        } catch (Exception e) {
            throw new DataAccessException(400, "GameID not a number");
        }
        GameData obs_game = null;
        for (var game : gameList) {
            if (game.gameID() == gameID) {
                obs_game = game;
            }
        }
        if (obs_game == null) {
            return "No game with that ID";
        }
        state = State.GamePlayClient;
        return obs_game.toString();
    }

    public String printPrompt() {
        return switch (state) {
            case PreloginClient -> "Logged Out";
            case PostloginClient -> "Logged In";
            case GamePlayClient -> "GamePlay";
        };
    }

}

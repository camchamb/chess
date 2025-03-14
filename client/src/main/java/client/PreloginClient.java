package client;

import com.sun.nio.sctp.NotificationHandler;
import model.UserData;
import server.ServerFacade;
import dataaccess.DataAccessException;

import java.util.Arrays;

public class PreloginClient {
    private final NotificationHandler notificationHandler;
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.PreloginClient;

    public PreloginClient(String serverUrl, NotificationHandler notificationHandler) {
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
//                case "rescue" -> rescuePet(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
            case "quit" -> "quit";
            default -> preHelp();
        };
    }

    public String postEval(String[] params, String cmd) throws DataAccessException{
        return switch (cmd) {
            case "register" -> register(params);
//                case "rescue" -> rescuePet(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
            case "quit" -> "quit";
            default -> postHelp();
        };
    }

    public String gameEval(String[] params, String cmd) throws DataAccessException{
        return switch (cmd) {
            case "register" -> register(params);
//                case "rescue" -> rescuePet(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
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
        return "Registered:" + user.username();
    }
}

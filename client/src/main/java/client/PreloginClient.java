package client;

import com.sun.nio.sctp.NotificationHandler;
import model.UserData;
import server.ServerFacade;
import dataaccess.DataAccessException;

import java.util.Arrays;

public class PreloginClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
//    private State state = State.SIGNEDOUT;

    public PreloginClient(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
//        this.notificationHandler = notificationHandler;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
//                case "rescue" -> rescuePet(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (DataAccessException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return """
                register <username> <password> <email> - to register
                login <username> <password> - to login
                quit - stop client
                help - all commands
                """;}

    public String register(String... params) throws DataAccessException {
        if (params.length < 3) {
            throw new DataAccessException(400, "Expected: <username> <password> <email>");
        }
        var username = params[0];
        var password = params[1];
        var email = params[2];
        var user = server.addUser(new UserData(username, password, email));
        return "Registered:" + user.username();
    }
}

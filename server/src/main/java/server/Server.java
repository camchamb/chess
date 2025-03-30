package server;

import com.google.gson.Gson;
import dataaccess.*;
import server.websocket.WebSocketHandler;
import service.GameService;
import service.requests.*;
import service.UserService;
import spark.*;

public class Server {
    UserDAO userAccess;
    GameDAO gameAccess;
    AuthDAO authAccess;

    public int run(int desiredPort) {
        try {
            userAccess = new UserSqlAccess();
            gameAccess = new GameSqlAccess();
            authAccess = new AuthSqlAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        UserService userService = new UserService(userAccess, authAccess);
        GameService gameService = new GameService(gameAccess, authAccess);

        WebSocketHandler webSocketHandler = new WebSocketHandler(userAccess, gameAccess, authAccess);

        Spark.webSocket("/ws", webSocketHandler);

        var serializer = new Gson();

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        endpoints(serializer, userService, gameService);


        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void endpoints(Gson serializer, UserService userService, GameService gameService) {
        Spark.post("/user", (request, response) -> {
            try {
                var regReq = serializer.fromJson(request.body(), RegisterRequest.class);
                RegisterResult regRes = userService.register(regReq);
                response.body(serializer.toJson(regRes));
                System.out.println(serializer.toJson(regRes));
                return response.body();
            }
            catch (DataAccessException ex) {
                errorHandling(ex, request, response);
                return response.body();
            }
        });

        Spark.post("/session", (request, response) -> {
            try {
                var loginRequest = serializer.fromJson(request.body(), LoginRequest.class);
                LoginResult loginResult = userService.login(loginRequest);
                response.body(serializer.toJson(loginResult));
                System.out.println(serializer.toJson(loginResult));
                return response.body();
            }
            catch (DataAccessException ex) {
                errorHandling(ex, request, response);
                return response.body();
            }
        });

        Spark.delete("/session", (request, response) -> {
            try {
                String authToken = request.headers("authorization");
                userService.logout(new LogoutRequest(authToken));
                response.body("{ }");
                return response.body();
            }
            catch (DataAccessException ex) {
                errorHandling(ex, request, response);
                return response.body();
            }
        });

        Spark.get("/game", (request, response) -> {
            try {
                String authToken = request.headers("authorization");
                var listGamesRequest = new ListGamesRequest(authToken);
                ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);
                response.body(serializer.toJson(listGamesResult));
                System.out.println(serializer.toJson(listGamesResult));
                return response.body();
            }
            catch (DataAccessException ex) {
                errorHandling(ex, request, response);
                return response.body();
            }
        });

        Spark.post("/game", (request, response) -> {
            try {
                var tempRequest = serializer.fromJson(request.body(), CreateGameRequest.class);
                var createGameRequest = new CreateGameRequest(tempRequest.gameName(), request.headers("authorization"));
                CreateGameResult createGameResult = gameService.createGame(createGameRequest);
                response.body(serializer.toJson(createGameResult));
                System.out.println(serializer.toJson(createGameResult));
                return response.body();
            }
            catch (DataAccessException ex) {
                errorHandling(ex, request, response);
                return response.body();
            }
        });

        Spark.put("/game", (request, response) -> {
            try {
                var tempRequest = serializer.fromJson(request.body(), JoinGameRequest.class);
                var joinGameRequest = new JoinGameRequest(tempRequest.playerColor(),
                        tempRequest.gameID(), request.headers("authorization"));
                gameService.joinGame(joinGameRequest);
                response.body("{ }");
                return response.body();
            }
            catch (DataAccessException ex) {
                errorHandling(ex, request, response);
                return response.body();
            }
        });

        Spark.delete("/db", (request, response) -> {
            try {
                userService.clear();
                gameService.clear();
                response.body("{ }");
                return response.body();
            }
            catch (DataAccessException ex) {
                errorHandling(ex, request, response);
                return response.body();
            }
        });
    }

    public void errorHandling(DataAccessException ex, Request req, Response res) {
        res.status(ex.getStatus());
        String message = ex.getMessage();
        String json = "{\"message\": \"" + message + "\" }";
        System.out.println(json);
        res.body(json);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
package server;

import com.google.gson.Gson;
import dataAccess.*;
import service.GameService;
import service.Requests.*;
import service.UserService;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        UserDAO userAccess = new UserMemoryAccess();
        GameDAO gameAccess = new GameMemoryAccess();
        AuthDAO authAccess = new AuthMemoryAccess();

        UserService userService = new UserService(userAccess, authAccess);
        GameService gameService = new GameService(gameAccess);

        var serializer = new Gson();

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

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


        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
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
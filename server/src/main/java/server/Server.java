package server;

import com.google.gson.Gson;
import dataAccess.*;
import service.Requests.*;
import service.UserService;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        UserDAO userAccess = new UserMemoryAccess();
        GameDAO gameAccess = null;
        AuthDAO authAccess = null;

        UserService userService = new UserService(userAccess, authAccess);

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        // Show something
        Spark.get("/", (request, response) -> {
            // Show something
        });

        Spark.post("/user", (request, response) -> {
            try {
                var serializer = new Gson();
                String json = request.body();
                var regReq = serializer.fromJson(json, RegisterRequest.class);
                RegisterResult regRes = userService.register(regReq);
                response.body(serializer.toJson(regRes));
            }
            catch (DataAccessException ex) {
                errorHandling(ex, request, response);
            }
        });

        Spark.put("/", (request, response) -> {
            // Update something
        });

        Spark.delete("/", (request, response) -> {
            // Delete something
        });


        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void errorHandling(DataAccessException ex, Request req, Response res) {
        res.status(ex.getStatus());
        String message = ex.getMessage();
        String error = ex.getError();
        String json = "{message:" + message + "Error:" + error + "}";
        res.body(json);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
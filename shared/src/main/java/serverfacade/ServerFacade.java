package serverfacade;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.*;
import service.requests.*;

import java.io.*;
import java.net.*;
import java.util.Collection;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(UserData u) throws RuntimeException {
        var path = "/user";
        return makeRequest("POST", path, u, RegisterResult.class, null);
    }

    public LoginResult login(UserData u) throws RuntimeException {
        var path = "/session";
        return makeRequest("POST", path, u, LoginResult.class, null);
    }

    public CreateGameResult create(CreateGameRequest req) throws RuntimeException {
        var path = "/game";
        return makeRequest("POST", path, req, CreateGameResult.class, req.authToken());
    }

    public Collection<GameData> list(String authToken) throws RuntimeException {
        var path = "/game";
        return makeRequest("GET", path, null, ListGamesResult.class, authToken).games();
    }

    public void join(JoinGameRequest req) throws RuntimeException {
        var path = "/game";
        makeRequest("PUT", path, req, null, req.authToken());
    }

    public void logout(LogoutRequest req) throws RuntimeException {
        var path = "/session";
        makeRequest("DELETE", path, req, null, req.authToken());
    }

    public void clear() throws RuntimeException {
        var path = "/db";
        var req = new LogoutRequest(null);
        makeRequest("DELETE", path, req, null, null);
    }

    public <T> T makeRequest(String method, String path, Object request, Class<T> objectClass, String authToken) throws RuntimeException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http, authToken);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, objectClass);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private static void writeBody(Object request, HttpURLConnection http, String authToken) throws IOException {
        http.setRequestProperty ("authorization", authToken);
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, RuntimeException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    try {
                        throw DataAccessException.fromJson(respErr);
                    } catch (DataAccessException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            }

            throw new RuntimeException("other failure: " + status);
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}

package server;

import com.google.gson.Gson;
import model.*;
import service.requests.*;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public UserData addUser(UserData u) {
        var path = "/user";
        return makeRequest("POST", path, u, UserData.class);
    }

    public LoginResult login(UserData u) {
        var path = "/session";
        return makeRequest("POST", path, u, LoginResult.class);
    }

    public <T> T makeRequest(String method, String path, Object request, Class<T> objectClass) {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
//            throwIfNotSuccessful(http);
            return readBody(http, objectClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }
}

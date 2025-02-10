package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import dataAccess.UserMemoryAccess;
import model.AuthData;
import model.UserData;
import java.util.UUID;

record RegisterRequest (String username, String password, String email) {}

record RegisterResult (String username, String authToken, String message) {}

record LoginRequest (String username, String password) {}

record LoginResult (String username, String authToken, String message) {}

record LogoutRequest (String authToken) {}

record LogoutResult (String message) {}

public class UserService {

    private final UserDAO userAccess;
    private final AuthDAO authAccess;

    public UserService(UserDAO userAccess, AuthDAO authAccess) {
        this.userAccess = userAccess;
        this.authAccess = authAccess;
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        UserData userData = userAccess.getUser(registerRequest.username());
        if (userData != null) {
            return new RegisterResult(null, null, "Username Taken");
        }
        userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userAccess.createUser(userData);
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, registerRequest.username());
        authAccess.createAuth(authData);
        return new RegisterResult(registerRequest.username(), authToken, "Success");
    }

    public LoginResult login(LoginRequest loginRequest) {}
    public void logout(LogoutRequest logoutRequest) {}
}

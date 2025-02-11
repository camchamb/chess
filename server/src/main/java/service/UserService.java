package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;
import service.Requests.*;

import java.util.UUID;

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
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new DataAccessException(400, "Error: bad request");
        }
        UserData userData = userAccess.getUser(registerRequest.username());
        if (userData != null) {
            throw new DataAccessException(403, "Error: already taken");
        }
        userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userAccess.createUser(userData);
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, registerRequest.username());
        authAccess.createAuth(authData);
        return new RegisterResult(registerRequest.username(), authToken);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        if (loginRequest.password() == null || loginRequest.username() == null) {
            throw new DataAccessException(500, "Error: invalid request");
        }
        UserData userData = userAccess.getUser(loginRequest.username());
        if (userData == null) {
            throw new DataAccessException(401, "Error: invalid username");
        }
        if (!userData.password().equals(loginRequest.password())) {
            throw new DataAccessException(401, "Error: unauthorized");
        }
        String authToken = generateToken();
        var authdata = new AuthData(authToken, loginRequest.username());
        authAccess.createAuth(authdata);
        return new LoginResult(loginRequest.username(), authToken);
    }

    public void logout(LogoutRequest logoutRequest) {}

    public void clear() throws DataAccessException{
        userAccess.clear();
        authAccess.clear();
    }
}

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
        UserData userData = userAccess.getUser(registerRequest.username());
        if (userData != null) {
            throw new DataAccessException("403", "Username Taken", "already taken");
        }
        userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userAccess.createUser(userData);
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, registerRequest.username());
        authAccess.createAuth(authData);
        return new RegisterResult(registerRequest.username(), authToken);
    }

    public LoginResult login(LoginRequest loginRequest) {return null;}
    public void logout(LogoutRequest logoutRequest) {}
}

package service;

import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.*;
import service.requests.*;

public class ServiceTests {
    private static final UserDAO UserAccess = new UserMemoryAccess();
    private static final GameDAO GameAccess = new GameMemoryAccess();
    private static final AuthDAO AuthAccess = new AuthMemoryAccess();

    private static final UserService userService = new UserService(UserAccess, AuthAccess);
    private static final GameService gameService = new GameService(GameAccess, AuthAccess);

    @Test
    @Order(1)
    @DisplayName("user register")
    public void userRegister() throws DataAccessException {
        userService.clear();
        gameService.clear();
        var registerRequest = new RegisterRequest("username", "password", "email.com");
        var registerResult = new RegisterResult("username", "123456");
        Assertions.assertEquals(registerResult.username(), userService.register(registerRequest).username(),
                "Not right username");
    }

    @Test
    @Order(2)
    @DisplayName("invalid user register")
    public void invalidUserRegister() {
        var invalidRequest = new RegisterRequest("ya", "no", null);
        Assertions.assertThrows(DataAccessException.class, () -> userService.register(invalidRequest));
    }

    @Test
    @Order(3)
    @DisplayName("user login")
    public void userLogin() throws DataAccessException {
        userService.clear();
        userRegister();
        var loginResult = new LoginResult("username", "123456");
        Assertions.assertEquals(loginResult.username(),
                userService.login(new LoginRequest("username", "password")).username(),
                "Not right username");
    }

    @Test
    @Order(4)
    @DisplayName("invalid user login")
    public void invalidUserLogin() throws DataAccessException {
        userService.clear();
        userRegister();
        var invalidRequest = new RegisterRequest("ya", "no", null);
        Assertions.assertThrows(DataAccessException.class, () -> userService.register(invalidRequest));
    }

    @Test
    @Order(5)
    @DisplayName("user logout")
    public void userLogout() throws DataAccessException {
        userService.clear();
        userRegister();
        var loginResult = userService.login(new LoginRequest("username", "password"));
        var logoutRequest = new LogoutRequest(loginResult.authToken());
        userService.logout(logoutRequest);
    }

    @Test
    @Order(6)
    @DisplayName("invalid user logout")
    public void invalidUserLogout() throws DataAccessException {
        userService.clear();
        userRegister();
        userService.login(new LoginRequest("username", "password"));
        var invalidLogoutRequest = new LogoutRequest("1234");
        Assertions.assertThrows(DataAccessException.class, () ->  userService.logout(invalidLogoutRequest));
    }

    @Test
    @Order(7)
    @DisplayName("Create game")
    public void gameCreate() throws DataAccessException {
        userService.clear();
        gameService.clear();
        userRegister();
        var loginResult = userService.login(new LoginRequest("username", "password"));
        String authToken = loginResult.authToken();
        var createGameRequest = new CreateGameRequest("gamename", authToken);
        gameService.createGame(createGameRequest);
    }

    @Test
    @Order(8)
    @DisplayName("invalid game create")
    public void invalidGameCreate() throws DataAccessException {
        clear();
    }

    @Test
    @Order(9)
    @DisplayName("List games")
    public void gameList() throws DataAccessException {
        userService.clear();
        gameService.clear();
        gameCreate();
        var loginResult = userService.login(new LoginRequest("username", "password"));
        String authToken = loginResult.authToken();
        var createGameRequest = new CreateGameRequest("gamename", authToken);
        gameService.createGame(createGameRequest);
    }

    @Test
    @Order(10)
    @DisplayName("invalid Listgames")
    public void invalidGameList() throws DataAccessException {
        clear();
    }

    private void clear() throws DataAccessException {
        userService.clear();
        gameService.clear();
        userRegister();
        var loginResult = userService.login(new LoginRequest("username", "password"));
        loginResult.authToken();
        var createGameRequest = new CreateGameRequest("gamename", "wrong");
        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(createGameRequest));
    }

    @Test
    @Order(11)
    @DisplayName("join game")
    public void joinGame() throws DataAccessException {
        userService.clear();
        gameService.clear();
        userRegister();
        var loginResult = userService.login(new LoginRequest("username", "password"));
        String authToken = loginResult.authToken();
        var createGameRequest = new CreateGameRequest("gamename", authToken);
        var createGameResult = gameService.createGame(createGameRequest);
        var joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID(), authToken);
        gameService.joinGame(joinGameRequest);
        var listGamesResult = gameService.listGames(new ListGamesRequest(authToken));
        var testObject = new GameData(createGameResult.gameID(), "username", null, "gamename", null);
        assert listGamesResult.games().contains(testObject);
    }

    @Test
    @Order(12)
    @DisplayName("invalid join game")
    public void invalidJoinGame() throws DataAccessException {
        gameService.clear();
        userRegister();
        var loginResult = userService.login(new LoginRequest("username", "password"));
        String authToken = loginResult.authToken();
        var createGameRequest = new CreateGameRequest("gamename", authToken);
        var createGameResult = gameService.createGame(createGameRequest);
        var badJoinGameRequest = new JoinGameRequest("GREEN", createGameResult.gameID(), authToken);
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(badJoinGameRequest));
    }

    @Test
    @Order(12)
    @DisplayName("Check clear")
    public void clearAll() throws DataAccessException {
        userRegister();
        userService.clear();
        var loginRequest = new LoginRequest("username", "password");
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(loginRequest));
    }

}

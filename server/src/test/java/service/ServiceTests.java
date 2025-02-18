package service;

import dataAccess.*;
import org.junit.jupiter.api.*;
import passoff.model.TestCreateRequest;
import passoff.model.TestUser;
import passoff.server.TestServerFacade;
import server.Server;
import service.Requests.*;

public class ServiceTests {
    private static TestUser existingUser;

    private static TestUser newUser;

    private static TestCreateRequest createRequest;

    private static TestServerFacade serverFacade;
    private static Server server;

    private String existingAuth;
    private static final UserDAO userAccess = new UserMemoryAccess();
    private static final GameDAO gameAccess = new GameMemoryAccess();
    private static final AuthDAO authAccess = new AuthMemoryAccess();

    private static final UserService userService = new UserService(userAccess, authAccess);
    private static final GameService gameService = new GameService(gameAccess, authAccess);

//    @BeforeAll
//    public static void init() {
//
//        existingUser = userService.register(new RegisterRequest("username", "password", "email.com"));
//
//        userRegister();
//        newUser = new TestUser("NewUser", "newUserPassword", "nu@mail.com");
//
//        createRequest = new TestCreateRequest("testGame");
//    }

    @Test
    @Order(1)
    @DisplayName("user register")
    public void userRegister() throws DataAccessException {
        var registerRequest = new RegisterRequest("username", "password", "email.com");
        var registerResult = new RegisterResult("username", "123456");
        Assertions.assertEquals(registerResult.username(), userService.register(registerRequest).username(),
                "Not right username");
    }

    @Test
    @Order(2)
    @DisplayName("invalid user register")
    public void invalidUserRegister() throws DataAccessException {
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
        var loginResult = userService.login(new LoginRequest("username", "password"));
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
        userService.clear();
        gameService.clear();
        userRegister();
        var loginResult = userService.login(new LoginRequest("username", "password"));
        String authToken = loginResult.authToken();
        var createGameRequest = new CreateGameRequest("gamename", "wrong");
        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(createGameRequest));
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
    @DisplayName("invalid game create")
    public void invalidGameList() throws DataAccessException {
        userService.clear();
        gameService.clear();
        userRegister();
        var loginResult = userService.login(new LoginRequest("username", "password"));
        String authToken = loginResult.authToken();
        var createGameRequest = new CreateGameRequest("gamename", "wrong");
        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(createGameRequest));
    }

}

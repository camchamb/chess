package dataaccess;

import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import passoff.server.TestServerFacade;
import server.Server;
import service.GameService;
import service.UserService;
import service.requests.*;

public class DataAccessTests {
    private static final UserDAO USER_ACCESS;
    private static final GameDAO GAME_ACCESS;
    private static final AuthDAO AUTH_ACCESS;

    static {
        try {
            USER_ACCESS = new UserSqlAccess();
            GAME_ACCESS = new GameSqlAccess();
            AUTH_ACCESS = new AuthSqlAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @BeforeEach
    public void setUp() throws DataAccessException {
        USER_ACCESS.clear();
        GAME_ACCESS.clear();
        AUTH_ACCESS.clear();
    }

    @AfterAll
    public static void takeDown() throws DataAccessException {
        USER_ACCESS.clear();
        GAME_ACCESS.clear();
        AUTH_ACCESS.clear();
    }

    @Test
    @Order(0)
    @DisplayName("user clear")
    public void userClear() throws DataAccessException {
        USER_ACCESS.clear();
    }

    @Test
    @Order(0)
    @DisplayName("game clear")
    public void gameClear() throws DataAccessException {
        GAME_ACCESS.clear();
    }

    @Test
    @Order(0)
    @DisplayName("game clear")
    public void authClear() throws DataAccessException {
        AUTH_ACCESS.clear();
    }



    @Test
    @Order(1)
    @DisplayName("create user")
    public void userRegister() throws DataAccessException {
        var user = new UserData("username", "password", "email.com");
        USER_ACCESS.createUser(user);
        Assertions.assertEquals(user.username(), USER_ACCESS.getUser("username").username(),
                "Not right username");
    }

    @Test
    @Order(2)
    @DisplayName("invalid create user")
    public void invalidUserRegister() throws DataAccessException {
        var user = new UserData("username", "password", "email.com");
        USER_ACCESS.createUser(user);
        var invalidRequest = new UserData("username", "no", "fake");
        Assertions.assertThrows(DataAccessException.class, () -> USER_ACCESS.createUser(invalidRequest));
    }

    @Test
    @Order(3)
    @DisplayName("get user")
    public void userLogin() throws DataAccessException {
        var user = new UserData("username", "password", "email.com");
        USER_ACCESS.createUser(user);
        Assertions.assertEquals(user.username(), USER_ACCESS.getUser("username").username(),
                "Not right username");
    }

    @Test
    @Order(4)
    @DisplayName("invalid user login")
    public void invalidUserLogin() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> USER_ACCESS.getUser("invalidRequest"));
    }

    @Test
    @Order(5)
    @DisplayName("user logout")
    public void userLogout() throws DataAccessException {
        USER_ACCESS.clear();
        userRegister();
        var loginResult = USER_ACCESS.login(new LoginRequest("username", "password"));
        var logoutRequest = new LogoutRequest(loginResult.authToken());
        USER_ACCESS.logout(logoutRequest);
    }

    @Test
    @Order(6)
    @DisplayName("invalid user logout")
    public void invalidUserLogout() throws DataAccessException {
        USER_ACCESS.clear();
        userRegister();
        USER_ACCESS.login(new LoginRequest("username", "password"));
        var invalidLogoutRequest = new LogoutRequest("1234");
        Assertions.assertThrows(DataAccessException.class, () ->  USER_ACCESS.logout(invalidLogoutRequest));
    }
//
//    @Test
//    @Order(7)
//    @DisplayName("Create game")
//    public void gameCreate() throws DataAccessException {
//        USER_ACCESS.clear();
//        GAME_SERVICE.clear();
//        userRegister();
//        var loginResult = USER_ACCESS.login(new LoginRequest("username", "password"));
//        String authToken = loginResult.authToken();
//        var createGameRequest = new CreateGameRequest("gamename", authToken);
//        GAME_SERVICE.createGame(createGameRequest);
//    }
//
//    @Test
//    @Order(8)
//    @DisplayName("invalid game create")
//    public void invalidGameCreate() throws DataAccessException {
//        clear();
//    }
//
//    @Test
//    @Order(9)
//    @DisplayName("List games")
//    public void gameList() throws DataAccessException {
//        USER_ACCESS.clear();
//        GAME_SERVICE.clear();
//        gameCreate();
//        var loginResult = USER_ACCESS.login(new LoginRequest("username", "password"));
//        String authToken = loginResult.authToken();
//        var createGameRequest = new CreateGameRequest("gamename", authToken);
//        GAME_SERVICE.createGame(createGameRequest);
//    }
//
//    @Test
//    @Order(10)
//    @DisplayName("invalid Listgames")
//    public void invalidGameList() throws DataAccessException {
//        clear();
//    }
//
//    private void clear() throws DataAccessException {
//        USER_ACCESS.clear();
//        GAME_SERVICE.clear();
//        userRegister();
//        var loginResult = USER_ACCESS.login(new LoginRequest("username", "password"));
//        loginResult.authToken();
//        var createGameRequest = new CreateGameRequest("gamename", "wrong");
//        Assertions.assertThrows(DataAccessException.class, () -> GAME_SERVICE.createGame(createGameRequest));
//    }
//
//    @Test
//    @Order(11)
//    @DisplayName("join game")
//    public void joinGame() throws DataAccessException {
//        USER_ACCESS.clear();
//        GAME_SERVICE.clear();
//        userRegister();
//        var loginResult = USER_ACCESS.login(new LoginRequest("username", "password"));
//        String authToken = loginResult.authToken();
//        var createGameRequest = new CreateGameRequest("gamename", authToken);
//        var createGameResult = GAME_SERVICE.createGame(createGameRequest);
//        var joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID(), authToken);
//        GAME_SERVICE.joinGame(joinGameRequest);
//        var listGamesResult = GAME_SERVICE.listGames(new ListGamesRequest(authToken));
//        var testObject = new GameData(createGameResult.gameID(), "username", null, "gamename", null);
//        assert listGamesResult.games().contains(testObject);
//    }
//
//    @Test
//    @Order(12)
//    @DisplayName("invalid join game")
//    public void invalidJoinGame() throws DataAccessException {
//        GAME_SERVICE.clear();
//        userRegister();
//        var loginResult = USER_ACCESS.login(new LoginRequest("username", "password"));
//        String authToken = loginResult.authToken();
//        var createGameRequest = new CreateGameRequest("gamename", authToken);
//        var createGameResult = GAME_SERVICE.createGame(createGameRequest);
//        var badJoinGameRequest = new JoinGameRequest("GREEN", createGameResult.gameID(), authToken);
//        Assertions.assertThrows(DataAccessException.class, () -> GAME_SERVICE.joinGame(badJoinGameRequest));
//    }
//
//    @Test
//    @Order(12)
//    @DisplayName("Check clear")
//    public void clearAll() throws DataAccessException {
//        userRegister();
//        USER_ACCESS.clear();
//        var loginRequest = new LoginRequest("username", "password");
//        Assertions.assertThrows(DataAccessException.class, () -> USER_ACCESS.login(loginRequest));
//    }
//
}

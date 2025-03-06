package dataaccess;

import model.AuthData;
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
    @DisplayName("invalid get user")
    public void invalidUserLogin() throws DataAccessException {
        Assertions.assertEquals(null, USER_ACCESS.getUser("invalidRequest"));
    }

    @Test
    @Order(5)
    @DisplayName("create Auth")
    public void userLogout() throws DataAccessException {
        var user = new UserData("username", "password", "email.com");
        USER_ACCESS.createUser(user);
        var auth = new AuthData("55-24-AJ", "username");
        AUTH_ACCESS.createAuth(auth);
    }

    @Test
    @Order(6)
    @DisplayName("invalid create Auth")
    public void invalidUserLogout() throws DataAccessException {
        var auth = new AuthData("55-24-AJ", "username");
        Assertions.assertThrows(DataAccessException.class, () -> AUTH_ACCESS.createAuth(auth));
    }

    @Test
    @Order(7)
    @DisplayName("Get Auth")
    public void gameCreate() throws DataAccessException {
        var user = new UserData("username", "password", "email.com");
        USER_ACCESS.createUser(user);
        var auth = new AuthData("55-24-AJ", "username");
        AUTH_ACCESS.createAuth(auth);
        Assertions.assertEquals(user.username(), AUTH_ACCESS.getAuth("55-24-AJ").username(),
                "Not right username");
    }

    @Test
    @Order(8)
    @DisplayName("invalid get Auth")
    public void invalidGameCreate() throws DataAccessException {
        Assertions.assertEquals(null, AUTH_ACCESS.getAuth("invalidRequest"));
    }

    @Test
    @Order(9)
    @DisplayName("Delete Auth")
    public void gameList() throws DataAccessException {
        var user = new UserData("username", "password", "email.com");
        USER_ACCESS.createUser(user);
        var auth = new AuthData("55-24-AJ", "username");
        AUTH_ACCESS.createAuth(auth);
        Assertions.assertEquals(user.username(), AUTH_ACCESS.getAuth("55-24-AJ").username(),
                "Not right username");
        AUTH_ACCESS.deleteAuth(auth.authToken());
        Assertions.assertNull(AUTH_ACCESS.getAuth("55-24-AJ"), "Not right username");
    }

    @Test
    @Order(10)
    @DisplayName("invalid delete Auth")
    public void invalidGameList() throws DataAccessException {
        AUTH_ACCESS.deleteAuth("Invalid");
        Assertions.assertNull(AUTH_ACCESS.getAuth("55-24-AJ"), "Not null");
    }


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

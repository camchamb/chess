package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import passoff.server.TestServerFacade;
import server.Server;
import service.GameService;
import service.UserService;
import service.requests.*;

import java.util.ArrayList;
import java.util.Collection;

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
        var user = new UserData("test", "password", "email.com");
        USER_ACCESS.createUser(user);
        Assertions.assertEquals(user.username(), USER_ACCESS.getUser("test").username(),
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


    @Test
    @Order(11)
    @DisplayName("create game")
    public void joinGame() throws DataAccessException {
        GAME_ACCESS.createGame("gamename");
    }

    @Test
    @Order(12)
    @DisplayName("invalid create game")
    public void invalidJoinGame() throws DataAccessException {
        var gameid = GAME_ACCESS.createGame("gamename");
        var gameid2 = GAME_ACCESS.createGame("gamename");
        assert gameid != gameid2;
    }

    @Test
    @Order(12)
    @DisplayName("get Game")
    public void clearAll() throws DataAccessException {
        var gameid = GAME_ACCESS.createGame("gamename");
        Assertions.assertEquals("gamename", GAME_ACCESS.getGame(gameid).gameName());
    }

    @Test
    @Order(13)
    @DisplayName("invalid get game")
    public void igetgame() throws DataAccessException {
        Assertions.assertNull(GAME_ACCESS.getGame(1234));
    }

    @Test
    @Order(14)
    @DisplayName("list Games")
    public void listGames() throws DataAccessException {
        GAME_ACCESS.createGame("gamename");
        var list = GAME_ACCESS.listGames();
        for (var game : list) {
            Assertions.assertEquals("gamename", game.gameName());
        }
    }

    @Test
    @Order(15)
    @DisplayName("invalid list game")
    public void ilistgame() throws DataAccessException {
        Assertions.assertEquals(new ArrayList<>(), GAME_ACCESS.listGames());
    }

    @Test
    @Order(16)
    @DisplayName("update Games")
    public void updateGames() throws DataAccessException {
        var gameId = GAME_ACCESS.createGame("gamename");
        var game = new GameData(gameId, null, null, "newGameName", new ChessGame());
        GAME_ACCESS.updateGame(game);
        Assertions.assertEquals("newGameName", GAME_ACCESS.getGame(gameId).gameName());
    }

    @Test
    @Order(17)
    @DisplayName("invalid update game")
    public void iupdategame() throws DataAccessException {
        var game = new GameData(1234, null, null, "newGameName", new ChessGame());
        GAME_ACCESS.updateGame(game);
        Assertions.assertEquals(new ArrayList<>(), GAME_ACCESS.listGames());
    }

}

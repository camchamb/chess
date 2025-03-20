package client;

import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;
import requests.*;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private String authToken;
    private Integer gameID;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void clear() throws RuntimeException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void register() throws Exception {
        var u = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(u);
        assertTrue(authData.authToken().length() > 10);
        authToken = authData.authToken();
    }

    @Test
    void registerFalse() throws Exception {
        register();
        var u = new UserData("player1", "password", "p1@email.com");
        Assertions.assertThrows(RuntimeException.class, () -> facade.register(u));
    }

    @Test
    void login() throws Exception {
        register();
        var u = new UserData("player1", "password", null);
        var authData = facade.login(u);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void loginFalse() throws Exception {
        var u = new UserData("player1", "password", null);
        Assertions.assertThrows(RuntimeException.class, () -> facade.login(u));
    }

    @Test
    void create() throws Exception {
        register();
        var u = new CreateGameRequest("Name", authToken);
        var data = facade.create(u);
        gameID = data.gameID();
    }

    @Test
    void createFalse() throws Exception {
        register();
        var u = new CreateGameRequest("Name", "1234");
        Assertions.assertThrows(RuntimeException.class, () -> facade.create(u));
    }

    @Test
    void list() throws Exception {
        create();
        var data = facade.list(authToken);
        Assertions.assertFalse(data.isEmpty());
    }

    @Test
    void listFalse() throws Exception {
        register();
        Assertions.assertThrows(RuntimeException.class, () -> facade.list("1234"));
    }

    @Test
    void join() throws Exception {
        create();
        var u = new JoinGameRequest("WHITE", gameID, authToken);
        facade.join(u);
    }

    @Test
    void joinFalse() throws Exception {
        register();
        var u = new JoinGameRequest("WHiTE", 1, authToken);
        Assertions.assertThrows(RuntimeException.class, () -> facade.join(u));
    }

    @Test
    void logout() throws Exception {
        register();
        var u = new LogoutRequest(authToken);
        facade.logout(u);
    }

    @Test
    void logoutFalse() throws Exception {
        register();
        var u = new LogoutRequest("123");
        Assertions.assertThrows(RuntimeException.class, () -> facade.logout(u));
    }

}

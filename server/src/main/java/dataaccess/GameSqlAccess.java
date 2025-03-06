package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import service.requests.RegisterRequest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class GameSqlAccess implements GameDAO{
    private final Gson serializer = new Gson();

    public GameSqlAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO game (gameName, game) VALUES(?, ?)";
        var game = serializer.toJson(new ChessGame(), ChessGame.class);
        return executeUpdate(statement, gameName, game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whiteUsername, blackUsername, gameName, game FROM game WHERE gameID = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        var game = rs.getString("game");
                        var gameObject = serializer.fromJson(game, ChessGame.class);
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, gameObject);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var allGames = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName FROM game";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        var gameID = rs.getInt("gameID");
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        allGames.add(new GameData(gameID, whiteUsername, blackUsername, gameName, null));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return allGames;
    }

    @Override
    public void updateGame(GameData u) throws DataAccessException {
        var statement = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
        var game = serializer.toJson(u.game(), ChessGame.class);
        executeUpdate(statement, u.whiteUsername(), u.blackUsername(), u.gameName(), game, u.gameID());
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "DELETE FROM game";
        executeUpdate(statement);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {

            var createUserTable = """
            CREATE TABLE  IF NOT EXISTS game (
                gameID INT NOT NULL AUTO_INCREMENT,
                whiteUsername VARCHAR(255) DEFAULT NULL,
                blackUsername VARCHAR(255) DEFAULT NULL,
                gameName VARCHAR(255) DEFAULT NULL,
                game TEXT DEFAULT NULL,
                PRIMARY KEY (gameID),
                FOREIGN KEY (whiteUsername) REFERENCES user(username) ON DELETE CASCADE,
                FOREIGN KEY (blackUsername) REFERENCES user(username) ON DELETE CASCADE
            )""";

            try (var preparedStatement = conn.prepareStatement(createUserTable)) {
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }

    }
}

package dataAccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData a) throws DataAccessException;

    AuthData getAuth(int authToken) throws DataAccessException;

    void deleteAuth(AuthData a) throws DataAccessException;

    void clear() throws DataAccessException;
}

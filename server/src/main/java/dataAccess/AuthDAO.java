package dataAccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData u) throws DataAccessException;

    AuthData getAuth(int authToken) throws DataAccessException;

    void deleteAuth(AuthData u) throws DataAccessException;

}

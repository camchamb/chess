package dataaccess;

import model.AuthData;

public class AuthSqlAccess implements AuthDAO{
    @Override
    public void createAuth(AuthData a) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}

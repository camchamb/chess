package dataAccess;

import model.AuthData;

import java.util.HashMap;

public class AuthMemoryAccess implements AuthDAO{
    final private HashMap<String, AuthData> data = new HashMap<>();

    @Override
    public void createAuth(AuthData a) throws DataAccessException {
        data.put(a.authToken(), a);
    }

    @Override
    public AuthData getAuth(int authToken) throws DataAccessException {
        return data.get(authToken);
    }

    @Override
    public void deleteAuth(AuthData a) throws DataAccessException {
        data.remove(a.authToken());
    }

    @Override
    public void clear() throws DataAccessException {
        data.clear();
    }
}

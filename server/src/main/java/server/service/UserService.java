package server.service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccessDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import server.service.request.LoginRequest;
import server.service.request.LogoutRequest;
import server.service.request.RegisterRequest;
import server.service.result.LoginResult;
import server.service.result.RegisterResult;

import java.util.UUID;

//endpoints: register, login/logout
public class UserService {

    private final MemoryDataAccessDAO dao;

    public UserService(MemoryDataAccessDAO dao) {

        this.dao = dao;

    }

    public RegisterResult register(RegisterRequest request) throws ResponseException, DataAccessException {
        String username = request.username();
        UserData user = dao.getUserDAO().getUser(username);

        if (dao.getUserDAO().usernameTaken(username)){
            throw new ResponseException(ResponseException.Code.AlreadyTaken, "Error: username already taken");
        }

        UserData newUser = new UserData(username, request.password(), request.email());
        dao.getUserDAO().createUser(newUser);

        String authToken = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(authToken, username);
        dao.getAuthDAO().createAuth(newAuth);

        return new RegisterResult(username, authToken);
    }

    public LoginResult login(LoginRequest request) throws ResponseException, DataAccessException {
        String username = request.username();
        UserData user = dao.getUserDAO().getUser(username);
        if (user == null || user.password() == null || !user.password().equals(request.password())) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, username);
        dao.getAuthDAO().createAuth(auth);

        return new LoginResult(username, authToken);
    }

    public void logout(LogoutRequest request) throws ResponseException, DataAccessException {
        String authToken = request.authToken();
        AuthData auth = dao.getAuthDAO().getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");
        }
        dao.getAuthDAO().deleteAuth(authToken);
    }
}

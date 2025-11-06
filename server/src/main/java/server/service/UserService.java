package server.service;

import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.service.request.LoginRequest;
import server.service.request.LogoutRequest;
import server.service.request.RegisterRequest;
import server.service.result.LoginResult;
import server.service.result.RegisterResult;

import java.util.UUID;

//endpoints: register, login/logout
public class UserService {

    private final DataAccessDAO dao;

    public UserService(DataAccessDAO dao) {

        this.dao = dao;

    }

    public RegisterResult register(RegisterRequest request) throws ResponseException, DataAccessException {
        String username = request.username();
        String password = request.password();

        if (username == null || username.isEmpty()) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Error: username cannot be empty");
        }
        if (password == null || password.isEmpty()) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Error: password cannot be empty");
        }

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
        String password = request.password();

        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            throw new ResponseException(ResponseException.Code.BadRequest, "Error: username or password doesn't exist");
        }

        UserData user = dao.getUserDAO().getUser(username);
        if (user == null || user.password() == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");
        }

        try {
            if (!BCrypt.checkpw(password, user.password())) {
                throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseException(ResponseException.Code.ServerError, "Error: invalid password hash");
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

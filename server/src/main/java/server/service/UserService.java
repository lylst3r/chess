package server.service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccessDAO;
import exception.ResponseException;
import model.UserData;

import java.util.UUID;

//endpoints: register, login/logout
public class UserService {

    private final MemoryDataAccessDAO dao;

    public UserService(MemoryDataAccessDAO dao) {

        this.dao = dao;

    }

    /*public RegisterResult register(RegisterRequest request) throws ResponseException, DataAccessException {
        UserData user = dao.getUserDAO().getUser(request.username());
        if (dao.usernameTaken(username)){
            throw new ResponseException(ResponseException.Code.AlreadyTaken, "Error: username already taken");
        }

        dao.createUser(username, password, email);
        dao.createAuth(username, password);
    }

    public String[] login(String username, String password, MemoryDataAccessDAO dao) throws ResponseException {
        UserData user = dao.getUser(username);
        if (user == null){
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        //AuthData auth = dao.createAuth(authToken, username);
        String[] authInfo = new String[2];
        authInfo[0] = username;
        authInfo[1] = authToken;
        return authInfo;
    }*/
}

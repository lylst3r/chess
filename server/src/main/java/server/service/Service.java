package server.service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccessDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import server.service.request.*;
import server.service.result.*;
import server.service.result.LoginResult;

public class Service {

    MemoryDataAccessDAO dao;
    ClearService clearService;
    UserService userService;
    GameService gameService;

    public Service(MemoryDataAccessDAO dao) {
        this.dao = dao;
        clearService = new ClearService(dao);
        userService = new UserService(dao);
        gameService = new GameService(dao);
    }

    public void clear() throws DataAccessException, ResponseException {
        clearService.clearAll();

    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException, DataAccessException {
        return userService.login(loginRequest);
    }

    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException, DataAccessException {
        return userService.register(registerRequest);
    }

    public void logout(LogoutRequest logoutRequest) throws ResponseException, DataAccessException {
        userService.logout(logoutRequest);
    }

    public AuthData getAuth(String authToken) throws ResponseException, DataAccessException {
        return dao.getAuthDAO().getAuth(authToken);
    }

    public ListGamesResult listGames(String authToken) throws ResponseException, DataAccessException {
        return gameService.listGames();
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ResponseException, DataAccessException {
        return gameService.createGame(request);
    }

    public void joinGame(JoinGameRequest request, String username) throws ResponseException, DataAccessException {
        gameService.joinGame(request, username);
    }

}

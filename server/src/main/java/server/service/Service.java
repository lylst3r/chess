package server.service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccessDAO;
import exception.ResponseException;

public class Service {

    MemoryDataAccessDAO dao;
    ClearService clearService;
    UserService userService;
    GameService gameService;

    public Service() {
        dao = new MemoryDataAccessDAO();
        clearService = new ClearService();
        userService = new UserService(dao);
        gameService = new GameService(dao);
    }

    public void clear() throws DataAccessException {
        clearService.clearAll(dao);

    }

    /*public String[] login(String username, String password) throws ResponseException {
        return userService.login(username, password, dao);
    }

    public void register(String username, String password, String email) throws ResponseException {
        userService.register(username, password, email, dao);
    }*/

}

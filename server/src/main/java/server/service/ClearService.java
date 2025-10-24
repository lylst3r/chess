package server.service;

import dataaccess.MemoryDataAccessDAO;
import dataaccess.DataAccessException;
import exception.ResponseException;

public class ClearService {

    MemoryDataAccessDAO dao;

    public ClearService(MemoryDataAccessDAO dao) {

        this.dao = dao;

    }

    public void clearAll() throws DataAccessException, ResponseException {
//        if (authToken = null && !authToken.isEmpty()) {
//            var username = dao.getAuthDAO().getUsername(authToken);
//            if (username == null) {
//                throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");
//            }
//        }

        /*if (authToken == null || dao.getAuthDAO().getUsername(authToken) == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");
        }*/

        dao.clearAll();
    }

}

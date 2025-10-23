package server.service;

import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;

public class ClearService {

    public ClearService(){}

    public void clearAll(DataAccessDAO dao) throws DataAccessException {
        dao.clearAll();
    }
}

package server.services;

import dataaccess.DataAccessDAO;
import dataaccess.UserDAO;

public class ClearService {

    public void clearAll(DataAccessDAO dao) {
        dao.clear();
    }
}

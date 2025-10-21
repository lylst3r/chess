package server.services;

import dataaccess.DataAccessDAO;

public class Service {

    DataAccessDAO dao = new DataAccessDAO();
    ClearService clearService = new ClearService();

    public void clear() {
        clearService.clearAll(dao);
    }

}

package dataaccess.memory;

import dataaccess.sql.MainDataAccessDAO;

public class MemoryDataAccessDAO extends MainDataAccessDAO {

    public MemoryDataAccessDAO() {
        super(new MemoryUserDAO(), new MemoryGameDAO(), new MemoryAuthDAO());
    }
}

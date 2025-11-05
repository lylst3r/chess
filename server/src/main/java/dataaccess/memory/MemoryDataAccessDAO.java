package dataaccess.memory;

import dataaccess.MainDataAccessDAO;

public class MemoryDataAccessDAO extends MainDataAccessDAO {

    public MemoryDataAccessDAO() {
        super(new MemoryUserDAO(), new MemoryGameDAO(), new MemoryAuthDAO());
    }
}

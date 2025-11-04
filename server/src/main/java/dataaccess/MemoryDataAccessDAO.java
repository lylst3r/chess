package dataaccess;

public class MemoryDataAccessDAO extends mainDataAccessDAO {

    public MemoryDataAccessDAO() {
        super(new MemoryUserDAO(), new MemoryGameDAO(), new MemoryAuthDAO());
    }
}

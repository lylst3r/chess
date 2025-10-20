package dataaccess;
import java.util.UUID;

public class AuthDAO extends DataAccessDAO{

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}

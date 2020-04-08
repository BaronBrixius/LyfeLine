import java.sql.ResultSet;
import java.sql.SQLException;

public interface DBObject<T> {
    T createFromDB(ResultSet rs) throws SQLException;
    String getInsertQuery() throws SQLException;
}

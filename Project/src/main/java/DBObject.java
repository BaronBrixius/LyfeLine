import java.sql.SQLException;

public interface DBObject<T> extends CreatableFromDB<T> {
    String getInsertQuery() throws SQLException;
}

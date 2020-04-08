import java.sql.SQLException;

interface DBObject<T> extends CreatableFromDB<T> {
    String getInsertQuery() throws SQLException;
}

import java.sql.PreparedStatement;
import java.sql.SQLException;

interface DBObject<T> extends CreatableFromDB<T> {
    PreparedStatement getInsertQuery() throws SQLException;
    PreparedStatement getUpdateQuery() throws SQLException;
    void setID(int id);
}

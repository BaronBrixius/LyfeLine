import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

interface DBObject<T> extends CreatableFromDB<T> {
    String getInsertQuery() throws SQLException, SQLIntegrityConstraintViolationException;
    String getUpdateQuery() throws SQLException;
    void setID(int id);
}

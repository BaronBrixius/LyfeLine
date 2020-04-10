import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

interface DBObject<T> extends CreatableFromDB<T> {
    PreparedStatement getInsertQuery() throws SQLException, SQLIntegrityConstraintViolationException;
    PreparedStatement getUpdateQuery() throws SQLException;
    void setID(int id);
}

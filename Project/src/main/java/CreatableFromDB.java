import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
interface CreatableFromDB<T> {
    T createFromDB(ResultSet rs) throws SQLException;
}

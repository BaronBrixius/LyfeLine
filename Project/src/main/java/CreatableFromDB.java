import java.sql.ResultSet;
import java.sql.SQLException;

public interface CreatableFromDB<T> {
    T createFromDB(ResultSet rs) throws SQLException;
}

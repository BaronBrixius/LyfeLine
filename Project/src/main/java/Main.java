import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        DBManager db = null;
        try {
            db = new DBManager();

            Event now = new Event();
            System.out.print(now.getInsertQuery());
            db.insertIntoDB(now);
        } finally {
            if (db != null)
                db.close();
        }
    }
}
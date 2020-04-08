import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        DBManager db = null;
        try {
            db = new DBManager();

            Event now = new Event();
            System.out.println(now.getInsertQuery());
            db.insertIntoDB(now);

            List<Event> newNow = Event.getAllEvents();
            for (Event e: newNow) {
                System.out.println(e);
            }
        } finally {
            if (db != null)
                db.close();
        }



    }
}
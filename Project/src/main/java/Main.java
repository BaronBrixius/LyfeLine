import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        DBM dbm = null;
        try {
            dbm = new DBM();

            Event now = new Event(1, -44, 3, 15);
            System.out.println(now.getInsertQuery());
            dbm.insertIntoDB(now);

            List<Event> newNow = DBM.getFromDB("SELECT * FROM events", new Event());     //blank object so functional interface method can be accessed
            List<Integer> years = DBM.getFromDB("SELECT StartYear, StartMonth FROM events", rs -> rs.getInt("StartYear"));

            for (Event e: newNow) {
                System.out.println(e);
            }


            for (Integer i: years)
                System.out.println(i);
        } finally {
            if (dbm != null)
                dbm.close();
        }



    }
}
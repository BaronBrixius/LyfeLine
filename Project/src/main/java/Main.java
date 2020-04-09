import java.sql.SQLException;
import java.util.List;

class Main {
    public static void main(String[] args) throws SQLException {
        DBM dbm = new DBM();
        try {
            Event now = new Event(1, -44, 3, 15);
            DBM.insertIntoDB(now);

            List<Event> newNow = DBM.getFromDB("SELECT * FROM events", new Event());     //blank object so functional interface method can be accessed
            for (Event e : newNow)
                System.out.println(e);

            List<Integer> years = DBM.getFromDB("SELECT StartYear, StartMonth FROM events", rs -> rs.getInt("StartYear"));
            for (Integer i : years)
                System.out.println(i);
        } finally {
                dbm.close();
        }
    }
}
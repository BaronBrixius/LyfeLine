import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

class Main {
    public static void main(String[] args) throws SQLException {
        DBM dbm = null;
        try {
            dbm = new DBM();
            //DBM.createDB();       //remakes DB with default settings

            Event now = new Event(1, 2020, 4, 9);
            Event then = new Event(2, -44, 3, 15);
            DBM.insertIntoDB(now, then);

            try {                   //throws as a demonstration of anti-dupe
                DBM.insertIntoDB(now);
            } catch (SQLIntegrityConstraintViolationException e) {
                System.err.println(e.getMessage());
            }

            //Makes a list of events from the DB and prints it
            List<Event> eventList = DBM.getFromDB("SELECT * FROM events", new Event());     //blank object so functional interface method can be accessed
            System.out.println("\nEvent List:");
            for (Event e : eventList)
                System.out.println(e);

            //Makes a list of event years from the DB and prints it
            System.out.println("\nYear List:");
            List<Integer> yearList = DBM.getFromDB("SELECT StartYear, StartMonth FROM events", rs -> rs.getInt("StartYear"));
            for (Integer i : yearList)
                System.out.println(i);

            //User has its own validation method so an object doesn't have to be created to validate email
            User doctor = new User("BigDoc@math.biz", "Jerry Muhfan", "hunter2");
            if (User.validateUnique("BigDoc@math.biz"))
                DBM.insertIntoDB(doctor);
            else
                System.out.println("\nNot a unique email!");

        } finally {
            if (dbm != null)
                dbm.close();
        }
    }
}
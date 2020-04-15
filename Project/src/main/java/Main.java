import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        DBM dbm = null;
        PreparedStatement stmt;
        try {
            dbm = new DBM();

            DBM.createDB();       //remakes DB with default settings

            DBM.createDB();       //destroys + remakes DB with default settings, can comment this out after first run if desired


            Event now = new Event(1, 2020, 4, 9);
            Event then = new Event(2, -44, 3, 15);
            DBM.insertIntoDB(now, then);

            try {                   //throws as a demonstration of anti-dupe
                DBM.insertIntoDB(now);
            } catch (SQLIntegrityConstraintViolationException e) {
                System.err.println(e.getMessage() + " (This is just a demonstration exception. -Max)");
            }

            //Makes a list of events from the DB and prints it
            stmt = DBM.conn.prepareStatement("SELECT * FROM events");
            List<Event> eventList = DBM.getFromDB(stmt, new Event());           //blank object so functional interface method can be accessed
            System.out.println("\nEvent List:");
            for (Event e : eventList)
                System.out.println(e);

            //Makes a list of event years from the DB and prints it
            //note: you can just prepare a statement right in the method parameters if there aren't any field values that need to be set
            System.out.println("\nYear List:");
            List<Integer> yearList = DBM.getFromDB(DBM.conn.prepareStatement("SELECT StartYear, StartMonth FROM events"), rs -> rs.getInt("StartYear"));
            for (Integer i : yearList)
                System.out.println(i);

            User professorChaos = new User("Seeqwul Encurshun", "email@yo.mama", "hunter2");    //SQL injection attempt
            DBM.insertIntoDB(professorChaos);
            
            User teacher = new User("Hans Ove", "Hans@math.biz", "IloveMath");
            if (User.validateUnique("Hans@math.biz"))
                DBM.insertIntoDB(teacher);
            else
                System.out.println("\nNot a unique email!");

            //Example of Prepared Statement with field value
            stmt = DBM.conn.prepareStatement("SELECT * FROM users WHERE userEmail = ?");
            stmt.setString(1, teacher.getUserEmail());

            List<User> userList = DBM.getFromDB(stmt, new User());    //blank object so functional interface method can be accessed
            System.out.println("\nUser:");
            for (User e : userList)
                System.out.println(e);

        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dbm != null)
                    dbm.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
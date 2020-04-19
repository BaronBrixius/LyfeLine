import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

class Main {
    public static void main(String[] args) {
        PreparedStatement stmt;
        try {
            new DBM("jdbc:mysql://localhost?useTimezone=true&serverTimezone=UTC", "Halli", "dragon", "project");
            DBM.setupSchema();       //destroys + remakes DB with default settings, can comment this out after first run if desired

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
            List<Event> eventList = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM events"), new Event());           //blank object so functional interface method can be accessed
            System.out.println("\nEvent List:");
            for (Event e : eventList)
                System.out.println(e);

            //Makes a list of event years from the DB and prints it
            //note: you can just prepare a statement right in the method parameters if there aren't any field values that need to be set
            System.out.println("\nYear List:");
            List<Integer> yearList = DBM.getFromDB(DBM.conn.prepareStatement("SELECT StartYear, StartMonth FROM events"), rs -> rs.getInt("StartYear"));
            for (Integer i : yearList)
                System.out.println(i);

            User professorChaos = new User("Seeqwul Encurshun', 'BigDoc@abuseme.biz', 'FunPass', 'TheSalt', '1'); -- ", "email@yo.mama", "Passw0rd!");    //SQL injection attempt
            DBM.insertIntoDB(professorChaos);
            //I add 3 timelines manually to get the exception that this user has already this timelinename in it - timeline 2 is good but 3 is the same so exception is thrown
            Timeline test = new Timeline(0, "My timeline", "Very cool timeline", "Month", "pink", new Date(1,0,0,0,0,0,0), new Date(2,0,0,0,0,0,0),  new Date(2,0,0,0,0,0,0), 10, false);
            DBM.insertIntoDB(test);
            Timeline test1 = new Timeline(0, "My other timeline", "Very cool timeline", "Month", "pink", new Date(1,0,0,0,0,0,0), new Date(2,0,0,0,0,0,0),  new Date(2,0,0,0,0,0,0), 10, false);
            DBM.insertIntoDB(test1); //Here are two timelines with same name == ok because I changed userID
            Timeline test3 = new Timeline(0, "My other timeline", "Very cool timeline", "Month", "pink", new Date(1,0,0,0,0,0,0), new Date(2,0,0,0,0,0,0),  new Date(2,0,0,0,0,0,0), 11, false);
            DBM.insertIntoDB(test3); //Here are two timelines with same name == NOT OK because I now same  userID and same name
            Timeline test4 = new Timeline(0, "My other timeline", "Very cool timeline", "Month", "pink", new Date(1,0,0,0,0,0,0), new Date(2,0,0,0,0,0,0),  new Date(2,0,0,0,0,0,0), 11, false);
            DBM.insertIntoDB(test3);


            User teacher = new User("Hans Ove", "Hans@math.biz", "Passw0rd!");
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

        } catch (FileNotFoundException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                DBM.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
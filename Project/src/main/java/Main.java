import database.DBM;
import database.Timeline;
import database.User;
import utils.Date;
import database.Event;

import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

class Main {
    public static void main(String[] args) {
        PreparedStatement stmt;
        PreparedStatement stmt2;
        try {
            new DBM();
            DBM.setupSchema();       //destroys + remakes DB with default settings, can comment this out after first run if desired

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

            User professorChaos = new User("Seeqwul Encurshun', 'BigDoc@abuseme.biz', 'FunPass', 'TheSalt', '1'); -- ", "email@yo.mama", "Passw0rd!");    //SQL injection attempt
            DBM.insertIntoDB(professorChaos);
            Date testing = new Date(1984,24,10,0,0,0,0);
            System.out.println(testing.toString());
            User teacher = new User("Hans Ove", "Hans@math.biz", "Passw0rd!");
            if (User.validateUnique("Hans@math.biz"))
                DBM.insertIntoDB(teacher);
            else
                System.out.println("\nNot a unique email!");

            //Example of Prepared Statement with field value
            stmt = DBM.conn.prepareStatement("SELECT * FROM users WHERE userEmail = ?");
            stmt.setString(1, teacher.getUserEmail());
            
            //PreparedStatement for printing out timelines
            stmt2 = DBM.conn.prepareStatement("SELECT * FROM timelines");
            List<Timeline> timelineList = DBM.getFromDB(stmt2, new Timeline());          
            System.out.println("\nTimeline List:");
            for (Timeline f : timelineList)
                System.out.println(f);

            List<User> userList = DBM.getFromDB(stmt, new User());    //blank object so functional interface method can be accessed
            System.out.println("\nUser:");
            for (User e : userList)
                System.out.println(e);

        } catch (SQLException | ClassNotFoundException | FileNotFoundException e) {
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
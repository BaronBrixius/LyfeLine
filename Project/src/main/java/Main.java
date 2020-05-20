import com.google.gson.Gson;
import database.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

class Main {
    public static void main(String[] args) throws SQLException {
        PreparedStatement stmt;
        PreparedStatement stmt2;
        Timeline time = new Timeline();
        try {
            new DBM();
            DBM.setupSchema();       //destroys + remakes DB with default settings, can comment this out after first run if desired


            //Makes a list of events from the DB and prints it
            stmt = DBM.conn.prepareStatement("SELECT * FROM events");
            List<Event> events = DBM.getFromDB(stmt, new Event());           //blank object so functional interface method can be accessed

            for (int i = 0; i < 7; i++) {
                events.addAll(events);
            }
            for (Event t: events)
                t.setID(0);
            DBM.insertIntoDB(events);


            long before = System.currentTimeMillis();
                DBM.updateInDB(events);
            long after = System.currentTimeMillis();
            System.out.println(after-before);
/*
            Gson gson = new Gson();
            JSONTimeline exportable = new JSONTimeline(timelines.get(0));
            String out = gson.toJson(exportable);
            System.out.println(out + "\n");
            File file = new File("jsonTest.json");
            PrintWriter outFile = new PrintWriter(file);
            outFile.println(out);
            outFile.close();

            Scanner inFile = new Scanner(file);
            JSONTimeline readJson = gson.fromJson(inFile.nextLine(), JSONTimeline.class);
            readJson.importToDB();
            inFile.close();
*/
            //long before = System.currentTimeMillis();

            //for (int i = 0; i < 1000; i++) {
            //    readJson.importRatings();
            //}

            //long after = System.currentTimeMillis();

            //System.out.println(after-before);
/*
            //Makes a list of event years from the DB and prints it
            //note: you can just prepare a statement right in the method parameters if there aren't any field values that need to be set
            System.out.println("\nYear List:");
            List<Integer> yearList = DBM.getFromDB(DBM.conn.prepareStatement("SELECT StartYear, StartMonth FROM events"), rs -> rs.getInt("StartYear"));
            for (Integer i : yearList)
                System.out.println(i);

            User professorChaos = new User("Seeqwul Encurshun', 'BigDoc@abuseme.biz', 'FunPass', 'TheSalt', '1'); -- ", "email@yo.mama", "Passw0rd!");    //SQL injection attempt
            DBM.insertIntoDB(professorChaos);
            LocalDateTime testing = LocalDateTime.of(1984,24,10,0,0,0,0);
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

            //timeline.setStartDate(LocalDateTime.of(startInputs.get(0).getValue(), startInputs.get(1).getValue(), startInputs.get(2).getValue(),
               //     startInputs.get(3).getValue(), startInputs.get(4).getValue(), startInputs.get(5).getValue(), startInputs.get(6).getValue()));


           // timeline.setEndDate(LocalDateTime.of(endInputs.get(0).getValue(), endInputs.get(1).getValue(), endInputs.get(2).getValue(),
            // endInputs.get(3).getValue(), endInputs.get(4).getValue(), endInputs.get(5).getValue(), endInputs.get(6).getValue()));


            //THE CODE FOR ADVANCED SEARCH,JUST ADD TO IT FOR MORE SEARCH OPTIONS - NOW IT DEALS WITH TWO OF THE MORE COMPLICATED ONES - GETTING THE CREATOR NAME FROM USERS AND READING THE COMMA SPLIT KEYWORDS
            String name = null; //IMAGEN THESE TREE ARE THE TextFields inputs from the user
            String keyword2 = "bronze";
            String author = null ;
            LocalDateTime startDateSpinner = null;
            LocalDateTime endDateSpinner = null;

            advancedSearch(name,keyword2,author,startDateSpinner,endDateSpinner);


           /* //EXAMPLE OF RETURNING THE TIMELINE's IDs THAT FULFILL THE SEARCH. THE IDs WILL THEN BE USED TO FILTER THE DASHBOARD OBSERVABLE LIST
            PreparedStatement stmt4 = DBM.conn.prepareStatement("SELECT `TimelineID` FROM `timelines` LEFT JOIN `users` ON users.UserID = timelines.TimelineOwner WHERE " +
                    " CONCAT(' ', `TimelineName`, ' ') LIKE CONCAT('% ', COALESCE(?, '%'), ' %') AND CONCAT(',', `Keywords`, ',') LIKE CONCAT('%,', COALESCE(?, '%'), ',%')  AND `UserName` = COALESCE(NULLIF(?, ''), `UserName`);") ;
            stmt4.setString(1, name);
            stmt4.setString(2, keyword2);
            stmt4.setString(3, author);
            List<Integer> list2 = DBM.getFromDB(stmt3, rs -> rs.getInt("TimelineID"));
            System.out.println("======SEARCH RESULTS - THE TIMELINES ID's==========");
            for(int i = 0; i<list2.size();i++)
                System.out.println(list2.get(i)); */


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


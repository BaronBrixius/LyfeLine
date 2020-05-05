import database.DBM;
import database.Timeline;
import database.User;
import utils.Date;
import database.Event;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class Main {
    public static void main(String[] args) {
        PreparedStatement stmt;
        PreparedStatement stmt2;
        try {
            new DBM(    );
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

            //timeline.setStartDate(new Date(startInputs.get(0).getValue(), startInputs.get(1).getValue(), startInputs.get(2).getValue(),
               //     startInputs.get(3).getValue(), startInputs.get(4).getValue(), startInputs.get(5).getValue(), startInputs.get(6).getValue()));


           // timeline.setEndDate(new Date(endInputs.get(0).getValue(), endInputs.get(1).getValue(), endInputs.get(2).getValue(),
            // endInputs.get(3).getValue(), endInputs.get(4).getValue(), endInputs.get(5).getValue(), endInputs.get(6).getValue()));


            //THE CODE FOR ADVANCED SEARCH,JUST ADD TO IT FOR MORE SEARCH OPTIONS - NOW IT DEALS WITH TWO OF THE MORE COMPLICATED ONES - GETTING THE CREATOR NAME FROM USERS AND READING THE COMMA SPLIT KEYWORDS
            String name = null; //IMAGEN THESE TREE ARE THE TextFields inputs from the user
            String keyword2 = "bronze";
            String author = null ;
            Date startDateSpinner = null;
            Date endDateSpinner = null;

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

    public static List<Integer> advancedSearch(String name, String keyword2, String author, Date startDateSpinner, Date endDateSpinner) throws SQLException {


        String[] keywords = null;
        StringBuilder dynamicParameter = new StringBuilder();
        if(keyword2 != null){
            keywords = keyword2.split(" ");

            for (int i = 1; i < keywords.length; i++) {
                dynamicParameter.append("OR  CONCAT(',', `Keywords`, ',') LIKE CONCAT('%,', COALESCE(?, '%'), ',%')");
            }}

        PreparedStatement stmt3 = DBM.conn.prepareStatement("SELECT * FROM `timelines` LEFT JOIN `users` ON users.UserID = timelines.TimelineOwner WHERE " +
                " CONCAT(' ', `TimelineName`, ' ') LIKE CONCAT('% ', COALESCE(?, '%'), ' %') AND `UserName` = COALESCE(NULLIF(?, ''), `UserName`) AND (CONCAT(',', `Keywords`, ',') LIKE CONCAT('%,', COALESCE(?, '%'), ',%') " + dynamicParameter + ")  ;") ;
        stmt3.setString(1, name);
        stmt3.setString(2, author);
        if(keywords != null)
            for (int i = 3; i < keywords.length + 3; i++) {
                stmt3.setString(i, keywords[i - 3]);
                System.out.println(stmt3);
            }
        else
            stmt3.setString(3, keyword2);


        //EXAMPLE OF RETURNING THE TIMELINES THAT FULFILL THE SEARCH AS TIMELINE OBJECT
        System.out.println();
        System.out.println("======SEARCH RESULTS as objects - THE TIMELINES NAMES==========");
        List<Timeline> list = DBM.getFromDB(stmt3, new Timeline());
        List<Timeline> tempAllList;
        List<Timeline> rightTimelines = list; //Currently the right list unless we need to update it with spinner search
        //If only searching with Range and nothing else
        if(list.isEmpty() & (startDateSpinner != null || endDateSpinner != null )) {
            rightTimelines = new ArrayList<>();
            PreparedStatement out = DBM.conn.prepareStatement("SELECT * FROM timelines");
            tempAllList = DBM.getFromDB(out, new Timeline());
            //If range is defined in both ends
            if (startDateSpinner != null & endDateSpinner != null) {
                Date start = startDateSpinner;
                Date end = endDateSpinner;
                for(int i = 0; i<tempAllList.size(); i++){
                    if(tempAllList.get(i).getStartDate().compareTo(start) != -1 || tempAllList.get(i).getEndDate().compareTo(end) != 1)
                        rightTimelines.add(tempAllList.get(i));
                }

            }
            //If range is defined in start
            else if (startDateSpinner != null ) {
                Date start = startDateSpinner;
                Date end = endDateSpinner;
                for(int i = 0; i<tempAllList.size(); i++){
                    if(tempAllList.get(i).getStartDate().compareTo(start) != -1 )
                        rightTimelines.add(tempAllList.get(i));
                }
            }
            //If range is defined in end
            else {
                Date start = startDateSpinner;
                Date end = endDateSpinner;
                for(int i = 0; i<tempAllList.size(); i++){
                    if(tempAllList.get(i).getEndDate().compareTo(end) != 1)
                        rightTimelines.add(tempAllList.get(i));
                }
            }
        }

        //If searching with Range amongst else
        if(!list.isEmpty() & (startDateSpinner != null || endDateSpinner != null )) {
            PreparedStatement out = DBM.conn.prepareStatement("SELECT * FROM timelines");
            rightTimelines = new ArrayList<>();
            //If range is defined in both ends
            if (startDateSpinner != null & endDateSpinner != null) {
                Date start = startDateSpinner;
                Date end = endDateSpinner;
                for(int i = 0; i<list.size(); i++){
                    if(list.get(i).getStartDate().compareTo(start) != -1 || list.get(i).getEndDate().compareTo(end) != 1)
                        rightTimelines.add(list.get(i));
                }

            }
            //If range is defined in start
            else if (startDateSpinner != null ) {
                Date start = startDateSpinner;
                Date end = endDateSpinner;
                for(int i = 0; i<list.size(); i++){
                    if(list.get(i).getStartDate().compareTo(start) != -1 )
                        rightTimelines.add(list.get(i));
                }
            }
            //If range is defined in end
            else {
                Date start = startDateSpinner;
                Date end = endDateSpinner;
                for(int i = 0; i<list.size(); i++){
                    if(list.get(i).getEndDate().compareTo(end) != 1)
                        rightTimelines.add(list.get(i));
                }
            }
        }

        for(int i = 0; i<rightTimelines.size();i++)
            System.out.println(list.get(i).getName());

        List<Integer> timelineIDList = new ArrayList<>();
        for(int i = 0; i< rightTimelines.size(); i++)
            timelineIDList.add(rightTimelines.get(i).getID());

        return timelineIDList;
    }

}


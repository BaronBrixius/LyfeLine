import javafx.scene.control.DatePicker;

import java.sql.*;
import java.util.List;

class Event implements DBObject<Event> {
    private int eventID = 0;
    private int timelineID = 0;
    private int userID;
    private int eventType;
    private String eventName;
    private String eventDescription;
    private int imageID;//For now, not sure how we handle this later on
    private  Date startDate;
    private  Date endDate;
    private Date creationDate;

   public Event(){} //dummy constructor

    public Event(User user, Timeline timeline) {         //defaults, bare minimum - only related to the logged in user, timeline working on and sets creation date
        this.userID = user.getUserID();
        this.timelineID = timeline.getTimelineID();

    }

    private Event(int eventID, int timelineID, int userID,  Date startDate, Date endDate, Date creationDate , String title , String description, int imageID) {      //for reading from database
        this.eventID = eventID;
        this. timelineID = timelineID;
        this.userID = userID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.creationDate = creationDate;
        this.eventName=title;
        this.eventDescription=description;
        this.imageID = imageID;
        

    }



    //Some examples of working with the database
    /*static List<Event> getAll() throws SQLException {
        return DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM events"), new Event());     //blank object so functional interface method can be accessed
    }*/

    static List<Integer> getYears() throws SQLException {
        return DBM.getFromDB(DBM.conn.prepareStatement("SELECT StartYear FROM events"), rs -> rs.getInt("StartYear"));
    }

    @Override
    public PreparedStatement getInsertQuery() throws SQLException, RuntimeException {
        if (eventID > 0)
            throw new SQLIntegrityConstraintViolationException("Event is already in DB.");

        PreparedStatement out = DBM.conn.prepareStatement("INSERT INTO `events` (`EventType`, `EventName`, `EventDescription`,`StartYear`,`StartMonth`,`StartDay`,`StartHour`\"\n" +
                "\t\t\t\t+ \",`StartMinute`,`StartSecond`,`StartMillisecond`,`EndYear`,`EndMonth`,`EndDay`,`EndHour`,`EndMinute`,`EndSecond`,\"\n" +
                "\t\t\t\t+ \"`EndMillisecond`,`CreatedYear`,`CreatedMonth`,`CreatedDay`,`CreatedHour`,`CreatedMinute`,`CreatedSecond`,`CreatedMillisecond`,) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
        out.setInt(1, eventType);
        out.setString(2, eventName);
        out.setString(3, eventDescription);
        out.setInt(4, startDate.getYear());
        out.setInt(5, startDate.getMonth());
        out.setInt(6, startDate.getDay());
        out.setInt(7, startDate.getHours());
        out.setInt(8, startDate.getMinutes());
        out.setInt(9, startDate.getSeconds());
        out.setInt(10, startDate.getMilliseconds());
        out.setInt(11, endDate.getYear());
        out.setInt(12, endDate.getMonth());
        out.setInt(13, endDate.getDay());
        out.setInt(14, endDate.getHours());
        out.setInt(15, endDate.getMinutes());
        out.setInt(16, endDate.getSeconds());
        out.setInt(17, endDate.getMilliseconds());
        out.setInt(18, creationDate.getYear());
        out.setInt(19, creationDate.getMonth());
        out.setInt(20, creationDate.getDay());
        out.setInt(21, creationDate.getHours());
        out.setInt(22, creationDate.getMinutes());
        out.setInt(23, creationDate.getSeconds());
        out.setInt(24, creationDate.getMilliseconds());
        return out;
    }
    @Override
    public Event createFromDB(ResultSet rs) throws SQLException {
        int eventID = rs.getInt("EventID");
        int eventType = rs.getInt("EventType");
        String eventName = rs.getString("EventName");
        String eventDescription = rs.getString("EventDescription");
        int StartYear = rs.getInt("StartYear");
        int StartMonth = rs.getInt("StartMonth");
        int StartDay = rs.getInt("StartDay");
        int StartHour = rs.getInt("StartHour");
        int StartMinute = rs.getInt("StartMinute");
        int StartSecond = rs.getInt("StartSecond");
        int StartMillisecond = rs.getInt("StartMillisecond");
        int EndYear = rs.getInt("EndYear");
        int EndMonth = rs.getInt("EndMonth");
        int EndDay = rs.getInt("EndDay");
        int EndHour = rs.getInt("EndHour");
        int EndMinute = rs.getInt("EndMinute");
        int EndSecond = rs.getInt("EndSecond");
        int EndMillisecond = rs.getInt("EndMillisecond");
        int CreatedYear = rs.getInt("CreatedYear");
        int CreatedMonth = rs.getInt("CreatedMonth");
        int CreatedDay = rs.getInt("CreatedDay");
        int CreatedHour = rs.getInt("CreatedHour");
        int CreatedMinute = rs.getInt("CreatedMinute");
        int CreatedSecond = rs.getInt("CreatedSecond");
        int CreatedMillisecond = rs.getInt("CreatedMillisecond");
       // String start = rs.getString("Start");       //probably don't need to pull from table, can recalculate here, but I wanted to test it a bit

        return new  Event( eventID, timelineID,  userID,   startDate,  endDate, creationDate ,  eventName ,  eventDescription,  imageID);

    }



    @Override
    public void setID(int id) {
        this.eventID = id;
    }
    //Setters for editing Event fields
    public void setTitle(String title) {
        this.eventName = title;
    }
    public void setDescription(String description) {
        this.eventDescription = description;
    }
    public void setStartDate(String startDate) {
       String string = startDate;
       String[] parts = string.split("-");
       int year = Integer.parseInt(parts[0]);
       int month = Integer.parseInt(parts[1]);;
       int date = Integer.parseInt(parts[2]);;
        this.startDate = new Date(year,month,date,0,0,0,0);
    }
    public void setEndDate(String endDate) {
        String string = endDate;
        String[] parts = string.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);;
        int date = Integer.parseInt(parts[2]);;
        this.endDate = new Date(year,month,date,0,0,0,0);
    }
    public void setImage(int image) {
        this.imageID = image;
    }

    //Getter for editing Event fields
    public int  getEventID() {
        return this.eventID;
    }


    @Override
    public PreparedStatement getUpdateQuery() throws SQLException {
        if (eventID == 0)
            throw new SQLDataException("Event not in database cannot be updated.");
        PreparedStatement out = DBM.conn.prepareStatement("UPDATE `events` SET `EventTitle` = ?, `EventDescription` = ?, `EventImage` = ?, `StartYear` = ?,  `StartMonth` = ?,  `StartDay` = ?,  `StartHour` = ?,  `StartMinute` = ?,  `StartSecond` = ?,  `StartMillisecond` = ?,    `EndYear` = ?,  `EndMonth` = ?,  `EndDay` = ?,  `EndHour` = ?,  `EndMinute` = ?,  `EndSecond` = ?,  `EndMillisecond` = ? = ? WHERE (`EventID` = ?);");
        out.setString(1, this.eventName);
        out.setString(2, this.eventDescription);
        out.setInt(3, this.imageID);
        out.setInt(5, startDate.getYear());
        out.setInt(6, startDate.getMonth());
        out.setInt(7, startDate.getDay());
        out.setInt(8, startDate.getHours());
        out.setInt(9, startDate.getMinutes());
        out.setInt(10, startDate.getSeconds());
        out.setInt(11, startDate.getMilliseconds());
        out.setInt(12, endDate.getYear());
        out.setInt(13, endDate.getMonth());
        out.setInt(14, endDate.getDay());
        out.setInt(15, endDate.getHours());
        out.setInt(16, endDate.getMinutes());
        out.setInt(17, endDate.getSeconds());
        out.setInt(18, endDate.getMilliseconds());
        out.setInt(5, eventID);
        return out;
    }

    @Override
    public PreparedStatement getDeleteQuery() throws SQLException {
        PreparedStatement out = DBM.conn.prepareStatement("DELETE FROM `events` WHERE (`EventID` = ?)");
        out.setInt(1, eventID);
        return out;
    }

    @Override
    public String toString() {
        return "EventID: " + eventID + " EventType: " + eventType+"EventName"+eventName+"eventDescription"+eventDescription+" Start Date: "+startDate+" End Date: "+endDate+" Created: "+creationDate;
    }
}

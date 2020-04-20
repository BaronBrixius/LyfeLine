import javafx.scene.control.DatePicker;

import java.sql.*;
import java.util.List;

class Event implements DBObject<Event> {
    private int eventID = 0;
    private int timelineID = 0;
    private int userID;
    private String description;
    private  String title;
    private String imagePath;//For now, not sure how we handle this later on
    private  Date startDate;
    private  Date endDate;
    private Date creationDate;

   public Event(){} //dummy constructor

    public Event(User user, Timeline timeline) {         //defaults, bare minimum - only related to the logged in user, timeline working on and sets creation date
        this.userID = user.getUserID();
        this.timelineID = timeline.getTimelineID();

    }

    private Event(int eventID, int timelineID, int userID,  Date startDate, Date endDate, Date creationDate , String title , String description, String imagePath) {      //for reading from database
        this.eventID = eventID;
        this. timelineID = timelineID;
        this.userID = userID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.creationDate = creationDate;
        this.title=title;
        this.description=description;
        this.imagePath = imagePath;


    }

    //Some examples of working with the database
    static List<Event> getAll() throws SQLException {
        return DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM events"), new Event());     //blank object so functional interface method can be accessed
    }

    static List<Integer> getYears() throws SQLException {
        return DBM.getFromDB(DBM.conn.prepareStatement("SELECT StartYear FROM events"), rs -> rs.getInt("StartYear"));
    }

    @Override
    public Event createFromDB(ResultSet rs) throws SQLException {
        int eventID = rs.getInt("EventID");
        int eventType = rs.getInt("EventType");
        int startYear = rs.getInt("StartYear");
        int startMonth = rs.getInt("StartMonth");
        int startDay = rs.getInt("StartDay");
        // String start = rs.getString("Start");       //probably don't need to pull from table, can recalculate here, but I wanted to test it a bit

        return new Event(eventID, eventType, startYear, startMonth, startDay, start);
    }

    @Override
    public PreparedStatement getInsertQuery() throws SQLException, RuntimeException {
        if (eventID > 0)
            throw new SQLIntegrityConstraintViolationException("Event is already in DB.");

        PreparedStatement out = DBM.conn.prepareStatement("INSERT INTO `events` (`EventType`, `StartYear`, `StartMonth`, `StartDay`) VALUES (?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
        out.setInt(1, eventType);
        out.setInt(2, startYear);
        out.setInt(3, startMonth);
        out.setInt(4, startDay);
        return out;
    }

    @Override
    public void setID(int id) {
        this.eventID = id;
    }
    //Setters for editing Event fields
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
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
    public void setImage(String image) {
        this.imagePath = image;
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
        out.setString(1, this.title);
        out.setString(2, this.description);
        out.setString(3, this.imagePath);
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
        return "EventID: " + eventID + " EventType: " + eventType;
    }
}

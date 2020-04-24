package database;

import utils.Date;

import java.sql.*;
import java.util.List;

public class Event implements DBObject<Event> {
    private int eventID = 0;
    private int userID;
    private int eventType;
    private String eventName = "";
    private String eventDescription = "";
    private int imageID;//For now, not sure how we handle this later on
    private Date startDate = new Date();
    private Date endDate = new Date();
    private Date creationDate;

    public Event() {
    } //dummy constructor

    public Event(User user) {//defaults, bare minimum - only related to the logged in user, timeline working on and sets creation date
        this.userID = user.getUserID();
    }

    private Event(int eventID, int userID, Date startDate, Date endDate, Date creationDate, String title, String description, int imageID) {      //for reading from database
        this.eventID = eventID;
        this.userID = userID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.creationDate = creationDate;
        this.eventName = title;
        this.eventDescription = description;
        this.imageID = imageID;
    }

    public static List<Integer> getYears() throws SQLException {
        return DBM.getFromDB(DBM.conn.prepareStatement("SELECT StartYear FROM events"), rs -> rs.getInt("StartYear"));
    }

    public int getImageID() {
        return imageID;
    }


    //Some examples of working with the database
    /*static List<Event> getAll() throws SQLException {
        return DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM events"), new Event());     //blank object so functional interface method can be accessed
    }*/

    public Date getCreationDate() {
        return creationDate;
    }

    public int getUserID() {
        return userID;
    }

    @Override
    public PreparedStatement getInsertQuery() throws SQLException, RuntimeException {
        if (eventID > 0)
            throw new SQLIntegrityConstraintViolationException("Event is already in DB.");

        PreparedStatement out = DBM.conn.prepareStatement("INSERT INTO `events` (`EventType`, `EventName`, `EventDescription`,`StartYear`,`StartMonth`,`StartDay`,`StartHour`, " +
                "`StartMinute`,`StartSecond`,`StartMillisecond`,`EndYear`,`EndMonth`,`EndDay`,`EndHour`,`EndMinute`,`EndSecond`, " +
                "`EndMillisecond`,`CreatedYear`,`CreatedMonth`,`CreatedDay`,`CreatedHour`,`CreatedMinute`,`CreatedSecond`,`CreatedMillisecond`,`EventOwner`, `EventImage`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
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

        if (creationDate == null) {       //if new event
            out.setNull(18, Types.INTEGER);
            out.setNull(19, Types.INTEGER);
            out.setNull(20, Types.INTEGER);
            out.setNull(21, Types.INTEGER);
            out.setNull(22, Types.INTEGER);
            out.setNull(23, Types.INTEGER);
            out.setNull(24, Types.INTEGER);
        } else {
            out.setInt(18, creationDate.getYear());
            out.setInt(19, creationDate.getMonth());
            out.setInt(20, creationDate.getDay());
            out.setInt(21, creationDate.getHours());
            out.setInt(22, creationDate.getMinutes());
            out.setInt(23, creationDate.getSeconds());
            out.setInt(24, creationDate.getMilliseconds());
        }
        out.setInt(25, userID);
        if (imageID == 0)
            out.setNull(26, Types.INTEGER);
        else
            out.setInt(26, imageID);
        return out;
    }

    public void addToTimeline(int timelineID) throws SQLException {
        PreparedStatement out = DBM.conn.prepareStatement("INSERT IGNORE INTO `timelineevents` (`TimelineID`, `EventID`) VALUES (?, ?);");
        out.setInt(1, timelineID);
        out.setInt(2, this.eventID);
        out.execute();

    }

    public void removeFromTimeline(int timelineID) throws SQLException {
        PreparedStatement out = DBM.conn.prepareStatement("DELETE FROM `timelineevents` WHERE EventID = " + this.eventID + " AND TimelineID = " + timelineID + ";");
        out.execute();
    }

    @Override
    public Event createFromDB(ResultSet rs) throws SQLException {
        int eventID = rs.getInt("EventID");
        int imageID = rs.getInt("EventImage");
        int ownerID = rs.getInt("EventOwner");
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
        Date start = new Date(StartYear, StartMonth, StartDay, StartHour, StartMinute, StartSecond, StartMillisecond);
        Date end = new Date(EndYear, EndMonth, EndDay, EndHour, EndMinute, EndSecond, EndMillisecond);
        Date created = new Date(CreatedYear, CreatedMonth, CreatedDay, CreatedHour, CreatedMinute, CreatedSecond, CreatedMillisecond);

        return new Event(eventID, ownerID, start, end, created, eventName, eventDescription, imageID);
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
    /*public void setStartDate(String startDate) {
       String string = startDate;
       String[] parts = string.split("-");
       int year = Integer.parseInt(parts[0]);
       int month = Integer.parseInt(parts[1]);;
       int date = Integer.parseInt(parts[2]);;
        this.startDate = new Date(year,month,date,0,0,0,0);
    }
    public void setDateEnd(String endDate) {
        String string = endDate;
        String[] parts = string.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);;
        int date = Integer.parseInt(parts[2]);;
        this.endDate = new Date(year,month,date,0,0,0,0);
    }*/

    public void setImage(int image) {
        this.imageID = image;
    }

    //Getters for Event fields
    public int getEventID() {
        return this.eventID;
    }

    public String getEventDescrition() {
        return this.eventDescription;
    }

    public String getEventName() {
        return this.eventName;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public PreparedStatement getUpdateQuery() throws SQLException {
        if (eventID == 0)
            throw new SQLDataException("Event not in database cannot be updated.");
        PreparedStatement out = DBM.conn.prepareStatement("UPDATE `events` SET `EventName` = ?, `EventDescription` = ?, `EventImage` = ?, `StartYear` = ?,  `StartMonth` = ?,  `StartDay` = ?,  `StartHour` = ?,  `StartMinute` = ?,  " +
                "`StartSecond` = ?,  `StartMillisecond` = ?,    `EndYear` = ?,  `EndMonth` = ?,  `EndDay` = ?,  `EndHour` = ?,  `EndMinute` = ?,  `EndSecond` = ?,  `EndMillisecond` = ?, `EventOwner` = ?  WHERE (`EventID` = ?);");
        out.setString(1, this.eventName);
        out.setString(2, this.eventDescription);
        out.setInt(3, this.imageID);
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
        out.setInt(18, userID);
        out.setInt(19, eventID);
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
        return "EventID: " + eventID + " EventType: " + eventType + " EventName " + eventName + " EventDescription " + eventDescription + " Start Date: " + startDate + " End Date: " + endDate + " Created: " + creationDate;
    }

}

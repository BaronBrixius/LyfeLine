import java.sql.*;
import java.util.List;

class Event implements DBObject<Event> {
    int eventID = 0;
    private int eventType;
    private String eventName;
    private String eventDescription;
    private Date startDate;
    private Date endDate;
    private Date dateCreated;

   /* public Event() {         //defaults
        this(1, -44, 3, 15);
    }*/

    public Event(int eventID,int eventType,String eventName,String eventDescription,Date startDate,Date endDate, Date dateCreated) {
        this.eventID = eventID;
        this.eventType = eventType;
        this.eventName=eventName;
        this.eventDescription=eventDescription;
        this.startDate=startDate;
        this.endDate=endDate;
        this.dateCreated=dateCreated;
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
        out.setInt(18, dateCreated.getYear());
        out.setInt(19, dateCreated.getMonth());
        out.setInt(20, dateCreated.getDay());
        out.setInt(21, dateCreated.getHours());
        out.setInt(22, dateCreated.getMinutes());
        out.setInt(23, dateCreated.getSeconds());
        out.setInt(24, dateCreated.getMilliseconds());
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

        return new Event(eventID, eventType,eventName, eventDescription,
                new Date(StartYear,StartMonth,StartDay,StartHour,StartMinute,StartSecond,StartMillisecond),
                new Date(EndYear,EndMonth,EndDay,EndHour,EndMinute,EndSecond,EndMillisecond),
                new Date(CreatedYear,CreatedMonth,CreatedDay,CreatedHour,CreatedMinute,CreatedSecond,CreatedMillisecond));
    }



    @Override
    public void setID(int id) {
        this.eventID = id;
    }

    @Override
    public PreparedStatement getUpdateQuery() throws SQLException {
        if (eventID == 0)
            throw new SQLDataException("Event not in database cannot be updated.");
        PreparedStatement out = DBM.conn.prepareStatement("UPDATE `events` SET `EventType` = ?, `EventName` = ?, `EventDescription` = ?,   `StartYear` = ?,  `StartMonth` = ?,  `StartDay` = ?,  `StartHour` = ?,  `StartMinute` = ?,  `StartSecond` = ?,  `StartMillisecond` = ?,    `EndYear` = ?,  `EndMonth` = ?,  `EndDay` = ?,  `EndHour` = ?,  `EndMinute` = ?,  `EndSecond` = ?,  `EndMillisecond` = ?,   `CreatedYear` = ?,  `ECreatedMonth` = ?,  `CreatedDay` = ?,  `CreatedHour` = ?,  `CreatedMinute` = ?,  `CreatedSecond` = ?,  `CreatedMillisecond` = ? WHERE (`EventID` = ?);");
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
        out.setInt(18, dateCreated.getYear());
        out.setInt(19, dateCreated.getMonth());
        out.setInt(20, dateCreated.getDay());
        out.setInt(21, dateCreated.getHours());
        out.setInt(22, dateCreated.getMinutes());
        out.setInt(23, dateCreated.getSeconds());
        out.setInt(24, dateCreated.getMilliseconds());
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
        return "EventID: " + eventID + " EventType: " + eventType+"EventName"+eventName+"eventDescription"+eventDescription+" Start Date: "+startDate+" End Date: "+endDate+" Created: "+dateCreated;
    }
}

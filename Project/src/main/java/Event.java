import java.sql.*;
import java.util.List;

class Event implements DBObject<Event> {
    int eventID = 0;
    private final String eventName = "New Event";
    private final int eventType;
    private final int startYear;
    private final int startMonth;
    private final int startDay;
    private String start;

    public Event() {         //defaults
        this(1, -44, 3, 15);
    }

    public Event(int eventType, int startYear, int startMonth, int startDay) {
        this.eventType = eventType;
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDay = startDay;
    }

    private Event(int eventID, int eventType, int startYear, int startMonth, int startDay, String start) {      //for reading from database
        this.eventID = eventID;
        this.eventType = eventType;
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDay = startDay;
        this.start = start;
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

    @Override
    public PreparedStatement getUpdateQuery() throws SQLException {
        if (eventID == 0)
            throw new SQLDataException("Event not in database cannot be updated.");
        PreparedStatement out = DBM.conn.prepareStatement("UPDATE `events` SET `EventType` = ?, `StartYear` = ?, `StartMonth` = ?, `StartDay` = ? WHERE (`EventID` = ?);");
        out.setInt(1, eventType);
        out.setInt(2, startYear);
        out.setInt(3, startMonth);
        out.setInt(4, startDay);
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

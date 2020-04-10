import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

class Event implements DBObject<Event> {
    int eventID = 0;
    private int eventType;
    private int startYear;
    private int startMonth;
    private int startDay;
    private String start;

    Event() {         //defaults
        this.eventType = 1;
        this.startYear = -44;
        this.startMonth = 3;
        this.startDay = 15;
    }

    Event(int eventType, int startYear, int startMonth, int startDay) {
        this.eventType = eventType;
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDay = startDay;
    }

    private Event(int eventID, int eventType, int startYear, int startMonth, int startDay, String start) {
        this.eventID = eventID;
        this.eventType = eventType;
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDay = startDay;
        this.start = start;
    }

    //Some examples of working with the database
    static List<Event> getAll() throws SQLException {
        return DBM.getFromDB("SELECT * FROM events", new Event());     //blank object so functional interface method can be accessed
    }

    static List<Integer> getYears() throws SQLException {
        return DBM.getFromDB("SELECT StartYear FROM events", rs -> rs.getInt("StartYear"));
    }

    @Override
    public Event createFromDB(ResultSet rs) throws SQLException {
        Event out = null;
        try {
            int eventID = rs.getInt("EventID");
            int eventType = rs.getInt("EventType");
            int startYear = rs.getInt("StartYear");
            int startMonth = rs.getInt("StartMonth");
            int startDay = rs.getInt("StartDay");
            String start = rs.getString("Start");       //probably don't need to pull from table, can recalc here, but I wanted to test it a bit

            out = new Event(eventID, eventType, startYear, startMonth, startDay, start);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    @Override
    public String getInsertQuery() throws SQLException, RuntimeException {
        if (eventID > 0)
            throw new SQLIntegrityConstraintViolationException("Event is already in DB.");
        return "INSERT INTO `events` (`EventType`, `StartYear`, `StartMonth`, `StartDay`) " +
                "VALUES ('" + eventType + "', '" + startYear + "', '" + startMonth + "', '" + startDay + "');";
    }

    @Override
    public void setID(int id) {
        this.eventID = id;
    }

    @Override
    public String getUpdateQuery() throws SQLException {
        return "UPDATE `events` SET `EventType` = '" + eventType + "', `StartYear` = '" + startYear + "', `StartMonth` = '" + startMonth + "', `StartDay` = '" + startDay + "' " +
                "WHERE (`EventID` = '" + eventID + "');";
    }

    @Override
    public String toString() {
        return "EventID: " + eventID + " EventType: " + eventType + " Start: " + start;
    }
}

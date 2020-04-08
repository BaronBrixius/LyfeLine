import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;

public class Event implements DBObject<Event> {
    private int eventType;
    private int startYear;
    private int startMonth;
    private int startDay;

    public Event() {         //defaults for easier dev work
        this.eventType = 1;
        this.startYear = 2020;
        this.startMonth = 3;
        this.startDay = 15;
    }

    public Event(int eventType, int startYear, int startMonth, int startDay) {
        this.eventType = eventType;
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDay = startDay;
    }

    @Override
    public Event createFromDB(ResultSet rs) throws SQLException {
        Event out = null;
        try
        {
            int eventType = rs.getInt("EventType");
            int startYear = rs.getInt("StartYear");
            int startMonth = rs.getInt("StartMonth");
            int startDay = rs.getInt("StartDay");

            out = new Event(eventType, startYear, startMonth, startDay);

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return out;
    }

    @Override
    public String getInsertQuery() throws SQLException {
        return "INSERT INTO `events` (`EventType`, `StartYear`, `StartMonth`, `StartDay`) " +
                "VALUES ('" + eventType + "', '" + startYear + "', '" + startMonth + "', '" + startDay + "');";
    }
}

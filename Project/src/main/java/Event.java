import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;

public class Event implements DBObject<Event> {
    private int eventType;
    private int startYear;
    private int startMonth;
    private int startDay;
    private LocalTime startTime;

    public Event() {         //defaults for easier dev work
        this.eventType = 1;
        this.startYear = 2020;
        this.startMonth = 3;
        this.startDay = 15;
        this.startTime = LocalTime.MIDNIGHT;
    }

    public Event(int eventType, int startYear, int startMonth, int startDay) {
        this.eventType = eventType;
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDay = startDay;
        this.startTime = LocalTime.MIDNIGHT;
    }

    @Override
    public Event createFromDB(ResultSet rs) throws SQLException {
        return null;
    }

    @Override
    public String getInsertQuery() throws SQLException {
        return "INSERT INTO `events` (`EventType`, `StartYear`, `StartMonth`, `StartDay`) " +
                "VALUES ('" + eventType + "', '" + startYear + "', '" + startMonth + "', '" + startDay + "');";
    }
}

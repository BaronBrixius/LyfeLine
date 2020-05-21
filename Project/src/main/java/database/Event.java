package database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;

public class Event extends TimelineObject<Event> {
    private transient int eventID = 0;
    private int eventPriority = 0;
    private String eventName = "";
    private String eventDescription = "";


    public Event() {
    }

    public Event(User user) {//defaults, bare minimum - only related to the logged in user, timeline working on and sets creation date
        this.ownerID = user.getUserID();
    }

    private Event(int eventID, int ownerID, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime creationDate, String title, String description, String imagePath, int eventPriority) {      //for reading from database
        this.eventID = eventID;
        this.ownerID = ownerID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.creationDate = creationDate;
        this.eventName = title;
        this.eventDescription = description;
        this.imagePath = imagePath;
        this.eventPriority = eventPriority;
    }

    @Override
    public PreparedStatement getInsertQuery() throws SQLException, RuntimeException {
        if (eventID > 0)
            throw new SQLIntegrityConstraintViolationException("Event is already in DB.");

        PreparedStatement out = DBM.conn.prepareStatement("INSERT INTO `events` (`EventName`, `EventDescription`,`StartYear`,`StartMonth`,`StartDay`,`StartHour`, " +
                "`StartMinute`,`StartSecond`,`StartMillisecond`,`EndYear`,`EndMonth`,`EndDay`,`EndHour`,`EndMinute`,`EndSecond`, " +
                "`EndMillisecond`,`EventOwner`, `ImagePath`, `EventPriority`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
        out.setString(1, eventName);
        out.setString(2, eventDescription);
        out.setInt(3, startDate.getYear());
        out.setInt(4, startDate.getMonthValue());
        out.setInt(5, startDate.getDayOfMonth());
        out.setInt(6, startDate.getHour());
        out.setInt(7, startDate.getMinute());
        out.setInt(8, startDate.getSecond());
        out.setInt(9, startDate.getNano() / 1000000);
        out.setInt(10, endDate.getYear());
        out.setInt(11, endDate.getMonthValue());
        out.setInt(12, endDate.getDayOfMonth());
        out.setInt(13, endDate.getHour());
        out.setInt(14, endDate.getMinute());
        out.setInt(15, endDate.getSecond());
        out.setInt(16, endDate.getNano() / 1000000);
        out.setInt(17, ownerID);
        if (this.imagePath == null)
            out.setNull(18, Types.INTEGER);
        else
            out.setString(18, this.imagePath);

        out.setInt(19, eventPriority);

        return out;
    }

    public boolean addToTimeline(int timelineID) throws SQLException {
        PreparedStatement out = DBM.conn.prepareStatement("INSERT IGNORE INTO `timelineevents` (`TimelineID`, `EventID`) VALUES (?, ?);");
        out.setInt(1, timelineID);
        out.setInt(2, this.eventID);
        return out.executeUpdate() > 0;

    }

    public boolean removeFromTimeline(int timelineID) throws SQLException {
        PreparedStatement out = DBM.conn.prepareStatement("DELETE FROM `timelineevents` WHERE EventID = ? AND TimelineID = ?");
        out.setInt(1, eventID);
        out.setInt(2, timelineID);
        return out.executeUpdate() > 0;
    }

    @Override
    public Event createFromDB(ResultSet rs) throws SQLException {
        int eventID = rs.getInt("EventID");
        String imagePath = rs.getString("ImagePath");
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
        LocalDateTime start = LocalDateTime.of(StartYear, StartMonth, StartDay, StartHour, StartMinute, StartSecond, StartMillisecond);
        LocalDateTime end = LocalDateTime.of(EndYear, EndMonth, EndDay, EndHour, EndMinute, EndSecond, EndMillisecond);
        LocalDateTime created = LocalDateTime.of(CreatedYear, CreatedMonth, CreatedDay, CreatedHour, CreatedMinute, CreatedSecond, CreatedMillisecond);
        int EventPriority = rs.getInt("EventPriority");

        return new Event(eventID, ownerID, start, end, created, eventName, eventDescription, imagePath, EventPriority);
    }

    public int getID() {
        return this.eventID;
    }

    @Override
    public void setID(int id) {
        this.eventID = id;
    }

    public String getDescription() {
        return this.eventDescription;
    }

    public void setDescription(String description) {
        this.eventDescription = description;
    }

    public String getName() {
        return this.eventName;
    }

    public void setName(String name) {
        this.eventName = name;
    }

    @Override
    public PreparedStatement getUpdateQuery() throws SQLException {
        if (eventID == 0)
            throw new SQLDataException("Event not in database cannot be updated.");
        PreparedStatement out = DBM.conn.prepareStatement("UPDATE `events` SET `EventName` = ?, `EventDescription` = ?, `ImagePath` = ?, `StartYear` = ?,  `StartMonth` = ?,  `StartDay` = ?,  `StartHour` = ?,  `StartMinute` = ?,  " +
                "`StartSecond` = ?,  `StartMillisecond` = ?,    `EndYear` = ?,  `EndMonth` = ?,  `EndDay` = ?,  `EndHour` = ?,  `EndMinute` = ?,  `EndSecond` = ?,  `EndMillisecond` = ?, `EventOwner` = ?, `EventPriority` = ?  WHERE (`EventID` = ?);");
        out.setString(1, eventName);
        out.setString(2, eventDescription);
        out.setString(3, imagePath);
        out.setInt(4, startDate.getYear());
        out.setInt(5, startDate.getMonthValue());
        out.setInt(6, startDate.getDayOfMonth());
        out.setInt(7, startDate.getHour());
        out.setInt(8, startDate.getMinute());
        out.setInt(9, startDate.getSecond());
        out.setInt(10, startDate.getNano() / 1000000);
        out.setInt(11, endDate.getYear());
        out.setInt(12, endDate.getMonthValue());
        out.setInt(13, endDate.getDayOfMonth());
        out.setInt(14, endDate.getHour());
        out.setInt(15, endDate.getMinute());
        out.setInt(16, endDate.getSecond());
        out.setInt(17, endDate.getNano() / 1000000);
        out.setInt(18, ownerID);
        out.setInt(19, eventPriority);
        out.setInt(20, eventID);
        return out;
    }

    @Override
    public PreparedStatement getDeleteQuery() throws SQLException {
        PreparedStatement out = DBM.conn.prepareStatement("DELETE FROM `events` WHERE (`EventID` = ?)");
        out.setInt(1, eventID);

        // Deleting the images
        if (getImagePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(getImagePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out;
    }

    public int getEventPriority() {
        return eventPriority;
    }

    public void setEventPriority(int eventPriority) {
        this.eventPriority = eventPriority;
    }

    @Override
    public String toString() {
        return "EventID: " + eventID + " EventName " + eventName + " EventDescription " + eventDescription + " Start : " + startDate + " End : " + endDate + " Created: " + creationDate;
    }
}

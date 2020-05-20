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
        this.ownerID = user.getID();
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
    public PreparedStatement getInsertQuery() throws SQLException, RuntimeException {
        return DBM.conn.prepareStatement("INSERT INTO `events` (`EventName` , `EventDescription` , `ImagePath`, `StartYear`,  `StartMonth`,  `StartDay`,  `StartHour`,  `StartMinute`, " +
                " `StartSecond`,  `StartMillisecond`,    `EndYear`,  `EndMonth`,  `EndDay`,  `EndHour`,  `EndMinute`,  `EndSecond`,  `EndMillisecond`, `EventOwner`, `EventPriority`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
    }

    @Override
    public PreparedStatement getUpdateQuery() throws SQLException {
        return DBM.conn.prepareStatement("UPDATE `events` SET `EventName` = ?, `EventDescription` = ?, `ImagePath` = ?, `StartYear` = ?,  `StartMonth` = ?,  `StartDay` = ?,  `StartHour` = ?,  `StartMinute` = ?,  " +
                "`StartSecond` = ?,  `StartMillisecond` = ?,    `EndYear` = ?,  `EndMonth` = ?,  `EndDay` = ?,  `EndHour` = ?,  `EndMinute` = ?,  `EndSecond` = ?,  `EndMillisecond` = ?, `EventOwner` = ?, `EventPriority` = ?  WHERE (`EventID` = ?);");
    }

    @Override
    public PreparedStatement setQueryValues(PreparedStatement stmt) throws SQLException {
        stmt.setString(1, eventName);
        stmt.setString(2, eventDescription);
        stmt.setString(3, imagePath);
        stmt.setInt(4, startDate.getYear());
        stmt.setInt(5, startDate.getMonthValue());
        stmt.setInt(6, startDate.getDayOfMonth());
        stmt.setInt(7, startDate.getHour());
        stmt.setInt(8, startDate.getMinute());
        stmt.setInt(9, startDate.getSecond());
        stmt.setInt(10, startDate.getNano() / 1000000);
        stmt.setInt(11, endDate.getYear());
        stmt.setInt(12, endDate.getMonthValue());
        stmt.setInt(13, endDate.getDayOfMonth());
        stmt.setInt(14, endDate.getHour());
        stmt.setInt(15, endDate.getMinute());
        stmt.setInt(16, endDate.getSecond());
        stmt.setInt(17, endDate.getNano() / 1000000);
        stmt.setInt(18, ownerID);
        stmt.setInt(19, eventPriority);
        if (eventID > 0)
            stmt.setInt(20, eventID);

        return stmt;
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

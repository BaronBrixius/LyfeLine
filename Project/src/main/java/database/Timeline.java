package database;

import controllers.GUIManager;
import utils.Date;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Timeline implements DBObject<Timeline> {
    private int timelineID;
    private int scale;
    private String timelineName;
    private String theme;
    private Date startDate;
    private Date endDate;
    private Date dateCreated;
    private String timelineDescription;
    private int timelineOwner;
    private boolean isPrivate = false;
    private List<Event> eventList;
    private List<String> keywords;

    // Default timeline
    public Timeline() {
    }

    // Public method for creating the timeline
    public Timeline(String TimelineName, String TimelineDescription, int Scale, String Theme, Date StartDate,
                    Date Enddate, boolean Private, List<String> keywords) {
        this.timelineName = TimelineName;
        this.scale = Scale;
        this.timelineDescription = TimelineDescription;
        this.theme = Theme;
        this.startDate = StartDate;
        this.endDate = Enddate;
        this.timelineOwner = GUIManager.loggedInUser.getUserID();
        this.isPrivate = Private;
        this.keywords = keywords;
    }

    private Timeline(int TimeLineID, String TimelineName, String TimelineDescription, int Scale, String Theme,
                     Date StartDate, Date Enddate, Date DateCreated, int TimelineOwner, boolean Private, List<String> keywords, List<Event> eventList)
            throws SQLException {

        this.timelineID = TimeLineID;
        this.timelineName = TimelineName;
        this.scale = Scale;
        this.timelineDescription = TimelineDescription;
        this.theme = Theme;
        this.startDate = StartDate;
        this.endDate = Enddate;
        this.dateCreated = DateCreated;
        this.timelineOwner = TimelineOwner;
        this.isPrivate = Private;
        this.keywords = keywords;
        this.eventList = eventList;
    }

    @Override
    public PreparedStatement getInsertQuery() throws SQLException {
        if (timelineID > 0)
            throw new SQLIntegrityConstraintViolationException("TimelineID is already in DB.");

        PreparedStatement out = DBM.conn.prepareStatement(
                "INSERT INTO `timelines` ( `Scale`,`TimelineName`, `TimelineDescription`, `Theme`,`StartYear`,`StartMonth`,`StartDay`,`StartHour`"
                        + ",`StartMinute`,`StartSecond`,`StartMillisecond`,`EndYear`,`EndMonth`,`EndDay`,`EndHour`,`EndMinute`,`EndSecond`,"
                        + "`EndMillisecond`,`Private`,`TimelineOwner`,`Keywords`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
        out.setInt(1, scale);
        out.setString(2, timelineName);
        out.setString(3, timelineDescription);
        out.setString(4, theme);
        out.setInt(5, startDate.getYear());
        out.setInt(6, startDate.getMonth());
        out.setInt(7, startDate.getDay());
        out.setInt(8, startDate.getHour());
        out.setInt(9, startDate.getMinute());
        out.setInt(10, startDate.getSecond());
        out.setInt(11, startDate.getMillisecond());
        out.setInt(12, endDate.getYear());
        out.setInt(13, endDate.getMonth());
        out.setInt(14, endDate.getDay());
        out.setInt(15, endDate.getHour());
        out.setInt(16, endDate.getMinute());
        out.setInt(17, endDate.getSecond());
        out.setInt(18, endDate.getMillisecond());
        out.setBoolean(19, isPrivate);
        out.setInt(20, timelineOwner);
        // keyword string generation from list
        StringBuilder sb = new StringBuilder();
        for (String s : keywords) {
            sb.append(s);
            sb.append(",");
        }
        out.setString(21, sb.toString());
        return out;
    }

    @Override
    public PreparedStatement getUpdateQuery() throws SQLException {
        PreparedStatement out = DBM.conn.prepareStatement(
                "UPDATE `timelines` SET `Scale` = ?, `TimelineName` = ?, `TimelineDescription` = ?,  `Theme` = ?,   "
                        + "`StartYear` = ?,  `StartMonth` = ?,  `StartDay` = ?,  `StartHour` = ?,  `StartMinute` = ?,  `StartSecond` = ?,  "
                        + "`StartMillisecond` = ?,    `EndYear` = ?,  `EndMonth` = ?,  `EndDay` = ?,  `EndHour` = ?,  `EndMinute` = ?,  "
                        + "`EndSecond` = ?,  `EndMillisecond` = ?, `Private` = ?, `Keywords` = ? WHERE (`TimelineID` = ?)");
        out.setInt(1, scale);
        out.setString(2, timelineName);
        out.setString(3, timelineDescription);
        out.setString(4, theme);
        out.setInt(5, startDate.getYear());
        out.setInt(6, startDate.getMonth());
        out.setInt(7, startDate.getDay());
        out.setInt(8, startDate.getHour());
        out.setInt(9, startDate.getMinute());
        out.setInt(10, startDate.getSecond());
        out.setInt(11, startDate.getMillisecond());
        out.setInt(12, endDate.getYear());
        out.setInt(13, endDate.getMonth());
        out.setInt(14, endDate.getDay());
        out.setInt(15, endDate.getHour());
        out.setInt(16, endDate.getMinute());
        out.setInt(17, endDate.getSecond());
        out.setInt(18, endDate.getMillisecond());
        out.setBoolean(19, isPrivate);
        // keyword string generation from list
        StringBuilder sb = new StringBuilder();
        for (String s : keywords) {
            sb.append(s);
            sb.append(",");
        }
        out.setString(20, sb.toString());
        out.setInt(21, timelineID);
        return out;
    }


    public void deleteOrphans() throws SQLException {
        PreparedStatement out = DBM.conn.prepareStatement("SELECT e.* FROM `timelines` t " +
                "LEFT JOIN timelineevents te " +
                "ON t.TimelineID = te.TimelineID " +        	//destroys orphaned events (i.e. events where there are no
                "LEFT JOIN events e " +                        	//junction table records for them with a different TimelineID
                "ON te.EventID = e.EventID AND e.EventID NOT IN (SELECT EventID FROM timelineevents WHERE TimelineID != ?) " +
                "WHERE t.TimelineID = ? ");

        out.setInt(1, timelineID);
        out.setInt(2, timelineID);

        DBM.deleteFromDB(DBM.getFromDB(out, new Event()));

    }

    @Override
    public PreparedStatement getDeleteQuery() throws SQLException {
        PreparedStatement out = DBM.conn.prepareStatement("DELETE FROM `timelines` WHERE (`TimelineID` = ?)");
        out.setInt(1, timelineID);
        return out;
    }



    @Override
    public Timeline createFromDB(ResultSet rs) throws SQLException {
        int TimelineID = rs.getInt("TimelineID");
        int Scale = rs.getInt("Scale");
        String TimelineName = rs.getString("TimelineName");
        String TimelineDesription = rs.getString("TimelineDescription");
        String Theme = rs.getString("Theme");
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
        int TimelineOwner = rs.getInt("TimelineOwner");
        boolean isPrivate = rs.getBoolean("Private");
        String keywordString = rs.getString("Keywords");

        // keyword list generation from comma string
        List<String> keywords = new ArrayList<String>();
        String[] words = keywordString.split(",");
        for (String s : words) {
            keywords.add(s);
        }
        List<Event> eventList;
        try (PreparedStatement stmt = DBM.conn.prepareStatement("SELECT e.* FROM events e " +
                "INNER JOIN timelineevents t " +
                "ON e.EventID = t.EventID " +
                "WHERE t.TimelineID = ?")) {
            stmt.setInt(1, TimelineID);
            eventList = DBM.getFromDB(stmt, new Event());
        }
        return new Timeline(TimelineID, TimelineName, TimelineDesription, Scale, Theme,
                new Date(StartYear, StartMonth, StartDay, StartHour, StartMinute, StartSecond, StartMillisecond),
                new Date(EndYear, EndMonth, EndDay, EndHour, EndMinute, EndSecond, EndMillisecond),
                new Date(CreatedYear, CreatedMonth, CreatedDay, CreatedHour, CreatedMinute, CreatedSecond,
                        CreatedMillisecond),
                TimelineOwner, isPrivate, keywords, eventList);
    }

    @Override
    public String toString() {
        return "Name: " + timelineName + " Description: " + timelineDescription;
    }

    // This method will set the name of the timeline if this user has not timeline
    // with the same name already in the DB
    public void setTimelineName(String name, int userID) throws SQLException, IllegalArgumentException {
        if (validName(name, userID)) // uses this private method for validation
            this.timelineName = name;
        else
            throw new IllegalArgumentException(
                    "This user has already a timeline with this name, choose another name or remove the former timeline");
    }
    // This method takes the new timeline name and the userID that is creating the
    // line and checks if the name is already in the DB, in relation with this user

    private boolean validName(String name, int user) throws SQLException {
        PreparedStatement stmt = DBM.conn.prepareStatement("SELECT * FROM timelines WHERE TimelineOwner = ?");
        stmt.setInt(1, user);
        List<String> timelineNameList = DBM.getFromDB(stmt, rs -> rs.getString("TimelineName"));
        // Then check if the new timeline name equals to any of the ones gotten from the
        // DB
        for (int i = 0; i < timelineNameList.size(); i++) {
            if (name.equals(timelineNameList.get(i)))
                return false;// this user has this name already as a timeline name in the DB
        }
        // If not found in the DB its good and returns true
        return true;
    }

    // Sets all the info from the column unit from scale_lookup and set it to an array to be read in TimelineEditor GUI for
    // the timeInput combobox
    public ArrayList<String> scales() throws SQLException{
        try {
            PreparedStatement state = DBM.conn.prepareStatement("SELECT unit FROM scale_lookup");
            ResultSet rs = state.executeQuery();
            ArrayList<String> array = new ArrayList<String>();
            while(rs.next()){
                array.add(rs.getString("unit"));
            }
            return array;
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return null;
    }

    // Getters
    public int getTimelineID() {
        return this.timelineID;
    }

    public String getName() {
        return this.timelineName;
    }

    public int getScale() {
        return this.scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public String getTimelineDescription() {
        return this.timelineDescription;
    }

    public void setTimelineDescription(String description) {
        this.timelineDescription = description;
    }

    public String getTimelineName() {
        return this.timelineName;
    }

    public void setTimelineName(String name) {
        this.timelineName = name;
    }

    public String getTheme() {
        return this.theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
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

    public Date getDateCreated() {
        return this.dateCreated;
    }

    public int getTimelineOwner() {
        return this.timelineOwner;
    }

    public void setTimelineOwner(int TimelineOwner) {
        this.timelineOwner = TimelineOwner;
    }

    public boolean getPrivate() {
        return this.isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    // Setters
    @Override
    public void setID(int id) {
        this.timelineID = id;
    }

    public int getTimelineOwnerID() {
        return timelineOwner;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public boolean equals(Timeline other) {
        if (this.timelineID == 0)
            return false;
        return this.timelineID == other.timelineID;

    }
}
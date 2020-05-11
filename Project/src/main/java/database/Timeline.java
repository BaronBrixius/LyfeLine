package database;

import controllers.GUIManager;
import utils.Date;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Timeline extends TimelineObject<Timeline> {
    private int timelineID;
    private int scale;
    private String timelineName = "";
    private String theme;
    private String timelineDescription = "";
    private List<Event> eventList = new ArrayList<>();
    private List<String> keywords = new ArrayList<>();
    private List<Integer> ratingsList = new ArrayList<>();
    private double rating;

    public Timeline() {
    }

    //Do we need this? We mostly create blank timelines and then use setters called from GUI fields for new timelines
    public Timeline(String timelineName, String timelineDescription, int scale, String theme, Date startDate,
                    Date endDate, List<String> keywords) {
        this(0, timelineName, timelineDescription, scale, theme, startDate, endDate, null, 0, keywords, null);
    }

    private Timeline(int timelineID, String timelineName, String timelineDescription, int scale, String theme,
                     Date startDate, Date endDate, Date dateCreated, int timelineOwner, List<String> keywords, List<Event> eventList) {
        this.timelineID = timelineID;
        this.timelineName = timelineName;
        this.scale = scale;
        this.timelineDescription = timelineDescription;
        this.theme = theme;
        this.startDate = startDate;
        this.endDate = endDate;
        this.creationDate = dateCreated;
        this.ownerID = timelineOwner;
        this.keywords = keywords;
        this.eventList = eventList;
        try {
            this.rating = calcRating();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public PreparedStatement getInsertQuery() throws SQLException {
        if (timelineID > 0)
            throw new SQLIntegrityConstraintViolationException("TimelineID is already in DB.");

        PreparedStatement out = DBM.conn.prepareStatement(
                "INSERT INTO `timelines` ( `Scale`,`TimelineName`, `TimelineDescription`, `Theme`,`StartYear`,`StartMonth`,`StartDay`,`StartHour`"
                        + ",`StartMinute`,`StartSecond`,`StartMillisecond`,`EndYear`,`EndMonth`,`EndDay`,`EndHour`,`EndMinute`,`EndSecond`,"
                        + "`EndMillisecond`,`TimelineOwner`,`Keywords`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
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
        out.setInt(19, ownerID);
        // keyword string generation from list
        StringBuilder sb = new StringBuilder();
        for (String s : keywords) {
            sb.append(s);
            sb.append(",");
        }
        out.setString(20, sb.toString());
        return out;
    }

    @Override
    public PreparedStatement getUpdateQuery() throws SQLException {
        PreparedStatement out = DBM.conn.prepareStatement(
                "UPDATE `timelines` SET `Scale` = ?, `TimelineName` = ?, `TimelineDescription` = ?,  `Theme` = ?,   "
                        + "`StartYear` = ?,  `StartMonth` = ?,  `StartDay` = ?,  `StartHour` = ?,  `StartMinute` = ?,  `StartSecond` = ?,  "
                        + "`StartMillisecond` = ?,    `EndYear` = ?,  `EndMonth` = ?,  `EndDay` = ?,  `EndHour` = ?,  `EndMinute` = ?,  "
                        + "`EndSecond` = ?,  `EndMillisecond` = ?, `Keywords` = ? WHERE (`TimelineID` = ?)");
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
        // keyword string generation from list
        StringBuilder sb = new StringBuilder();
        for (String s : keywords) {
            sb.append(s);
            sb.append(",");
        }
        out.setString(19, sb.toString());
        out.setInt(20, timelineID);
        return out;
    }

    public void deleteOrphans() throws SQLException {
        PreparedStatement out = DBM.conn.prepareStatement("SELECT e.* FROM `timelines` t " +
                "LEFT JOIN timelineevents te " +
                "ON t.TimelineID = te.TimelineID " +            //destroys orphaned events (i.e. events where there are no
                "LEFT JOIN events e " +                            //junction table records for them with a different TimelineID
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
        int timelineID = rs.getInt("TimelineID");
        int scale = rs.getInt("Scale");
        String timelineName = rs.getString("TimelineName");
        String timelineDescription = rs.getString("TimelineDescription");
        String theme = rs.getString("Theme");
        int startYear = rs.getInt("StartYear");
        int startMonth = rs.getInt("StartMonth");
        int startDay = rs.getInt("StartDay");
        int startHour = rs.getInt("StartHour");
        int startMinute = rs.getInt("StartMinute");
        int startSecond = rs.getInt("StartSecond");
        int startMillisecond = rs.getInt("StartMillisecond");
        int endYear = rs.getInt("EndYear");
        int endMonth = rs.getInt("EndMonth");
        int endDay = rs.getInt("EndDay");
        int endHour = rs.getInt("EndHour");
        int endMinute = rs.getInt("EndMinute");
        int endSecond = rs.getInt("EndSecond");
        int endMillisecond = rs.getInt("EndMillisecond");
        int createdYear = rs.getInt("CreatedYear");
        int createdMonth = rs.getInt("CreatedMonth");
        int createdDay = rs.getInt("CreatedDay");
        int createdHour = rs.getInt("CreatedHour");
        int createdMinute = rs.getInt("CreatedMinute");
        int createdSecond = rs.getInt("CreatedSecond");
        int createdMillisecond = rs.getInt("CreatedMillisecond");
        int timelineOwner = rs.getInt("TimelineOwner");
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
            stmt.setInt(1, timelineID);
            eventList = DBM.getFromDB(stmt, new Event());
        }
        return new Timeline(timelineID, timelineName, timelineDescription, scale, theme,
                new Date(startYear, startMonth, startDay, startHour, startMinute, startSecond, startMillisecond),
                new Date(endYear, endMonth, endDay, endHour, endMinute, endSecond, endMillisecond),
                new Date(createdYear, createdMonth, createdDay, createdHour, createdMinute, createdSecond,
                        createdMillisecond),
                timelineOwner, keywords, eventList);
    }


    public PreparedStatement addRating(int rating, int userId) throws SQLException {
        PreparedStatement out = DBM.conn.prepareStatement("INSERT INTO rating (`rating`, `userId`, `timeLineID`) VALUES (?, ?, ?)");
        out.setInt(1, rating);
        out.setInt(2, userId);
        out.setInt(3, this.timelineID);
        out.execute();
        return out;
    }

    public PreparedStatement updateRating(int rating, int userId) throws SQLException {

        PreparedStatement out = DBM.conn.prepareStatement("UPDATE rating SET `rating` = ?, `timeLineID` = ? WHERE (`userId` = ?)");
        out.setInt(1, rating);
        out.setInt(2, this.timelineID);
        out.setInt(3, userId);
        out.execute();
        return out;
    }

    public boolean checkRating() throws SQLException {
        PreparedStatement rate = DBM.conn.prepareStatement("SELECT COUNT(*) FROM rating WHERE userId = ? AND timeLineID = ? ");
        rate.setInt(1, GUIManager.loggedInUser.getUserID());
        rate.setInt(2, this.getID());
        ResultSet rs = rate.executeQuery();
        rs.next();
        return rs.getInt(1)>0;
    }

    private double calcRating() throws SQLException {
        PreparedStatement rate = DBM.conn.prepareStatement("SELECT AVG(rating) FROM rating WHERE timeLineID = ?");
        rate.setInt(1, this.getID());
        ResultSet rs = rate.executeQuery();
        rs.next();
        return rs.getDouble(1);
    }

    public void rateTimeline(int index) {
        try {
            if (checkRating()) {
                updateRating(index, GUIManager.loggedInUser.getUserID());
            } else {
                addRating(index, GUIManager.loggedInUser.getUserID());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public double getRating() throws SQLException
    { return rating; }

    @Override
    public String toString() {
        return "Name: " + timelineName + " Description: " + timelineDescription;
    }

    // This method will set the name of the timeline if this user has not timeline
    // with the same name already in the DB
    public void setName(String name, int userID) throws SQLException, IllegalArgumentException {
        if (validName(name, userID)) // uses this private method for validation
            this.timelineName = name;
        else
            throw new IllegalArgumentException(
                    "This user has already a timeline with this name, choose another name or remove the former timeline");
    }
    // This method takes the new timeline name and the userID that is creating the
    // line and checks if the name is already in the DB, in relation with this user

    boolean validName(String name, int user) throws SQLException {
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


    // Getters
    public int getID() {
        return this.timelineID;
    }

    public int getScale() {
        return this.scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public String getDescription() {
        return this.timelineDescription;
    }

    public void setDescription(String description) {
        this.timelineDescription = description;
    }

    public String getName() {
        return this.timelineName;
    }

    public void setName(String name) {
        this.timelineName = name;
    }

    public String getTheme() {
        return this.theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
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

    public List<Event> getEventList() {
        return eventList;
    }

    public boolean equals(Timeline other) {
        if (this.timelineID == 0)
            return false;
        return this.timelineID == other.timelineID;

    }
}
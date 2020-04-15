import java.util.List;
import java.sql.*;

public class Timeline implements DBObject<Timeline>{

	private int timelineID;
	private String scale;
	private String timelineName;
	private String theme;
	private Date startDate;
	private Date endDate;
	private Date dateCreated;
	private String timelineDescription;
	private int timelineOwner;
	private boolean isPrivate=false;
	private List<Event> eventList;

	public Timeline() {
		
	}

	public Timeline(int TimeLineID, String TimelineName, String TimelineDescription, String Scale, String Theme, Date StartDate, Date Enddate, Date DateCreated, int TimelineOwner, boolean Private) {

		this.timelineID=TimeLineID;
		this.timelineName=TimelineName;
		this.scale=Scale;
		this.timelineDescription=TimelineDescription;
		this.theme=Theme;
		this.startDate=StartDate;
		this.endDate=Enddate;
		this.dateCreated=DateCreated;
		this.timelineOwner=TimelineOwner;
		this.isPrivate=Private;
	}



	@Override
	public PreparedStatement getInsertQuery() throws SQLException {
		if (timelineID > 0)
			throw new SQLIntegrityConstraintViolationException("TimelineID is already in DB.");

		PreparedStatement out = DBM.conn.prepareStatement("INSERT INTO `timelines` ( `Scale`,`TimelineName`, `TimelineDescription`, `Theme`,`StartYear`,`StartMonth`,`StartDay`,`StartHour`"
				+ ",`StartMinute`,`StartSecond`,`StartMillisecond`,`EndYear`,`EndMonth`,`EndDay`,`EndHour`,`EndMinute`,`EndSecond`,"
				+ "`EndMillisecond`,`CreatedYear`,`CreatedMonth`,`CreatedDay`,`CreatedHour`,`CreatedMinute`,`CreatedSecond`,`CreatedMillisecond`,"
				+ "`Private`,`TimelineOwner`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
		out.setString(1, scale);
		out.setString(2, timelineName);
		out.setString(3, timelineDescription);
		out.setString(4, theme);
		out.setInt(5, startDate.getYear());
		out.setInt(6, startDate.getMonth());
		out.setInt(7, startDate.getDay());
		out.setInt(8, startDate.getHours());
		out.setInt(9, startDate.getMinutes());
		out.setInt(10, startDate.getSeconds());
		out.setInt(11, startDate.getMilliseconds());
		out.setInt(12, endDate.getYear());
		out.setInt(13, endDate.getMonth());
		out.setInt(14, endDate.getDay());
		out.setInt(15, endDate.getHours());
		out.setInt(16, endDate.getMinutes());
		out.setInt(17, endDate.getSeconds());
		out.setInt(18, endDate.getMilliseconds());
		out.setInt(19, dateCreated.getYear());
		out.setInt(20, dateCreated.getMonth());
		out.setInt(21, dateCreated.getDay());
		out.setInt(22, dateCreated.getHours());
		out.setInt(23, dateCreated.getMinutes());
		out.setInt(24, dateCreated.getSeconds());
		out.setInt(25, dateCreated.getMilliseconds());
		out.setBoolean(26, isPrivate);
		out.setInt(27, timelineOwner);
		return out;
	}

	@Override
	public PreparedStatement getUpdateQuery() throws SQLException {
		PreparedStatement out = DBM.conn.prepareStatement("UPDATE `timelines` SET `Scale` = ?, `TimelineName` = ?, `TimelineDescription` = ?,  `Theme` = ?,   `StartYear` = ?,  `StartMonth` = ?,  `StartDay` = ?,  `StartHour` = ?,  `StartMinute` = ?,  `StartSecond` = ?,  `StartMillisecond` = ?,    `EndYear` = ?,  `EndMonth` = ?,  `EndDay` = ?,  `EndHour` = ?,  `EndMinute` = ?,  `EndSecond` = ?,  `EndMillisecond` = ?,   `CreatedYear` = ?,  `ECreatedMonth` = ?,  `CreatedDay` = ?,  `CreatedHour` = ?,  `CreatedMinute` = ?,  `CreatedSecond` = ?,  `CreatedMillisecond` = ?, `Private` = ? WHERE (`TimelineID` = ?)");
		out.setString(1, scale);
		out.setString(2, timelineName);
		out.setString(3, timelineDescription);
		out.setString(4, theme);
		out.setInt(5, startDate.getYear());
		out.setInt(6, startDate.getMonth());
		out.setInt(7, startDate.getDay());
		out.setInt(8, startDate.getHours());
		out.setInt(9, startDate.getMinutes());
		out.setInt(10, startDate.getSeconds());
		out.setInt(11, startDate.getMilliseconds());
		out.setInt(12, endDate.getYear());
		out.setInt(13, endDate.getMonth());
		out.setInt(14, endDate.getDay());
		out.setInt(15, endDate.getHours());
		out.setInt(16, endDate.getMinutes());
		out.setInt(17, endDate.getSeconds());
		out.setInt(18, endDate.getMilliseconds());
		out.setInt(19, dateCreated.getYear());
		out.setInt(20, dateCreated.getMonth());
		out.setInt(21, dateCreated.getDay());
		out.setInt(22, dateCreated.getHours());
		out.setInt(23, dateCreated.getMinutes());
		out.setInt(24, dateCreated.getSeconds());
		out.setInt(25, dateCreated.getMilliseconds());
		out.setBoolean(26, isPrivate);
		return out;
	}

	@Override
	public PreparedStatement getDeleteQuery() throws SQLException {
		PreparedStatement out = DBM.conn.prepareStatement("DELETE FROM `timelines` WHERE (`TimelineID` = ?)");
		out.setInt(1, timelineID);
		return out;
	}

	@Override
	public Timeline createFromDB(ResultSet rs) throws SQLException {
		int TimelineID=rs.getInt("TimelineID");
		String Scale = rs.getString("Scale");
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
				
		return new Timeline(TimelineID,TimelineName,TimelineDesription,Scale,Theme,
				new Date(StartYear,StartMonth,StartDay,StartHour,StartMinute,StartSecond,StartMillisecond),
				new Date(EndYear,EndMonth,EndDay,EndHour,EndMinute,EndSecond,EndMillisecond),
				new Date(CreatedYear,CreatedMonth,CreatedDay,CreatedHour,CreatedMinute,CreatedSecond,CreatedMillisecond),
				TimelineOwner,isPrivate);
	}
	@Override
	public String toString() {
		return "Time line ID: " + timelineID + " Time line Name: " + timelineName + " Time line Description: " + timelineDescription + " Private:" + isPrivate+" Scale:"+scale+" Theme: "+theme+" Start Date: "+startDate+" End Date: "+endDate+" Created: "+dateCreated+" Owner: "+timelineOwner;
	}
		
	@Override
	public void setID(int id) {
	this.timelineID=id;
	}
	
	public String getName() {
		return this.timelineName;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	@Override
	public PreparedStatement getInsertQuery() throws SQLException {
		return null;
	}

	@Override
	public PreparedStatement getUpdateQuery() throws SQLException {
		return null;
	}

	@Override
	public PreparedStatement getDeleteQuery() throws SQLException {
		return null;
	}

	@Override
	public void setID(int id) {

	}

	@Override
	public Timeline createFromDB(ResultSet rs) throws SQLException {
		return null;
	}
}

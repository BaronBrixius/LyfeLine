
import java.sql.*;
import java.util.List;

public class Timeline implements DBObject<Timeline>{

	private String name;
	private int date;
	private int TimelineID;
	private String Scale;
	private String TimelineName;
	private String Theme;
	private Date StartDate;
	private Date Enddate;
	private Date DateCreated;
	private String TimelineDescription;
	private int TimelineOwner;
	private boolean Private=false;
	private List<Event> eventList;


	public Timeline(String name, int date, int TimeLineID, String TimelineName, String TimelineDescription, String Scale, String Theme, Date StartDate, Date Enddate, Date DateCreated, int TimelineOwner, boolean Private) {
		this.name = name;
		this.date = date;
		this.TimelineID=TimeLineID;
		this.TimelineName=TimelineName;
		this.Scale=Scale;
		this.TimelineDescription=TimelineDescription;
		this.Theme=Theme;
		this.StartDate=StartDate;
		this.Enddate=Enddate;
		this.DateCreated=DateCreated;
		this.TimelineOwner=TimelineOwner;
		this.Private=Private;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Time line ID: " + TimelineID + " Time line Name: " + TimelineName + " Time line Description: " + TimelineDescription + " Private:" + Private+Scale+Theme+StartDate+Enddate+DateCreated+TimelineOwner;
	}

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	@Override
	public PreparedStatement getInsertQuery() throws SQLException {
		if (TimelineID > 0)
			throw new SQLIntegrityConstraintViolationException("TimelineID is already in DB.");

		PreparedStatement out = DBM.conn.prepareStatement("INSERT INTO `timelines` ( `Scale`,`TimelineName`, `TimelineDescription`, `Theme`,`StartDate`,`Enddate`,`DateCreated`,`Private`,`TimelineOwner`) VALUES (?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
		out.setString(1, Scale);
		out.setString(2, TimelineName);
		out.setString(3, TimelineDescription);
		out.setString(4, Theme);
		out.setDate(5, (Date) StartDate);
		out.setDate(6, (Date) Enddate);
		out.setDate(7, (Date) DateCreated);
		out.setBoolean(8, Private);
		return out;
	}

	@Override
	public PreparedStatement getUpdateQuery() throws SQLException {
		PreparedStatement out = DBM.conn.prepareStatement("UPDATE `timelines` SET `Scale` = ?, `TimelineName` = ?,  `TimelineDescription` = ?,  `Theme` = ?,  `StartDate` = ?,  `Enddate` = ?,  `DateCreated` = ?,  `Private` = ? WHERE (`TimelineID` = ?)");
		out.setString(1, Scale);
		out.setString(2, TimelineName);
		out.setString(3, TimelineDescription);
		out.setString(4, Theme);
		out.setDate(5, (Date) StartDate);
		out.setDate(6, (Date) Enddate);
		out.setDate(7, (Date) DateCreated);
		out.setBoolean(8, Private);
		return out;
	}

	@Override
	public PreparedStatement getDeleteQuery() throws SQLException {
		PreparedStatement out = DBM.conn.prepareStatement("DELETE FROM `timelines` WHERE (`TimelineID` = ?)");
		out.setInt(1, TimelineID);
		return out;
	}

	@Override
	public void setID(int id) {
	this.TimelineID=id;
	}

	@Override
	public Timeline createFromDB(ResultSet rs) throws SQLException {
		int TimelineID=rs.getInt("timelineID");
		String Scale = rs.getString("Scale");
		boolean Private = rs.getBoolean("Private");
		String TimelineName = rs.getString("TimelineName");
		String TimelineDesription = rs.getString("TimelineDesription");
		String Theme = rs.getString("Theme");
		return new Timeline(name,date,TimelineID,TimelineName, Scale, TimelineDesription, Theme,rs.getDate("`StartDate"),rs.getDate("Enddate"),rs.getDate("DateCreated"),rs.getInt("TimelineOwner"),Private);

	}
}

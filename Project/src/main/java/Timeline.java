import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Timeline implements DBObject<Timeline>{
	private String name;
	private int date;
	private List<Event> eventList;

	public Timeline(String name, int date) {
		this.name = name;
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
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

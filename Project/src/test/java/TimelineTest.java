import database.DBM;
import database.Timeline;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimelineTest {
	static private DBM sut;

	static Timeline[] timelines = new Timeline[4];
	static Timeline[] timeline = new Timeline[4];

	@BeforeAll
	static void init() throws SQLException, IOException, ClassNotFoundException {
		sut = new DBM("jdbc:mysql://localhost", "root", "AJnuHA^8VKHht=uB", "project");
		DBM.setupSchema();
		createTestDB(); // Adds some rows to the database tables and exports them to .xml, don't need to
						// run this often
	}

	static void createTestDB() throws SQLException {
										//(int TimeLineID, String TimelineName, String TimelineDescription, String Scale, String Theme, Date StartDate, Date Enddate, Date DateCreated, int TimelineOwner, boolean Private)
		Timeline timeline1 = new Timeline();
		timelines[0] = timeline1;
		DBM.insertIntoDB(timeline1);
		Timeline timeline2 = new Timeline();
		timelines[1] = timeline2;
		DBM.insertIntoDB(timeline2);
		Timeline timeline3 = new Timeline();
		timelines[2] = timeline3;
		DBM.insertIntoDB(timeline3);	
		Timeline timeline4 = new Timeline();
		timelines[3] = timeline4;
		DBM.insertIntoDB(timeline4);
	}

	@AfterAll
	static void tearDown() throws SQLException {
		DBM.conn.createStatement().execute("DROP DATABASE IF EXISTS test");
		DBM.conn.close();
	}
}
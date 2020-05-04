package database;

import database.DBM;
import database.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import utils.Date;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.Start;

import controllers.Dashboard;
import controllers.GUIManager;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class TimelineTest {
	private final String SCHEMA = "test";
    Dashboard sut;

	static Timeline[] timelines = new Timeline[4];
	static Timeline[] timeline = new Timeline[4];

	@Start
	public void init() throws Exception {
		// sut = new DBM("jdbc:mysql://localhost", "root", "AJnuHA^8VKHht=uB",
		// "project");
		GUIManager.loggedInUser = new User();
		GUIManager.loggedInUser.setID(-1);
		new DBM(SCHEMA);
		DBM.setupSchema();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../../classes/FXML/Dashboard.fxml"));
        //stage.setScene(new Scene(loader.load(), 300, 300));
        sut = loader.getController();
        //stage.show();
		// createTestDB(); // Adds some rows to the database tables and exports them to
		// .xml, don't need to
		// run this often
		
		//setting up array of a few test timelines
		/*
		Date TL1Start =new Date();
		List<String> TL1KeyWords = new ArrayList<String>();TL1KeyWords.add("Keyword1");TL1KeyWords.add("Keyword2");
		Timeline testTimeline1 = new Timeline(1,"Normal Name","Normal Description, normal length.",3,"dark",TL1Start,TL1Start,TL1Start,-1,TL1KeyWords,null);
		Timeline testTimeline2 = new Timeline();
		Timeline testTimeline3 = new Timeline();
		Timeline testTimeline4 = new Timeline();
		timelines[0] = testTimeline1;
		timelines[1] = testTimeline2;
		timelines[2] = testTimeline3;
		timelines[3] = testTimeline4;
		*/
	}

	/*
	@AfterAll
	static void tearDown() throws SQLException {
		DBM.conn.createStatement().execute("DROP DATABASE IF EXISTS test");
		DBM.conn.close();
	}
	*/
	
	// int TimeLineID, String TimelineName, String TimelineDescription, int Scale,
	// String Theme,
	// Date StartDate, Date Enddate, Date DateCreated, int TimelineOwner,
	// List<String> keywords, List<Event> eventList

	void getInsertQueryTest() {

	}

	void getUpdateQueryTest() {

	}

	void deleteOrphansTest() {

	}

	void getDeleteQueryTest() {

	}

	void createFromDBTest() {

	}

	@Test
	void toStringTest() {
		// "Name: " + timelineName + " Description: " + timelineDescription
		// normal case
		Timeline testTimeline1 = new Timeline();
		String testName1 = "TestingName";
		testTimeline1.setName(testName1);
		String testDesc1 = "This is a test description, with small amount of chars";
		testTimeline1.setDescription(testDesc1);
		assertEquals(testTimeline1.getName(), testName1);
		assertEquals(testTimeline1.getDescription(), testDesc1);
		
		
		// small case
		Timeline testTimeline2 = new Timeline();
		String testName2 = "";
		testTimeline2.setName(testName2);
		String testDesc2 = "";
		testTimeline2.setDescription(testDesc2);
		assertEquals(testTimeline2.getName(), testName2);
		assertEquals(testTimeline2.getDescription(), testDesc2);

	}

	void setTimelineNameTest() {

	}

	void validNameTest() {

	}

	void getTimelineIDTest() {

	}

	void getNameTest() {

	}

}
package controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;

import database.DBM;
import database.Timeline;
import database.User;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

//focusing on rating testing for now, probably lacking a ton of testing

class TimelineCellTest {
	static private int testCount = 0;
	Dashboard dash;
	TimelineCell tc;
	String StyleSheetName = "None";
	FxRobot robot = new FxRobot();
	int loginUserID;

	@BeforeAll
	public static void beforeAll() {
		try {
			new DBM("test");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Start
	public void start(Stage stage) throws Exception {
		System.out.println("===========================================================================");
		// Makes each test easier to distinguish in console view
		System.out.println("Test " + ++testCount);
		DBM.setupSchema();

		try {
			PreparedStatement stat = DBM.conn.prepareStatement("SELECT * FROM Users WHERE UserID=?");
			stat.setInt(1, 14);
			GUIManager.loggedInUser = DBM.getFromDB(stat, new User()).get(0);
			loginUserID = GUIManager.loggedInUser.getUserID();
		} catch (SQLException e) {
			System.out.println("Could not get test user from database");
		}

		FXMLLoader loader = new FXMLLoader(getClass().getResource("../../classes/FXML/Dashboard.fxml"));
		stage.setScene(new Scene(loader.load()));
		stage.getScene().getStylesheets().add("File:src/main/resources/styles/" + StyleSheetName + ".css");
		dash = loader.getController();
		GUIManager.mainStage = stage;
		stage.show();
	}
	
	   @BeforeEach
	    void setUp() {
	        try {
	            DBM.setupSchema();
	        } catch (SQLException | FileNotFoundException e) {e.printStackTrace();}
	        System.out.println("Test " + ++testCount);
	    }

	@AfterAll
	static void end() throws SQLException {
		DBM.conn.createStatement().execute("DROP DATABASE IF EXISTS test");
		DBM.conn.close();
	}
	
	@Test 
	void changeIfHoverTest() {
		
	}

	@Test
	void ratingInDBTest() {
		GUIManager.main = new BorderPane(); // Avoids a null pointer?
		ArrayList<Timeline> timelinesList = new ArrayList<>(dash.list.getItems());
		for (Timeline t : timelinesList)
            System.out.println(t.getName());
        int listSize = timelinesList.size();
        for(int i = 0 ; i < listSize - 1; i++) {
        	dash.list.getSelectionModel().select(i);
    		Timeline timelineSelected = dash.list.getSelectionModel().getSelectedItem();
     		double actual = timelineSelected.getRating();
    		double expected = 0;
    		assertEquals(actual,expected);
        }
	}

	@Test
	void initializeTest() {
		// do we need this?
	}

	@Test
	void setupRatingButtonTest() {
		// do we need this?
	}

	@Test
	void colorStarsByRatingTest() {

	}

	@Test
	void updateTest() {

	}

	@Test
	void getTimelineTest() throws InterruptedException {
		// Throws null pointer for anything with timelinecells, need to select one?
		/*
		GUIManager.main = new BorderPane(); // Avoids a null pointer?
		TimelineCell testCell = new TimelineCell();
		dash.list.getSelectionModel().select(0);
		Timeline timelineSelected = dash.list.getSelectionModel().getSelectedItem();
		// Timeline TestTimeline = new Timeline();
		testCell.setTimeline(timelineSelected, 25); // Random Width for now
		GUIManager.main = new BorderPane(); // Avoids a null pointer
		// Select the first timeline in the list
		dash.list.getSelectionModel().select(0);
		String actual = testCell.timeline.getName();
		String expected = testCell.getTimeline().getName();
		assertEquals(actual, expected);
		*/

		//dash.list.getSelectionModel().select(0);
		//waitForRunLater();
        ArrayList<Timeline> timelinesList = new ArrayList<>(dash.list.getItems());
        int listSize = timelinesList.size();
        for(int i = 0 ; i < listSize; i++) {
        	dash.list.getSelectionModel().select(i);
    		Timeline timelineSelected = dash.list.getSelectionModel().getSelectedItem();    		
        }

		Timeline timelineSelected = dash.list.getSelectionModel().getSelectedItem();
		tc.setTimeline(timelineSelected, 25); // Random Width for now
		String actual = tc.timeline.getName();
		String expected = tc.getTimeline().getName();
		assertEquals(actual, expected);
	}

	@Test
	void setTimelineTest() {

	}

	@Test
	void setBGImageTest() {

	}

	/* ********** Helper Methods ****************/
	// Helper methods to make changing GUI elements possible
	void changeSortBy(int selection) throws InterruptedException {
		Platform.runLater(() -> dash.sortBy.getSelectionModel().clearAndSelect(selection));
		waitForRunLater();
	}

	void waitForRunLater() throws InterruptedException {
		Semaphore semaphore = new Semaphore(0);
		Platform.runLater(semaphore::release);
		semaphore.acquire();
	}

	// Helper methods to change who is logged in
	static void setAdminLoggedIn(boolean admin) {
		GUIManager.loggedInUser.setAdmin(admin);
	}

	void reinitializeDashboard() throws InterruptedException {
		Platform.runLater(() -> dash.initialize());
		waitForRunLater();
	}

}

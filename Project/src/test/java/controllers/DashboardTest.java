package controllers;

import database.DBM;
import database.Timeline;
import database.User;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ExtendWith(ApplicationExtension.class)
public class DashboardTest {
    static private int testCount = 0;
    private final String SCHEMA = "test";
    Dashboard sut;

    @Start
    public void start(Stage stage) throws Exception {
        System.out.println("===========================================================================");  //Makes each test easier to distinguish in console view
        System.out.println("Test " + ++testCount);
        GUIManager.loggedInUser = new User();
        GUIManager.loggedInUser.setID(-1);
        new DBM(SCHEMA);
        DBM.setupSchema();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../classes/FXML/Dashboard.fxml"));
        stage.setScene(new Scene(loader.load(), 300, 300));
        sut = loader.getController();
        stage.show();
    }

    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() {
    }

    @AfterAll
    static void end() throws SQLException {
        DBM.conn.createStatement().execute("DROP DATABASE IF EXISTS test");
        DBM.conn.close();
    }


    @Test
    void testSortTimelinesAlphabetically() throws InterruptedException {
        Timeline higherTimelineOnList;
        Timeline lowerTimelineOnList;

        changeSortBy(0);    //Select sort alphabetically
        ArrayList<Timeline> timelinesList = new ArrayList<>(sut.list.getItems());

        for (int i = 0; i < timelinesList.size() - 1; i++) {  //For each timeline on the list except the last one,
            higherTimelineOnList = timelinesList.get(i);
            lowerTimelineOnList = timelinesList.get(i + 1);

            assertTrue(higherTimelineOnList.getName().compareTo(lowerTimelineOnList.getName()) <= 0);   //assert that the one below it comes after alphabetically by name, or is the same
        }

        for (Timeline t : timelinesList)
            System.out.println(t.getTimelineName());
    }

    @Test
    void testSortTimelinesAlphabeticallyAfterAddingNewTimelines() throws InterruptedException {
        Timeline higherTimelineOnList;
        Timeline lowerTimelineOnList;

        int intitialListSize = sut.list.getItems().size();

        addNewTimelineToDB("");
        addNewTimelineToDB("abcd");
        addNewTimelineToDB("ABCD");
        addNewTimelineToDB("1234");
        addNewTimelineToDB("!@#$");
        addNewTimelineToDB("åöäå§");

        Platform.runLater(() -> sut.initialize());
        waitForRunLater();

        int finalListSize = sut.list.getItems().size();

        changeSortBy(0);    //Select sort alphabetically
        ArrayList<Timeline> timelinesList = new ArrayList<>(sut.list.getItems());

        for (int i = 0; i < timelinesList.size() - 1; i++) {  //For each timeline on the list except the last one,
            higherTimelineOnList = timelinesList.get(i);
            lowerTimelineOnList = timelinesList.get(i + 1);

            assertTrue(higherTimelineOnList.getName().compareToIgnoreCase(lowerTimelineOnList.getName()) <= 0);   //assert that the one below it comes after alphabetically by name, or is the same
        }

        int expected = 6;
        int actual = finalListSize - intitialListSize;
        assertEquals(expected, actual); //Checks to make sure that the Timelines were actually added to the list.

        for (Timeline t : timelinesList)
            System.out.println(t.getTimelineName());
    }

    @Test
    void testSortTimelinesAlphabeticallyAfterRemovingSomeTimelines() throws InterruptedException {
        Timeline higherTimelineOnList;
        Timeline lowerTimelineOnList;

        int intitialListSize = sut.list.getItems().size();

        try {
            DBM.deleteFromDB(sut.list.getItems().get(0));
            DBM.deleteFromDB(sut.list.getItems().get(3));
            DBM.deleteFromDB(sut.list.getItems().get(1));
            DBM.deleteFromDB(sut.list.getItems().get(4));
        }
        catch (SQLException e) {e.printStackTrace();}

        Platform.runLater(() -> sut.initialize());
        waitForRunLater();

        int finalListSize = sut.list.getItems().size();

        changeSortBy(0);    //Select sort alphabetically
        ArrayList<Timeline> timelinesList = new ArrayList<>(sut.list.getItems());

        for (int i = 0; i < timelinesList.size() - 1; i++) {  //For each timeline on the list except the last one,
            higherTimelineOnList = timelinesList.get(i);
            lowerTimelineOnList = timelinesList.get(i + 1);

            assertTrue(higherTimelineOnList.getName().compareTo(lowerTimelineOnList.getName()) <= 0);   //assert that the one below it comes after alphabetically by name, or is the same
        }

        int expected = -4;
        int actual = finalListSize - intitialListSize;
        assertEquals(expected, actual);         //Makes sure that the Timelines were removed

        for (Timeline t : timelinesList)
            System.out.println(t.getTimelineName());
    }

    @Test
    void testSortTimelinesAlphabeticallyAfterRemovingAllTimelines() throws InterruptedException {
        Timeline higherTimelineOnList;
        Timeline lowerTimelineOnList;

        //int intitialListSize = sut.list.getItems().size();

        try {
            for (Timeline t : sut.list.getItems())
                DBM.deleteFromDB(t);
        } catch (SQLException e) {e.printStackTrace();}

        Platform.runLater(() -> sut.initialize());
        waitForRunLater();

        int finalListSize = sut.list.getItems().size();

        changeSortBy(0);    //Select sort alphabetically
        ArrayList<Timeline> timelinesList = new ArrayList<>(sut.list.getItems());

        for (int i = 0; i < timelinesList.size() - 1; i++) {  //For each timeline on the list except the last one,
            higherTimelineOnList = timelinesList.get(i);
            lowerTimelineOnList = timelinesList.get(i + 1);

            assertTrue(higherTimelineOnList.getName().compareTo(lowerTimelineOnList.getName()) <= 0);   //assert that the one below it comes after alphabetically by name, or is the same
        }

        int expected = 0;
        int actual = finalListSize;
        assertEquals(expected, actual);         //Makes sure that the Timelines were removed
    }

    @Test
    void testSortTimelinesAlphabeticallyAfterAddingAndRemovingTimelines() throws InterruptedException {
        Timeline higherTimelineOnList;
        Timeline lowerTimelineOnList;

        int intitialListSize = sut.list.getItems().size();

        addNewTimelineToDB("");
        addNewTimelineToDB("abcd");
        addNewTimelineToDB("ABCD");
        addNewTimelineToDB("1234");
        addNewTimelineToDB("!@#$");
        addNewTimelineToDB("åöäå§");

        try {
            DBM.deleteFromDB(sut.list.getItems().get(0));
            DBM.deleteFromDB(sut.list.getItems().get(3));
            DBM.deleteFromDB(sut.list.getItems().get(1));
            DBM.deleteFromDB(sut.list.getItems().get(4));
        }
        catch (SQLException e) {e.printStackTrace();}

        addNewTimelineToDB("");
        addNewTimelineToDB("1234");
        addNewTimelineToDB("!@#$");
        addNewTimelineToDB("☺☻♥♦♣♠");
        addNewTimelineToDB("ÖÄÅåöäå§");

        try {
            DBM.deleteFromDB(sut.list.getItems().get(5));
            DBM.deleteFromDB(sut.list.getItems().get(6));
        }
        catch (SQLException e) {e.printStackTrace();}

        Platform.runLater(() -> sut.initialize());
        waitForRunLater();

        int finalListSize = sut.list.getItems().size();

        changeSortBy(0);    //Select sort alphabetically
        ArrayList<Timeline> timelinesList = new ArrayList<>(sut.list.getItems());

        for (int i = 0; i < timelinesList.size() - 1; i++) {  //For each timeline on the list except the last one,
            higherTimelineOnList = timelinesList.get(i);
            lowerTimelineOnList = timelinesList.get(i + 1);

            assertTrue(higherTimelineOnList.getName().compareToIgnoreCase(lowerTimelineOnList.getName()) <= 0);   //assert that the one below it comes after alphabetically by name, or is the same
        }

        int expected = 5;
        int actual = finalListSize - intitialListSize;
        assertEquals(expected, actual);         //Makes sure that the Timelines were added and removed

        for (Timeline t : timelinesList)
            System.out.println(t.getTimelineName());
    }

    @Test
    void testSortTimelinesReverseAlphabetically() throws InterruptedException {
        Timeline higherTimelineOnList;
        Timeline lowerTimelineOnList;

        changeSortBy(1);    //Select sort reverse alphabetically
        ArrayList<Timeline> timelinesList = new ArrayList<>(sut.list.getItems());

        for (int i = 0; i < timelinesList.size() - 1; i++) {  //For each timeline on the list except the last one,
            higherTimelineOnList = timelinesList.get(i);
            lowerTimelineOnList = timelinesList.get(i + 1);

            assertTrue(higherTimelineOnList.getName().compareTo(lowerTimelineOnList.getName()) >= 0);   //assert that the one below it comes before alphabetically by name, or is the same
        }

        for (Timeline t : timelinesList)
            System.out.println(t.getTimelineName());
    }

    @Test
    void testSortTimelinesDateCreatedNewestFirst() throws InterruptedException {
        Timeline higherTimelineOnList;
        Timeline lowerTimelineOnList;

        changeSortBy(2);    //Select sort date created, newest first
        ArrayList<Timeline> timelinesList = new ArrayList<>(sut.list.getItems());

        for (int i = 0; i < timelinesList.size() - 1; i++) {  //For each timeline on the list except the last one,
            higherTimelineOnList = timelinesList.get(i);
            lowerTimelineOnList = timelinesList.get(i + 1);

            assertTrue(higherTimelineOnList.getDateCreated().compareTo(lowerTimelineOnList.getDateCreated()) >= 0);   //assert that the one below it was created later, or at the same time
        }

        for (Timeline t : timelinesList)
            System.out.println(t.getTimelineName());
    }

    @Test
    void testSortTimelinesDateCreatedOldestFirst() throws InterruptedException {
        Timeline higherTimelineOnList;
        Timeline lowerTimelineOnList;

        changeSortBy(3);    //Select sort date created, oldest first
        ArrayList<Timeline> timelinesList = new ArrayList<>(sut.list.getItems());

        for (int i = 0; i < timelinesList.size() - 1; i++) {  //For each timeline on the list except the last one,
            higherTimelineOnList = timelinesList.get(i);
            lowerTimelineOnList = timelinesList.get(i + 1);

            assertTrue(higherTimelineOnList.getDateCreated().compareTo(lowerTimelineOnList.getDateCreated()) <= 0);   //assert that the one below it was created sooner, or at the same time
        }

        for (Timeline t : timelinesList)
            System.out.println(t.getTimelineName());
    }

    @Test
    void testOnlyViewPersonalTimelines() throws InterruptedException {
        Timeline newTimeline1 = new Timeline();
        newTimeline1.setTimelineOwner(-1);
        Timeline newTimeline2 = new Timeline();
        newTimeline2.setTimelineOwner(-1);
        Timeline newTimeline3 = new Timeline();
        newTimeline3.setTimelineOwner(-1);

        try {DBM.insertIntoDB(newTimeline1, newTimeline2, newTimeline3);} catch (SQLException e) {e.printStackTrace();}

        Platform.runLater(() -> {
            sut.initialize();
            sut.cbOnlyViewPersonalLines.setSelected(true);
            sut.onlyUserTimelines();
        });
        waitForRunLater();

        ArrayList<Timeline> timelinesList = new ArrayList<>(sut.list.getItems());

        int actual;
        int expected;
        for (Timeline timeline : timelinesList) {
            expected = GUIManager.loggedInUser.getUserID();
            actual = timeline.getTimelineOwnerID();

            assertEquals(expected, actual);
        }

        expected = 3;
        actual = sut.list.getItems().size();

        assertEquals(expected, actual);    //Makes sure that the timelines are on the list
    }

    //Helper methods to make changing GUI elements possible
    void changeSortBy(int selection) throws InterruptedException {
        Platform.runLater(() -> sut.sortBy.getSelectionModel().clearAndSelect(selection));
        waitForRunLater();
    }

    void waitForRunLater() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        semaphore.acquire();
    }

    //Helper methods to change who is logged in
    static void setAdminLoggedIn(boolean admin) {
        GUIManager.loggedInUser.setAdmin(admin);
    }

    //Helper method for making and adding Timelines
    void addNewTimelineToDB(String name) {
        Timeline newTimeline = new Timeline();
        newTimeline.setTimelineName(name);
        try {DBM.insertIntoDB(newTimeline);} catch (SQLException e) {e.printStackTrace();}
    }
}
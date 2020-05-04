package controllers;

import database.DBM;
import database.Timeline;
import database.User;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
public class DashboardTest {
    static private int testCount = 0;
    Dashboard sut;
    String StyleSheetName = "None";
    FxRobot robot = new FxRobot();

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
        System.out.println("===========================================================================");  //Makes each test easier to distinguish in console view
        System.out.println("Test " + ++testCount);
        DBM.setupSchema();

        GUIManager.loggedInUser = new User();
        GUIManager.loggedInUser.setID(-1);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../classes/FXML/Dashboard.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.getScene().getStylesheets().add("File:src/main/resources/styles/"+ StyleSheetName +".css");
        sut = loader.getController();
        GUIManager.mainStage = stage;
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
            System.out.println(t.getName());
    }

    @Test
    void testSortTimelinesAlphabeticallyAfterAddingNewTimelines() throws InterruptedException {
        Timeline higherTimelineOnList;
        Timeline lowerTimelineOnList;

        int intitialListSize = sut.list.getItems().size();

        addNewTimelineToDBByName("", "abcd", "ABCD", "1234", "!@#$", "åöäå§");

        reinitializeDashboard();

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
            System.out.println(t.getName());
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

        reinitializeDashboard();

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
            System.out.println(t.getName());
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

        reinitializeDashboard();

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

        addNewTimelineToDBByName("", "abcd", "ABCD", "1234", "!@#$", "åöäå§");

        try {
            DBM.deleteFromDB(sut.list.getItems().get(0));
            DBM.deleteFromDB(sut.list.getItems().get(3));
            DBM.deleteFromDB(sut.list.getItems().get(1));
            DBM.deleteFromDB(sut.list.getItems().get(4));
        }
        catch (SQLException e) {e.printStackTrace();}

        addNewTimelineToDBByName("", "1234", "!@#$", "☺☻♥♦♣♠", "ÖÄÅåöäå§");

        try {
            DBM.deleteFromDB(sut.list.getItems().get(5));
            DBM.deleteFromDB(sut.list.getItems().get(6));
        }
        catch (SQLException e) {e.printStackTrace();}

        reinitializeDashboard();

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
            System.out.println(t.getName());
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
            System.out.println(t.getName());
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

            assertTrue(higherTimelineOnList.getCreationDate().compareTo(lowerTimelineOnList.getCreationDate()) >= 0);   //assert that the one below it was created later, or at the same time
        }

        for (Timeline t : timelinesList)
            System.out.println(t.getName());
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

            assertTrue(higherTimelineOnList.getCreationDate().compareTo(lowerTimelineOnList.getCreationDate()) <= 0);   //assert that the one below it was created sooner, or at the same time
        }

        for (Timeline t : timelinesList)
            System.out.println(t.getName());
    }

    @Test
    void testOnlyViewPersonalTimelines() throws InterruptedException {
        addNewTimelineToDBByOwnerId(-1, -1, -1);

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
            actual = timeline.getOwnerID();

            assertEquals(expected, actual);
        }

        expected = 3;
        actual = sut.list.getItems().size();

        assertEquals(expected, actual);    //Makes sure that the timelines are on the list
    }

    @Test
    void testViewPersonalChangeSortMethodThenViewAllTimelines() throws InterruptedException {
        addNewTimelineToDBByOwnerId(-1, -1, -1);    //Add some new timelines to the list

        Platform.runLater(() -> {       //View only personal timelines
            sut.initialize();
            sut.cbOnlyViewPersonalLines.setSelected(true);
            sut.onlyUserTimelines();
        });
        waitForRunLater();

        changeSortBy(1);    //Change the sorting method

        ArrayList<Timeline> timelinesList = new ArrayList<>(sut.list.getItems());
        int initialListSize = timelinesList.size(); //Keep track of how many timelines are currently in the list

        int actual;
        int expected;
        Timeline higherTimelineOnList;

        for (int i = 0; i < timelinesList.size(); i++) {
            higherTimelineOnList = timelinesList.get(i);

            expected = GUIManager.loggedInUser.getUserID();
            actual = higherTimelineOnList.getOwnerID();
            assertEquals(expected, actual);     //Check that the timelines are owned by the user

            if (i != timelinesList.size() - 1)  //Don't compare the last one to avoid null pointer
                assertTrue(higherTimelineOnList.getName().compareTo(timelinesList.get(i + 1).getName()) >= 0);   //assert that the one below it comes before alphabetically by name, or is the same
        }

        expected = 3;
        actual = sut.list.getItems().size();
        assertEquals(expected, actual);    //Make sure that only the User's timelines are on the list

        Platform.runLater(() -> {       //View all timelines again
            sut.cbOnlyViewPersonalLines.setSelected(false);
            sut.onlyUserTimelines();
        });
        waitForRunLater();

        Timeline lowerTimelineOnList;
        for (int i = 0; i < timelinesList.size() - 1; i++) {
            higherTimelineOnList = timelinesList.get(i);
            lowerTimelineOnList = timelinesList.get(i + 1);

            assertTrue(higherTimelineOnList.getName().compareTo(lowerTimelineOnList.getName()) >= 0);   //assert that the one below it comes before alphabetically by name, or is the same
        }

        expected = initialListSize;
        actual = timelinesList.size();
        assertEquals(expected, actual);     //Check that all the timelines are being shown
    }

    @Test
    void testDeleteTimelineConfirm() throws InterruptedException {
        setAdminLoggedIn(true);
        addNewTimelineToDBByOwnerId(-1);
        reinitializeDashboard();

        //Select the first timeline in the list that has an owner ID of -1
        sut.list.getSelectionModel().select(sut.list.getItems().stream().findFirst().filter(t -> t.getOwnerID() == -1).get());
        int initialListSize = sut.list.getItems().size();

        Platform.runLater(() -> robot.clickOn("#btnDelete"));
        waitForRunLater();
        Platform.runLater(() -> {
            DialogPane popup = getDialogPane();
            robot.clickOn(popup.lookupButton(ButtonType.OK));
        });
        waitForRunLater();

        reinitializeDashboard();
        int expected = initialListSize - 1; //Check that it was actually deleted
        int actual = sut.list.getItems().size();
        assertEquals(expected, actual);
    }

    @Test
    void testDeleteTimelineClose() throws InterruptedException {
        setAdminLoggedIn(true);
        addNewTimelineToDBByOwnerId(-1);
        reinitializeDashboard();

        //Select the first timeline in the list that has an owner ID of -1
        sut.list.getSelectionModel().select(sut.list.getItems().stream().findFirst().filter(t -> t.getOwnerID() == -1).get());
        int initialListSize = sut.list.getItems().size();

        Platform.runLater(() -> robot.clickOn("#btnDelete"));
        waitForRunLater();
        Platform.runLater(() -> {
            DialogPane popup = getDialogPane();
            robot.clickOn(popup.lookupButton(ButtonType.CANCEL));
        });
        waitForRunLater();

        reinitializeDashboard();
        int expected = initialListSize; //Check that the timeline is still in the list
        int actual = sut.list.getItems().size();
        assertEquals(expected, actual);
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

    void reinitializeDashboard() throws InterruptedException {
        Platform.runLater(() -> sut.initialize());
        waitForRunLater();
    }

    //Helper method for making and adding Timelines
    void addNewTimelineToDBByName(String... name) {
        for (String n : name) {
            Timeline newTimeline = new Timeline();
            newTimeline.setName(n);
            try {DBM.insertIntoDB(newTimeline);} catch (SQLException e) {e.printStackTrace();}
        }
    }

    void addNewTimelineToDBByOwnerId(int... ownerID) {
        for (int n : ownerID) {
            Timeline newTimeline = new Timeline();
            newTimeline.setOwnerID(n);
            try {DBM.insertIntoDB(newTimeline);} catch (SQLException e) {e.printStackTrace();}
        }
    }

    private DialogPane getDialogPane() {
        final List<Window> allWindows = Window.getWindows();        //Get a list of windows
        for (Window w : allWindows)                                 //if a window is a DialogPane with the correct title, return it
        {
            if (w != null && w.isFocused())
                return (DialogPane) w.getScene().getRoot();
        }


        return null;
    }


}
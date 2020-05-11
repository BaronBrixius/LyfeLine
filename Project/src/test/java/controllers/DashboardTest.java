package controllers;

import database.DBM;
import database.Event;
import database.Timeline;
import database.User;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class DashboardTest {
    static private int testCount = 0;
    Dashboard sut;
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
        System.out.println("===========================================================================");  //Makes each test easier to distinguish in console view
        System.out.println("Test " + ++testCount);
        DBM.setupSchema();

        try {
            PreparedStatement stat = DBM.conn.prepareStatement("SELECT * FROM Users WHERE UserID=?");
            stat.setInt(1, 999);
            GUIManager.loggedInUser =  DBM.getFromDB(stat, new User()).get(0);
            loginUserID = GUIManager.loggedInUser.getUserID();
        } catch (SQLException e) { System.out.println("Could not get test user from database"); }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../classes/FXML/Dashboard.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.getScene().getStylesheets().add("File:src/main/resources/styles/"+ StyleSheetName +".css");
        sut = loader.getController();
        GUIManager.mainStage = stage;
        stage.show();
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

        int initialListSize = sut.list.getItems().size();

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
        int actual = finalListSize - initialListSize;
        assertEquals(expected, actual); //Checks to make sure that the Timelines were actually added to the list.

        for (Timeline t : timelinesList)
            System.out.println(t.getName());
    }

    @Test
    void testSortTimelinesAlphabeticallyAfterRemovingSomeTimelines() throws InterruptedException {
        Timeline higherTimelineOnList;
        Timeline lowerTimelineOnList;

        int initialListSize = sut.list.getItems().size();

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
        int actual = finalListSize - initialListSize;
        assertEquals(expected, actual);         //Makes sure that the Timelines were removed

        for (Timeline t : timelinesList)
            System.out.println(t.getName());
    }

    @Test
    void testSortTimelinesAlphabeticallyAfterRemovingAllTimelines() throws InterruptedException {
        Timeline higherTimelineOnList;
        Timeline lowerTimelineOnList;

        //int initialListSize = sut.list.getItems().size();

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

        int initialListSize = sut.list.getItems().size();

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
        int actual = finalListSize - initialListSize;
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
        addNewTimelineToDBByOwnerId(loginUserID, loginUserID, loginUserID);

        Platform.runLater(() -> {
            sut.initialize();
            sut.cbOnlyViewPersonalLines.setSelected(true);
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
        addNewTimelineToDBByOwnerId(loginUserID, loginUserID, loginUserID);    //Add some new timelines to the list

        Platform.runLater(() -> {       //View only personal timelines
            sut.initialize();
            sut.cbOnlyViewPersonalLines.setSelected(true);
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
        addNewTimelineToDBByOwnerId(loginUserID);
        reinitializeDashboard();

        //Select the first timeline in the list that has an owner ID of the logged in user
        sut.list.getSelectionModel().select(sut.list.getItems().stream().filter(t -> t.getOwnerID() == loginUserID).findFirst().get());
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
        addNewTimelineToDBByOwnerId(loginUserID);
        reinitializeDashboard();

        //Select the first timeline in the list that has an owner ID of the logged in user
        sut.list.getSelectionModel().select(sut.list.getItems().stream().filter(t -> t.getOwnerID() == loginUserID).findFirst().get());
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

    @Test
    void testCreateTimelineButton() throws InterruptedException {
        GUIManager.main = new BorderPane(); //Avoids a null pointer
        setAdminLoggedIn(true);
        addNewTimelineToDBByOwnerId(loginUserID);
        reinitializeDashboard();
        //Select the first timeline in the list that has an owner ID of the logged in user to ensure that the new timeline doesn't get overridden in edit mode
        sut.list.getSelectionModel().select(sut.list.getItems().stream().filter(t -> t.getOwnerID() == loginUserID).findFirst().get());


        Platform.runLater(() -> {
            TimelineView testView = sut.createTimeline();
            assertTrue(testView.timelineEditorController.editable); //Makes sure that the create timeline screen starts in edit mode.


            //Check all timeline attributes to make sure that it is a blank timeline
            String actualString = testView.activeTimeline.getName();
            String expectedString = "";
            assertEquals(expectedString, actualString);

            actualString = testView.activeTimeline.getDescription();
            expectedString = "";
            assertEquals(expectedString, actualString);

            int actualInt = testView.activeTimeline.getOwnerID();
            int expectedInt = loginUserID;
            assertEquals(expectedInt, actualInt);

            actualInt = testView.activeTimeline.getKeywords().size();
            expectedInt = 0;
            assertEquals(expectedInt, actualInt);

            actualInt = testView.activeTimeline.getScale();
            expectedInt = 0;
            assertEquals(expectedInt, actualInt);

            actualInt = testView.activeTimeline.getEventList().size();
            expectedInt = 0;
            assertEquals(expectedInt, actualInt);

            assertNull(testView.activeTimeline.getTheme());
        });
        waitForRunLater();
    }

    @Test
    void testEditTimelineButton() throws InterruptedException {
        GUIManager.main = new BorderPane(); //Avoids a null pointer
        setAdminLoggedIn(true);

        Timeline newTimeline = new Timeline();
        newTimeline.setOwnerID(loginUserID);
        newTimeline.setName("Name");
        newTimeline.setDescription("Description");
        newTimeline.getKeywords().add("Keyword");
        newTimeline.setScale(3);
        newTimeline.setTheme("None");
        try {DBM.insertIntoDB(newTimeline);} catch (SQLException e) {e.printStackTrace();}

        Event testEvent = new Event();
        testEvent.setOwnerID(loginUserID);
        try {DBM.insertIntoDB(testEvent);} catch (SQLException e) {e.printStackTrace();}
        try {testEvent.addToTimeline(newTimeline.getID());} catch (SQLException e) {e.printStackTrace();}

        reinitializeDashboard();

        //Select the first timeline in the list that has an owner ID of the logged in user
        sut.list.getSelectionModel().select(sut.list.getItems().stream().filter(t -> t.getOwnerID() == loginUserID).findFirst().get());

        Platform.runLater(() -> {
            TimelineView testView = sut.editTimeline();
            assertTrue(testView.timelineEditorController.editable); //Makes sure that the edit timeline screen starts in edit mode.

            //Check all timeline attributes to make sure that it is the proper timeline
            String actualString = testView.activeTimeline.getName();
            String expectedString = "Name";
            assertEquals(expectedString, actualString);

            actualString = testView.activeTimeline.getDescription();
            expectedString = "Description";
            assertEquals(expectedString, actualString);

            int actualInt = testView.activeTimeline.getOwnerID();
            int expectedInt = loginUserID;
            assertEquals(expectedInt, actualInt);

            actualInt = testView.activeTimeline.getKeywords().size();
            expectedInt = 1;
            assertEquals(expectedInt, actualInt);

            actualInt = testView.activeTimeline.getScale();
            expectedInt = 3;
            assertEquals(expectedInt, actualInt);

            actualInt = testView.activeTimeline.getEventList().size();
            expectedInt = 1;
            assertEquals(expectedInt, actualInt);

            actualString = testView.activeTimeline.getTheme();
            expectedString = "None";
            assertEquals(expectedString, actualString);
        });
        waitForRunLater();
    }

    @Test
    void testViewTimelineButton() throws InterruptedException {
        GUIManager.main = new BorderPane(); //Avoids a null pointer
        setAdminLoggedIn(false);    //Makes sure that non admins can still view timelines
        reinitializeDashboard();
        //Select the first timeline in the list
        sut.list.getSelectionModel().select(0);
        Timeline timelineSelected = sut.list.getSelectionModel().getSelectedItem();

        Platform.runLater(() -> {
            TimelineView testView = sut.openTimeline();
            assertFalse(testView.timelineEditorController.editable); //Makes sure that the view timeline screen doesn't start in edit mode.

            //Check all timeline attributes to make sure that it is a blank timeline
            String actualString = testView.activeTimeline.getName();
            String expectedString = timelineSelected.getName();
            assertEquals(expectedString, actualString);

            actualString = testView.activeTimeline.getDescription();
            expectedString = timelineSelected.getDescription();
            assertEquals(expectedString, actualString);

            int actualInt = testView.activeTimeline.getOwnerID();
            int expectedInt = timelineSelected.getOwnerID();
            assertEquals(expectedInt, actualInt);

            actualInt = testView.activeTimeline.getKeywords().size();
            expectedInt = timelineSelected.getKeywords().size();
            assertEquals(expectedInt, actualInt);

            actualInt = testView.activeTimeline.getScale();
            expectedInt = timelineSelected.getScale();
            assertEquals(expectedInt, actualInt);

            actualInt = testView.activeTimeline.getEventList().size();
            expectedInt = timelineSelected.getEventList().size();
            assertEquals(expectedInt, actualInt);

            actualString = testView.activeTimeline.getTheme();
            expectedString = timelineSelected.getTheme();
            assertEquals(expectedString, actualString);
        });
        waitForRunLater();
    }

    @Test
    void testSimpleSearchByTimelineName() throws InterruptedException {
        addNewTimelineToDBByName("Please don't make a timeline with this name it will ruin my tests");
        reinitializeDashboard();

        Platform.runLater(() -> sut.searchInput.setText("me")); //Checks that searching for something in the middle of the name still counts
        waitForRunLater();



        assertTrue(sut.list.getItems().stream().allMatch(timeline -> timeline.getName().toLowerCase().contains(sut.searchInput.getText().toLowerCase())));
    }

    @Test
    void testSimpleSearchByTimelineNameCaseInsensitive() throws InterruptedException {
        addNewTimelineToDBByName("Please don't make a timeline with this name it will ruin my tests");
        reinitializeDashboard();

        Platform.runLater(() -> sut.searchInput.setText("ME"));
        waitForRunLater();

        assertTrue(sut.list.getItems().stream().allMatch(timeline -> timeline.getName().toLowerCase().contains(sut.searchInput.getText().toLowerCase())));
    }

    @Test
    void testSimpleSearchByWeirdTimelineName() throws InterruptedException {
        addNewTimelineToDBByName("☺☻♥♦♣♠");
        reinitializeDashboard();

        Platform.runLater(() -> sut.searchInput.setText("♦"));
        waitForRunLater();

        assertTrue(sut.list.getItems().stream().allMatch(timeline -> timeline.getName().toLowerCase().contains(sut.searchInput.getText().toLowerCase())));
    }

    @Test
    void testSimpleSearchByTimelineNameFailure() throws InterruptedException {
        addNewTimelineToDBByName("Please don't make a timeline with this name it will ruin my tests");
        reinitializeDashboard();

        Platform.runLater(() -> sut.searchInput.setText("Please x")); //Checks that searching for something not in the name actually removes it from the list
        waitForRunLater();

        assertFalse(sut.list.getItems().stream().allMatch(timeline -> timeline.getName().toLowerCase().contains(sut.searchInput.getText().toLowerCase()))
        && sut.list.getItems().size() > 0); //If list is empty, then previous line defaults to true
    }

    @Test
    void testSimpleSearchByKeyWord() throws InterruptedException {
        addNewTimelineToDBByKeyWords("Please don't make a timeline with this keyword it will ruin my tests");
        reinitializeDashboard();

        Platform.runLater(() -> sut.searchInput.setText("t mak")); //Checks that searching for something in the middle of the key word still counts
        waitForRunLater();

        assertTrue(sut.list.getItems().stream().allMatch(timeline -> timeline.getKeywords().stream().anyMatch(keyWord->keyWord.contains(sut.searchInput.getText().toLowerCase()))));
    }

    @Test
    void testSimpleSearchByTimelineNamePersonalOnly() throws InterruptedException {
        Platform.runLater(() -> {
            sut.cbOnlyViewPersonalLines.setSelected(true);
            addNewTimelineToDBByOwnerIdAndName("Please don't make a timeline with this name it will ruin my tests", loginUserID);
        });
        waitForRunLater();
        reinitializeDashboard();

        Platform.runLater(() -> sut.searchInput.setText("me")); //Checks that searching for something in the middle of the name still counts
        waitForRunLater();

        assertTrue(sut.list.getItems().stream().allMatch(timeline -> timeline.getName().toLowerCase().contains(sut.searchInput.getText().toLowerCase())));
    }

    @Test
    void testSimpleSearchByTimelineNameCaseInsensitivePersonalOnly() throws InterruptedException {
        Platform.runLater(() -> {
            sut.cbOnlyViewPersonalLines.setSelected(true);
            addNewTimelineToDBByOwnerIdAndName("Please don't make a timeline with this name it will ruin my tests", loginUserID);
        });
        waitForRunLater();
        reinitializeDashboard();

        Platform.runLater(() -> sut.searchInput.setText("ME"));
        waitForRunLater();

        assertTrue(sut.list.getItems().stream().allMatch(timeline -> timeline.getName().toLowerCase().contains(sut.searchInput.getText().toLowerCase())));
    }

    @Test
    void testSimpleSearchByWeirdTimelineNamePersonalOnly() throws InterruptedException {
        Platform.runLater(() -> {
            sut.cbOnlyViewPersonalLines.setSelected(true);
            addNewTimelineToDBByOwnerIdAndName("☺☻♥♦♣♠", loginUserID);
        });
        waitForRunLater();
        reinitializeDashboard();

        Platform.runLater(() -> sut.searchInput.setText("♦"));
        waitForRunLater();

        assertTrue(sut.list.getItems().stream().allMatch(timeline -> timeline.getName().toLowerCase().contains(sut.searchInput.getText().toLowerCase())));
    }

    @Test
    void testSimpleSearchByTimelineNamePersonalOnlyFailure() throws InterruptedException {
        Platform.runLater(() -> {
            sut.cbOnlyViewPersonalLines.setSelected(true);
            addNewTimelineToDBByOwnerIdAndName("Please don't make a timeline with this name it will ruin my tests", loginUserID);
        });
        waitForRunLater();
        reinitializeDashboard();

        Platform.runLater(() -> sut.searchInput.setText("Please x")); //Checks that searching for something not in the name actually removes it from the list
        waitForRunLater();

        assertFalse(sut.list.getItems().stream().allMatch(timeline -> timeline.getName().toLowerCase().contains(sut.searchInput.getText().toLowerCase()))
                && sut.list.getItems().size() > 0); //If list is empty, then previous line defaults to true);
    }

    @Test
    void testSimpleSearchByTimelineNamePersonalOnlyIDFailure() throws InterruptedException {
        Platform.runLater(() -> {
            sut.cbOnlyViewPersonalLines.setSelected(true);
            addNewTimelineToDBByOwnerIdAndName("Please don't make a timeline with this name it will ruin my tests", 1);
        });
        waitForRunLater();
        reinitializeDashboard();

        Platform.runLater(() -> sut.searchInput.setText("Please")); //Checks that searching for something not in the name actually removes it from the list
        waitForRunLater();

        assertFalse(sut.list.getItems().stream().allMatch(timeline -> timeline.getName().toLowerCase().contains(sut.searchInput.getText().toLowerCase()))
                && sut.list.getItems().size() > 0); //If list is empty, then previous line defaults to true);
    }

    @Test
    void testSimpleSearchByKeyWordPersonalOnly() throws InterruptedException {
        Platform.runLater(() -> {
            sut.cbOnlyViewPersonalLines.setSelected(true);
            addNewTimelineToDBByOwnerIdAndKeyword(loginUserID,"Please don't make a timeline with this keyword it will ruin my tests");
        });
        waitForRunLater();
        reinitializeDashboard();

        Platform.runLater(() -> sut.searchInput.setText("t mak")); //Checks that searching for something in the middle of the key word still counts
        waitForRunLater();

        assertTrue(sut.list.getItems().stream().allMatch(timeline -> timeline.getKeywords().stream().anyMatch(keyWord->keyWord.toLowerCase().contains(sut.searchInput.getText().toLowerCase()))));
    }

    @Test
    void testSimpleSearchByTimelineNameAndSort() throws InterruptedException {
        addNewTimelineToDBByName("aPlease don't make a timeline with this name it will ruin my tests",
                "bPlease don't make a timeline with this name it will ruin my tests",
                "cPlease don't make a timeline with this name it will ruin my tests",
                "dPlease don't make a timeline with this name it will ruin my tests",
                "ePlease don't make a timeline with this name it will ruin my tests");
        reinitializeDashboard();

        Platform.runLater(() -> sut.searchInput.setText("Please don't make a timeline with this name it will ruin my tests"));
        waitForRunLater();
        changeSortBy(0);    //Alphabetically

        Timeline higherTimelineOnList;
        Timeline lowerTimelineOnList;
        ArrayList<Timeline> timelinesList = new ArrayList<>(sut.list.getItems());

        for (int i = 0; i < timelinesList.size() - 1; i++) {  //For each timeline on the list except the last one,
            higherTimelineOnList = timelinesList.get(i);
            lowerTimelineOnList = timelinesList.get(i + 1);

            assertTrue(higherTimelineOnList.getName().compareTo(lowerTimelineOnList.getName()) <= 0);   //assert that the one below it comes after alphabetically by name, or is the same
            assertTrue(lowerTimelineOnList.getName().contains("Please don't make a timeline with this name it will ruin my tests"));
        }
        assertTrue(timelinesList.get(0).getName().contains("Please don't make a timeline with this name it will ruin my tests"));

        changeSortBy(1);    //Reverse alphabetically
        timelinesList = new ArrayList<>(sut.list.getItems());

        for (int i = 0; i < timelinesList.size() - 1; i++) {  //For each timeline on the list except the last one,
            higherTimelineOnList = timelinesList.get(i);
            lowerTimelineOnList = timelinesList.get(i + 1);

            assertTrue(higherTimelineOnList.getName().compareTo(lowerTimelineOnList.getName()) >= 0);   //assert that the one below it comes before alphabetically by name, or is the same
            assertTrue(lowerTimelineOnList.getName().contains("Please don't make a timeline with this name it will ruin my tests"));
        }
        assertTrue(timelinesList.get(0).getName().contains("Please don't make a timeline with this name it will ruin my tests"));
    }

    @Test
    void testSimpleSearchByTimelineNameAndSortPersonalOnly() throws InterruptedException {
        Platform.runLater(() -> {
            sut.cbOnlyViewPersonalLines.setSelected(true);
            addNewTimelineToDBByOwnerIdAndName("aPlease don't make a timeline with this name it will ruin my tests", loginUserID);
            addNewTimelineToDBByOwnerIdAndName("bPlease don't make a timeline with this name it will ruin my tests", loginUserID);
            addNewTimelineToDBByOwnerIdAndName("cPlease don't make a timeline with this name it will ruin my tests", loginUserID);
            addNewTimelineToDBByOwnerIdAndName("dPlease don't make a timeline with this name it will ruin my tests", loginUserID);
            addNewTimelineToDBByOwnerIdAndName("ePlease don't make a timeline with this name it will ruin my tests", loginUserID);
            addNewTimelineToDBByOwnerIdAndName("aPlease don't make a timeline with this name it will ruin my tests", 1);
            addNewTimelineToDBByOwnerIdAndName("bPlease don't make a timeline with this name it will ruin my tests", 1);
            addNewTimelineToDBByOwnerIdAndName("cPlease don't make a timeline with this name it will ruin my tests", 1);
            addNewTimelineToDBByOwnerIdAndName("dPlease don't make a timeline with this name it will ruin my tests", 1);
            addNewTimelineToDBByOwnerIdAndName("ePlease don't make a timeline with this name it will ruin my tests", 1);
        });
        reinitializeDashboard();

        Platform.runLater(() -> sut.searchInput.setText("Please don't make a timeline with this name it will ruin my tests"));
        waitForRunLater();
        changeSortBy(0);    //Alphabetically

        Timeline higherTimelineOnList;
        Timeline lowerTimelineOnList;
        ArrayList<Timeline> timelinesList = new ArrayList<>(sut.list.getItems());

        for (int i = 0; i < timelinesList.size() - 1; i++) {  //For each timeline on the list except the last one,
            higherTimelineOnList = timelinesList.get(i);
            lowerTimelineOnList = timelinesList.get(i + 1);

            assertTrue(higherTimelineOnList.getName().compareTo(lowerTimelineOnList.getName()) <= 0);   //assert that the one below it comes after alphabetically by name, or is the same
            assertTrue(lowerTimelineOnList.getName().contains("Please don't make a timeline with this name it will ruin my tests") && lowerTimelineOnList.getOwnerID()==loginUserID);
        }
        assertTrue(timelinesList.get(0).getName().contains("Please don't make a timeline with this name it will ruin my tests") && timelinesList.get(0).getOwnerID()==loginUserID);

        changeSortBy(1);    //Reverse alphabetically
        timelinesList = new ArrayList<>(sut.list.getItems());

        for (int i = 0; i < timelinesList.size() - 1; i++) {  //For each timeline on the list except the last one,
            higherTimelineOnList = timelinesList.get(i);
            lowerTimelineOnList = timelinesList.get(i + 1);

            assertTrue(higherTimelineOnList.getName().compareTo(lowerTimelineOnList.getName()) >= 0);   //assert that the one below it comes before alphabetically by name, or is the same
            assertTrue(lowerTimelineOnList.getName().contains("Please don't make a timeline with this name it will ruin my tests") && lowerTimelineOnList.getOwnerID()==loginUserID);
        }
        assertTrue(timelinesList.get(0).getName().contains("Please don't make a timeline with this name it will ruin my tests") && timelinesList.get(0).getOwnerID()==loginUserID);
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
            newTimeline.setOwnerID(loginUserID);
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

    void addNewTimelineToDBByKeyWords(String... words) {
        Timeline newTimeline = new Timeline();
        newTimeline.setOwnerID(loginUserID);
        try {DBM.insertIntoDB(newTimeline);} catch (SQLException e) {e.printStackTrace();}
        for (String n : words)
            newTimeline.getKeywords().add(n);
        try {DBM.updateInDB(newTimeline);} catch (SQLException e) {e.printStackTrace();}
    }

    void addNewTimelineToDBByOwnerIdAndName(String name, int ownerID) {
        Timeline newTimeline = new Timeline();
        newTimeline.setOwnerID(ownerID);
        newTimeline.setName(name);
        try {DBM.insertIntoDB(newTimeline);} catch (SQLException e) {e.printStackTrace();}
    }

    void addNewTimelineToDBByOwnerIdAndKeyword(int ownerID, String... words) {
        Timeline newTimeline = new Timeline();
        newTimeline.setOwnerID(ownerID);
        for (String n : words)
            newTimeline.getKeywords().add(n);
        try {DBM.insertIntoDB(newTimeline);} catch (SQLException e) {e.printStackTrace();}
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
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
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(ApplicationExtension.class)
public class EventEditorTest {
    static private int testCount = 0;
    TimelineView parent;
    EventEditor sut;
    EventSelector selector;
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
        System.out.println("Test " + ++testCount);
        DBM.setupSchema();
        GUIManager.loggedInUser = new User();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../classes/FXML/TimelineView.fxml"));
        stage.setScene(new Scene(loader.load()));
        parent = loader.getController();
        parent.setActiveTimeline(new Timeline());
        selector = parent.eventSelectorController;
        sut = parent.eventEditorController;

        stage.show();
    }

    @AfterEach
    void tearDown() {
    }

    /*
    @Test
    void initialize() {
    }


        @Test
        void setParentController() {
        }

        @Test
        void saveEditButton() {
        }

        @Test
        void toggleEditable() {
        }

        @Test
        void setEvent() {
        }

        @Test
        void testSetEvent() {
        }

        @Test
        void updateEvent() {
        }

        @Test
        void toggleStartExpanded() {
        }

        @Test
        void toggleEndExpanded() {
        }
*/
    @Test
    void hasChangesNewEventNoChanges() throws InterruptedException {
        Platform.runLater(() -> {
            sut.setEvent(new Event());
            assertFalse(sut.hasChanges());
        });
        waitForRunLater();

    }

    @Test
    void hasChangesViewEventNoChanges() throws SQLException, InterruptedException {
        Event event1 = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM events"), new Event()).get(0);
        Platform.runLater(() -> {
            sut.setEvent(event1);
            assertFalse(sut.hasChanges());
        });
        waitForRunLater();
    }

    @Test
    void newEventSaved() throws SQLException, InterruptedException {
        int expectedDB = 1 + DBM.getFromDB(DBM.conn.prepareStatement("SELECT COUNT(*) FROM events"), rs -> rs.getInt(1)).get(0);
        int expectedTimelineList = 1 + parent.activeTimeline.getEventList().size();

        setAdminLoggedIn(true);
        Platform.runLater(() -> {
            selector.newEvent();
        });
        waitForRunLater();
        Platform.runLater(() -> {
            sut.toggleEditable(true);
            sut.titleInput.setText("test");
            sut.saveEditButton();

            DialogPane alert = getDialogPane();
            robot.clickOn(alert.lookupButton(ButtonType.OK));
            int actualDB = 0;
            try {
                actualDB = DBM.getFromDB(DBM.conn.prepareStatement("SELECT COUNT(*) FROM events"), rs -> rs.getInt(1)).get(0);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            int actualTimelineList = parent.activeTimeline.getEventList().size();
            assertEquals(expectedDB, actualDB);
            assertEquals(expectedTimelineList, actualTimelineList);
        });
        waitForRunLater();
    }

    @Test
    void oldEventSaved() throws SQLException, InterruptedException {
        String expected = "test";

        setOwnerLoggedIn();

        Platform.runLater(() -> {
            selector.eventListView.getSelectionModel().select(1);
            selector.openEvent();
        });
        waitForRunLater();
        Platform.runLater(() -> {
            sut.toggleEditable(true);
            sut.titleInput.setText("test");
            sut.saveEditButton();

            DialogPane alert = getDialogPane();
            robot.clickOn(alert.lookupButton(ButtonType.OK));
            String actual = null;
            try {
                actual = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM events WHERE UserID = " + sut.event), rs -> rs.getString("EventName")).get(0);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            assertEquals(expected, actual);
        });
        waitForRunLater();
    }

/*
            @Test
            void hasChangesNewEventNoChanges() {
                sut.setEvent(new Event());
                assertFalse(sut.hasChanges());
            }

private void assertFalse(boolean hasChanges) {
}

@Test
void close() {
}

@Test
void clearImage() {
}

*/

    //helper method to run code that was constrained to the main thread (so something isn't called before it loads)
    void waitForRunLater() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        semaphore.acquire();
    }

    //helper method returns popup windows, requires input of Title string
    private DialogPane getDialogPane() {
        final List<Window> allWindows = Window.getWindows();        //Get a list of windows
        for (Window w : allWindows) {                                //if a window is a DialogPane with the correct title, return it
            if (w != null && w.isFocused())
                return (DialogPane) w.getScene().getRoot();
        }
        return null;
    }


    //helper methods control who is logged in
    void setAdminLoggedIn(boolean admin) {
        GUIManager.loggedInUser.setAdmin(admin);
    }

    void setUserIDLoggedIn(int ownerID) {
        GUIManager.loggedInUser.setID(ownerID);
    }

    void setOwnerLoggedIn() {
        setAdminLoggedIn(true);
        setUserIDLoggedIn(parent.activeTimeline.getOwnerID());
    }
}
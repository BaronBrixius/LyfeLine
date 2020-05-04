package controllers;

import database.DBM;
import database.Event;
import database.Timeline;
import database.User;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.api.FxToolkit;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class EventEditorTest {
    static private int testCount = 0;
    TimelineView parent;
    EventEditor sut;
    EventSelector selector;
    FxRobot robot = new FxRobot();


    //helper methods control who is logged in
    static void setAdminLoggedIn(boolean admin) {
        GUIManager.loggedInUser.setAdmin(admin);
    }

    static void setUserIDLoggedIn(int ownerID) {
        GUIManager.loggedInUser.setID(ownerID);
    }

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
    void hasChangesNewEventNoChanges() {
        Platform.runLater(() -> {
            sut.setEvent(new Event());
            assertFalse(sut.hasChanges());
        });
        waitForRunLater();

    }

    @Test
    void hasChangesViewEventNoChanges() throws SQLException {
        Event event1 = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM events"), new Event()).get(0);
        Platform.runLater(() -> {
            sut.setEvent(event1);
            assertFalse(sut.hasChanges());
        });
        waitForRunLater();
    }

    @Test
    void savedEventChangedInDatabase() throws SQLException {
        int expected = 1 + DBM.getFromDB(DBM.conn.prepareStatement("SELECT COUNT(*) FROM events"), rs -> rs.getInt(1)).get(0);

        Platform.runLater(() -> {
            sut.setEvent(new Event());
            sut.toggleEditable(true);
            sut.titleInput.setText("Test changes");
            sut.saveEditButton.fire();
            DialogPane alert = getDialogPane("Confirm Save");
            robot.clickOn(alert.lookupButton(ButtonType.OK));
            int actual = 0;
            try {
                actual = DBM.getFromDB(DBM.conn.prepareStatement("SELECT COUNT(*) FROM events"), rs -> rs.getInt(1)).get(0);
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
    void waitForRunLater() {
        try {
            Semaphore semaphore = new Semaphore(0);
            Platform.runLater(semaphore::release);
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private DialogPane getDialogPane(String popupTitle) {
        final List<Window> allWindows = Window.getWindows();        //Get a list of windows
        for (Window w : allWindows)                                 //if a window is a DialogPane with the correct title, return it
            if (w != null && ((DialogPane) w.getScene().getRoot()).getHeaderText().equals(popupTitle))
                return (DialogPane) w.getScene().getRoot();

        return null;
    }
}
package controllers;

import database.DBM;
import database.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
public class EventEditorTest {
    static private int testCount = 0;
    private final String SCHEMA = "test";
    TimelineView parent;
    EventEditor sut;
    EventSelector selector;


    //helper methods control who is logged in
    static void setAdminLoggedIn(boolean admin) {
        GUIManager.loggedInUser.setAdmin(admin);
    }

    static void setUserIDLoggedIn(int ownerID) {
        GUIManager.loggedInUser.setID(ownerID);
    }

    @Start
    public void start(Stage stage) throws Exception {
        new DBM(SCHEMA);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("../../classes/FXML/EventEditor.fxml"));
        stage.setScene(new Scene(loader.load(), 300, 300));
        parent = loader.getController();
        sut = parent.eventEditorController;
        stage.show();
    }

    @BeforeEach
    void setUp() throws Exception {
        GUIManager.loggedInUser = new User();
        System.out.println("Test " + ++testCount);
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

    @Test
    void hasChangesNewEventNoChanges() {
        sut.setEvent(new Event());
        assertFalse(sut.hasChanges());
    }

    @Test
    void hasChangesViewEventNoChanges() throws SQLException {
        Event event1 = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM events"), new Event()).get(0);
        sut.setEvent(event1);
        assertFalse(sut.hasChanges());
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

        @Test
        void hasChangesNewEventNoChanges() {
            sut.setEvent(new Event());
            assertFalse(sut.hasChanges());
        }

    @Test
    void close() {
    }
  */
    @Test
    void clearImage() {
    }
}
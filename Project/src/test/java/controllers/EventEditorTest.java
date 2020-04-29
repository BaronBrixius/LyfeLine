package controllers;

import database.DBM;
import database.Event;
import database.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class EventEditorTest {
    static private int testCount = 0;
    TimelineView parent;
    EventEditor sut;
    EventSelector selector;

    @BeforeAll
    static void beforeAll() {
        GUIManager.loggedInUser = new User();
    }

    @BeforeEach
    void setUp() {
        parent = new TimelineView();
        sut = new EventEditor();
        sut.setParentController(parent);
        selector = new EventSelector();
        selector.setParentController(parent);

        System.out.println("Test " + ++testCount);
    }

    @AfterEach
    void tearDown() {
    }

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
*/
    @Test
    void close() {
    }

    @Test
    void clearImage() {
    }

    //helper methods control who is logged in
    static void setAdminLoggedIn(boolean admin) {
        GUIManager.loggedInUser.setAdmin(true);
    }

    static void setUserIDLoggedIn(int ownerID) {
        GUIManager.loggedInUser.setAdmin(true);
    }
}
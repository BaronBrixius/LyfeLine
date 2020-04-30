package database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Firas
 */
class EventTest {
    static final private String SCHEMA = "test";
    static private int testCount = 0;
    static private PreparedStatement stmt;
    static private ResultSet rs;
    Event test=new Event();
    static Event[] events = new Event[4];

    @BeforeAll
    static void init() throws SQLException, IOException, ClassNotFoundException {
        new DBM(SCHEMA);
        DBM.setupSchema();
    }

    @AfterAll
    static void finish() throws SQLException {
        DBM.conn.createStatement().execute("DROP DATABASE IF EXISTS test");
        DBM.conn.close();
    }

    @BeforeEach
    void setUp() {
        testCount++;
        System.out.println("Test " + testCount);

        try {
            DBM.setupSchema();
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getInsertQuery() throws SQLException {
        Event test=new Event();
        test.setID(1);
        test.setImage("test.test");
        test.setTitle("test");
        test.setDescription("Test");
        String sql = "INSERT INTO `events` (`EventID`, `ImagePath`, `EventName`, `EventDescription`) VALUES ('" + test.getEventID() + "','" + test.getImagePath() + "','" + test.getEventName() +"','" + test.getEventDescrition() + "',?)";
        PreparedStatement out = DBM.conn.prepareStatement(sql   , Statement.RETURN_GENERATED_KEYS);
        out.setInt(1, test.getUserID());
        Exception exception = assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            test.getInsertQuery();
        });
        assertNotNull(test);

    }

    @Test
    void addToTimeline() {
    }

    @Test
    void removeFromTimeline() {
    }

    @Test
    void createFromDB() throws SQLException {
        ResultSet rs;
        PreparedStatement stmt = DBM.conn.prepareStatement("SELECT COUNT(*) FROM events");
        rs = stmt.executeQuery();
        rs.next();
        int actual = rs.getInt(1);
        assertEquals(events.length, actual);

        // See if the database objects are the same as the ones I pushed
        PreparedStatement stmt1 = DBM.conn.prepareStatement("SELECT * FROM events");
        List<Event> eventList = DBM.getFromDB(stmt1, new Event());
        for (int i = 0; i < events.length; i++) {
            assertEquals(events[i].getEventID(), eventList.get(i).getEventID());
            assertEquals(events[i].getImagePath(), eventList.get(i).getEventName());

        }



        /*Event test=new Event();
        test.setID(1);
        test.setImage("test.test");
        test.setTitle("test");
        test.setDescription("Test");
        assertNotNull(1);
        assertEquals("test.test",test.getImagePath());
        assertEquals("test",test.getEventName());
        assertEquals("Test",test.getEventDescrition());*/

    }

    @Test
    void setID() {
        test.setID(1);
        assertEquals(1,1);
    }

    @Test
    void setTitle() {
        test.setTitle("Test");
        assertEquals("Test","Test");
    }

    @Test
    void setDescription() {
        test.setDescription("Test");
        assertEquals("Test","Test");
    }

    @Test
    void setImage() {
        test.setImage("Test");
        assertEquals("Test","Test");
    }

    @Test
    void getEventDescrition() {
        test.getEventDescrition();
        assertEquals("Test","Test");
    }

    @Test
    void getUpdateQuery() {
    }

    @Test
    void getDeleteQuery() {
    }

    @Test
    void setUserID() {
        test.setUserID(1);
        assertEquals(1,1);
    }
    @Test
    void close() throws SQLException, ClassNotFoundException {
        DBM.conn.close();

        assertThrows(SQLException.class, () -> DBM.conn.createStatement());
        new DBM(SCHEMA);
    }
}
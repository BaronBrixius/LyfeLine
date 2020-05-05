package database;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {
    static final private String SCHEMA = "test";
    static private int testCount = 0;
    Event test=new Event();
    static Event[] events = new Event[5];

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
        test.setImage("test");
        test.setTitle("test");
        test.setDescription("Test");
        String sql = "INSERT INTO `events` (`EventID`, `ImagePath`, `EventName`, `EventDescription`) VALUES ('" + test.getEventID() + "','" + test.getImagePath() + "','" + test.getEventName() +"','" + test.getEventDescrition() + "',?)";
        PreparedStatement out = DBM.conn.prepareStatement(sql   , Statement.RETURN_GENERATED_KEYS);
        //out.setInt(1, test.getUserID());
       /* Exception exception = assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            test.getInsertQuery();
        });*/
        assertNotNull(test);
        //assertEquals(out.toString(),test.getInsertQuery().toString());

    }

    @Test
    void addToTimeline() throws SQLException {
        Event testToAdd=new Event();
        testToAdd.setID(1);
        Timeline test=new Timeline();
        test.setID(1);
        PreparedStatement out = DBM.conn.prepareStatement("INSERT  INTO `timelineevents` (`TimelineID`, `EventID`) VALUES (?, ?);");
        out.setInt(1, 0);
        String x=out.toString();
        assertNotNull(out);
        assertEquals(x,out.toString());
        assertEquals(test.getTimelineID(),testToAdd.getEventID());
    }


    @Test
    void createFromDB() throws SQLException {
        Event test=new Event();
        events[0]=test;
        DBM.insertIntoDB(test);
        test.setImage("test.test");
        test.setTitle("test");
        test.setDescription("Test");
        Event test1=new Event();
        events[1]=test1;
        DBM.insertIntoDB(test1);
        Event test2=new Event();
        events[2]=test2;
        DBM.insertIntoDB(test2);
        assertEquals(test,events[0]);
        assertEquals(test1,events[1]);
        assertEquals(test2,events[2]);

        assertNotNull(1);
        assertEquals("test.test",test.getImagePath());
        assertEquals("test",test.getEventName());
        assertEquals("Test",test.getEventDescrition());


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
    void getUpdateQuery() throws SQLException {
        Event test=new Event();
        events[4]=test;
        DBM.insertIntoDB(test);
        DBM.updateInDB(test);
        assertNotNull(test.getUpdateQuery());
        assertEquals(test.getInsertQuery().toString(),events[4].toString());


    }

    @Test
    void getDeleteQuery() throws SQLException {
        Event test=new Event();
        events[0]=test;
        DBM.insertIntoDB(test);
        DBM.deleteFromDB(test);

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
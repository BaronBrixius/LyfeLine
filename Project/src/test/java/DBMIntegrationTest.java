import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DBMIntegrationTest {
    static final private String SCHEMA = "test";
    static private int testCount = 0;
    static private PreparedStatement stmt;
    static private ResultSet rs;


    @BeforeAll
    static void init() throws SQLException, ClassNotFoundException {
        new DBM(SCHEMA);
    }

    static void resetTable(String table) throws SQLException {
        try {
            Statement executer = DBM.conn.createStatement();
            executer.execute("DELETE FROM " + table);
            DBM.runScript("src/test/resources/" + table + ".sql");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
    void insertMultiple() throws SQLException {
        resetTable("events");
        int expected = 8;

        Event event1 = new Event();
        Event event2 = new Event();
        Event event3 = new Event();
        DBM.insertIntoDB(event1, event2, event3);

        stmt = DBM.conn.prepareStatement("SELECT COUNT(*) FROM events");
        rs = stmt.executeQuery();
        rs.next();
        int actual = rs.getInt(1);

        assertEquals(expected, actual);
    }

    @Test
    void insertList() throws SQLException {
        resetTable("events");
        int expected = 9;

        Event[] events = new Event[4];
        for (int i = 0; i < 4; i++) {
            events[i] = new Event();
        }
        DBM.insertIntoDB(events);

        stmt = DBM.conn.prepareStatement("SELECT COUNT(*) FROM events");
        rs = stmt.executeQuery();
        rs.next();
        int actual = rs.getInt(1);

        assertEquals(expected, actual);
    }

    @Test
    void insertNulls() throws SQLException {
        resetTable("events");
        int expected = 6;

        Event[] events = new Event[3];
        events[1] = new Event();
        DBM.insertIntoDB(events);

        stmt = DBM.conn.prepareStatement("SELECT COUNT(*) FROM events");
        rs = stmt.executeQuery();
        rs.next();
        int actual = rs.getInt(1);

        assertEquals(expected, actual);
    }

    @Test
    void insertDuplicateThrowsException() throws SQLException {
        //Event event = new Event(1, 2020, 4, 9);
        //DBM.insertIntoDB(event);

        //assertThrows(SQLIntegrityConstraintViolationException.class, () -> DBM.insertIntoDB(event));
    }

    @Test
    void updatebyArray() throws SQLException {
        resetTable("users");
        String expected;

        List<User> userList = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM users"), new User());
        for (int i = 0; i < 5; i++) {
            userList.get(i).setUserEmail("test"+i+"@newdomain.biz");
        }
        DBM.updateInDB(userList);
        List<User> updatedList = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM users"), new User());

        String actual;
        for (int i = 0; i < 5; i++) {
            expected = "test"+i+"@newdomain.biz";
            actual = updatedList.get(i).getUserEmail();
            assertEquals(expected, actual);
        }
    }

    @Test
    void updateNulls() throws SQLException {
        resetTable("users");
        User[] users = new User[3];
        DBM.updateInDB(users);
    }

    @Test
    void deleteByList() throws SQLException {
        resetTable("events");
        int expected = 2;

        List<Event> eventList = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM events"), new Event());
        eventList.remove(1);
        eventList.remove(3);
        DBM.deleteFromDB(eventList);

        int actual = DBM.getFromDB(DBM.conn.prepareStatement("SELECT count(*) FROM events"), rs -> rs.getInt(1)).get(0);    //first row of count column

        assertEquals(expected, actual);
    }

    @Test
    void sanitizeSQLInjection() throws SQLException {
        resetTable("users");
        User injection = new User("TestName', 'TestEmail', 'TestPass', 'TestSalt', '1'); -- ", "email@domain.com", "Passw0rd!");    //1 in last slot would ordinarily mean user is admin
        DBM.insertIntoDB(injection);

        List<User> userList = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM users"), new User());

        assertFalse(userList.get(0).getAdmin());
    }

    @Test
    void setupSchemaFailureReturnsToOldList() throws SQLException {
        String expected = "src/main/resources/Database Creation Script.sql";
        try {
            DBM.setupSchema("NonexistentFile.sql");
        } catch (FileNotFoundException ignore) {
        }
        String actual = DBM.creationScript;

        assertEquals(expected, actual);
    }
}

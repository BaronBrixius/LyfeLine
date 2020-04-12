import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DBMIntegrationTest {
    static private final String testDBPath = "src/test/resources/TestDB.xml";

    static private int testCount = 0;
    static private DBM sut;
    static private PreparedStatement stmt;
    static private ResultSet rs;


    @BeforeAll
    static void init() throws SQLException, IOException, ClassNotFoundException {
        sut = new DBM("test");
        DBM.setupSchema();

        //createTestDB();         //Adds some rows to the database tables and exports them to .xml, don't need to run this often
    }

    static void resetTable(String table) throws SQLException {
        DBM.conn.createStatement().execute("LOAD DATA INFILE 'src/test/resources/" + table + ".csv' " +
                "INTO TABLE " + table + " " +
                "FIELDS TERMINATED BY ',' " +
                "ENCLOSED BY '\"' " +
                "LINES TERMINATED BY '\\n' "// +
                //"IGNORE 1 ROWS"
        );
    }

    //@AfterAll
    static void tearDown() throws SQLException {
        DBM.conn.createStatement().execute("DROP DATABASE IF EXISTS test");
        DBM.conn.close();
    }

    static void createTestDB() throws SQLException {
        Event[] events = new Event[4];
        for (int i = 0; i < 4; i++) {
            events[i] = new Event();
        }
        DBM.insertIntoDB(events);

        DBM.conn.createStatement().execute("SELECT * " +
                "INTO OUTFILE 'src/test/resources/events.csv' " +
                "FIELDS TERMINATED BY ',' " +
                "ENCLOSED BY '\"' " +
                "ESCAPED BY '\\\\' " +
                "LINES TERMINATED BY '\\n' " +
                "FROM test.events");

        User[] users = new User[4];
        for (int i = 0; i < 4; i++) {
            users[i] = new User("Name", "email" + i + "@domain.com", "Passw0rd!");
        }
        DBM.insertIntoDB(users);

        DBM.conn.createStatement().execute("SELECT * " +
                "INTO OUTFILE 'src/test/resources/users.csv' " +
                "FIELDS TERMINATED BY ',' " +
                "ENCLOSED BY '\"' " +
                "ESCAPED BY '\\\\' " +
                "LINES TERMINATED BY '\\n'" +
                "FROM test.users");
    }

    @Test
    void blah() {
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
        int expected = 3;

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
        int expected = 4;

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
        int expected = 1;

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
        Event event = new Event(1, 2020, 4, 9);
        DBM.insertIntoDB(event);

        assertThrows(SQLIntegrityConstraintViolationException.class, () -> DBM.insertIntoDB(event));
    }

    @Test
    void updateNulls() throws SQLException {
        User[] users = new User[3];
        DBM.updateInDB(users);
    }

    @Test
    void sanitizeSQLInjection() throws SQLException {
        User injection = new User("TestName', 'TestEmail', 'TestPass', 'TestSalt', '1'); -- ", "email@domain.com", "Passw0rd!");    //1 in last slot would ordinarily mean user is admin
        DBM.insertIntoDB(injection);

        List<User> userList = DBM.getFromDB(DBM.conn.prepareStatement("SELECT * FROM users"), new User());

        assertFalse(userList.get(0).getAdmin());
    }

    @Test
    void setupSchemaFailureReturnsToOldList() throws FileNotFoundException, SQLException {
        String expected = "src/main/resources/Database Creation Script.sql";
        try {
            DBM.setupSchema("NonexistentFile.sql");
        } catch (FileNotFoundException ignore) {
        }
        String actual = DBM.creationScript;

        assertEquals(expected, actual);
    }
}

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBMIntegrationTest {
    static final private String SCHEMA = "test";
    static private int testCount = 0;
    static private DBM sut;
    static private PreparedStatement stmt;
    static private ResultSet rs;


    @BeforeAll
    static void init() throws SQLException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        sut = new DBM(SCHEMA);
        DBM.setupSchema();
    }

    static void resetTable(String table) throws SQLException, FileNotFoundException {
        String[] statements = DBM.readFile("src/test/resources/" + table + ".sql");
        Statement executer = DBM.conn.createStatement();
        for (String s : statements) {
            executer.execute(s);
        }
    }

    //@AfterAll
    static void tearDown() throws SQLException {
        DBM.conn.createStatement().execute("DROP DATABASE IF EXISTS test");
        DBM.conn.close();
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
    void insertMultiple() throws SQLException, FileNotFoundException {
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

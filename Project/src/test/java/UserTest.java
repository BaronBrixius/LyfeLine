import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    static private DBM sut;





    @BeforeAll
    static void init() throws SQLException, IOException, ClassNotFoundException {
        sut = new DBM("jdbc:mysql://localhost","Halli", "dragon", "test");
        DBM.setupSchema();
        createTestDB();  //Adds some rows to the database tables and exports them to .xml, don't need to run this often
    }

    static void createTestDB() throws SQLException {
        User user1 = new User("John", "john@gmail.com", "somethingCool#1");
        DBM.insertIntoDB(user1);
        User user2 = new User("John", "john2@gmail.com", "somethingCool#2");
        DBM.insertIntoDB(user2);
        User user3 = new User("John", "john3@gmail.com", "somethingCool#3");
        DBM.insertIntoDB(user3);
        User user4 = new User("John", "john4@gmail.com", "somethingCool#4");
        DBM.insertIntoDB(user4);

    }

    @AfterAll
    static void tearDown() throws SQLException {
        DBM.conn.createStatement().execute("DROP DATABASE IF EXISTS test");
        DBM.conn.close();
    }

    @Test
    void validateUnique() throws SQLException {
        User user1 = new User("John", "john@gmail.com", "somethingCool#1");
        User user2 = new User("John", "johnny@gmail.com", "somethingCool#1");

        //Test if email returns false - exists
        assertFalse(User.validateUnique(user1.getUserEmail()));
        //Test if email returns true - does not exist
        assertTrue(User.validateUnique(user2.getUserEmail()));
        //check exception if not right format - missing @
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            user2.setUserEmail("jonny.gmail.com");
        });
        String expectedMessage = "Invalid email format";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage)); //Checks both that exception is thrown and correct message printed
        //check exception if not right format - missing .
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            user2.setUserEmail("jonny@gmailcom");
        });
        String expectedMessage1 = "Invalid email format";
        String actualMessage1 = exception.getMessage();
        assertTrue(actualMessage1.contains(expectedMessage1)); //Checks both that exception is thrown and correct message printed
    }

    @Test
    void getUserEmail() {
        //ble
    }

    @Test
    void setUserEmail() {
        //IllegalArgumentException test
        //Check if email is proper
    }

    @Test
    void setUserName() {
        //ble
    }

    @Test
    void isAdmin() {
        //ble
    }

    @Test
    void setPassword() {
        //IllegalArgumentException check
        //See if encrpt. is right??
    }

    @Test
    void getUser() {
        //not sure
    }

    @Test
    void verifyPass() {
        //yehhhh already done..
    }

    @Test
    void createFromDB() {
        //try object creation of user in db... should be the same that i pushed to
        // the DB in getInsertQuery()
    }

    @Test
    void getInsertQuery() {

        //try right
    }

    @Test
    void setID() {
        //ok
    }

    @Test
    void getUpdateQuery() {
        //Try updateQuery and see then if createFromDB() fits the changes
    }

    @Test
    void testToString() {
    }
}
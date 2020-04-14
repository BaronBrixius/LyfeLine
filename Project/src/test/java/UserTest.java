import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void validateUnique() {
        //Test if email returns false - exists
        //Test if email returns true - does not exist
        //check exception if not right format
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
        //Try sql injection -- add admin
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
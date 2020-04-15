import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void validateUnique() {
    }

    @Test
    void getUserEmail() {
    }

    @Test
    void setUserEmail() {
    }

    @Test
    void setUserName() {
    }

    @Test
    void isAdmin() {
        User user1 = new User("John", "john@gmail.com", "somethingCool#1");
        // set user1 as an admin
        user1.setAdmin(true);
        assertTrue(user1.getAdmin());
        //remove admin from user1
        user1.setAdmin(false);
        assertFalse(user1.getAdmin());

    }

    @Test
    void setPassword() {
    }

    @Test
    void getUser() {
    }

    @Test
    void verifyPass() {
    }

    @Test
    void createFromDB() {
    }

    @Test
    void getInsertQuery() {
    }

    @Test
    void setID() {
    }

    @Test
    void getUpdateQuery() {
    }

    @Test
    void testToString() {
    }
}
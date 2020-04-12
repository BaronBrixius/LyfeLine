import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncryptionTest {
    //Should create random salt and that we can not test, just trust
    // secure random class from Java security package
    @Test
    void getSalt() {
        //Create salt of 30,2 and 0 in length and test if output equals
        //Create 10000 salts and all should be unique when using this big random salt
    }

    @Test
    void hash() {
        //Not sure how to test the two exceptions, first the
        //algorithm exception is only thrown if I go into the method and make wrong string
        //int the secretKeyFactory
        //The hashing will be testing via generateSecurePassword()
        //and verifyUserPassword()
        //Look into how to test clearPassword() to see if its is retrievable from Java memory after
    }

    @Test
    void generateSecurePassword() {
        //Add same password 1000 times with diff. salt each time - should give diff hash each time
    }

    @Test
    void verifyUserPassword() {
        //check wrong password with wrong salt
        //Check wrong password with right salt
        //check right password with wrong salt
        //check right password with right salt
    }
}
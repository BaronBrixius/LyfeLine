import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    static int testCount = 0;

    @BeforeEach
    void setUp(){
        testCount++;
        System.out.println("Test " + testCount);
    }

    @AfterEach
    void tearDown(){

    }

    @Test
    void test(){
        System.out.print("Testing, testing. 1 2 3. Is this thing on?");
    }
}
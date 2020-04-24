package utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void distanceToCenturies() {
        int expected = 20;

        Date first = new Date(0,1,1);
        Date second = new Date(2046, 7, 12);

        int actual = first.distanceTo(second, 10);

        assertEquals(expected, actual);
    }

        @Test
    void distanceToYears() {
            int expected = 213;

            Date first = new Date(23,1,1);
            Date second = new Date(246, 7, 12);

            int actual = first.distanceTo(second, 8);

            assertEquals(expected, actual);
    }

    @Test
    void distanceToMonths() {
        int expected = 20;

        Date first = new Date(10,1,1);
        Date second = new Date(46, 7, 12);

        int actual = first.distanceTo(second, 10);

        assertEquals(expected, actual);
    }

    @Test
    void distanceToMinutes() {
        int expected = 20;

        Date first = new Date(0,1,1,1,1,1,1);
        Date second = new Date(0, 1, 1, 9, 12, 42, 66);

        int actual = first.distanceTo(second, 10);

        assertEquals(expected, actual);
    }

    @Test
    void distanceToMilliseconds() {
        int expected = 20;

        Date first = new Date(0,1,1,1,1,1,321);
        Date second = new Date(0,1,1,1,1,31,1);

        int actual = first.distanceTo(second, 10);

        assertEquals(expected, actual);
    }

    @Test
    void testToString() {
    }

    @Test
    void compareTo() {
    }
}
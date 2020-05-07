package utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void distanceToMillennia() {
        int expected = 9;

        Date first = new Date(640,1,1);
        Date second = new Date(10460, 7, 12);

        int actual = first.distanceTo(second, 11);

        assertEquals(expected, actual);
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
    void distanceToDecades() {
        int expected = 87;

        Date first = new Date(173,1,1);
        Date second = new Date(1046, 7, 12);

        int actual = first.distanceTo(second, 9);

        assertEquals(expected, actual);
    }

        @Test
    void distanceToYears() {
            int expected = 223;

            Date first = new Date(23,1,1);
            Date second = new Date(246, 7, 12);

            int actual = first.distanceTo(second, 8);

            assertEquals(expected, actual);
    }

    @Test
    void distanceToMonths() {
        int expected = 438;

        Date first = new Date(10,1,1);
        Date second = new Date(46, 7, 12);

        int actual = first.distanceTo(second, 7);

        assertEquals(expected, actual);
    }

    @Test
    void distanceToWeeks() {
        int expected = 85;

        Date first = new Date(1,7,11);
        Date second = new Date(3, 3, 1);

        int actual = first.distanceTo(second, 6);

        assertEquals(expected, actual);
    }

    @Test
    void distanceToDaysLeapYear() {
        int expected = 4020;

        Date first = new Date(1999,1,30);
        Date second = new Date(2010, 2, 1);

        int actual = first.distanceTo(second, 5);

        assertEquals(expected, actual);
    }

    @Test
    void distanceToDaysStartLeapDay() {
        int expected = 1;

        Date first = new Date(2004,2,29);
        Date second = new Date(2004, 3, 1);

        int actual = first.distanceTo(second, 5);

        assertEquals(expected, actual);
    }

    @Test
    void distanceToDaysEndLeapDay() {
        int expected = 365;

        Date first = new Date(2003,3,1);
        Date second = new Date(2004, 2, 29);

        int actual = first.distanceTo(second, 5);

        assertEquals(expected, actual);
    }

    @Test
    void distanceToDaysStartLeapDayLastYear() {
        int expected = 367;

        Date first = new Date(2003,2,28);
        Date second = new Date(2004, 3, 1);

        int actual = first.distanceTo(second, 5);

        assertEquals(expected, actual);
    }

    @Test
    void distanceToHoursLeapYear() {
        int expected = 104;

        Date first = new Date(2004,2,27,1,1,1,1);
        Date second = new Date(2004, 3, 2, 9, 12, 42, 66);

        int actual = first.distanceTo(second, 4);

        assertEquals(expected, actual);
    }

    @Test
    void distanceToMinutes() {
        int expected = 491;

        Date first = new Date(0,1,1,1,1,1,1);
        Date second = new Date(0, 1, 1, 9, 12, 42, 66);

        int actual = first.distanceTo(second, 3);

        assertEquals(expected, actual);
    }

    @Test
    void distanceToSeconds() {
        int expected = 2682030;

        Date first = new Date(0,1,1,1,1,1,0);
        Date second = new Date(0,2,1,2,1,31,0);

        int actual = first.distanceTo(second, 2);

        assertEquals(expected, actual);
    }

    @Test
    void distanceToMilliSeconds() {
        int expected = 781229680;

        Date first = new Date(0,1,23,1,1,1,321);
        Date second = new Date(0,2,1,2,1,31,1);

        int actual = first.distanceTo(second, 1);

        assertEquals(expected, actual);
    }

    @Test
    void testToString() {
    }

    @Test
    void compareTo() {
    }
}
package utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class DateUtil {
    public static int distanceBetween(LocalDateTime from, LocalDateTime to, int scale) {
        TemporalUnit unit = null;
        switch (scale) {
            case 1:
                unit = ChronoUnit.MILLIS;
                break;
            case 2:
                unit = ChronoUnit.SECONDS;
                break;
            case 3:
                unit = ChronoUnit.MINUTES;
                break;
            case 4:
                unit = ChronoUnit.HOURS;
                break;
            case 5:
                unit = ChronoUnit.DAYS;
                break;
            case 6:
                unit = ChronoUnit.WEEKS;
                break;
            case 7:
                unit = ChronoUnit.MONTHS;
                break;
            case 8:
                unit = ChronoUnit.YEARS;
                break;
            case 9:
                unit = ChronoUnit.DECADES;
                break;
            case 10:
                unit = ChronoUnit.CENTURIES;
                break;
            case 11:
                unit = ChronoUnit.MILLENNIA;
                break;
        }

        return (int) from.until(to, unit);
    }
}

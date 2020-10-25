package assignment.sensor;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class TestUtils {

    public static ZonedDateTime time(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        return ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, 0, ZoneOffset.UTC);
    }

    public static LocalDate date(int year, int month, int dayOfMonth) {
        return LocalDate.of(year, month, dayOfMonth);
    }

}

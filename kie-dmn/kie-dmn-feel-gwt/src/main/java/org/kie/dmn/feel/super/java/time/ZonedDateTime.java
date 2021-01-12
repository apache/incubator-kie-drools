package java.time;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;

public class ZonedDateTime implements Temporal {

    public static ZonedDateTime of(LocalDate date, LocalTime time, ZoneId zone) {
        return null;
    }

    public static ZonedDateTime of(LocalDateTime localDateTime, ZoneId zone) {
        return null;
    }

    public static ZonedDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond, ZoneId zone) {
        return null;
    }

    public Temporal minus(TemporalAmount amount) {
        return null;
    }

    public long until(final Temporal endExclusive, final TemporalUnit unit) {
        return 0;
    }

    public boolean isSupported(final TemporalUnit unit) {
        return false;
    }

    public Temporal with(final TemporalField field, final long newValue) {
        return null;
    }

    public ZonedDateTime plus(TemporalAmount amountToAdd) {
        return null;
    }

    public Temporal plus(final long amountToAdd, final TemporalUnit unit) {
        return null;
    }

    public static ZonedDateTime from(TemporalAccessor temporal) {
        return null;
    }

    public long toEpochSecond() {
        return 0;
    }

    public boolean isSupported(final TemporalField field) {
        return false;
    }

    public long getLong(final TemporalField field) {
        return 0;
    }

    public static ZonedDateTime now() {
        return null;
    }
}
package java.time;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;

public class LocalDateTime
        implements Temporal,
                   TemporalAdjuster,
                   ChronoLocalDateTime<LocalDate>,
                   Serializable {

    public static LocalDateTime from(TemporalAccessor temporal) {
        return null;
    }

    public Temporal adjustInto(Temporal temporal) {
        return null;
    }

    public LocalDate toLocalDate() {
        return null;
    }

    public LocalTime toLocalTime() {
        return null;
    }

    public ChronoLocalDateTime<LocalDate> with(final TemporalField field, final long newValue) {
        return null;
    }

    public LocalDateTime plus(TemporalAmount temporalAmount) {
        return null;
    }

    public ChronoLocalDateTime<LocalDate> plus(final long a, final TemporalUnit temporalUnit) {
        return null;
    }

    public long until(final Temporal endExclusive, final TemporalUnit unit) {
        return 0;
    }

    public ChronoZonedDateTime<LocalDate> atZone(final ZoneId zone) {
        return null;
    }

    public static LocalDateTime of(LocalDate date, LocalTime time) {
        return null;
    }

    public static LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
        return null;
    }

    public static LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        return null;
    }

    public static LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute) {
        return null;
    }

    public static LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
        return null;
    }

    public static LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute, int second) {
        return null;
    }

    public static LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute) {
        return null;
    }

    public boolean isSupported(final TemporalField field) {
        return false;
    }

    public long getLong(final TemporalField field) {
        return 0;
    }

    public LocalDateTime minus(TemporalAmount temporalAmount) {
        return null;
    }
}
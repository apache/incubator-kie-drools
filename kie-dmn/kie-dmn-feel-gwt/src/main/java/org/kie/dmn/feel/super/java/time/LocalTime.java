
package java.time;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;

public final class LocalTime
        implements Temporal,
                   TemporalAdjuster,
                   Comparable<LocalTime>,
                   Serializable {

    public static final LocalTime MIN = null;
    public static final LocalTime MAX = null;
    public static final LocalTime MIDNIGHT = null;
    public static final LocalTime NOON = null;

    public boolean isSupported(final TemporalField field) {
        return true;
    }

    public boolean isSupported(final TemporalUnit unit) {
        return true;
    }

    public ValueRange range(final TemporalField field) {
        return null;
    }

    public int get(final TemporalField field) {
        return 0;
    }

    public long getLong(final TemporalField field) {
        return 0L;
    }

    public int getHour() {
        return 0;
    }

    public int getMinute() {
        return 0;
    }

    public int getSecond() {
        return 0;
    }

    public int getNano() {
        return 0;
    }

    public LocalTime with(final TemporalAdjuster adjuster) {
        return null;
    }

    public LocalTime with(final TemporalField field, final long newValue) {
        return null;
    }

    public LocalTime withHour(final int hour) {
        return null;
    }

    public LocalTime withMinute(final int minute) {
        return null;
    }

    public LocalTime withSecond(final int second) {
        return null;
    }

    public LocalTime withNano(final int nanoOfSecond) {
        return null;
    }

    public LocalTime truncatedTo(final TemporalUnit unit) {
        return null;
    }

    public LocalTime plus(final TemporalAmount amountToAdd) {
        return null;
    }

    public LocalTime plus(final long amountToAdd, final TemporalUnit unit) {
        return null;
    }

    public LocalTime plusHours(final long hoursToAdd) {
        return null;
    }

    public LocalTime plusMinutes(final long minutesToAdd) {
        return null;
    }

    public LocalTime plusSeconds(final long secondstoAdd) {
        return null;
    }

    public LocalTime plusNanos(final long nanosToAdd) {
        return null;
    }

    public LocalTime minus(final TemporalAmount amountToSubtract) {
        return null;
    }

    public LocalTime minus(final long amountToSubtract, final TemporalUnit unit) {
        return null;
    }

    public LocalTime minusHours(final long hoursToSubtract) {
        return null;
    }

    public LocalTime minusMinutes(final long minutesToSubtract) {
        return null;
    }

    public LocalTime minusSeconds(final long secondsToSubtract) {
        return null;
    }

    public LocalTime minusNanos(final long nanosToSubtract) {
        return null;
    }

    public <R> R query(final TemporalQuery<R> query) {
        return null;
    }

    public Temporal adjustInto(final Temporal temporal) {
        return null;
    }

    public long until(final Temporal endExclusive, final TemporalUnit unit) {
        return 0L;
    }

    public String format(final DateTimeFormatter formatter) {
        return null;
    }

    public LocalDateTime atDate(final LocalDate date) {
        return null;
    }

    public OffsetTime atOffset(final ZoneOffset offset) {
        return null;
    }

    public int toSecondOfDay() {
        return 0;
    }

    public long toNanoOfDay() {
        return 0L;
    }

    public int compareTo(final LocalTime other) {
        return 0;
    }

    public boolean isAfter(final LocalTime other) {
        return true;
    }

    public boolean isBefore(final LocalTime other) {
        return true;
    }

    public boolean equals(final LocalTime obj) {
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return null;
    }

    public static LocalTime now() {
        return null;
    }

    public static LocalTime now(ZoneId zone) {
        return null;
    }

    public static LocalTime now(Clock clock) {
        return null;
    }

    public static LocalTime of(int hour, int minute) {
        return null;
    }

    public static LocalTime of(int hour, int minute, int second) {
        return null;
    }

    public static LocalTime of(int hour, int minute, int second, int nanoOfSecond) {
        return null;
    }

    public static LocalTime ofSecondOfDay(long secondOfDay) {
        return null;
    }

    public static LocalTime ofNanoOfDay(long nanoOfDay) {
        return null;
    }

    public static LocalTime from(TemporalAccessor temporal) {
        return null;
    }

    public static LocalTime parse(CharSequence text) {
        return null;
    }

    public static LocalTime parse(CharSequence text, DateTimeFormatter formatter) {
        return null;
    }
}

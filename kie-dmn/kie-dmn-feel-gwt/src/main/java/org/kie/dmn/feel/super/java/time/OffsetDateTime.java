
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
import java.util.Comparator;

public final class OffsetDateTime
        implements Temporal,
                   TemporalAdjuster,
                   Comparable<OffsetDateTime>,
                   Serializable {

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

    public ZoneOffset getOffset() {
        return null;
    }

    public OffsetDateTime withOffsetSameLocal(final ZoneOffset offset) {
        return null;
    }

    public OffsetDateTime withOffsetSameInstant(final ZoneOffset offset) {
        return null;
    }

    public LocalDateTime toLocalDateTime() {
        return null;
    }

    public LocalDate toLocalDate() {
        return null;
    }

    public int getYear() {
        return 0;
    }

    public int getMonthValue() {
        return 0;
    }

    public Month getMonth() {
        return null;
    }

    public int getDayOfMonth() {
        return 0;
    }

    public int getDayOfYear() {
        return 0;
    }

    public DayOfWeek getDayOfWeek() {
        return null;
    }

    public LocalTime toLocalTime() {
        return null;
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

    public OffsetDateTime with(final TemporalAdjuster adjuster) {
        return null;
    }

    public OffsetDateTime with(final TemporalField field, final long newValue) {
        return null;
    }

    public OffsetDateTime withYear(final int year) {
        return null;
    }

    public OffsetDateTime withMonth(final int month) {
        return null;
    }

    public OffsetDateTime withDayOfMonth(final int dayOfMonth) {
        return null;
    }

    public OffsetDateTime withDayOfYear(final int dayOfYear) {
        return null;
    }

    public OffsetDateTime withHour(final int hour) {
        return null;
    }

    public OffsetDateTime withMinute(final int minute) {
        return null;
    }

    public OffsetDateTime withSecond(final int second) {
        return null;
    }

    public OffsetDateTime withNano(final int nanoOfSecond) {
        return null;
    }

    public OffsetDateTime truncatedTo(final TemporalUnit unit) {
        return null;
    }

    public OffsetDateTime plus(final TemporalAmount amountToAdd) {
        return null;
    }

    public OffsetDateTime plus(final long amountToAdd, final TemporalUnit unit) {
        return null;
    }

    public OffsetDateTime plusYears(final long years) {
        return null;
    }

    public OffsetDateTime plusMonths(final long months) {
        return null;
    }

    public OffsetDateTime plusWeeks(final long weeks) {
        return null;
    }

    public OffsetDateTime plusDays(final long days) {
        return null;
    }

    public OffsetDateTime plusHours(final long hours) {
        return null;
    }

    public OffsetDateTime plusMinutes(final long minutes) {
        return null;
    }

    public OffsetDateTime plusSeconds(final long seconds) {
        return null;
    }

    public OffsetDateTime plusNanos(final long nanos) {
        return null;
    }

    public OffsetDateTime minus(final TemporalAmount amountToSubtract) {
        return null;
    }

    public OffsetDateTime minus(final long amountToSubtract, final TemporalUnit unit) {
        return null;
    }

    public OffsetDateTime minusYears(final long years) {
        return null;
    }

    public OffsetDateTime minusMonths(final long months) {
        return null;
    }

    public OffsetDateTime minusWeeks(final long weeks) {
        return null;
    }

    public OffsetDateTime minusDays(final long days) {
        return null;
    }

    public OffsetDateTime minusHours(final long hours) {
        return null;
    }

    public OffsetDateTime minusMinutes(final long minutes) {
        return null;
    }

    public OffsetDateTime minusSeconds(final long seconds) {
        return null;
    }

    public OffsetDateTime minusNanos(final long nanos) {
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

    public ZonedDateTime atZoneSameInstant(final ZoneId zone) {
        return null;
    }

    public ZonedDateTime atZoneSimilarLocal(final ZoneId zone) {
        return null;
    }

    public OffsetTime toOffsetTime() {
        return null;
    }

    public ZonedDateTime toZonedDateTime() {
        return null;
    }

    public Instant toInstant() {
        return null;
    }

    public long toEpochSecond() {
        return 0L;
    }

    public int compareTo(final OffsetDateTime other) {
        return 0;
    }

    public boolean isAfter(final OffsetDateTime other) {
        return true;
    }

    public boolean isBefore(final OffsetDateTime other) {
        return true;
    }

    public boolean isEqual(final OffsetDateTime other) {
        return true;
    }

    public boolean equals(final Object obj) {
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return null;
    }

    public static final OffsetDateTime MIN = LocalDateTime.MIN.atOffset(ZoneOffset.MAX);
    public static final OffsetDateTime MAX = LocalDateTime.MAX.atOffset(ZoneOffset.MIN);

    public static Comparator<OffsetDateTime> timeLineOrder() {
        return null;
    }

    public static OffsetDateTime now() {
        return null;
    }

    public static OffsetDateTime now(ZoneId zone) {
        return null;
    }

    public static OffsetDateTime now(Clock clock) {
        return null;
    }

    public static OffsetDateTime of(LocalDate date, LocalTime time, ZoneOffset offset) {
        return null;
    }

    public static OffsetDateTime of(LocalDateTime dateTime, ZoneOffset offset) {
        return null;
    }

    public static OffsetDateTime of(
            int year, int month, int dayOfMonth,
            int hour, int minute, int second, int nanoOfSecond, ZoneOffset offset) {
        return null;
    }

    public static OffsetDateTime ofInstant(Instant instant, ZoneId zone) {
        return null;
    }

    public static OffsetDateTime from(TemporalAccessor temporal) {
        return null;
    }

    public static OffsetDateTime parse(CharSequence text) {
        return null;
    }

    public static OffsetDateTime parse(CharSequence text, DateTimeFormatter formatter) {
        return null;
    }
}

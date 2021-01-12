
package java.time;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;

public final class LocalDateTime
        implements Temporal,
                   TemporalAdjuster,
                   ChronoLocalDateTime<LocalDate>,
                   Serializable {

    public static final LocalDateTime MIN = LocalDateTime.of(LocalDate.MIN, LocalTime.MIN);

    public static final LocalDateTime MAX = LocalDateTime.of(LocalDate.MAX, LocalTime.MAX);

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

    public LocalDateTime with(final TemporalAdjuster adjuster) {
        return null;
    }

    public LocalDateTime with(final TemporalField field, final long newValue) {
        return null;
    }

    public LocalDateTime withYear(final int year) {
        return null;
    }

    public LocalDateTime withMonth(final int month) {
        return null;
    }

    public LocalDateTime withDayOfMonth(final int dayOfMonth) {
        return null;
    }

    public LocalDateTime withDayOfYear(final int dayOfYear) {
        return null;
    }

    public LocalDateTime withHour(final int hour) {
        return null;
    }

    public LocalDateTime withMinute(final int minute) {
        return null;
    }

    public LocalDateTime withSecond(final int second) {
        return null;
    }

    public LocalDateTime withNano(final int nanoOfSecond) {
        return null;
    }

    public LocalDateTime truncatedTo(final TemporalUnit unit) {
        return null;
    }

    public LocalDateTime plus(final TemporalAmount amountToAdd) {
        return null;
    }

    public LocalDateTime plus(final long amountToAdd, final TemporalUnit unit) {
        return null;
    }

    public LocalDateTime plusYears(final long years) {
        return null;
    }

    public LocalDateTime plusMonths(final long months) {
        return null;
    }

    public LocalDateTime plusWeeks(final long weeks) {
        return null;
    }

    public LocalDateTime plusDays(final long days) {
        return null;
    }

    public LocalDateTime plusHours(final long hours) {
        return null;
    }

    public LocalDateTime plusMinutes(final long minutes) {
        return null;
    }

    public LocalDateTime plusSeconds(final long seconds) {
        return null;
    }

    public LocalDateTime plusNanos(final long nanos) {
        return null;
    }

    public LocalDateTime minus(final TemporalAmount amountToSubtract) {
        return null;
    }

    public LocalDateTime minus(final long amountToSubtract, final TemporalUnit unit) {
        return null;
    }

    public LocalDateTime minusYears(final long years) {
        return null;
    }

    public LocalDateTime minusMonths(final long months) {
        return null;
    }

    public LocalDateTime minusWeeks(final long weeks) {
        return null;
    }

    public LocalDateTime minusDays(final long days) {
        return null;
    }

    public LocalDateTime minusHours(final long hours) {
        return null;
    }

    public LocalDateTime minusMinutes(final long minutes) {
        return null;
    }

    public LocalDateTime minusSeconds(final long seconds) {
        return null;
    }

    public LocalDateTime minusNanos(final long nanos) {
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

    public OffsetDateTime atOffset(final ZoneOffset offset) {
        return null;
    }

    public ZonedDateTime atZone(final ZoneId zone) {
        return null;
    }

    public int compareTo(final ChronoLocalDateTime<?> other) {
        return 0;
    }

    public boolean isAfter(final ChronoLocalDateTime<?> other) {
        return true;
    }

    public boolean isBefore(final ChronoLocalDateTime<?> other) {
        return true;
    }

    public boolean isEqual(final ChronoLocalDateTime<?> other) {
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

    public static LocalDateTime now() {
        return null;
    }

    public static LocalDateTime now(ZoneId zone) {
        return null;
    }

    public static LocalDateTime now(Clock clock) {
        return null;
    }

    public static LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute) {
        return null;
    }

    public static LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute, int second) {
        return null;
    }

    public static LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
        return null;
    }

    public static LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute) {
        return null;
    }

    public static LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        return null;
    }

    public static LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
        return null;
    }

    public static LocalDateTime of(LocalDate date, LocalTime time) {
        return null;
    }

    public static LocalDateTime ofInstant(Instant instant, ZoneId zone) {
        return null;
    }

    public static LocalDateTime ofEpochSecond(long epochSecond, int nanoOfSecond, ZoneOffset offset) {
        return null;
    }

    public static LocalDateTime from(TemporalAccessor temporal) {
        return null;
    }

    public static LocalDateTime parse(CharSequence text) {
        return null;
    }

    public static LocalDateTime parse(CharSequence text, DateTimeFormatter formatter) {
        return null;
    }
}

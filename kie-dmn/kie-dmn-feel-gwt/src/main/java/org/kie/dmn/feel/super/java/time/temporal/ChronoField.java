
package java.time.temporal;

import java.util.Locale;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.ERAS;
import static java.time.temporal.ChronoUnit.FOREVER;
import static java.time.temporal.ChronoUnit.HALF_DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MICROS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;

public enum ChronoField implements TemporalField {

    NANO_OF_SECOND("NanoOfSecond", NANOS, SECONDS, null),
    NANO_OF_DAY("NanoOfDay", NANOS, DAYS, null),
    MICRO_OF_SECOND("MicroOfSecond", MICROS, SECONDS, null),
    MICRO_OF_DAY("MicroOfDay", MICROS, DAYS, null),
    MILLI_OF_SECOND("MilliOfSecond", MILLIS, SECONDS, null),
    MILLI_OF_DAY("MilliOfDay", MILLIS, DAYS, null),
    SECOND_OF_MINUTE("SecondOfMinute", SECONDS, MINUTES, null),
    SECOND_OF_DAY("SecondOfDay", SECONDS, DAYS, null),
    MINUTE_OF_HOUR("MinuteOfHour", MINUTES, HOURS, null),
    MINUTE_OF_DAY("MinuteOfDay", MINUTES, DAYS, null),
    HOUR_OF_AMPM("HourOfAmPm", HOURS, HALF_DAYS, null),
    CLOCK_HOUR_OF_AMPM("ClockHourOfAmPm", HOURS, HALF_DAYS, null),
    HOUR_OF_DAY("HourOfDay", HOURS, DAYS, null),
    CLOCK_HOUR_OF_DAY("ClockHourOfDay", HOURS, DAYS, null),
    AMPM_OF_DAY("AmPmOfDay", HALF_DAYS, DAYS, null),
    DAY_OF_WEEK("DayOfWeek", DAYS, WEEKS, null),
    ALIGNED_DAY_OF_WEEK_IN_MONTH("AlignedDayOfWeekInMonth", DAYS, WEEKS, null),
    ALIGNED_DAY_OF_WEEK_IN_YEAR("AlignedDayOfWeekInYear", DAYS, WEEKS, null),
    DAY_OF_MONTH("DayOfMonth", DAYS, MONTHS, null),
    DAY_OF_YEAR("DayOfYear", DAYS, YEARS, null),
    EPOCH_DAY("EpochDay", DAYS, FOREVER, null),
    ALIGNED_WEEK_OF_MONTH("AlignedWeekOfMonth", WEEKS, MONTHS, null),
    ALIGNED_WEEK_OF_YEAR("AlignedWeekOfYear", WEEKS, YEARS, null),
    MONTH_OF_YEAR("MonthOfYear", MONTHS, YEARS, null),
    PROLEPTIC_MONTH("ProlepticMonth", MONTHS, FOREVER, null),
    YEAR_OF_ERA("YearOfEra", YEARS, FOREVER, null),
    YEAR("Year", YEARS, FOREVER, null),
    ERA("Era", ERAS, FOREVER, null),
    INSTANT_SECONDS("InstantSeconds", SECONDS, FOREVER, null),
    OFFSET_SECONDS("OffsetSeconds", SECONDS, FOREVER, null);

    private ChronoField(String name, TemporalUnit baseUnit, TemporalUnit rangeUnit, ValueRange range) {

    }

    private ChronoField(String name, TemporalUnit baseUnit, TemporalUnit rangeUnit,
                        ValueRange range, String displayNameKey) {

    }

    public String getDisplayName(Locale locale) {
        return null;
    }

    public TemporalUnit getBaseUnit() {
        return null;
    }

    public TemporalUnit getRangeUnit() {
        return null;
    }

    public ValueRange range() {
        return null;
    }

    public boolean isDateBased() {
        return true;
    }

    public boolean isTimeBased() {
        return true;
    }

    public long checkValidValue(long value) {
        return 0;
    }

    public int checkValidIntValue(long value) {
        return 0;
    }

    public boolean isSupportedBy(TemporalAccessor temporal) {
        return true;
    }

    public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
        return null;
    }

    public long getFrom(TemporalAccessor temporal) {
        return 0L;
    }

    public <R extends Temporal> R adjustInto(R temporal, long newValue) {
        return null;
    }

    public String toString() {
        return null;
    }
}

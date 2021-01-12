package java.time.chrono;

import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.List;
import java.util.Map;

public class IsoChronology implements Chronology{

    public String getId() {
        return null;
    }

    public String getCalendarType() {
        return null;
    }

    public ChronoLocalDate date(final int prolepticYear, final int month, final int dayOfMonth) {
        return null;
    }

    public ChronoLocalDate dateYearDay(final int prolepticYear, final int dayOfYear) {
        return null;
    }

    public ChronoLocalDate dateEpochDay(final long epochDay) {
        return null;
    }

    public ChronoLocalDate date(final TemporalAccessor temporal) {
        return null;
    }

    public boolean isLeapYear(final long prolepticYear) {
        return false;
    }

    public int prolepticYear(final Era era, final int yearOfEra) {
        return 0;
    }

    public Era eraOf(final int eraValue) {
        return null;
    }

    public List<Era> eras() {
        return null;
    }

    public ValueRange range(final ChronoField field) {
        return null;
    }

    public ChronoLocalDate resolveDate(final Map<TemporalField, Long> fieldValues, final ResolverStyle resolverStyle) {
        return null;
    }

    public int compareTo(final Chronology other) {
        return 0;
    }
}
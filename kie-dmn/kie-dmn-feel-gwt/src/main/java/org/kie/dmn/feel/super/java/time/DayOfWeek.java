
package java.time;

import java.util.Locale;

import java.time.format.TextStyle;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.ValueRange;

public enum DayOfWeek implements TemporalAccessor,
                                 TemporalAdjuster {

    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public static DayOfWeek of(int dayOfWeek) {
        return null;
    }

    public static DayOfWeek from(TemporalAccessor temporal) {
        return null;
    }

    public int getValue() {
        return 0;
    }

    public String getDisplayName(TextStyle style, Locale locale) {
        return null;
    }

    public boolean isSupported(TemporalField field) {
        return true;
    }

    public ValueRange range(TemporalField field) {
        return null;
    }

    public int get(TemporalField field) {
        return 0;
    }

    public long getLong(TemporalField field) {
        return 0L;
    }

    public DayOfWeek plus(long days) {
        return null;
    }

    public DayOfWeek minus(long days) {
        return null;
    }

    public <R> R query(TemporalQuery<R> query) {
        return null;
    }

    public Temporal adjustInto(Temporal temporal) {
        return null;
    }
}

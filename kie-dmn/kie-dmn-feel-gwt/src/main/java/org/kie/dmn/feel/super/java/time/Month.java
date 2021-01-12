
package java.time;

import java.util.Locale;

import java.time.format.TextStyle;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.ValueRange;

public enum Month implements TemporalAccessor,
                             TemporalAdjuster {

    JANUARY,
    FEBRUARY,
    MARCH,
    APRIL,
    MAY,
    JUNE,
    JULY,
    AUGUST,
    SEPTEMBER,
    OCTOBER,
    NOVEMBER,
    DECEMBER;

    public static Month of(int month) {
        return null;
    }

    public static Month from(TemporalAccessor temporal) {
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

    public Month plus(long months) {
        return null;
    }

    public Month minus(long months) {
        return null;
    }

    public int length(boolean leapYear) {
        return 0;
    }

    public int minLength() {
        return 0;
    }

    public int maxLength() {
        return 0;
    }

    public int firstDayOfYear(boolean leapYear) {
        return 0;
    }

    public Month firstMonthOfQuarter() {
        return null;
    }

    public <R> R query(TemporalQuery<R> query) {
        return null;
    }

    public Temporal adjustInto(Temporal temporal) {
        return null;
    }
}

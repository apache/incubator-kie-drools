
package java.time;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;

public final class LocalDate
        implements Temporal,
                   TemporalAdjuster,
                   ChronoLocalDate,
                   Serializable {

    public Chronology getChronology() {
        return null;
    }

    public int lengthOfMonth() {
        return 0;
    }

    public ChronoPeriod until(final ChronoLocalDate endDateExclusive) {
        return null;
    }

    public long until(final Temporal endExclusive, final TemporalUnit unit) {
        return 0;
    }

    public long getLong(final TemporalField field) {
        return 0;
    }

    public static LocalDate from(TemporalAccessor temporal) {
        return null;
    }

    public static LocalDate of(int year, int month, int dayOfMonth) {
        return null;
    }

    public LocalDate plusDays(long daysToAdd) {
        return null;
    }

    public LocalDate minusDays(long daysToSubtract) {
        return null;
    }
}

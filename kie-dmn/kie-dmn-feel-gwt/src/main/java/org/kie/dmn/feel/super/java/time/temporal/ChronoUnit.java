
package java.time.temporal;

import java.time.Duration;

public enum ChronoUnit implements TemporalUnit {

    NANOS("Nanos", null),
    MICROS("Micros", null),
    MILLIS("Millis", null),
    SECONDS("Seconds", null),
    MINUTES("Minutes", null),
    HOURS("Hours", null),
    HALF_DAYS("HalfDays", null),
    DAYS("Days", null),
    WEEKS("Weeks", null),
    MONTHS("Months", null),
    YEARS("Years", null),
    DECADES("Decades", null),
    CENTURIES("Centuries", null),
    MILLENNIA("Millennia", null),
    ERAS("Eras", null),
    FOREVER("Forever", null);

    private ChronoUnit(String name, Duration estimatedDuration) {
    }

    public Duration getDuration() {
        return null;
    }

    public boolean isDurationEstimated() {
        return true;
    }

    public boolean isDateBased() {
        return true;
    }

    public boolean isTimeBased() {
        return true;
    }

    public boolean isSupportedBy(Temporal temporal) {
        return true;
    }

    public <R extends Temporal> R addTo(R temporal, long amount) {
        return null;
    }

    public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
        return 0;
    }

    public String toString() {
        return "";
    }
}

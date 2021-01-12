
package java.time.temporal;

import java.time.Duration;

public enum ChronoUnit implements TemporalUnit {
    DAYS,
    MONTHS,
    YEARS;

    public Duration getDuration() {
        return null;
    }

    public boolean isDurationEstimated() {
        return false;
    }

    public boolean isDateBased() {
        return false;
    }

    public boolean isTimeBased() {
        return false;
    }

    public <R extends Temporal> R addTo(final R temporal, final long amount) {
        return null;
    }

    public long between(Temporal a, Temporal b) {
        return 0;
    }
}
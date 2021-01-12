
package java.time;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

public class Duration
        implements TemporalAmount {

    public long getSeconds() {
        return 0;
    }

    public int getNano() {
        return 0;
    }

    public boolean isNegative() {
        return false;
    }

    public long toDays() {
        return 0;
    }

    public long toHours() {
        return 0;
    }

    public long toMinutes() {
        return 0;
    }

    public static Duration ofSeconds(long seconds) {
        return null;
    }

    public Duration plus(Duration duration) {
        return null;
    }

    @Override
    public long get(final TemporalUnit unit) {
        return 0;
    }

    @Override
    public List<TemporalUnit> getUnits() {
        return null;
    }

    @Override
    public Temporal addTo(final Temporal temporal) {
        return null;
    }

    @Override
    public Temporal subtractFrom(final Temporal temporal) {
        return null;
    }

    public static Duration ofDays(long days) {
        return null;
    }

    public static Duration between(Temporal startInclusive, Temporal endExclusive) {
        return null;
    }

    public Duration minus(Duration duration) {
        return null;
    }

    public Duration multipliedBy(long multiplicand) {
        return null;
    }

    public Duration dividedBy(long divisor) {
        return null;
    }

    public static Duration parse(CharSequence text) {
        return null;
    }

    public Duration abs() {
        return null;
    }
}

package java.time.chrono;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Objects;

public interface ChronoPeriod
        extends TemporalAmount {

    static ChronoPeriod between(ChronoLocalDate startDateInclusive, ChronoLocalDate endDateExclusive) {
        Objects.requireNonNull(startDateInclusive, "startDateInclusive");
        Objects.requireNonNull(endDateExclusive, "endDateExclusive");
        return startDateInclusive.until(endDateExclusive);
    }

    long get(TemporalUnit unit);

    List<TemporalUnit> getUnits();

    Chronology getChronology();

    default boolean isZero() {
        for (TemporalUnit unit : getUnits()) {
            if (get(unit) != 0) {
                return false;
            }
        }
        return true;
    }

    default boolean isNegative() {
        for (TemporalUnit unit : getUnits()) {
            if (get(unit) < 0) {
                return true;
            }
        }
        return false;
    }

    ChronoPeriod plus(TemporalAmount amountToAdd);

    ChronoPeriod minus(TemporalAmount amountToSubtract);

    ChronoPeriod multipliedBy(int scalar);

    default ChronoPeriod negated() {
        return multipliedBy(-1);
    }

    ChronoPeriod normalized();

    Temporal addTo(Temporal temporal);

    Temporal subtractFrom(Temporal temporal);

    boolean equals(Object obj);

    int hashCode();

    String toString();
}

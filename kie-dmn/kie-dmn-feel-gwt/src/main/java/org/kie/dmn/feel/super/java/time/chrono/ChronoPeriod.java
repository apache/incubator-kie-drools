package java.time.chrono;

import java.time.temporal.TemporalAmount;

public interface ChronoPeriod
        extends TemporalAmount {

    ChronoPeriod plus(TemporalAmount amountToAdd);

    ChronoPeriod minus(TemporalAmount amountToSubtract);

    ChronoPeriod normalized();

    Chronology getChronology();

    ChronoPeriod multipliedBy(int scalar);
}
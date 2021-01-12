package java.time;

import java.time.chrono.ChronoPeriod;
import java.time.chrono.IsoChronology;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

public class Period
        implements ChronoPeriod {

    public Period minus(TemporalAmount amountToSubtract) {
        return null;
    }

    public Temporal subtractFrom(Temporal temporal) {
        return null;
    }

    public long get(TemporalUnit unit) {
        return 0;
    }

    public static Period ofMonths(int months) {
        return null;
    }

    public int getYears() {
        return 0;
    }

    public Period multipliedBy(int scalar) {
        return null;
    }

    public List<TemporalUnit> getUnits() {
        return null;
    }

    public static Period of(int years, int months, int days) {
        return null;
    }

    public Period negated() {
        return null;
    }

    public long toTotalMonths() {
        return 0;
    }

    public IsoChronology getChronology() {
        return null;
    }

    public Temporal addTo(Temporal temporal) {
        return null;
    }

    public Period plus(TemporalAmount amountToAdd) {
        return null;
    }

    public int getMonths() {
        return 0;
    }

    public static Period between(LocalDate startDateInclusive, LocalDate endDateExclusive) {
        return null;
    }

    public Period withDays(int days) {
        return null;
    }

    public static Period from(TemporalAmount amount) {
        return null;
    }

    public static Period parse(CharSequence text) {
        return null;
    }

    public Period normalized() {
        return null;
    }
}
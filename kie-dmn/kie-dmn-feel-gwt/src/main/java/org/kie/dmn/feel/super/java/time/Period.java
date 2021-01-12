
package java.time;

import java.io.Serializable;
import java.util.List;

import java.time.chrono.ChronoPeriod;
import java.time.chrono.IsoChronology;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

public final class Period
        implements ChronoPeriod,
                   Serializable {

    public long get(final TemporalUnit unit) {
        return 0L;
    }

    public List<TemporalUnit> getUnits() {
        return null;
    }

    public IsoChronology getChronology() {
        return null;
    }

    public boolean isZero() {
        return true;
    }

    public boolean isNegative() {
        return true;
    }

    public int getYears() {
        return 0;
    }

    public int getMonths() {
        return 0;
    }

    public int getDays() {
        return 0;
    }

    public Period withYears(final int years) {
        return null;
    }

    public Period withMonths(final int months) {
        return null;
    }

    public Period withDays(final int days) {
        return null;
    }

    public Period plus(final TemporalAmount amountToAdd) {
        return null;
    }

    public Period plusYears(final long yearsToAdd) {
        return null;
    }

    public Period plusMonths(final long monthsToAdd) {
        return null;
    }

    public Period plusDays(final long daysToAdd) {
        return null;
    }

    public Period minus(final TemporalAmount amountToSubtract) {
        return null;
    }

    public Period minusYears(final long yearsToSubtract) {
        return null;
    }

    public Period minusMonths(final long monthsToSubtract) {
        return null;
    }

    public Period minusDays(final long daysToSubtract) {
        return null;
    }

    public Period multipliedBy(final int scalar) {
        return null;
    }

    public Period negated() {
        return null;
    }

    public Period normalized() {
        return null;
    }

    public long toTotalMonths() {
        return 0L;
    }

    public Temporal addTo(final Temporal temporal) {
        return null;
    }

    public Temporal subtractFrom(final Temporal temporal) {
        return null;
    }

    public boolean equals(final Period obj) {
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return null;
    }
}

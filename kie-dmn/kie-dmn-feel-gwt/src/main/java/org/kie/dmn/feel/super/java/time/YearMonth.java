package java.time;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;

public final class YearMonth implements Temporal {

    public boolean isSupported(final TemporalUnit unit) {
        return false;
    }

    public Temporal with(final TemporalField field, final long newValue) {
        return null;
    }

    public Temporal plus(final long amountToAdd, final TemporalUnit unit) {
        return null;
    }

    public long until(final Temporal endExclusive, final TemporalUnit unit) {
        return 0;
    }

    public boolean isSupported(final TemporalField field) {
        return false;
    }

    public long getLong(final TemporalField field) {
        return 0;
    }

    public int getYear() {
        return 0;
    }

    public Month getMonth() {
        return null;
    }

    public YearMonth minus(TemporalAmount amountToSubtract) {
        return null;
    }
}

package java.time.temporal;

public interface Temporal extends TemporalAccessor {

    boolean isSupported(TemporalUnit unit);

    default Temporal with(TemporalAdjuster adjuster) {
        return null;
    }

    Temporal with(TemporalField field, long newValue);

    default Temporal plus(TemporalAmount amount) {
        return null;
    }

    Temporal plus(long amountToAdd, TemporalUnit unit);

    default Temporal minus(TemporalAmount amount) {
        return null;
    }

    default Temporal minus(long amountToSubtract, TemporalUnit unit) {
        return null;
    }

    long until(Temporal endExclusive, TemporalUnit unit);
}

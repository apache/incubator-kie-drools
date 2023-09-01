package org.drools.model.functions.temporal;

import java.util.concurrent.TimeUnit;

import static org.drools.model.functions.temporal.TimeUtil.unitToLong;

public class MetbyPredicate extends AbstractTemporalPredicate<MetbyPredicate> {

    private final long finalRange;

    public MetbyPredicate() {
        this(0);
    }

    public MetbyPredicate(long finalRange, TimeUnit finalRangeTimeUnit) {
        this( unitToLong(finalRange, finalRangeTimeUnit) );
    }

    private MetbyPredicate(long finalRange) {
        this.finalRange = finalRange;
    }

    @Override
    public String toString() {
        return (negated ? "not " : "") + "metBy[" + finalRange + "]";
    }

    @Override
    public Interval getInterval() {
        return negated ? new Interval( Interval.MIN, Interval.MAX ) : new Interval( Interval.MIN, 0 );
    }

    @Override
    public boolean evaluate(long start1, long duration1, long end1, long start2, long duration2, long end2) {
        long dist = Math.abs( start1 - end2 );
        return negated ^ ( dist <= this.finalRange );
    }

    @Override
    protected boolean isTemporalPredicateEqualTo( MetbyPredicate other ) {
        return finalRange == other.finalRange;
    }
}

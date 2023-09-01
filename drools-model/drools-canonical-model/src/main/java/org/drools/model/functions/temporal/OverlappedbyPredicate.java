package org.drools.model.functions.temporal;

import java.util.concurrent.TimeUnit;

import static org.drools.model.functions.temporal.TimeUtil.unitToLong;

public class OverlappedbyPredicate extends AbstractTemporalPredicate<OverlappedbyPredicate> {

    private final long minDev;
    private final long maxDev;

    public OverlappedbyPredicate() {
        this(1, Long.MAX_VALUE);
    }

    public OverlappedbyPredicate(long maxDev, TimeUnit maxDevTimeUnit) {
        this(1, unitToLong(maxDev, maxDevTimeUnit));
    }

    public OverlappedbyPredicate(long minDev, TimeUnit minDevTimeUnit, long maxDev, TimeUnit maxDevTimeUnit) {
        this( unitToLong(minDev, minDevTimeUnit), unitToLong(maxDev, maxDevTimeUnit) );
    }

    private OverlappedbyPredicate(long minDev, long maxDev) {
        this.minDev = minDev;
        this.maxDev = maxDev;
    }

    @Override
    public String toString() {
        return (negated ? "not " : "") + "overlappedby[" + minDev + ", " + maxDev + "]";
    }

    @Override
    public Interval getInterval() {
        return negated ? new Interval( Interval.MIN, Interval.MAX ) : new Interval( 0, Interval.MAX );
    }

    @Override
    public boolean evaluate(long start1, long duration1, long end1, long start2, long duration2, long end2) {

        long startTS = start1;
        long endTS = end2;
        long dist = endTS - startTS;
        return negated ^ ( start2 < startTS && endTS < end1 && dist >= this.minDev && dist <= this.maxDev );
    }

    @Override
    protected boolean isTemporalPredicateEqualTo( OverlappedbyPredicate other ) {
        return minDev == other.minDev && maxDev == other.maxDev;
    }
}

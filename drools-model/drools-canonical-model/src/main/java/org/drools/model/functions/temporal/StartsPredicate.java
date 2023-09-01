package org.drools.model.functions.temporal;

import java.util.concurrent.TimeUnit;

import static org.drools.model.functions.temporal.TimeUtil.unitToLong;

public class StartsPredicate extends AbstractTemporalPredicate<StartsPredicate> {

    private final long startDev;

    public StartsPredicate() {
        this(0);
    }

    public StartsPredicate(long endDev, TimeUnit endDevTimeUnit) {
        this( unitToLong(endDev, endDevTimeUnit) );
    }

    private StartsPredicate(long startDev) {
        this.startDev = startDev;
    }

    @Override
    public String toString() {
        return (negated ? "not " : "") + "starts[" + startDev + "]";
    }

    @Override
    public Interval getInterval() {
        return negated ? new Interval( Interval.MIN, Interval.MAX ) : new Interval( 0, 0 );
    }

    @Override
    public boolean evaluate(long start1, long duration1, long end1, long start2, long duration2, long end2) {

        long distStart = Math.abs( start1 - start2 );
        long distEnd = end2 - end1;
        return negated ^ (distStart <= this.startDev && distEnd > 0 );
    }

    @Override
    protected boolean isTemporalPredicateEqualTo( StartsPredicate other ) {
        return startDev == other.startDev;
    }
}

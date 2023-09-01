package org.drools.model.functions.temporal;

import java.util.concurrent.TimeUnit;

import static org.drools.model.functions.temporal.TimeUtil.unitToLong;

public class FinishesPredicate extends AbstractTemporalPredicate<FinishesPredicate> {

    private final long endDev;

    public FinishesPredicate() {
        this(0);
    }

    public FinishesPredicate(long endDev, TimeUnit endDevTimeUnit) {
        this( unitToLong(endDev, endDevTimeUnit) );
    }

    private FinishesPredicate(long endDev) {
        this.endDev = endDev;
    }

    @Override
    public String toString() {
        return (negated ? "not " : "") + "finishes[" + endDev + "]";
    }

    @Override
    public Interval getInterval() {
        return negated ? new Interval( Interval.MIN, Interval.MAX ) : new Interval( 0, Interval.MAX );
    }

    @Override
    public boolean evaluate(long start1, long duration1, long end1, long start2, long duration2, long end2) {

        long distStart = start1 - start2;
        long distEnd = Math.abs( end2 - end1 );
        return negated ^ (distStart > 0 && distEnd <= this.endDev);
    }

    @Override
    protected boolean isTemporalPredicateEqualTo( FinishesPredicate other ) {
        return endDev == other.endDev;
    }
}

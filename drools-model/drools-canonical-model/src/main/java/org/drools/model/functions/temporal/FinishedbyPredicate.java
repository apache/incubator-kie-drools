package org.drools.model.functions.temporal;

import java.util.concurrent.TimeUnit;

import static org.drools.model.functions.temporal.TimeUtil.unitToLong;

public class FinishedbyPredicate extends AbstractTemporalPredicate<FinishedbyPredicate> {

    private final long endDev;

    public FinishedbyPredicate() {
        this(0);
    }

    public FinishedbyPredicate(long endDev, TimeUnit endDevTimeUnit) {
        this( unitToLong(endDev, endDevTimeUnit) );
    }

    private FinishedbyPredicate(long endDev) {
        this.endDev = endDev;
    }

    @Override
    public String toString() {
        return (negated ? "not " : "") + "finishedby[" + endDev + "]";
    }

    @Override
    public Interval getInterval() {
        return negated ? new Interval( Interval.MIN, Interval.MAX ) : new Interval( Interval.MIN, 0 );
    }

    @Override
    public boolean evaluate(long start1, long duration1, long end1, long start2, long duration2, long end2) {
        long distStart = start2 - start1;
        long distEnd = Math.abs(end2 - end1);
        return negated ^ (distStart > 0 && distEnd <= this.endDev);
    }

    @Override
    protected boolean isTemporalPredicateEqualTo( FinishedbyPredicate other ) {
        return endDev == other.endDev;
    }
}

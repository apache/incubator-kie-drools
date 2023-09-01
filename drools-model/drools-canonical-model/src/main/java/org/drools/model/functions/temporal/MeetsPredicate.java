package org.drools.model.functions.temporal;

import java.util.concurrent.TimeUnit;

import static org.drools.model.functions.temporal.TimeUtil.unitToLong;

public class MeetsPredicate extends AbstractTemporalPredicate<MeetsPredicate> {

    private final long finalRange;

    public MeetsPredicate() {
        this(0);
    }

    public MeetsPredicate(long finalRange, TimeUnit finalRangeTimeUnit) {
        this( unitToLong(finalRange, finalRangeTimeUnit) );
    }

    private MeetsPredicate(long finalRange) {
        this.finalRange = finalRange;
    }

    @Override
    public String toString() {
        return (negated ? "not " : "") + "meets[" + finalRange + "]";
    }

    @Override
    public Interval getInterval() {
        return negated ? new Interval( Interval.MIN, Interval.MAX ) : new Interval( 0, Interval.MAX );
    }

    @Override
    public boolean evaluate(long start1, long duration1, long end1, long start2, long duration2, long end2) {
        long dist = Math.abs( start2 - end1 );
        return negated ^ (dist <= this.finalRange);
    }

    @Override
    protected boolean isTemporalPredicateEqualTo( MeetsPredicate other ) {
        return finalRange == other.finalRange;
    }
}

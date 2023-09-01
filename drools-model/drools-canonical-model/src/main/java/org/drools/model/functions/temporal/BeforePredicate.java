package org.drools.model.functions.temporal;

import java.util.concurrent.TimeUnit;

import static org.drools.model.functions.temporal.TimeUtil.unitToLong;

public class BeforePredicate extends AbstractTemporalPredicate<BeforePredicate> {

    private final long initRange;
    private final long finalRange;

    public BeforePredicate() {
        this( 1, Long.MAX_VALUE );
    }

    public BeforePredicate(long dev, TimeUnit devUnit) {
        this( unitToLong(dev, devUnit), Long.MAX_VALUE );
    }

    public BeforePredicate(long startDev, TimeUnit startDevUnit, long endDev, TimeUnit endDevUnit) {
        this( unitToLong(startDev, startDevUnit), unitToLong(endDev, endDevUnit) );
    }

    private BeforePredicate(long initRange, long finalRange) {
        this.initRange = initRange;
        this.finalRange = finalRange;
    }

    @Override
    public String toString() {
        return (negated ? "not " : "") + "before[" + initRange + ", " + finalRange + "]";
    }

    @Override
    public boolean evaluate(long start1, long duration1, long end1, long start2, long duration2, long end2) {
        long diff = start2 - end1;
        return negated ^ (diff >= initRange && diff <= finalRange);
    }

    @Override
    public Interval getInterval() {
        long init = (this.finalRange == Interval.MAX) ? Interval.MIN : -this.finalRange;
        long end = (this.initRange == Interval.MIN) ? Interval.MAX : -this.initRange;
        if ( negated ) {
            if ( init == Interval.MIN && end != Interval.MAX ) {
                init = finalRange + 1;
                end = Interval.MAX;
            } else if ( init != Interval.MIN && end == Interval.MAX ) {
                init = Interval.MIN;
                end = initRange - 1;
            } else if ( init == Interval.MIN ) {
                init = 0;
                end = -1;
            } else {
                init = Interval.MIN;
                end = Interval.MAX;
            }
        }
        return new Interval( init, end );
    }

    @Override
    protected boolean isTemporalPredicateEqualTo( BeforePredicate other ) {
        return initRange == other.initRange && finalRange == other.finalRange;
    }
}

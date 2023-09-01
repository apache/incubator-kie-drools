package org.drools.model.functions.temporal;

import java.util.concurrent.TimeUnit;

import static org.drools.model.functions.temporal.TimeUtil.unitToLong;

public class CoincidesPredicate extends AbstractTemporalPredicate<CoincidesPredicate> {

    private final long startDevLong;
    private final long endDevLong;

    public CoincidesPredicate(long dev, TimeUnit devUnit) {
        this( unitToLong(dev, devUnit), unitToLong(dev, devUnit) );
    }

    public CoincidesPredicate(long startDev, TimeUnit startDevUnit, long endDev, TimeUnit endDevUnit) {
        this( unitToLong(startDev, startDevUnit), unitToLong(endDev, endDevUnit) );
    }

    private CoincidesPredicate(long startDev, long endDev) {
        this.startDevLong = startDev;
        this.endDevLong = endDev;
    }

    @Override
    public String toString() {
        return (negated ? "not " : "") + "coincides[" + startDevLong + ", " + endDevLong + "]";
    }

    @Override
    public Interval getInterval() {
        return negated ? new Interval( Interval.MIN, Interval.MAX ) : new Interval( 0, 0 );
    }

    @Override
    public boolean evaluate(long start1, long duration1, long end1, long start2, long duration2, long end2) {
        long distStart = Math.abs( start2 - start1 );
        long distEnd = Math.abs( end2 - end1 );
        return negated ^ (distStart <= this.startDevLong && distEnd <= this.endDevLong);
    }

    @Override
    protected boolean isTemporalPredicateEqualTo( CoincidesPredicate other ) {
        return startDevLong == other.startDevLong && endDevLong == other.endDevLong;
    }
}

package org.drools.model.functions.temporal;

import java.util.concurrent.TimeUnit;

import static org.drools.model.functions.temporal.TimeUtil.unitToLong;

public class IncludesPredicate extends AbstractTemporalPredicate<IncludesPredicate> {

    private final long startMinDev, startMaxDev;
    private final long endMinDev, endMaxDev;

    public IncludesPredicate() {
        this(1, Long.MAX_VALUE);
    }

    public IncludesPredicate(long max, TimeUnit maxUnit) {
        this(1, unitToLong(max, maxUnit));
    }

    public IncludesPredicate(long min, TimeUnit minUnit, long max, TimeUnit maxUnit) {
        this(unitToLong(min, minUnit), unitToLong(max, maxUnit));
    }

    private IncludesPredicate( long min, long max ) {
        this.startMinDev = min;
        this.startMaxDev = max;
        this.endMinDev = min;
        this.endMaxDev = max;
    }

    @Override
    public String toString() {
        return (negated ? "not " : "") + "includes[" + startMinDev + ", " + startMaxDev + "]";
    }

    @Override
    public Interval getInterval() {
        return negated ? new Interval( Interval.MIN, Interval.MAX ) : new Interval( Interval.MIN, 0 );
    }

    @Override
    public boolean evaluate(long start1, long duration1, long end1, long start2, long duration2, long end2) {
        long distStart = start2 - start1;
        long distEnd = end1 - end2;
        return negated ^ (distStart >= this.startMinDev && distStart <= this.startMaxDev && distEnd >= this.endMinDev && distEnd <= this.endMaxDev);
    }

    @Override
    protected boolean isTemporalPredicateEqualTo( IncludesPredicate other ) {
        return startMinDev == other.startMinDev && startMaxDev == other.startMaxDev &&
                endMinDev == other.endMinDev && endMaxDev == other.endMaxDev;
    }
}

package org.drools.model.functions.temporal;

import java.util.concurrent.TimeUnit;

import static org.drools.model.functions.temporal.TimeUtil.unitToLong;

public class Interval {
    public static final long MIN = Long.MIN_VALUE;
    public static final long MAX = Long.MAX_VALUE;

    private final long lowerBound;
    private final long upperBound;

    public Interval( long lowerBound, long upperBound ) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public Interval( long lowerBound, TimeUnit lowerUnit, long upperBound, TimeUnit upperUnit ) {
        this( unitToLong( lowerBound, lowerUnit), unitToLong( upperBound, upperUnit) );
    }


    public long getLowerBound() {
        return lowerBound;
    }

    public long getUpperBound() {
        return upperBound;
    }

    @Override
    public String toString() {
        return "[" + lowerBound + "," + upperBound + "]";
    }
}
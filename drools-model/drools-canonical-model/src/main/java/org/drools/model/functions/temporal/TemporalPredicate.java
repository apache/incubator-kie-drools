package org.drools.model.functions.temporal;

public interface TemporalPredicate {

    boolean evaluate(long start1, long duration1, long end1, long start2, long duration2, long end2);

    Interval getInterval();

    boolean isNegated();

    TemporalPredicate negate();

    boolean isThisOnRight();

    TemporalPredicate thisOnRight();
}

package org.drools.core.time;

/**
 * @deprecated only for backwards compatibility reasons
 * (native compilation)
 * use {@link org.kie.kogito.timer.Interval}
 */
@Deprecated
public class Interval extends org.kie.kogito.timer.Interval {

    public Interval() {
    }

    public Interval(long lowerBound, long upperBound) {
        super(lowerBound, upperBound);
    }
}

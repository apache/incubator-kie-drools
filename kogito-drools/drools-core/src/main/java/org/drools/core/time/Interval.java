package org.drools.core.time;

/**
 * @deprecated only for backwards compatibility reasons
 * (native compilation)
 * use {@link org.kie.services.time.Interval}
 */
@Deprecated
public class Interval extends org.kie.services.time.Interval {

    public Interval() {
    }

    public Interval(long lowerBound, long upperBound) {
        super(lowerBound, upperBound);
    }
}

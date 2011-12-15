package org.drools.rule;

import org.drools.spi.Constraint;
import org.drools.time.Interval;

public interface IntervalProviderConstraint extends Constraint {
    Interval getInterval();
}

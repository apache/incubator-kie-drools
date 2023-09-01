package org.drools.base.rule;

import org.drools.base.rule.constraint.Constraint;
import org.drools.base.time.Interval;

public interface IntervalProviderConstraint extends Constraint {
    Interval getInterval();
}

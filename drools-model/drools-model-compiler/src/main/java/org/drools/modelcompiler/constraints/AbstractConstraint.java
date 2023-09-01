package org.drools.modelcompiler.constraints;

import org.drools.base.rule.IndexableConstraint;
import org.drools.base.rule.IntervalProviderConstraint;
import org.drools.base.rule.MutableTypeConstraint;

public abstract class AbstractConstraint extends MutableTypeConstraint implements IndexableConstraint, IntervalProviderConstraint {
    @Override
    public abstract AbstractConstraint clone();
}

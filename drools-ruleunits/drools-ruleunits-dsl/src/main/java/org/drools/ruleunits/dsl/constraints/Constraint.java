package org.drools.ruleunits.dsl.constraints;

import org.drools.model.PatternDSL;

public interface Constraint<A> {
    void addConstraintToPattern(PatternDSL.PatternDef<A> patternDef);
}

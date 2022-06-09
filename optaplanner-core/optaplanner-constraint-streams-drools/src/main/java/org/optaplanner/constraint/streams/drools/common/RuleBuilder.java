package org.optaplanner.constraint.streams.drools.common;

import java.util.function.BiFunction;

import org.drools.model.Global;
import org.drools.model.Rule;
import org.optaplanner.constraint.streams.common.inliner.WeightedScoreImpacter;
import org.optaplanner.constraint.streams.drools.DroolsConstraint;

@FunctionalInterface
public interface RuleBuilder<Solution_>
        extends BiFunction<DroolsConstraint<Solution_>, Global<WeightedScoreImpacter>, Rule> {
}

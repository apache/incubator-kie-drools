package org.optaplanner.constraint.streams.drools.common;

import java.util.function.BiFunction;

import org.drools.model.Global;
import org.drools.model.consequences.ConsequenceBuilder.ValidBuilder;
import org.optaplanner.constraint.streams.common.inliner.WeightedScoreImpacter;
import org.optaplanner.constraint.streams.drools.DroolsConstraint;

@FunctionalInterface
interface ConsequenceBuilder<Solution_>
        extends BiFunction<DroolsConstraint<Solution_>, Global<WeightedScoreImpacter>, ValidBuilder> {
}

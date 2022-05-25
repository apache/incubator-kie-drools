/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.constraint.streams.drools;

import java.util.Objects;
import java.util.function.Function;

import org.drools.model.Global;
import org.drools.model.Rule;
import org.optaplanner.constraint.streams.common.AbstractConstraint;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.constraint.streams.common.inliner.WeightedScoreImpacter;
import org.optaplanner.constraint.streams.drools.common.RuleBuilder;
import org.optaplanner.core.api.score.Score;

public final class DroolsConstraint<Solution_>
        extends AbstractConstraint<Solution_, DroolsConstraint<Solution_>, DroolsConstraintFactory<Solution_>> {

    private final RuleBuilder<Solution_> ruleBuilder;

    public DroolsConstraint(DroolsConstraintFactory<Solution_> constraintFactory, String constraintPackage,
            String constraintName, Function<Solution_, Score<?>> constraintWeightExtractor,
            ScoreImpactType scoreImpactType, boolean isConstraintWeightConfigurable,
            RuleBuilder<Solution_> ruleBuilder) {
        super(constraintFactory, constraintPackage, constraintName, constraintWeightExtractor, scoreImpactType,
                isConstraintWeightConfigurable);
        this.ruleBuilder = Objects.requireNonNull(ruleBuilder);
    }

    public Rule buildRule(Global<WeightedScoreImpacter> scoreImpacterGlobal) {
        return ruleBuilder.apply(this, scoreImpacterGlobal);
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public String toString() {
        return "DroolsConstraint(" + getConstraintId() + ")";
    }
}

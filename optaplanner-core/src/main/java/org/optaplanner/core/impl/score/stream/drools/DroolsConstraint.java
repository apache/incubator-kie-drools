/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools;

import java.util.List;
import java.util.function.Function;

import org.drools.model.Global;
import org.drools.model.PatternDSL;
import org.drools.model.Rule;
import org.drools.model.RuleItemBuilder;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.common.AbstractConstraint;
import org.optaplanner.core.impl.score.stream.common.ScoreImpactType;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsFromUniConstraintStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DroolsConstraint<Solution_> extends AbstractConstraint<Solution_, DroolsConstraintFactory<Solution_>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DroolsConstraint.class);
    private final List<DroolsFromUniConstraintStream<Solution_, Object>> fromStreamList;
    private final DroolsAbstractConstraintStream<Solution_> scoringStream;

    public DroolsConstraint(DroolsConstraintFactory<Solution_> constraintFactory, String constraintPackage,
            String constraintName, Function<Solution_, Score<?>> constraintWeightExtractor,
            ScoreImpactType scoreImpactType, boolean isConstraintWeightConfigurable,
            List<DroolsFromUniConstraintStream<Solution_, Object>> fromStreamList,
            DroolsAbstractConstraintStream<Solution_> scoringStream) {
        super(constraintFactory, constraintPackage, constraintName, constraintWeightExtractor, scoreImpactType,
                isConstraintWeightConfigurable);
        this.fromStreamList = fromStreamList;
        this.scoringStream = scoringStream;
    }

    public Rule createRule(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal) {
        final Rule result = PatternDSL.rule(getConstraintPackage(), getConstraintName())
                .build(scoringStream.createRuleItemBuilders(this, scoreHolderGlobal)
                        .toArray(new RuleItemBuilder<?>[0]));
        LOGGER.trace("Constraint stream {} resulted in a new Drools rule: {}.", scoringStream, result);
        return result;
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    /**
     * As defined by {@link DroolsRuleStructure#getExpectedJustificationTypes()}.
     *
     * @return never null, never empty
     */
    public Class[] getExpectedJustificationTypes() {
        return scoringStream.getExpectedJustificationTypes();
    }

    public int getExpectedJustificationCount() {
        return scoringStream.getCardinality();
    }

    @Override
    public String toString() {
        return "DroolsConstraint(" + getConstraintId() + ") in " + fromStreamList.size() + " from() stream(s)";
    }
}

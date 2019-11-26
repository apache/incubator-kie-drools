/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsFromUniConstraintStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DroolsConstraint<Solution_> implements Constraint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DroolsConstraint.class);
    private final DroolsConstraintFactory<Solution_> constraintFactory;
    private final String constraintPackage;
    private final String constraintName;
    private final boolean positive;
    private final List<DroolsFromUniConstraintStream<Solution_, Object>> fromStreamList;
    private final DroolsAbstractConstraintStream<Solution_> scoringStream;
    private Function<Solution_, Score<?>> constraintWeightExtractor;

    public DroolsConstraint(DroolsConstraintFactory<Solution_> constraintFactory,
            String constraintPackage, String constraintName,
            Function<Solution_, Score<?>> constraintWeightExtractor, boolean positive,
            List<DroolsFromUniConstraintStream<Solution_, Object>> fromStreamList,
            DroolsAbstractConstraintStream<Solution_> scoringStream) {
        this.constraintFactory = constraintFactory;
        this.constraintPackage = constraintPackage;
        this.constraintName = constraintName;
        this.constraintWeightExtractor = constraintWeightExtractor;
        this.positive = positive;
        this.fromStreamList = fromStreamList;
        this.scoringStream = scoringStream;
    }

    public Score<?> extractConstraintWeight(Solution_ workingSolution) {
        Score<?> constraintWeight = constraintWeightExtractor.apply(workingSolution);
        constraintFactory.getSolutionDescriptor().validateConstraintWeight(constraintPackage, constraintName, constraintWeight);
        return positive ? constraintWeight : constraintWeight.negate();
    }

    public Rule createRule(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal) {
        final Rule result = PatternDSL.rule(getConstraintPackage(), getConstraintName())
                .build(scoringStream.createRuleItemBuilders(scoreHolderGlobal)
                        .toArray(new RuleItemBuilder<?>[0]));
        LOGGER.trace("Constraint stream {} resulted in a new Drools rule: {}.", scoringStream, result);
        return result;
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public DroolsConstraintFactory<Solution_> getConstraintFactory() {
        return constraintFactory;
    }

    @Override
    public String getConstraintPackage() {
        return constraintPackage;
    }

    @Override
    public String getConstraintName() {
        return constraintName;
    }

    public int getConstraintStreamCardinality() {
        if (scoringStream instanceof UniConstraintStream) {
            return 1;
        } else if (scoringStream instanceof BiConstraintStream) {
            return 2;
        } else if (scoringStream instanceof TriConstraintStream) {
            return 3;
        } else if (scoringStream instanceof QuadConstraintStream) {
            return 4;
        } else {
            throw new UnsupportedOperationException("Unknown cardinality for constraint stream (" + scoringStream
                    + ").");
        }
    }

    @Override
    public String toString() {
        return "DroolsConstraint(" + getConstraintId() + ") in " + fromStreamList.size() + " from() stream(s)";
    }
}

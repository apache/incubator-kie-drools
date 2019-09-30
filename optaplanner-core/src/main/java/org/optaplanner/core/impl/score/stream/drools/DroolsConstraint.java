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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.drools.model.Global;
import org.drools.model.PatternDSL;
import org.drools.model.Rule;
import org.drools.model.RuleItemBuilder;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsFromUniConstraintStream;

public class DroolsConstraint<Solution_> implements Constraint {

    private final DroolsConstraintFactory<Solution_> constraintFactory;
    private final String constraintPackage;
    private final String constraintName;
    private Function<Solution_, Score<?>> constraintWeightExtractor;
    private final boolean positive;
    private final List<DroolsFromUniConstraintStream<Solution_, Object>> fromStreamList;

    public DroolsConstraint(DroolsConstraintFactory<Solution_> constraintFactory,
            String constraintPackage, String constraintName,
            Function<Solution_, Score<?>> constraintWeightExtractor, boolean positive,
            List<DroolsFromUniConstraintStream<Solution_, Object>> fromStreamList) {
        this.constraintFactory = constraintFactory;
        this.constraintPackage = constraintPackage;
        this.constraintName = constraintName;
        this.constraintWeightExtractor = constraintWeightExtractor;
        this.positive = positive;
        this.fromStreamList = fromStreamList;
    }

    public Score<?> extractConstraintWeight(Solution_ workingSolution) {
        Score<?> constraintWeight = constraintWeightExtractor.apply(workingSolution);
        constraintFactory.getSolutionDescriptor().validateConstraintWeight(constraintPackage, constraintName, constraintWeight);
        return positive ? constraintWeight : constraintWeight.negate();
    }

    public Rule createRule(Global<? extends AbstractScoreHolder> scoreHolderGlobal) {
        List<RuleItemBuilder<?>> ruleItemBuilderList = new ArrayList<>(fromStreamList.size());
        for (DroolsFromUniConstraintStream<Solution_, Object> fromStream : fromStreamList) {
            fromStream.createRuleItemBuilders(ruleItemBuilderList, scoreHolderGlobal, null, null);
        }
        return PatternDSL.rule(constraintPackage, constraintName)
                .build(ruleItemBuilderList.toArray(new RuleItemBuilder<?>[0]));
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public ConstraintFactory getConstraintFactory() {
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

}

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

package org.optaplanner.constraint.streams.bavet.common;

import java.util.List;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.BavetConstraint;
import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.uni.BavetFromUniConstraintStream;
import org.optaplanner.constraint.streams.common.AbstractConstraintStream;
import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.core.api.score.Score;

public abstract class BavetAbstractConstraintStream<Solution_> extends AbstractConstraintStream<Solution_> {

    // TODO make private
    protected final BavetConstraintFactory<Solution_> constraintFactory;

    public BavetAbstractConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            RetrievalSemantics retrievalSemantics) {
        super(retrievalSemantics);
        this.constraintFactory = constraintFactory;
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    protected BavetConstraint<Solution_> buildConstraint(String constraintPackage, String constraintName,
            Score<?> constraintWeight, ScoreImpactType impactType) {
        Function<Solution_, Score<?>> constraintWeightExtractor = buildConstraintWeightExtractor(
                constraintPackage, constraintName, constraintWeight);
        List<BavetFromUniConstraintStream<Solution_, Object>> fromStreamList = getFromStreamList();
        return new BavetConstraint<>(constraintFactory, constraintPackage, constraintName, constraintWeightExtractor,
                impactType, false, fromStreamList);
    }

    protected BavetConstraint<Solution_> buildConstraintConfigurable(String constraintPackage, String constraintName,
            ScoreImpactType impactType) {
        Function<Solution_, Score<?>> constraintWeightExtractor = buildConstraintWeightExtractor(
                constraintPackage, constraintName);
        List<BavetFromUniConstraintStream<Solution_, Object>> fromStreamList = getFromStreamList();
        return new BavetConstraint<>(constraintFactory, constraintPackage, constraintName, constraintWeightExtractor,
                impactType, true, fromStreamList);
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    public abstract List<BavetFromUniConstraintStream<Solution_, Object>> getFromStreamList();

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public BavetConstraintFactory<Solution_> getConstraintFactory() {
        return constraintFactory;
    }

}

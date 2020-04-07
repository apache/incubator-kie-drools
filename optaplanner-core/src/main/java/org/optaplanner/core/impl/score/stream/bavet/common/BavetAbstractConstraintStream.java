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

package org.optaplanner.core.impl.score.stream.bavet.common;

import java.util.List;
import java.util.function.Function;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintFactory;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetFromUniConstraintStream;
import org.optaplanner.core.impl.score.stream.common.AbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.common.ScoreImpactType;

public abstract class BavetAbstractConstraintStream<Solution_> extends AbstractConstraintStream<Solution_> {

    protected final BavetConstraintFactory<Solution_> constraintFactory;

    public BavetAbstractConstraintStream(BavetConstraintFactory<Solution_> constraintFactory) {
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

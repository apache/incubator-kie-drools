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

package org.optaplanner.core.impl.score.stream.drools.common;

import java.util.List;
import java.util.function.Function;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.stream.common.AbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsFromUniConstraintStream;

public abstract class DroolsAbstractConstraintStream<Solution_> extends AbstractConstraintStream<Solution_> {

    protected final DroolsConstraintFactory<Solution_> constraintFactory;

    public DroolsAbstractConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory) {
        this.constraintFactory = constraintFactory;
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    protected DroolsConstraint<Solution_> buildConstraint(String constraintPackage, String constraintName, Score<?> constraintWeight, boolean positive) {
        Function<Solution_, Score<?>> constraintWeightExtractor = buildConstraintWeightExtractor(
                constraintPackage, constraintName, constraintWeight);
        List<DroolsFromUniConstraintStream<Solution_, Object>> fromStreamList = getFromStreamList();
        return new DroolsConstraint<>(constraintFactory,
                constraintPackage, constraintName, constraintWeightExtractor, positive, fromStreamList);
    }

    protected DroolsConstraint<Solution_> buildConstraintConfigurable(String constraintPackage, String constraintName, boolean positive) {
        Function<Solution_, Score<?>> constraintWeightExtractor = buildConstraintWeightExtractor(
                constraintPackage, constraintName);
        List<DroolsFromUniConstraintStream<Solution_, Object>> fromStreamList = getFromStreamList();
        return new DroolsConstraint<>(constraintFactory,
                constraintPackage, constraintName, constraintWeightExtractor, positive, fromStreamList);
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    public abstract List<DroolsFromUniConstraintStream<Solution_, Object>> getFromStreamList();

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public DroolsConstraintFactory<Solution_> getConstraintFactory() {
        return constraintFactory;
    }

}

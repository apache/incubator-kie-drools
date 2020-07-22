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

package org.optaplanner.core.impl.score.stream.drools.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.stream.common.AbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.common.ScoreImpactType;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.common.consequences.ConstraintConsequence;

public abstract class DroolsAbstractConstraintStream<Solution_> extends AbstractConstraintStream<Solution_> {

    protected final DroolsConstraintFactory<Solution_> constraintFactory;
    private final List<DroolsAbstractConstraintStream<Solution_>> childStreamList = new ArrayList<>(2);

    public DroolsAbstractConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory) {
        this.constraintFactory = Objects.requireNonNull(constraintFactory);
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    protected DroolsConstraint<Solution_> buildConstraint(String constraintPackage, String constraintName,
            Score<?> constraintWeight, ScoreImpactType impactType, ConstraintConsequence consequence) {
        Function<Solution_, Score<?>> constraintWeightExtractor = buildConstraintWeightExtractor(constraintPackage,
                constraintName, constraintWeight);
        return new DroolsConstraint<>(constraintFactory, constraintPackage, constraintName, constraintWeightExtractor,
                impactType, false, consequence);
    }

    protected DroolsConstraint<Solution_> buildConstraintConfigurable(String constraintPackage, String constraintName,
            ScoreImpactType impactType, ConstraintConsequence constraintConsequence) {
        Function<Solution_, Score<?>> constraintWeightExtractor = buildConstraintWeightExtractor(constraintPackage,
                constraintName);
        return new DroolsConstraint<>(constraintFactory, constraintPackage, constraintName, constraintWeightExtractor,
                impactType, true, constraintConsequence);
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    public void addChildStream(DroolsAbstractConstraintStream<Solution_> childStream) {
        childStreamList.add(childStream);
    }

    public Collection<DroolsAbstractConstraintStream<Solution_>> getChildStreams() {
        return Collections.unmodifiableList(childStreamList);
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public DroolsConstraintFactory<Solution_> getConstraintFactory() {
        return constraintFactory;
    }

}

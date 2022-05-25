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

package org.optaplanner.constraint.streams.drools.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.optaplanner.constraint.streams.common.AbstractConstraintStream;
import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.constraint.streams.drools.DroolsConstraint;
import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.core.api.score.Score;

public abstract class DroolsAbstractConstraintStream<Solution_> extends AbstractConstraintStream<Solution_> {

    // TODO make private
    protected final DroolsConstraintFactory<Solution_> constraintFactory;
    private final List<DroolsAbstractConstraintStream<Solution_>> childStreamList = new ArrayList<>(2);

    public DroolsAbstractConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            RetrievalSemantics retrievalSemantics) {
        super(retrievalSemantics);
        this.constraintFactory = Objects.requireNonNull(constraintFactory);
    }

    protected DroolsConstraint<Solution_> buildConstraint(String constraintPackage, String constraintName,
            Score<?> constraintWeight, ScoreImpactType impactType, RuleBuilder<Solution_> ruleBuilder) {
        Function<Solution_, Score<?>> constraintWeightExtractor = buildConstraintWeightExtractor(constraintPackage,
                constraintName, constraintWeight);
        return new DroolsConstraint<>(constraintFactory, constraintPackage, constraintName, constraintWeightExtractor,
                impactType, false, ruleBuilder);
    }

    protected DroolsConstraint<Solution_> buildConstraintConfigurable(String constraintPackage, String constraintName,
            ScoreImpactType impactType, RuleBuilder<Solution_> ruleBuilder) {
        Function<Solution_, Score<?>> constraintWeightExtractor = buildConstraintWeightExtractor(constraintPackage,
                constraintName);
        return new DroolsConstraint<>(constraintFactory, constraintPackage, constraintName, constraintWeightExtractor,
                impactType, true, ruleBuilder);
    }

    public void addChildStream(DroolsAbstractConstraintStream<Solution_> childStream) {
        childStreamList.add(childStream);
    }

    public Collection<DroolsAbstractConstraintStream<Solution_>> getChildStreams() {
        return Collections.unmodifiableList(childStreamList);
    }

    @Override
    public DroolsConstraintFactory<Solution_> getConstraintFactory() {
        return constraintFactory;
    }
}

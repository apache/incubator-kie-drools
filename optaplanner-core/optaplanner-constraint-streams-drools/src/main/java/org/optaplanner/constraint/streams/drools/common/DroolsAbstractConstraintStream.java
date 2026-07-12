/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.constraint.streams.drools.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.optaplanner.constraint.streams.common.AbstractConstraintStream;
import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.constraint.streams.drools.DroolsConstraint;
import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;

public abstract class DroolsAbstractConstraintStream<Solution_, LHS_ extends AbstractLeftHandSide>
        extends AbstractConstraintStream<Solution_> {

    protected final DroolsConstraintFactory<Solution_> constraintFactory;
    private final List<DroolsAbstractConstraintStream<Solution_, ?>> childStreamList = new ArrayList<>(2);

    public DroolsAbstractConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            RetrievalSemantics retrievalSemantics) {
        super(retrievalSemantics);
        this.constraintFactory = Objects.requireNonNull(constraintFactory);
    }

    protected Constraint buildConstraint(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ScoreImpactType impactType, Object justificationMapping, Object indictedObjectsMapping,
            RuleBuilder<Solution_> ruleBuilder) {
        var resolvedConstraintPackage =
                Objects.requireNonNullElseGet(constraintPackage, this.constraintFactory::getDefaultConstraintPackage);
        var resolvedJustificationMapping =
                Objects.requireNonNullElseGet(justificationMapping, this::getDefaultJustificationMapping);
        var resolvedIndictedObjectsMapping =
                Objects.requireNonNullElseGet(indictedObjectsMapping, this::getDefaultIndictedObjectsMapping);
        var isConstraintWeightConfigurable = constraintWeight == null;
        var constraintWeightExtractor = isConstraintWeightConfigurable
                ? buildConstraintWeightExtractor(resolvedConstraintPackage, constraintName)
                : buildConstraintWeightExtractor(resolvedConstraintPackage, constraintName, constraintWeight);
        return new DroolsConstraint<>(constraintFactory, resolvedConstraintPackage, constraintName, constraintWeightExtractor,
                impactType, isConstraintWeightConfigurable, ruleBuilder, resolvedJustificationMapping,
                resolvedIndictedObjectsMapping);
    }

    public void addChildStream(DroolsAbstractConstraintStream<Solution_, ?> childStream) {
        childStreamList.add(childStream);
    }

    public Collection<DroolsAbstractConstraintStream<Solution_, ?>> getChildStreams() {
        return Collections.unmodifiableList(childStreamList);
    }

    @Override
    public DroolsConstraintFactory<Solution_> getConstraintFactory() {
        return constraintFactory;
    }

    /**
     * Some constructs in the Drools executable model may not be reused between different rules.
     * They are, among others, variable instances and beta indexes.
     * Therefore an instance of {@link AbstractLeftHandSide} must never be used to create more than one rule.
     * Therefore every constraint stream re-creates the entire chain of left hand sides every time a new rule is built.
     * It is then left up to Drools to node-share everything it can.
     *
     * @return never null, different instance on every call
     */
    public abstract LHS_ createLeftHandSide();
}

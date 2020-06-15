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

package org.optaplanner.core.impl.score.stream.drools.quad;

import java.math.BigDecimal;
import java.util.List;

import org.drools.model.Global;
import org.drools.model.RuleItemBuilder;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.ToIntQuadFunction;
import org.optaplanner.core.api.function.ToLongQuadFunction;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;

public final class DroolsScoringQuadConstraintStream<Solution_, A, B, C, D>
        extends DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> {

    private final boolean noMatchWeigher;
    private final ToIntQuadFunction<A, B, C, D> intMatchWeigher;
    private final ToLongQuadFunction<A, B, C, D> longMatchWeigher;
    private final QuadFunction<A, B, C, D, BigDecimal> bigDecimalMatchWeigher;

    public DroolsScoringQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent) {
        this(constraintFactory, parent, true, null, null, null);
    }

    public DroolsScoringQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent,
            ToIntQuadFunction<A, B, C, D> intMatchWeigher) {
        this(constraintFactory, parent, false, intMatchWeigher, null, null);
        if (intMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public DroolsScoringQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent,
            ToLongQuadFunction<A, B, C, D> longMatchWeigher) {
        this(constraintFactory, parent, false, null, longMatchWeigher, null);
        if (longMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public DroolsScoringQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent,
            QuadFunction<A, B, C, D, BigDecimal> bigDecimalMatchWeigher) {
        this(constraintFactory, parent, false, null, null, bigDecimalMatchWeigher);
        if (bigDecimalMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    private DroolsScoringQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent, boolean noMatchWeigher,
            ToIntQuadFunction<A, B, C, D> intMatchWeigher, ToLongQuadFunction<A, B, C, D> longMatchWeigher,
            QuadFunction<A, B, C, D, BigDecimal> bigDecimalMatchWeigher) {
        super(constraintFactory, parent);
        this.noMatchWeigher = noMatchWeigher;
        this.intMatchWeigher = intMatchWeigher;
        this.longMatchWeigher = longMatchWeigher;
        this.bigDecimalMatchWeigher = bigDecimalMatchWeigher;
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public List<RuleItemBuilder<?>> createRuleItemBuilders(DroolsConstraint<?> constraint,
            Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal) {
        DroolsQuadCondition<A, B, C, D, ?> condition = ((DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D>) parent)
                .getCondition();
        if (intMatchWeigher != null) {
            return condition.completeWithScoring(constraint, scoreHolderGlobal, intMatchWeigher);
        } else if (longMatchWeigher != null) {
            return condition.completeWithScoring(constraint, scoreHolderGlobal, longMatchWeigher);
        } else if (bigDecimalMatchWeigher != null) {
            return condition.completeWithScoring(constraint, scoreHolderGlobal, bigDecimalMatchWeigher);
        } else if (noMatchWeigher) {
            return condition.completeWithScoring(scoreHolderGlobal);
        } else {
            throw new IllegalStateException("Impossible state: noMatchWeigher (" + noMatchWeigher + ").");
        }
    }

    @Override
    public DroolsQuadCondition<A, B, C, D, ?> getCondition() {
        throw new UnsupportedOperationException("Scoring stream does not have its own QuadCondition.");
    }

    @Override
    public Class[] getExpectedJustificationTypes() {
        return ((DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D>) parent).getCondition()
                .getExpectedJustificationTypes();
    }

    @Override
    public String toString() {
        return "QuadScoring()";
    }

}

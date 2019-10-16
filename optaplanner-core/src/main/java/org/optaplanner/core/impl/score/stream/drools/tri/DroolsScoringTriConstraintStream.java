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

package org.optaplanner.core.impl.score.stream.drools.tri;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.drools.model.Global;
import org.drools.model.PatternDSL;
import org.drools.model.Rule;
import org.drools.model.RuleItemBuilder;
import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;

public final class DroolsScoringTriConstraintStream<Solution_, A, B, C>
        extends DroolsAbstractTriConstraintStream<Solution_, A, B, C> {

    private final boolean noMatchWeigher;
    private final ToIntTriFunction<A, B, C> intMatchWeigher;
    private final ToLongTriFunction<A, B, C> longMatchWeigher;
    private final TriFunction<A, B, C, BigDecimal> bigDecimalMatchWeigher;

    public DroolsScoringTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent) {
        this(constraintFactory, parent, true, null, null, null);
    }

    public DroolsScoringTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, ToIntTriFunction<A, B, C> intMatchWeigher) {
        this(constraintFactory, parent, false, intMatchWeigher, null, null);
        if (intMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public DroolsScoringTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, ToLongTriFunction<A, B, C> longMatchWeigher) {
        this(constraintFactory, parent, false, null, longMatchWeigher, null);
        if (longMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public DroolsScoringTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent,
            TriFunction<A, B, C, BigDecimal> bigDecimalMatchWeigher) {
        this(constraintFactory, parent, false, null, null, bigDecimalMatchWeigher);
        if (bigDecimalMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    private DroolsScoringTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, boolean noMatchWeigher,
            ToIntTriFunction<A, B, C> intMatchWeigher, ToLongTriFunction<A, B, C> longMatchWeigher,
            TriFunction<A, B, C, BigDecimal> bigDecimalMatchWeigher) {
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
    public Optional<Rule> buildRule(DroolsConstraint<Solution_> constraint,
            Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal) {
        DroolsTriCondition<A, B, C> condition = parent.createCondition();
        Rule rule = PatternDSL.rule(constraint.getConstraintPackage(), constraint.getConstraintName())
                .build(createRuleItemBuilders(condition, scoreHolderGlobal)
                        .toArray(new RuleItemBuilder<?>[0]));
        return Optional.of(rule);
    }

    private List<RuleItemBuilder<?>> createRuleItemBuilders(DroolsTriCondition<A, B, C> condition,
            Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal) {
        if (intMatchWeigher != null) {
            return condition.completeWithScoring(scoreHolderGlobal, intMatchWeigher);
        } else if (longMatchWeigher != null) {
            return condition.completeWithScoring(scoreHolderGlobal, longMatchWeigher);
        } else if (bigDecimalMatchWeigher != null) {
            return condition.completeWithScoring(scoreHolderGlobal, bigDecimalMatchWeigher);
        } else if (noMatchWeigher) {
            return condition.completeWithScoring(scoreHolderGlobal);
        } else {
            throw new IllegalStateException("Impossible state: noMatchWeigher (" + noMatchWeigher + ").");
        }
    }

    @Override
    public DroolsTriCondition<A, B, C> createCondition() {
        throw new UnsupportedOperationException("Cannot create TriCondition from a scoring stream.");
    }

    @Override
    public String toString() {
        return "TriScoring()";
    }


}

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

package org.optaplanner.core.impl.score.stream.drools.bi;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.drools.model.Global;
import org.drools.model.PatternDSL;
import org.drools.model.Rule;
import org.drools.model.RuleItemBuilder;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractConstraintStream;

public final class DroolsScoringBiConstraintStream<Solution_, A, B> extends DroolsAbstractBiConstraintStream<Solution_, A, B> {

    private final DroolsAbstractBiConstraintStream<Solution_, A, B> parent;
    private final boolean noMatchWeigher;
    private final ToIntBiFunction<A, B> intMatchWeigher;
    private final ToLongBiFunction<A, B> longMatchWeigher;
    private final BiFunction<A, B, BigDecimal> bigDecimalMatchWeigher;

    public DroolsScoringBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent) {
        this(constraintFactory, parent, true, null, null, null);
    }

    public DroolsScoringBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent, ToIntBiFunction<A, B> intMatchWeigher) {
        this(constraintFactory, parent, false, intMatchWeigher, null, null);
        if (intMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public DroolsScoringBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent, ToLongBiFunction<A, B> longMatchWeigher) {
        this(constraintFactory, parent, false, null, longMatchWeigher, null);
        if (longMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public DroolsScoringBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent,
            BiFunction<A, B, BigDecimal> bigDecimalMatchWeigher) {
        this(constraintFactory, parent, false, null, null, bigDecimalMatchWeigher);
        if (bigDecimalMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    private DroolsScoringBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent, boolean noMatchWeigher,
            ToIntBiFunction<A, B> intMatchWeigher, ToLongBiFunction<A, B> longMatchWeigher,
            BiFunction<A, B, BigDecimal> bigDecimalMatchWeigher) {
        super(constraintFactory);
        this.parent = parent;
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
        DroolsBiCondition<A, B> condition = parent.createCondition();
        Rule rule = PatternDSL.rule(constraint.getConstraintPackage(), constraint.getConstraintName())
                .build(createRuleItemBuilders(condition, scoreHolderGlobal)
                        .toArray(new RuleItemBuilder<?>[0]));
        return Optional.of(rule);
    }

    private List<RuleItemBuilder<?>> createRuleItemBuilders(DroolsBiCondition<A, B> condition,
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
    public DroolsBiCondition<A, B> createCondition() {
        throw new UnsupportedOperationException("Cannot create BiCondition from a scoring stream.");
    }

    @Override
    protected DroolsAbstractConstraintStream<Solution_> getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "BiScoring()";
    }

}

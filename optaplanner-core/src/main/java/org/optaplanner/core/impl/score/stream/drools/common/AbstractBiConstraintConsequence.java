/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.drools.model.DSL;
import org.drools.model.Global;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;

abstract class AbstractBiConstraintConsequence<A, B> extends AbstractConstraintConsequence<BiLeftHandSide<A, B>> {

    @Override
    public int getCardinality() {
        return 2;
    }

    /**
     * {@inheritDoc}
     *
     * @return Null.
     *         When this method returns {@link ConsequenceMatchWeightType#INTEGER}, children of this class are
     *         guaranteed to implement {@link ToIntBiFunction}.
     *         When this method returns {@link ConsequenceMatchWeightType#LONG}, children of this class are guaranteed
     *         to implement {@link ToLongBiFunction}.
     *         When this method returns {@link ConsequenceMatchWeightType#BIG_DECIMAL}, children of this class are
     *         guaranteed to implement {@link BiFunction} to {@link BigDecimal}.
     */
    @Override
    public abstract ConsequenceMatchWeightType getMatchWeightType();

    @Override
    protected final ConsequenceBuilder.ValidBuilder buildConsequence(DroolsConstraint<?> constraint,
            Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal, Variable<?>... variables) {
        AbstractConstraintConsequence<BiLeftHandSide<A, B>> consequence = constraint.getConsequence();
        switch (consequence.getMatchWeightType()) {
            case INTEGER:
                ToIntBiFunction intMatchWeighter = ((Supplier<ToIntBiFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0], variables[1])
                        .execute((drools, scoreHolder, a, b) -> impactScore(constraint, drools, scoreHolder,
                                intMatchWeighter.applyAsInt(a, b)));
            case LONG:
                ToLongBiFunction longMatchWeighter = ((Supplier<ToLongBiFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0], variables[1])
                        .execute((drools, scoreHolder, a, b) -> impactScore(constraint, drools, scoreHolder,
                                longMatchWeighter.applyAsLong(a, b)));
            case BIG_DECIMAL:
                BiFunction bigDecimalMatchWeighter = ((Supplier<BiFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0], variables[1])
                        .execute((drools, scoreHolder, a, b) -> impactScore(constraint, drools, scoreHolder,
                                (BigDecimal) bigDecimalMatchWeighter.apply(a, b)));
            case DEFAULT:
                return DSL.on(scoreHolderGlobal)
                        .execute(AbstractConstraintConsequence::impactScore);
            default:
                throw new UnsupportedOperationException(consequence.getMatchWeightType().toString());
        }
    }
}

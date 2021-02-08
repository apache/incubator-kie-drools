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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.drools.model.DSL;
import org.drools.model.Global;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;

abstract class AbstractUniConstraintConsequence<A> extends AbstractConstraintConsequence<UniLeftHandSide<A>> {

    @Override
    public int getCardinality() {
        return 1;
    }

    /**
     * {@inheritDoc}
     *
     * @return Never null.
     *         When this method returns {@link ConsequenceMatchWeightType#INTEGER}, children of this class are
     *         guaranteed to implement {@link ToIntFunction}.
     *         When this method returns {@link ConsequenceMatchWeightType#LONG}, children of this class are guaranteed
     *         to implement {@link ToLongFunction}.
     *         When this method returns {@link ConsequenceMatchWeightType#BIG_DECIMAL}, children of this class are
     *         guaranteed to implement {@link Function} to {@link BigDecimal}.
     */
    @Override
    protected abstract ConsequenceMatchWeightType getMatchWeightType();

    @Override
    protected final ConsequenceBuilder.ValidBuilder buildConsequence(DroolsConstraint<?> constraint,
            Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal, Variable<?>... variables) {
        AbstractConstraintConsequence<UniLeftHandSide<A>> consequence = constraint.getConsequence();
        switch (consequence.getMatchWeightType()) {
            case INTEGER:
                ToIntFunction intMatchWeighter = ((Supplier<ToIntFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0])
                        .execute((drools, scoreHolder, a) -> impactScore(constraint, drools, scoreHolder,
                                intMatchWeighter.applyAsInt(a), a));
            case LONG:
                ToLongFunction longMatchWeighter = ((Supplier<ToLongFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0])
                        .execute((drools, scoreHolder, a) -> impactScore(constraint, drools, scoreHolder,
                                longMatchWeighter.applyAsLong(a), a));
            case BIG_DECIMAL:
                Function bigDecimalMatchWeighter = ((Supplier<Function>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0])
                        .execute((drools, scoreHolder, a) -> impactScore(constraint, drools, scoreHolder,
                                (BigDecimal) bigDecimalMatchWeighter.apply(a), a));
            case DEFAULT:
                return DSL.on(scoreHolderGlobal, variables[0])
                        .execute((drools, scoreHolder, a) -> impactScore(drools, scoreHolder, a));
            default:
                throw new UnsupportedOperationException(consequence.getMatchWeightType().toString());
        }
    }

}

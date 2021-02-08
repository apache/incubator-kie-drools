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
import java.util.function.Supplier;

import org.drools.model.DSL;
import org.drools.model.Global;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.ToIntQuadFunction;
import org.optaplanner.core.api.function.ToLongQuadFunction;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;

abstract class AbstractQuadConstraintConsequence<A, B, C, D>
        extends AbstractConstraintConsequence<QuadLeftHandSide<A, B, C, D>> {

    @Override
    public int getCardinality() {
        return 4;
    }

    /**
     * {@inheritDoc}
     *
     * @return Never null.
     *         When this method returns {@link ConsequenceMatchWeightType#INTEGER}, children of this class are
     *         guaranteed to implement {@link ToIntQuadFunction}.
     *         When this method returns {@link ConsequenceMatchWeightType#LONG}, children of this class are guaranteed
     *         to implement {@link ToLongQuadFunction}.
     *         When this method returns {@link ConsequenceMatchWeightType#BIG_DECIMAL}, children of this class are
     *         guaranteed to implement {@link QuadFunction} to {@link BigDecimal}.
     */
    @Override
    protected abstract ConsequenceMatchWeightType getMatchWeightType();

    @Override
    protected final ConsequenceBuilder.ValidBuilder buildConsequence(DroolsConstraint<?> constraint,
            Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal, Variable<?>... variables) {
        AbstractConstraintConsequence<QuadLeftHandSide<A, B, C, D>> consequence = constraint.getConsequence();
        switch (consequence.getMatchWeightType()) {
            case INTEGER:
                ToIntQuadFunction intMatchWeighter = ((Supplier<ToIntQuadFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0], variables[1], variables[2], variables[3])
                        .execute((drools, scoreHolder, a, b, c, d) -> impactScore(constraint, drools, scoreHolder,
                                intMatchWeighter.applyAsInt(a, b, c, d), a, b, c, d));
            case LONG:
                ToLongQuadFunction longMatchWeighter = ((Supplier<ToLongQuadFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0], variables[1], variables[2], variables[3])
                        .execute((drools, scoreHolder, a, b, c, d) -> impactScore(constraint, drools, scoreHolder,
                                longMatchWeighter.applyAsLong(a, b, c, d), a, b, c, d));
            case BIG_DECIMAL:
                QuadFunction bigDecimalMatchWeighter = ((Supplier<QuadFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0], variables[1], variables[2], variables[3])
                        .execute((drools, scoreHolder, a, b, c, d) -> impactScore(constraint, drools, scoreHolder,
                                (BigDecimal) bigDecimalMatchWeighter.apply(a, b, c, d), a, b, c, d));
            case DEFAULT:
                return DSL.on(scoreHolderGlobal, variables[0], variables[1], variables[2], variables[3])
                        .execute((drools, scoreHolder, a, b, c, d) -> impactScore(drools, scoreHolder, a, b, c, d));
            default:
                throw new UnsupportedOperationException(consequence.getMatchWeightType().toString());
        }
    }

}

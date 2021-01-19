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

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.function.Supplier;

import org.optaplanner.core.api.function.TriFunction;

final class TriConstraintBigDecimalConsequence<A, B, C>
        extends AbstractTriConstraintConsequence<A, B, C>
        implements Supplier<TriFunction<A, B, C, BigDecimal>> {

    private final TriLeftHandSide<A, B, C> leftHandSide;
    private final TriFunction<A, B, C, BigDecimal> matchWeighter;

    TriConstraintBigDecimalConsequence(TriLeftHandSide<A, B, C> leftHandSide,
            TriFunction<A, B, C, BigDecimal> matchWeighter) {
        this.leftHandSide = requireNonNull(leftHandSide);
        this.matchWeighter = requireNonNull(matchWeighter);
    }

    @Override
    protected TriLeftHandSide<A, B, C> getLeftHandSide() {
        return leftHandSide;
    }

    @Override
    public ConsequenceMatchWeightType getMatchWeightType() {
        return ConsequenceMatchWeightType.BIG_DECIMAL;
    }

    @Override
    public TriFunction<A, B, C, BigDecimal> get() {
        return matchWeighter;
    }
}

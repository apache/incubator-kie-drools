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
import java.util.function.Function;
import java.util.function.Supplier;

final class UniConstraintBigDecimalConsequence<A>
        extends AbstractUniConstraintConsequence<A>
        implements Supplier<Function<A, BigDecimal>> {

    private final UniLeftHandSide<A> leftHandSide;
    private final Function<A, BigDecimal> matchWeighter;

    UniConstraintBigDecimalConsequence(UniLeftHandSide<A> leftHandSide, Function<A, BigDecimal> matchWeighter) {
        this.leftHandSide = requireNonNull(leftHandSide);
        this.matchWeighter = requireNonNull(matchWeighter);
    }

    @Override
    protected UniLeftHandSide<A> getLeftHandSide() {
        return leftHandSide;
    }

    @Override
    public ConsequenceMatchWeightType getMatchWeightType() {
        return ConsequenceMatchWeightType.BIG_DECIMAL;
    }

    @Override
    public Function<A, BigDecimal> get() {
        return matchWeighter;
    }
}

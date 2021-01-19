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

import java.util.function.Supplier;

import org.optaplanner.core.api.function.ToIntQuadFunction;

final class QuadConstraintIntConsequence<A, B, C, D>
        extends AbstractQuadConstraintConsequence<A, B, C, D>
        implements Supplier<ToIntQuadFunction<A, B, C, D>> {

    private final QuadLeftHandSide<A, B, C, D> leftHandSide;
    private final ToIntQuadFunction<A, B, C, D> matchWeighter;

    QuadConstraintIntConsequence(QuadLeftHandSide<A, B, C, D> leftHandSide,
            ToIntQuadFunction<A, B, C, D> matchWeighter) {
        this.leftHandSide = requireNonNull(leftHandSide);
        this.matchWeighter = requireNonNull(matchWeighter);
    }

    @Override
    protected QuadLeftHandSide<A, B, C, D> getLeftHandSide() {
        return leftHandSide;
    }

    @Override
    public ConsequenceMatchWeightType getMatchWeightType() {
        return ConsequenceMatchWeightType.INTEGER;
    }

    @Override
    public ToIntQuadFunction<A, B, C, D> get() {
        return matchWeighter;
    }
}

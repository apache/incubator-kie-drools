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
import java.util.function.ToLongBiFunction;

final class BiConstraintLongConsequence<A, B>
        extends AbstractBiConstraintConsequence<A, B>
        implements Supplier<ToLongBiFunction<A, B>> {

    private final BiLeftHandSide<A, B> leftHandSide;
    private final ToLongBiFunction<A, B> matchWeighter;

    BiConstraintLongConsequence(BiLeftHandSide<A, B> leftHandSide, ToLongBiFunction<A, B> matchWeighter) {
        this.leftHandSide = requireNonNull(leftHandSide);
        this.matchWeighter = requireNonNull(matchWeighter);
    }

    @Override
    protected BiLeftHandSide<A, B> getLeftHandSide() {
        return leftHandSide;
    }

    @Override
    public ConsequenceMatchWeightType getMatchWeightType() {
        return ConsequenceMatchWeightType.LONG;
    }

    @Override
    public ToLongBiFunction<A, B> get() {
        return matchWeighter;
    }
}

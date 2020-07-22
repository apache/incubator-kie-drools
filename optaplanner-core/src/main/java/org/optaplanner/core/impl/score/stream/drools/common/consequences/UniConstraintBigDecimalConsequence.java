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

package org.optaplanner.core.impl.score.stream.drools.common.consequences;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.core.impl.score.stream.drools.common.nodes.UniConstraintGraphNode;

final class UniConstraintBigDecimalConsequence<A> implements UniConstraintConsequence,
        Supplier<Function<A, BigDecimal>> {

    private final UniConstraintGraphNode terminalNode;
    private final Function<A, BigDecimal> matchWeighter;

    UniConstraintBigDecimalConsequence(UniConstraintGraphNode terminalNode, Function<A, BigDecimal> matchWeighter) {
        this.terminalNode = requireNonNull(terminalNode);
        this.matchWeighter = requireNonNull(matchWeighter);
    }

    @Override
    public UniConstraintGraphNode getTerminalNode() {
        return terminalNode;
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

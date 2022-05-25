/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.drools.common;

import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.util.Objects;

import org.drools.model.DSL;
import org.drools.model.Variable;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.ToIntQuadFunction;
import org.optaplanner.core.api.function.ToLongQuadFunction;

final class QuadRuleContext<A, B, C, D> extends AbstractRuleContext {

    private final Variable<A> variableA;
    private final Variable<B> variableB;
    private final Variable<C> variableC;
    private final Variable<D> variableD;

    public QuadRuleContext(Variable<A> variableA, Variable<B> variableB, Variable<C> variableC,
            Variable<D> variableD, ViewItem<?>... viewItems) {
        super(viewItems);
        this.variableA = Objects.requireNonNull(variableA);
        this.variableB = Objects.requireNonNull(variableB);
        this.variableC = Objects.requireNonNull(variableC);
        this.variableD = Objects.requireNonNull(variableD);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(ToIntQuadFunction<A, B, C, D> matchWeighter) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreImpacterGlobal) -> DSL.on(scoreImpacterGlobal, variableA, variableB, variableC, variableD)
                        .execute((drools, scoreImpacter, a, b, c, d) -> runConsequence(constraint, drools, scoreImpacter,
                                matchWeighter.applyAsInt(a, b, c, d),
                                () -> asList(a, b, c, d)));
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(ToLongQuadFunction<A, B, C, D> matchWeighter) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreImpacterGlobal) -> DSL.on(scoreImpacterGlobal, variableA, variableB, variableC, variableD)
                        .execute((drools, scoreImpacter, a, b, c, d) -> runConsequence(constraint, drools, scoreImpacter,
                                matchWeighter.applyAsLong(a, b, c, d),
                                () -> asList(a, b, c, d)));
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(QuadFunction<A, B, C, D, BigDecimal> matchWeighter) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreImpacterGlobal) -> DSL.on(scoreImpacterGlobal, variableA, variableB, variableC, variableD)
                        .execute((drools, scoreImpacter, a, b, c, d) -> runConsequence(constraint, drools, scoreImpacter,
                                matchWeighter.apply(a, b, c, d),
                                () -> asList(a, b, c, d)));
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder() {
        return newRuleBuilder((ToIntQuadFunction<A, B, C, D>) (a, b, c, d) -> 1);
    }

}

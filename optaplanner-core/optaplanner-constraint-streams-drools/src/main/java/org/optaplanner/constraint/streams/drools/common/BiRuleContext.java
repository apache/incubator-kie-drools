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
import java.util.function.BiFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.drools.model.DSL;
import org.drools.model.Variable;
import org.drools.model.view.ViewItem;

final class BiRuleContext<A, B> extends AbstractRuleContext {

    private final Variable<A> variableA;
    private final Variable<B> variableB;

    public BiRuleContext(Variable<A> variableA, Variable<B> variableB, ViewItem<?>... viewItems) {
        super(viewItems);
        this.variableA = Objects.requireNonNull(variableA);
        this.variableB = Objects.requireNonNull(variableB);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(ToIntBiFunction<A, B> matchWeighter) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreImpacterGlobal) -> DSL.on(scoreImpacterGlobal, variableA, variableB)
                        .execute((drools, scoreImpacter, a, b) -> runConsequence(constraint, drools, scoreImpacter,
                                matchWeighter.applyAsInt(a, b),
                                () -> asList(a, b)));
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(ToLongBiFunction<A, B> matchWeighter) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreImpacterGlobal) -> DSL.on(scoreImpacterGlobal, variableA, variableB)
                        .execute((drools, scoreImpacter, a, b) -> runConsequence(constraint, drools, scoreImpacter,
                                matchWeighter.applyAsLong(a, b),
                                () -> asList(a, b)));
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(BiFunction<A, B, BigDecimal> matchWeighter) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreImpacterGlobal) -> DSL.on(scoreImpacterGlobal, variableA, variableB)
                        .execute((drools, scoreImpacter, a, b) -> runConsequence(constraint, drools, scoreImpacter,
                                matchWeighter.apply(a, b),
                                () -> asList(a, b)));
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder() {
        return newRuleBuilder((ToIntBiFunction<A, B>) (a, b) -> 1);
    }

}

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

import static java.util.Collections.singletonList;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.drools.model.DSL;
import org.drools.model.Variable;
import org.drools.model.view.ViewItem;

final class UniRuleContext<A> extends AbstractRuleContext {

    private final Variable<A> variable;

    public UniRuleContext(Variable<A> variable, ViewItem<?>... viewItems) {
        super(viewItems);
        this.variable = Objects.requireNonNull(variable);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(ToIntFunction<A> matchWeighter) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreImpacterGlobal) -> DSL.on(scoreImpacterGlobal, variable)
                        .execute((drools, scoreImpacter, a) -> runConsequence(constraint, drools, scoreImpacter,
                                matchWeighter.applyAsInt(a),
                                () -> singletonList(a)));
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(ToLongFunction<A> matchWeighter) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreImpacterGlobal) -> DSL.on(scoreImpacterGlobal, variable)
                        .execute((drools, scoreImpacter, a) -> runConsequence(constraint, drools, scoreImpacter,
                                matchWeighter.applyAsLong(a),
                                () -> singletonList(a)));
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(Function<A, BigDecimal> matchWeighter) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreImpacterGlobal) -> DSL.on(scoreImpacterGlobal, variable)
                        .execute((drools, scoreImpacter, a) -> runConsequence(constraint, drools, scoreImpacter,
                                matchWeighter.apply(a),
                                () -> singletonList(a)));
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder() {
        return newRuleBuilder((ToIntFunction<A>) a -> 1);
    }

}

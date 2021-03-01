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
                (constraint, scoreHolderGlobal) -> DSL.on(scoreHolderGlobal, variable)
                        .execute((drools, scoreHolder, a) -> impactScore(constraint, drools, scoreHolder,
                                matchWeighter.applyAsInt(a), a));
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(ToLongFunction<A> matchWeighter) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreHolderGlobal) -> DSL.on(scoreHolderGlobal, variable)
                        .execute((drools, scoreHolder, a) -> impactScore(constraint, drools, scoreHolder,
                                matchWeighter.applyAsLong(a), a));
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(Function<A, BigDecimal> matchWeighter) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreHolderGlobal) -> DSL.on(scoreHolderGlobal, variable)
                        .execute((drools, scoreHolder, a) -> impactScore(constraint, drools, scoreHolder,
                                matchWeighter.apply(a), a));
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder() {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreHolderGlobal) -> DSL.on(scoreHolderGlobal, variable)
                        .execute((drools, scoreHolder, a) -> impactScore(drools, scoreHolder, a));
        return assemble(consequenceBuilder);
    }

}

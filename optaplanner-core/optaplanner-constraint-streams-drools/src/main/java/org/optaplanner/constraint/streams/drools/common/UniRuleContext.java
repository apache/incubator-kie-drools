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

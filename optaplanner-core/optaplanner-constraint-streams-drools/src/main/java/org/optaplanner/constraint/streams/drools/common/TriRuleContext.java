package org.optaplanner.constraint.streams.drools.common;

import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.util.Objects;

import org.drools.model.DSL;
import org.drools.model.Variable;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;

final class TriRuleContext<A, B, C> extends AbstractRuleContext {

    private final Variable<A> variableA;
    private final Variable<B> variableB;
    private final Variable<C> variableC;

    public TriRuleContext(Variable<A> variableA, Variable<B> variableB, Variable<C> variableC,
            ViewItem<?>... viewItems) {
        super(viewItems);
        this.variableA = Objects.requireNonNull(variableA);
        this.variableB = Objects.requireNonNull(variableB);
        this.variableC = Objects.requireNonNull(variableC);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(ToIntTriFunction<A, B, C> matchWeighter) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreImpacterGlobal) -> DSL.on(scoreImpacterGlobal, variableA, variableB, variableC)
                        .execute((drools, scoreImpacter, a, b, c) -> runConsequence(constraint, drools, scoreImpacter,
                                matchWeighter.applyAsInt(a, b, c),
                                () -> asList(a, b, c)));
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(ToLongTriFunction<A, B, C> matchWeighter) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreImpacterGlobal) -> DSL.on(scoreImpacterGlobal, variableA, variableB, variableC)
                        .execute((drools, scoreImpacter, a, b, c) -> runConsequence(constraint, drools, scoreImpacter,
                                matchWeighter.applyAsLong(a, b, c),
                                () -> asList(a, b, c)));
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(TriFunction<A, B, C, BigDecimal> matchWeighter) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreImpacterGlobal) -> DSL.on(scoreImpacterGlobal, variableA, variableB, variableC)
                        .execute((drools, scoreImpacter, a, b, c) -> runConsequence(constraint, drools, scoreImpacter,
                                matchWeighter.apply(a, b, c),
                                () -> asList(a, b, c)));
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder() {
        return newRuleBuilder((ToIntTriFunction<A, B, C>) (a, b, c) -> 1);
    }

}

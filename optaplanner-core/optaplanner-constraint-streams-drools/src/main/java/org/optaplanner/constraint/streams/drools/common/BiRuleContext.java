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

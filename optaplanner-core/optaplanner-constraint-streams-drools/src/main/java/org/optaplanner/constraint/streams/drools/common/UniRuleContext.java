package org.optaplanner.constraint.streams.drools.common;

import static org.optaplanner.constraint.streams.common.inliner.JustificationsSupplier.of;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.drools.model.DSL;
import org.drools.model.Variable;
import org.drools.model.view.ViewItem;
import org.optaplanner.constraint.streams.common.inliner.JustificationsSupplier;

final class UniRuleContext<A> extends AbstractRuleContext {

    private final Variable<A> variable;

    public UniRuleContext(Variable<A> variable, ViewItem<?>... viewItems) {
        super(viewItems);
        this.variable = Objects.requireNonNull(variable);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(ToIntFunction<A> matchWeigher) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreImpacterGlobal) -> DSL.on(scoreImpacterGlobal, variable)
                        .execute((drools, scoreImpacter, a) -> {
                            JustificationsSupplier justificationsSupplier =
                                    scoreImpacter.getContext().isConstraintMatchEnabled()
                                            ? of(constraint, constraint.getJustificationMapping(),
                                                    constraint.getIndictedObjectsMapping(), a)
                                            : null;
                            runConsequence(constraint, drools, scoreImpacter, matchWeigher.applyAsInt(a),
                                    justificationsSupplier);
                        });
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(ToLongFunction<A> matchWeigher) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreImpacterGlobal) -> DSL.on(scoreImpacterGlobal, variable)
                        .execute((drools, scoreImpacter, a) -> {
                            JustificationsSupplier justificationsSupplier =
                                    scoreImpacter.getContext().isConstraintMatchEnabled()
                                            ? of(constraint, constraint.getJustificationMapping(),
                                                    constraint.getIndictedObjectsMapping(), a)
                                            : null;
                            runConsequence(constraint, drools, scoreImpacter, matchWeigher.applyAsLong(a),
                                    justificationsSupplier);
                        });
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(Function<A, BigDecimal> matchWeigher) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreImpacterGlobal) -> DSL.on(scoreImpacterGlobal, variable)
                        .execute((drools, scoreImpacter, a) -> {
                            JustificationsSupplier justificationsSupplier =
                                    scoreImpacter.getContext().isConstraintMatchEnabled()
                                            ? of(constraint, constraint.getJustificationMapping(),
                                                    constraint.getIndictedObjectsMapping(), a)
                                            : null;
                            runConsequence(constraint, drools, scoreImpacter, matchWeigher.apply(a),
                                    justificationsSupplier);
                        });
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder() {
        return newRuleBuilder((ToIntFunction<A>) a -> 1);
    }

}

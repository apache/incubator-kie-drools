package org.optaplanner.constraint.streams.drools.common;

import static org.optaplanner.constraint.streams.common.inliner.JustificationsSupplier.of;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.drools.model.DSL;
import org.drools.model.Variable;
import org.drools.model.view.ViewItem;
import org.optaplanner.constraint.streams.common.inliner.JustificationsSupplier;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintJustification;

final class BiRuleContext<A, B> extends AbstractRuleContext {

    private final Variable<A> variableA;
    private final Variable<B> variableB;

    public BiRuleContext(Variable<A> variableA, Variable<B> variableB, ViewItem<?>... viewItems) {
        super(viewItems);
        this.variableA = Objects.requireNonNull(variableA);
        this.variableB = Objects.requireNonNull(variableB);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(ToIntBiFunction<A, B> matchWeigher) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreImpacterGlobal) -> {
                    TriFunction<A, B, Score<?>, ConstraintJustification> justificationMapping =
                            constraint.getJustificationMapping();
                    BiFunction<A, B, Collection<Object>> indictedObjectsMapping = constraint.getIndictedObjectsMapping();
                    return DSL.on(scoreImpacterGlobal, variableA, variableB)
                            .execute((drools, scoreImpacter, a, b) -> {
                                JustificationsSupplier justificationsSupplier =
                                        of(score -> justificationMapping.apply(a, b, score),
                                                () -> indictedObjectsMapping.apply(a, b));
                                runConsequence(constraint, drools, scoreImpacter, matchWeigher.applyAsInt(a, b),
                                        justificationsSupplier);
                            });
                };
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(ToLongBiFunction<A, B> matchWeigher) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreImpacterGlobal) -> {
                    TriFunction<A, B, Score<?>, ConstraintJustification> justificationMapping =
                            constraint.getJustificationMapping();
                    BiFunction<A, B, Collection<Object>> indictedObjectsMapping = constraint.getIndictedObjectsMapping();
                    return DSL.on(scoreImpacterGlobal, variableA, variableB)
                            .execute((drools, scoreImpacter, a, b) -> {
                                JustificationsSupplier justificationsSupplier =
                                        of(score -> justificationMapping.apply(a, b, score),
                                                () -> indictedObjectsMapping.apply(a, b));
                                runConsequence(constraint, drools, scoreImpacter, matchWeigher.applyAsLong(a, b),
                                        justificationsSupplier);
                            });
                };
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder(BiFunction<A, B, BigDecimal> matchWeigher) {
        ConsequenceBuilder<Solution_> consequenceBuilder =
                (constraint, scoreImpacterGlobal) -> {
                    TriFunction<A, B, Score<?>, ConstraintJustification> justificationMapping =
                            constraint.getJustificationMapping();
                    BiFunction<A, B, Collection<Object>> indictedObjectsMapping = constraint.getIndictedObjectsMapping();
                    return DSL.on(scoreImpacterGlobal, variableA, variableB)
                            .execute((drools, scoreImpacter, a, b) -> {
                                JustificationsSupplier justificationsSupplier =
                                        of(score -> justificationMapping.apply(a, b, score),
                                                () -> indictedObjectsMapping.apply(a, b));
                                runConsequence(constraint, drools, scoreImpacter, matchWeigher.apply(a, b),
                                        justificationsSupplier);
                            });
                };
        return assemble(consequenceBuilder);
    }

    public <Solution_> RuleBuilder<Solution_> newRuleBuilder() {
        return newRuleBuilder((ToIntBiFunction<A, B>) (a, b) -> 1);
    }

}

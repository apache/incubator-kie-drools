package org.optaplanner.constraint.streams.drools.common;

import static org.drools.model.PatternDSL.rule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.core.rule.consequence.InternalMatch;
import org.drools.model.Drools;
import org.drools.model.RuleItemBuilder;
import org.drools.model.view.ViewItem;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.constraint.streams.common.inliner.JustificationsSupplier;
import org.optaplanner.constraint.streams.common.inliner.UndoScoreImpacter;
import org.optaplanner.constraint.streams.common.inliner.WeightedScoreImpacter;
import org.optaplanner.constraint.streams.drools.DroolsConstraint;

/**
 * Used when building a consequence to a rule.
 */
abstract class AbstractRuleContext {

    private final List<ViewItem<?>> viewItems;

    protected AbstractRuleContext(ViewItem<?>... viewItems) {
        this.viewItems = Arrays.stream(viewItems).collect(Collectors.toList());
    }

    protected static void runConsequence(DroolsConstraint<?> constraint, Drools drools,
            WeightedScoreImpacter<?, ?> scoreImpacter, int impact, JustificationsSupplier justificationsSupplier) {
        try {
            constraint.assertCorrectImpact(impact);
            UndoScoreImpacter undoImpact = scoreImpacter.impactScore(impact, justificationsSupplier);
            addUndo(drools, undoImpact);
        } catch (Exception e) {
            throw createExceptionOnImpact(constraint, e);
        }
    }

    private static void addUndo(Drools drools, UndoScoreImpacter undoImpact) {
        InternalMatch match = (InternalMatch) ((RuleContext) drools).getMatch();
        match.setCallback(undoImpact);
    }

    private static RuntimeException createExceptionOnImpact(DroolsConstraint<?> constraint, Exception cause) {
        return new IllegalStateException(
                "Consequence of a constraint (" + constraint.getConstraintId() + ") threw an exception.", cause);
    }

    protected static void runConsequence(DroolsConstraint<?> constraint, Drools drools,
            WeightedScoreImpacter<?, ?> scoreImpacter, long impact, JustificationsSupplier justificationsSupplier) {
        try {
            constraint.assertCorrectImpact(impact);
            UndoScoreImpacter undoImpact = scoreImpacter.impactScore(impact, justificationsSupplier);
            addUndo(drools, undoImpact);
        } catch (Exception e) {
            throw createExceptionOnImpact(constraint, e);
        }
    }

    protected static void runConsequence(DroolsConstraint<?> constraint, Drools drools,
            WeightedScoreImpacter<?, ?> scoreImpacter, BigDecimal impact, JustificationsSupplier justificationsSupplier) {
        try {
            constraint.assertCorrectImpact(impact);
            UndoScoreImpacter undoImpact = scoreImpacter.impactScore(impact, justificationsSupplier);
            addUndo(drools, undoImpact);
        } catch (Exception e) {
            throw createExceptionOnImpact(constraint, e);
        }
    }

    protected <Solution_> RuleBuilder<Solution_> assemble(ConsequenceBuilder<Solution_> consequenceBuilder) {
        return (constraint, scoreImpacterGlobal) -> {
            List<RuleItemBuilder<?>> ruleItemBuilderList = new ArrayList<>(viewItems);
            ruleItemBuilderList.add(consequenceBuilder.apply(constraint, scoreImpacterGlobal));
            return rule(constraint.getConstraintPackage(), constraint.getConstraintName())
                    .build(ruleItemBuilderList.toArray(new RuleItemBuilder[0]));
        };
    }

}

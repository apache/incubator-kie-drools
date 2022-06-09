package org.optaplanner.constraint.streams.drools.common;

import static org.drools.model.PatternDSL.rule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.core.common.AgendaItem;
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
            WeightedScoreImpacter scoreImpacter, int impact, JustificationsSupplier justificationsSupplier) {
        constraint.assertCorrectImpact(impact);
        UndoScoreImpacter undoImpact = scoreImpacter.impactScore(impact, justificationsSupplier);
        addUndo(drools, undoImpact);
    }

    protected static void runConsequence(DroolsConstraint<?> constraint, Drools drools,
            WeightedScoreImpacter scoreImpacter, long impact, JustificationsSupplier justificationsSupplier) {
        constraint.assertCorrectImpact(impact);
        UndoScoreImpacter undoImpact = scoreImpacter.impactScore(impact, justificationsSupplier);
        addUndo(drools, undoImpact);
    }

    protected static void runConsequence(DroolsConstraint<?> constraint, Drools drools,
            WeightedScoreImpacter scoreImpacter, BigDecimal impact, JustificationsSupplier justificationsSupplier) {
        constraint.assertCorrectImpact(impact);
        UndoScoreImpacter undoImpact = scoreImpacter.impactScore(impact, justificationsSupplier);
        addUndo(drools, undoImpact);
    }

    private static void addUndo(Drools drools, UndoScoreImpacter undoImpact) {
        AgendaItem agendaItem = (AgendaItem) ((RuleContext) drools).getMatch();
        agendaItem.setCallback(undoImpact);
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

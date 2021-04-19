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
import org.optaplanner.core.impl.score.inliner.JustificationsSupplier;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;
import org.optaplanner.core.impl.score.inliner.WeightedScoreImpacter;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;

/**
 * Used when building a consequence to a rule.
 *
 * For rules where variables are directly used in a consequence,
 * the extra patterns (see {@link PatternVariable}) are unnecessary overhead.
 * In these cases, the left hand side will bring a simplified instance of this class.
 * In all other cases, the rule context will reference the extra pattern variables.
 *
 * <p>
 * Consider the following simple rule, in DRL-like pseudocode:
 *
 * <pre>
 * {@code
 *  rule "Simple rule"
 *  when
 *      accumulate(
 *          Something(),
 *          $count: count()
 *      )
 *  then
 *      // Do something with $count
 *  end
 * }
 * </pre>
 *
 * In this case, the consequence can use the variable "count" directly. However, consider the following rule, where
 * we also want to filter on the "count" variable:
 *
 * <pre>
 * {@code
 *  rule "Simple rule"
 *  when
 *      accumulate(
 *          Something(),
 *          $count: count()
 *      )
 *      $newA: Integer(this == $count, this > 0)
 *  then
 *      // Do something with $newA after we know it is greater than 0.
 *  end
 * }
 * </pre>
 *
 * In this case, the extra pattern variable "newA" is required,
 * because we want to have a pattern on which to apply the filter.
 * The same goes for joining etc.
 * Whenever the variable is not passed directly into a consequence,
 * the use of this class needs to be replaced by the use of {@link PatternVariable}.
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
        AgendaItem<?> agendaItem = (AgendaItem<?>) ((RuleContext) drools).getMatch();
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

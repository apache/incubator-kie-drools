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
import org.optaplanner.core.impl.score.inliner.BigDecimalWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.IntWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.JustificationsSupplier;
import org.optaplanner.core.impl.score.inliner.LongWeightedScoreImpacter;
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
            IntImpactExecutor impactExecutor, int impact, JustificationsSupplier justificationsSupplier) {
        constraint.assertCorrectImpact(impact);
        AgendaItem<?> agendaItem = (AgendaItem<?>) ((RuleContext) drools).getMatch();
        UndoScoreImpacter undoImpact = impactExecutor.execute(impact, justificationsSupplier);
        agendaItem.setCallback(undoImpact);
    }

    protected static void runConsequence(DroolsConstraint<?> constraint, Drools drools,
            LongImpactExecutor impactExecutor, long impact, JustificationsSupplier justificationsSupplier) {
        constraint.assertCorrectImpact(impact);
        AgendaItem<?> agendaItem = (AgendaItem<?>) ((RuleContext) drools).getMatch();
        UndoScoreImpacter undoImpact = impactExecutor.execute(impact, justificationsSupplier);
        agendaItem.setCallback(undoImpact);
    }

    protected static void runConsequence(DroolsConstraint<?> constraint, Drools drools,
            BigDecimalImpactExecutor impactExecutor, BigDecimal impact, JustificationsSupplier justificationsSupplier) {
        constraint.assertCorrectImpact(impact);
        AgendaItem<?> agendaItem = (AgendaItem<?>) ((RuleContext) drools).getMatch();
        UndoScoreImpacter undoImpact = impactExecutor.execute(impact, justificationsSupplier);
        agendaItem.setCallback(undoImpact);
    }

    protected static IntImpactExecutor buildIntImpactExecutor(WeightedScoreImpacter scoreImpacter) {
        if (scoreImpacter instanceof IntWeightedScoreImpacter) {
            return ((IntWeightedScoreImpacter) scoreImpacter)::impactScore;
        } else if (scoreImpacter instanceof LongWeightedScoreImpacter) {
            return ((LongWeightedScoreImpacter) scoreImpacter)::impactScore;
        } else if (scoreImpacter instanceof BigDecimalWeightedScoreImpacter) {
            return (impact, justificationsSupplier) -> ((BigDecimalWeightedScoreImpacter) scoreImpacter)
                    .impactScore(BigDecimal.valueOf(impact), justificationsSupplier);
        } else {
            throw new IllegalStateException("Impossible state: unsupported score impacter type (" +
                    scoreImpacter.getClass() + ").");
        }
    }

    protected static LongImpactExecutor buildLongImpactExecutor(WeightedScoreImpacter scoreImpacter) {
        if (scoreImpacter instanceof LongWeightedScoreImpacter) {
            return ((LongWeightedScoreImpacter) scoreImpacter)::impactScore;
        } else if (scoreImpacter instanceof BigDecimalWeightedScoreImpacter) {
            return (impact, justificationsSupplier) -> ((BigDecimalWeightedScoreImpacter) scoreImpacter)
                    .impactScore(BigDecimal.valueOf(impact), justificationsSupplier);
        } else {
            throw new IllegalStateException("Impossible state: unsupported score impacter type (" +
                    scoreImpacter.getClass() + ").");
        }
    }

    protected static BigDecimalImpactExecutor buildBigDecimalImpactExecutor(WeightedScoreImpacter scoreImpacter) {
        if (scoreImpacter instanceof BigDecimalWeightedScoreImpacter) {
            return ((BigDecimalWeightedScoreImpacter) scoreImpacter)::impactScore;
        } else {
            throw new IllegalStateException("Impossible state: unsupported score impacter type (" +
                    scoreImpacter.getClass() + ").");
        }
    }

    protected <Solution_> RuleBuilder<Solution_> assemble(ConsequenceBuilder<Solution_> consequenceBuilder) {
        return (constraint, scoreImpacter) -> {
            List<RuleItemBuilder<?>> ruleItemBuilderList = new ArrayList<>(viewItems);
            ruleItemBuilderList.add(consequenceBuilder.apply(constraint, scoreImpacter));
            return rule(constraint.getConstraintPackage(), constraint.getConstraintName())
                    .build(ruleItemBuilderList.toArray(new RuleItemBuilder[0]));
        };
    }

    /**
     * Based on the subtype of {@link WeightedScoreImpacter} received,
     * we may have to do some type checking and casting before we can process the impact.
     *
     * (See {@link #buildIntImpactExecutor(WeightedScoreImpacter)}.)
     *
     * The purpose of this interface is to abstract this away,
     * and to provide a streamlined lambda to the consequence for performance.
     */
    @FunctionalInterface
    protected interface IntImpactExecutor {

        UndoScoreImpacter execute(int impact, JustificationsSupplier justificationsSupplier);

    }

    /**
     * As defined by {@link IntImpactExecutor}.
     */
    @FunctionalInterface
    protected interface LongImpactExecutor {

        UndoScoreImpacter execute(long impact, JustificationsSupplier justificationsSupplier);

    }

    /**
     * As defined by {@link IntImpactExecutor}.
     */
    @FunctionalInterface
    protected interface BigDecimalImpactExecutor {

        UndoScoreImpacter execute(BigDecimal impact, JustificationsSupplier justificationsSupplier);

    }

}

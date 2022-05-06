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

package org.optaplanner.constraint.drl.holder;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.drools.core.common.AgendaItem;
import org.drools.core.rule.consequence.Activation;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.constraint.drl.DrlScoreDirector;
import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.score.holder.ScoreHolder;
import org.optaplanner.core.impl.score.buildin.BendableBigDecimalScoreDefinition;
import org.optaplanner.core.impl.score.buildin.BendableLongScoreDefinition;
import org.optaplanner.core.impl.score.buildin.BendableScoreDefinition;
import org.optaplanner.core.impl.score.buildin.HardMediumSoftBigDecimalScoreDefinition;
import org.optaplanner.core.impl.score.buildin.HardMediumSoftLongScoreDefinition;
import org.optaplanner.core.impl.score.buildin.HardMediumSoftScoreDefinition;
import org.optaplanner.core.impl.score.buildin.HardSoftBigDecimalScoreDefinition;
import org.optaplanner.core.impl.score.buildin.HardSoftLongScoreDefinition;
import org.optaplanner.core.impl.score.buildin.HardSoftScoreDefinition;
import org.optaplanner.core.impl.score.buildin.SimpleBigDecimalScoreDefinition;
import org.optaplanner.core.impl.score.buildin.SimpleLongScoreDefinition;
import org.optaplanner.core.impl.score.buildin.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.constraint.DefaultConstraintMatchTotal;
import org.optaplanner.core.impl.score.constraint.DefaultIndictment;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

/**
 * Abstract superclass for {@link ScoreHolder}.
 * Instances of this class are used only in DRL.
 *
 * @param <Score_> the {@link Score} type
 */
public abstract class AbstractScoreHolder<Score_ extends Score<Score_>> implements ScoreHolder<Score_> {

    @Deprecated(forRemoval = true)
    private static final String CUSTOM_SCORE_HOLDER_CLASS_PROPERTY_NAME =
            "org.optaplanner.score.drools.holder";

    public static <Score_ extends Score<Score_>, ScoreHolder_ extends AbstractScoreHolder<Score_>> ScoreHolder_
            buildScoreHolder(ScoreDefinition<Score_> scoreDefinition, boolean constraintMatchEnabled) {
        if (scoreDefinition instanceof SimpleScoreDefinition) {
            return (ScoreHolder_) new SimpleScoreHolderImpl(constraintMatchEnabled);
        } else if (scoreDefinition instanceof SimpleLongScoreDefinition) {
            return (ScoreHolder_) new SimpleLongScoreHolderImpl(constraintMatchEnabled);
        } else if (scoreDefinition instanceof SimpleBigDecimalScoreDefinition) {
            return (ScoreHolder_) new SimpleBigDecimalScoreHolderImpl(constraintMatchEnabled);
        } else if (scoreDefinition instanceof HardSoftScoreDefinition) {
            return (ScoreHolder_) new HardSoftScoreHolderImpl(constraintMatchEnabled);
        } else if (scoreDefinition instanceof HardSoftLongScoreDefinition) {
            return (ScoreHolder_) new HardSoftLongScoreHolderImpl(constraintMatchEnabled);
        } else if (scoreDefinition instanceof HardSoftBigDecimalScoreDefinition) {
            return (ScoreHolder_) new HardSoftBigDecimalScoreHolderImpl(constraintMatchEnabled);
        } else if (scoreDefinition instanceof HardMediumSoftScoreDefinition) {
            return (ScoreHolder_) new HardMediumSoftScoreHolderImpl(constraintMatchEnabled);
        } else if (scoreDefinition instanceof HardMediumSoftLongScoreDefinition) {
            return (ScoreHolder_) new HardMediumSoftLongScoreHolderImpl(constraintMatchEnabled);
        } else if (scoreDefinition instanceof HardMediumSoftBigDecimalScoreDefinition) {
            return (ScoreHolder_) new HardMediumSoftBigDecimalScoreHolderImpl(constraintMatchEnabled);
        } else if (scoreDefinition instanceof BendableScoreDefinition) {
            BendableScoreDefinition bendableScoreDefinition = (BendableScoreDefinition) scoreDefinition;
            return (ScoreHolder_) new BendableScoreHolderImpl(constraintMatchEnabled,
                    bendableScoreDefinition.getHardLevelsSize(), bendableScoreDefinition.getSoftLevelsSize());
        } else if (scoreDefinition instanceof BendableLongScoreDefinition) {
            BendableLongScoreDefinition bendableScoreDefinition = (BendableLongScoreDefinition) scoreDefinition;
            return (ScoreHolder_) new BendableLongScoreHolderImpl(constraintMatchEnabled,
                    bendableScoreDefinition.getHardLevelsSize(), bendableScoreDefinition.getSoftLevelsSize());
        } else if (scoreDefinition instanceof BendableBigDecimalScoreDefinition) {
            BendableBigDecimalScoreDefinition bendableScoreDefinition = (BendableBigDecimalScoreDefinition) scoreDefinition;
            return (ScoreHolder_) new BendableBigDecimalScoreHolderImpl(constraintMatchEnabled,
                    bendableScoreDefinition.getHardLevelsSize(), bendableScoreDefinition.getSoftLevelsSize());
        } else {
            String customScoreHolderClassName = System.getProperty(CUSTOM_SCORE_HOLDER_CLASS_PROPERTY_NAME);
            if (customScoreHolderClassName == null) {
                throw new UnsupportedOperationException("Unknown score definition class (" +
                        scoreDefinition.getClass().getCanonicalName() + ").\n" +
                        "If you're attempting to use a custom score, " +
                        "provide your " + AbstractScoreHolder.class.getSimpleName() + " implementation using the '" +
                        CUSTOM_SCORE_HOLDER_CLASS_PROPERTY_NAME + "' system property.\n" +
                        "Note: support for custom scores will be removed in OptaPlanner 9.0.");
            }
            try {
                Class<?> customScoreHolderClass = Class.forName(customScoreHolderClassName);
                if (!AbstractScoreHolder.class.isAssignableFrom(customScoreHolderClass)) {
                    throw new IllegalStateException("Custom score holder class (" + customScoreHolderClassName +
                            ") does not extend " + AbstractScoreHolder.class.getCanonicalName() + ".\n" +
                            "Note: support for custom scores will be removed in OptaPlanner 9.0.");
                }
                return ((Class<ScoreHolder_>) customScoreHolderClass).getConstructor()
                        .newInstance();
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException cause) {
                throw new IllegalStateException("Custom score holder class (" + customScoreHolderClassName +
                        ") can not be instantiated.\n" +
                        "Maybe add a no-arg public constructor?\n" +
                        "Note: support for custom scores will be removed in OptaPlanner 9.0.", cause);
            }
        }
    }

    protected final boolean constraintMatchEnabled;
    protected final Map<String, ConstraintMatchTotal<Score_>> constraintMatchTotalMap;
    protected final Map<Object, Indictment<Score_>> indictmentMap;

    protected AbstractScoreHolder(boolean constraintMatchEnabled) {
        this.constraintMatchEnabled = constraintMatchEnabled;
        // TODO Can we set the initial capacity of this map more accurately? For example: number of rules
        constraintMatchTotalMap = constraintMatchEnabled ? new LinkedHashMap<>() : null;
        // TODO Can we set the initial capacity of this map more accurately by using entitySize?
        indictmentMap = constraintMatchEnabled ? new LinkedHashMap<>() : null;
    }

    public boolean isConstraintMatchEnabled() {
        return constraintMatchEnabled;
    }

    public Map<String, ConstraintMatchTotal<Score_>> getConstraintMatchTotalMap() {
        if (!isConstraintMatchEnabled()) {
            throw new IllegalStateException("When constraintMatchEnabled (" + isConstraintMatchEnabled()
                    + ") is disabled in the constructor, this method should not be called.");
        }
        return constraintMatchTotalMap;
    }

    public Map<Object, Indictment<Score_>> getIndictmentMap() {
        if (!isConstraintMatchEnabled()) {
            throw new IllegalStateException("When constraintMatchEnabled (" + isConstraintMatchEnabled()
                    + ") is disabled in the constructor, this method should not be called.");
        }
        return indictmentMap;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void configureConstraintWeight(Rule rule, Score_ constraintWeight) {
        if (constraintWeight.getInitScore() != 0) {
            throw new IllegalStateException("The initScore (" + constraintWeight.getInitScore() + ") must be 0.");
        }
        if (constraintMatchEnabled) {
            String constraintPackage = rule.getPackageName();
            String constraintName = rule.getName();
            String constraintId = ConstraintMatchTotal.composeConstraintId(constraintPackage, constraintName);
            constraintMatchTotalMap.put(constraintId,
                    new DefaultConstraintMatchTotal<>(constraintPackage, constraintName, constraintWeight));
        }
    }

    /**
     * Requires a custom rule event listener to be added as event listener on {@link KieSession},
     * otherwise the score changes caused by the constraint matches would not be undone.
     * See {@link DrlScoreDirector#setWorkingSolution(Object)} for an example.
     *
     * @param kcontext The rule for which to register the match.
     * @param constraintUndoListener The operation to run to undo the match.
     * @param scoreSupplier The score change to be undone when constraint justification enabled.
     */
    protected void registerConstraintMatch(RuleContext kcontext, Runnable constraintUndoListener,
            Supplier<Score_> scoreSupplier) {
        AgendaItem agendaItem = (AgendaItem) kcontext.getMatch();
        ConstraintActivationUnMatchListener constraintActivationUnMatchListener = new ConstraintActivationUnMatchListener(
                constraintUndoListener);
        agendaItem.setCallback(constraintActivationUnMatchListener);
        if (constraintMatchEnabled) {
            List<Object> completeJustificationList = extractJustificationList(kcontext);
            // Not needed in fast code: Add ConstraintMatch
            constraintActivationUnMatchListener.constraintMatchTotal = findConstraintMatchTotal(kcontext);
            ConstraintMatch<Score_> constraintMatch = constraintActivationUnMatchListener.constraintMatchTotal
                    .addConstraintMatch(completeJustificationList, scoreSupplier.get());
            List<DefaultIndictment<Score_>> indictmentList = completeJustificationList.stream()
                    .distinct() // One match might have the same justification twice
                    .map(justification -> {
                        DefaultIndictment<Score_> indictment =
                                (DefaultIndictment<Score_>) indictmentMap.computeIfAbsent(justification,
                                        k -> new DefaultIndictment<>(justification, constraintMatch.getScore().zero()));
                        indictment.addConstraintMatch(constraintMatch);
                        return indictment;
                    }).collect(Collectors.toList());
            constraintActivationUnMatchListener.constraintMatch = constraintMatch;
            constraintActivationUnMatchListener.indictmentList = indictmentList;
        }
    }

    private DefaultConstraintMatchTotal<Score_> findConstraintMatchTotal(RuleContext kcontext) {
        Rule rule = kcontext.getRule();
        String constraintPackage = rule.getPackageName();
        String constraintName = rule.getName();
        String constraintId = ConstraintMatchTotal.composeConstraintId(constraintPackage, constraintName);
        return (DefaultConstraintMatchTotal<Score_>) constraintMatchTotalMap.computeIfAbsent(constraintId,
                k -> new DefaultConstraintMatchTotal<>(constraintPackage, constraintName));
    }

    /**
     * For internal use only, use penalize() or reward() instead.
     *
     * @param kcontext never null
     */
    public void impactScore(RuleContext kcontext) {
        throw new UnsupportedOperationException("In the rule (" + kcontext.getRule().getName()
                + "), the scoreHolder class (" + getClass()
                + ") requires a weightMultiplier.");
    }

    /**
     * For internal use only, use penalize() or reward() instead.
     *
     * @param kcontext never null
     * @param weightMultiplier any
     */
    public abstract void impactScore(RuleContext kcontext, int weightMultiplier);

    /**
     * For internal use only, use penalize() or reward() instead.
     *
     * @param kcontext never null
     * @param weightMultiplier any
     */
    public abstract void impactScore(RuleContext kcontext, long weightMultiplier);

    /**
     * For internal use only, use penalize() or reward() instead.
     *
     * @param kcontext never null
     * @param weightMultiplier any
     */
    public abstract void impactScore(RuleContext kcontext, BigDecimal weightMultiplier);

    public abstract Score_ extractScore(int initScore);

    protected List<Object> extractJustificationList(RuleContext kcontext) {
        // Unlike kcontext.getMatch().getObjects(), this includes the matches of accumulate and exists
        Activation activation = (Activation) kcontext.getMatch();
        return new ArrayList<>(activation.getObjectsDeep());
    }

    public class ConstraintActivationUnMatchListener implements Runnable {

        private final Runnable constraintUndoListener;

        private DefaultConstraintMatchTotal<Score_> constraintMatchTotal;
        private List<DefaultIndictment<Score_>> indictmentList;
        private ConstraintMatch<Score_> constraintMatch;

        public ConstraintActivationUnMatchListener(Runnable constraintUndoListener) {
            this.constraintUndoListener = constraintUndoListener;
        }

        @Override
        public final void run() {
            constraintUndoListener.run();
            if (constraintMatchEnabled) {
                // Not needed in fast code: Remove ConstraintMatch
                constraintMatchTotal.removeConstraintMatch(constraintMatch);
                for (DefaultIndictment<Score_> indictment : indictmentList) {
                    indictment.removeConstraintMatch(constraintMatch);
                    if (indictment.getConstraintMatchSet().isEmpty()) {
                        indictmentMap.remove(indictment.getJustification());
                    }
                }
            }
        }
    }

    @FunctionalInterface
    protected interface IntMatchExecutor {

        void accept(RuleContext kcontext, int matchWeight);

    }

    @FunctionalInterface
    protected interface LongMatchExecutor {

        void accept(RuleContext kcontext, long matchWeight);

    }

    @FunctionalInterface
    protected interface BigDecimalMatchExecutor {

        void accept(RuleContext kcontext, BigDecimal matchWeight);

    }

    /**
     * Unlike {@link IntMatchExecutor} and its counterparts, this is not being used on CS-D code paths.
     * Therefore it does not require justifications, as DRL will always infer them from the Drools working memory.
     * 
     * @param <Score_> the {@link Score} type
     */
    @FunctionalInterface
    protected interface ScoreMatchExecutor<Score_ extends AbstractScore<Score_>> {

        void accept(RuleContext kcontext, Score_ matchWeight);

    }

}

/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.bendable;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

/**
 * @see BendableScore
 */
public class BendableScoreHolder extends AbstractScoreHolder<BendableScore> {

    protected final Map<Rule, BiConsumer<RuleContext, Integer>> matchExecutorByNumberMap = new LinkedHashMap<>();
    /** Slower than {@link #matchExecutorByNumberMap} */
    protected final Map<Rule, BiConsumer<RuleContext, BendableScore>> matchExecutorByScoreMap = new LinkedHashMap<>();

    private int[] hardScores;
    private int[] softScores;

    public BendableScoreHolder(boolean constraintMatchEnabled, int hardLevelsSize, int softLevelsSize) {
        super(constraintMatchEnabled, BendableScore.zero(hardLevelsSize, softLevelsSize));
        hardScores = new int[hardLevelsSize];
        softScores = new int[softLevelsSize];
    }

    public int getHardLevelsSize() {
        return hardScores.length;
    }

    public int getHardScore(int hardLevel) {
        return hardScores[hardLevel];
    }

    public int getSoftLevelsSize() {
        return softScores.length;
    }

    public int getSoftScore(int softLevel) {
        return softScores[softLevel];
    }

    // ************************************************************************
    // Setup methods
    // ************************************************************************

    @Override
    public void configureConstraintWeight(Rule rule, BendableScore constraintWeight) {
        super.configureConstraintWeight(rule, constraintWeight);
        BiConsumer<RuleContext, Integer> matchExecutor;
        if (constraintWeight.equals(BendableScore.zero(hardScores.length, softScores.length))) {
            matchExecutor = (RuleContext kcontext, Integer matchWeight) -> {};
        } else {
            Integer singleLevel = null;
            Integer singleLevelWeight = null;
            for (int i = 0; i < constraintWeight.getLevelsSize(); i++) {
                int levelWeight = constraintWeight.getHardOrSoftScore(i);
                if (levelWeight != 0) {
                    if (singleLevel != null) {
                        singleLevel = null;
                        singleLevelWeight = null;
                        break;
                    }
                    singleLevel = i;
                    singleLevelWeight = levelWeight;
                }
            }
            if (singleLevel != null) {
                int levelWeight = singleLevelWeight;
                if (singleLevel < constraintWeight.getHardLevelsSize()) {
                    int level = singleLevel;
                    matchExecutor = (RuleContext kcontext, Integer matchWeight)
                            -> addHardConstraintMatch(kcontext, level, levelWeight * matchWeight);
                } else {
                    int level = singleLevel - constraintWeight.getHardLevelsSize();
                    matchExecutor = (RuleContext kcontext, Integer matchWeight)
                            -> addSoftConstraintMatch(kcontext, level, levelWeight * matchWeight);
                }
            } else {
                matchExecutor = (RuleContext kcontext, Integer matchWeight)-> {
                    int[] hardWeights = new int[hardScores.length];
                    int[] softWeights = new int[softScores.length];
                    for (int i = 0; i < hardWeights.length; i++) {
                        hardWeights[i] = constraintWeight.getHardScore(i) * matchWeight;
                    }
                    for (int i = 0; i < softWeights.length; i++) {
                        softWeights[i] = constraintWeight.getSoftScore(i) * matchWeight;
                    }
                    addMultiConstraintMatch(kcontext, hardWeights, softWeights);
                };
            }
        }
        matchExecutorByNumberMap.put(rule, matchExecutor);
        matchExecutorByScoreMap.put(rule, (RuleContext kcontext, BendableScore weightMultiplier) -> {
            int[] hardWeights = new int[hardScores.length];
            int[] softWeights = new int[softScores.length];
            for (int i = 0; i < hardWeights.length; i++) {
                hardWeights[i] = constraintWeight.getHardScore(i) * weightMultiplier.getHardScore(i);
            }
            for (int i = 0; i < softWeights.length; i++) {
                softWeights[i] = constraintWeight.getSoftScore(i) * weightMultiplier.getSoftScore(i);
            }
            addMultiConstraintMatch(kcontext, hardWeights, softWeights);
        });
    }

    // ************************************************************************
    // Penalize and reward methods
    // ************************************************************************

    /**
     * Penalize a match by the {@link ConstraintWeight} negated.
     * @param kcontext never null, the magic variable in DRL
     */
    public void penalize(RuleContext kcontext) {
        impactScore(kcontext, -1);
    }

    /**
     * Penalize a match by the {@link ConstraintWeight} negated and multiplied with the weightMultiplier for all score levels.
     * @param kcontext never null, the magic variable in DRL
     * @param weightMultiplier at least 0
     */
    public void penalize(RuleContext kcontext, int weightMultiplier) {
        impactScore(kcontext, -weightMultiplier);
    }

    /**
     * Penalize a match by the {@link ConstraintWeight} negated and multiplied with the specific weightMultiplier per score level.
     * Slower than {@link #penalize(RuleContext, int)}.
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeightsMultiplier elements at least 0
     * @param softWeightsMultiplier elements at least 0
     */
    public void penalize(RuleContext kcontext, int[] hardWeightsMultiplier, int[] softWeightsMultiplier) {
        int[] negatedHardWeightsMultiplier = new int[hardScores.length];
        int[] negatedSoftWeightsMultiplier = new int[softScores.length];
        for (int i = 0; i < negatedHardWeightsMultiplier.length; i++) {
            negatedHardWeightsMultiplier[i] = -hardWeightsMultiplier[i];
        }
        for (int i = 0; i < negatedSoftWeightsMultiplier.length; i++) {
            negatedSoftWeightsMultiplier[i] = -softWeightsMultiplier[i];
        }
        impactScore(kcontext, negatedHardWeightsMultiplier, negatedSoftWeightsMultiplier);
    }

    /**
     * Reward a match by the {@link ConstraintWeight}.
     * @param kcontext never null, the magic variable in DRL
     */
    public void reward(RuleContext kcontext) {
        impactScore(kcontext, 1);
    }

    /**
     * Reward a match by the {@link ConstraintWeight} multiplied with the weightMultiplier for all score levels.
     * @param kcontext never null, the magic variable in DRL
     * @param weightMultiplier at least 0
     */
    public void reward(RuleContext kcontext, int weightMultiplier) {
        impactScore(kcontext, weightMultiplier);
    }

    /**
     * Reward a match by the {@link ConstraintWeight} multiplied with the specific weightMultiplier per score level.
     * Slower than {@link #reward(RuleContext, int)}.
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeightsMultiplier elements at least 0
     * @param softWeightsMultiplier elements at least 0
     */
    public void reward(RuleContext kcontext, int[] hardWeightsMultiplier, int[] softWeightsMultiplier) {
        impactScore(kcontext, hardWeightsMultiplier, softWeightsMultiplier);
    }

    @Override
    public void impactScore(RuleContext kcontext) {
        impactScore(kcontext, 1);
    }

    @Override
    public void impactScore(RuleContext kcontext, int weightMultiplier) {
        Rule rule = kcontext.getRule();
        BiConsumer<RuleContext, Integer> matchExecutor = matchExecutorByNumberMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, weightMultiplier);
    }

    private void impactScore(RuleContext kcontext, int[] hardWeightsMultiplier, int[] softWeightsMultiplier) {
        Rule rule = kcontext.getRule();
        BiConsumer<RuleContext, BendableScore> matchExecutor = matchExecutorByScoreMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, BendableScore.of(hardWeightsMultiplier, softWeightsMultiplier));
    }

    // ************************************************************************
    // Other match methods
    // ************************************************************************

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardLevel {@code 0 <= hardLevel <} {@link #getHardLevelsSize()}.
     * The {@code scoreLevel} is {@code hardLevel} for hard levels and {@code softLevel + hardLevelSize} for soft levels.
     * @param weight higher is better, negative for a penalty, positive for a reward
     */
    public void addHardConstraintMatch(RuleContext kcontext, int hardLevel, int weight) {
        if (hardLevel >= hardScores.length) {
            throw new IllegalArgumentException("The hardLevel (" + hardLevel
                    + ") isn't lower than the hardScores length (" + hardScores.length
                    + ") defined by the @" + PlanningScore.class.getSimpleName() + " on the planning solution class.");
        }
        hardScores[hardLevel] += weight;
        registerConstraintMatch(kcontext,
                () -> hardScores[hardLevel] -= weight,
                () -> {
                    int[] newHardScores = new int[hardScores.length];
                    int[] newSoftScores = new int[softScores.length];
                    newHardScores[hardLevel] = weight;
                    return BendableScore.of(newHardScores, newSoftScores);
                });
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param softLevel {@code 0 <= softLevel <} {@link #getSoftLevelsSize()}.
     * The {@code scoreLevel} is {@code hardLevel} for hard levels and {@code softLevel + hardLevelSize} for soft levels.
     * @param weight higher is better, negative for a penalty, positive for a reward
     */
    public void addSoftConstraintMatch(RuleContext kcontext, int softLevel, int weight) {
        if (softLevel >= softScores.length) {
            throw new IllegalArgumentException("The softLevel (" + softLevel
                    + ") isn't lower than the softScores length (" + softScores.length
                    + ") defined by the @" + PlanningScore.class.getSimpleName() + " on the planning solution class.");
        }
        softScores[softLevel] += weight;
        registerConstraintMatch(kcontext,
                () -> softScores[softLevel] -= weight,
                () -> {
                    int[] newHardScores = new int[hardScores.length];
                    int[] newSoftScores = new int[softScores.length];
                    newSoftScores[softLevel] = weight;
                    return BendableScore.of(newHardScores, newSoftScores);
                });
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeights never null, array of length {@link #getHardLevelsSize()}
     * @param softWeights never null, array of length {@link #getSoftLevelsSize()}
     */
    public void addMultiConstraintMatch(RuleContext kcontext, int[] hardWeights, int[] softWeights) {
        if (hardWeights.length != hardScores.length) {
            throw new IllegalArgumentException("The hardWeights length (" + hardWeights.length
                    + ") is different than the hardScores length (" + hardScores.length
                    + ") defined by the @" + PlanningScore.class.getSimpleName() + " on the planning solution class.");
        }
        for (int i = 0; i < hardScores.length; i++) {
            hardScores[i] += hardWeights[i];
        }
        if (softWeights.length != softScores.length) {
            throw new IllegalArgumentException("The softWeights length (" + softWeights.length
                    + ") is different than the softScores length (" + softScores.length
                    + ") defined by the @" + PlanningScore.class.getSimpleName() + " on the planning solution class.");
        }
        for (int i = 0; i < softScores.length; i++) {
            softScores[i] += softWeights[i];
        }
        registerConstraintMatch(kcontext,
                () -> {
                    for (int i = 0; i < hardScores.length; i++) {
                        hardScores[i] -= hardWeights[i];
                    }
                    for (int i = 0; i < softScores.length; i++) {
                        softScores[i] -= softWeights[i];
                    }
                },
                () -> BendableScore.of(hardWeights, softWeights));
    }

    @Override
    public BendableScore extractScore(int initScore) {
        return new BendableScore(initScore,
                Arrays.copyOf(hardScores, hardScores.length),
                Arrays.copyOf(softScores, softScores.length));
    }

}

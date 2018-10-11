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

package org.optaplanner.core.api.score.buildin.bendablelong;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

/**
 * @see BendableLongScore
 */
public class BendableLongScoreHolder extends AbstractScoreHolder<BendableLongScore> {

    protected final Map<Rule, BiConsumer<RuleContext, Long>> matchExecutorMap = new LinkedHashMap<>();

    private long[] hardScores;
    private long[] softScores;

    public BendableLongScoreHolder(boolean constraintMatchEnabled, int hardLevelsSize, int softLevelsSize) {
        super(constraintMatchEnabled, BendableLongScore.zero(hardLevelsSize, softLevelsSize));
        hardScores = new long[hardLevelsSize];
        softScores = new long[softLevelsSize];
    }

    public int getHardLevelsSize() {
        return hardScores.length;
    }

    public long getHardScore(int hardLevel) {
        return hardScores[hardLevel];
    }

    public int getSoftLevelsSize() {
        return softScores.length;
    }

    public long getSoftScore(int softLevel) {
        return softScores[softLevel];
    }

    // ************************************************************************
    // Setup methods
    // ************************************************************************

    @Override
    public void putConstraintWeight(Rule rule, BendableLongScore constraintWeight) {
        BiConsumer<RuleContext, Long> matchExecutor;
        if (constraintWeight.equals(BendableLongScore.zero(hardScores.length, softScores.length))) {
            matchExecutor = (RuleContext kcontext, Long matchWeight) -> {};
        } else if (constraintWeight.getInitScore() != 0) {
            throw new IllegalStateException("The initScore (" + constraintWeight.getInitScore() + ") must be 0.");
        } else {
            Integer singleLevel = null;
            Long singleLevelWeight = null;
            for (int i = 0; i < constraintWeight.getLevelsSize(); i++) {
                long levelWeight = constraintWeight.getHardOrSoftScore(i);
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
                long levelWeight = singleLevelWeight;
                if (singleLevel < constraintWeight.getHardLevelsSize()) {
                    int level = singleLevel;
                    matchExecutor = (RuleContext kcontext, Long matchWeight)
                            -> addHardConstraintMatch(kcontext, level, levelWeight * matchWeight);
                } else {
                    int level = singleLevel - constraintWeight.getHardLevelsSize();
                    matchExecutor = (RuleContext kcontext, Long matchWeight)
                            -> addSoftConstraintMatch(kcontext, level, levelWeight * matchWeight);
                }
            } else {
                matchExecutor = (RuleContext kcontext, Long matchWeight)-> {
                    long[] hardWeights = new long[hardScores.length];
                    long[] softWeights = new long[softScores.length];
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
        matchExecutorMap.put(rule, matchExecutor);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardLevel {@code 0 <= hardLevel <} {@link #getHardLevelsSize()}.
     * The {@code scoreLevel} is {@code hardLevel} for hard levels and {@code softLevel + hardLevelSize} for soft levels.
     * @param weight higher is better, negative for a penalty, positive for a reward
     */
    public void addHardConstraintMatch(RuleContext kcontext, int hardLevel, long weight) {
        hardScores[hardLevel] += weight;
        registerConstraintMatch(kcontext,
                () -> hardScores[hardLevel] -= weight,
                () -> {
                    long[] newHardScores = new long[hardScores.length];
                    long[] newSoftScores = new long[softScores.length];
                    newHardScores[hardLevel] = weight;
                    return BendableLongScore.of(newHardScores, newSoftScores);
                });
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param softLevel {@code 0 <= softLevel <} {@link #getSoftLevelsSize()}.
     * The {@code scoreLevel} is {@code hardLevel} for hard levels and {@code softLevel + hardLevelSize} for soft levels.
     * @param weight higher is better, negative for a penalty, positive for a reward
     */
    public void addSoftConstraintMatch(RuleContext kcontext, int softLevel, long weight) {
        softScores[softLevel] += weight;
        registerConstraintMatch(kcontext,
                () -> softScores[softLevel] -= weight,
                () -> {
                    long[] newHardScores = new long[hardScores.length];
                    long[] newSoftScores = new long[softScores.length];
                    newSoftScores[softLevel] = weight;
                    return BendableLongScore.of(newHardScores, newSoftScores);
                });
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeights never null, array of length {@link #getHardLevelsSize()}
     * @param softWeights never null, array of length {@link #getSoftLevelsSize()}
     */
    public void addMultiConstraintMatch(RuleContext kcontext, long[] hardWeights, long[] softWeights) {
        if (hardScores.length != hardWeights.length) {
            throw new IllegalArgumentException("The hardScores length (" + hardScores.length
                    + ") is different than the hardWeights length (" + hardWeights.length + ").");
        }
        for (int i = 0; i < hardScores.length; i++) {
            hardScores[i] += hardWeights[i];
        }
        if (softScores.length != softWeights.length) {
            throw new IllegalArgumentException("The softScores length (" + softScores.length
                    + ") is different than the softWeights length (" + softWeights.length + ").");
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
                () -> BendableLongScore.of(hardWeights, softWeights));
    }

    @Override
    public BendableLongScore extractScore(int initScore) {
        return new BendableLongScore(initScore,
                Arrays.copyOf(hardScores, hardScores.length),
                Arrays.copyOf(softScores, softScores.length));
    }

}

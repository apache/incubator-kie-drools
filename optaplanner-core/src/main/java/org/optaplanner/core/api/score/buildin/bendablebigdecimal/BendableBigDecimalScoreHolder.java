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

package org.optaplanner.core.api.score.buildin.bendablebigdecimal;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

/**
 * @see BendableBigDecimalScore
 */
public class BendableBigDecimalScoreHolder extends AbstractScoreHolder<BendableBigDecimalScore> {

    protected final Map<Rule, BiConsumer<RuleContext, BigDecimal>> matchExecutorMap = new LinkedHashMap<>();

    private BigDecimal[] hardScores;
    private BigDecimal[] softScores;

    public BendableBigDecimalScoreHolder(boolean constraintMatchEnabled, int hardLevelsSize, int softLevelsSize) {
        super(constraintMatchEnabled, BendableBigDecimalScore.zero(hardLevelsSize, softLevelsSize));
        hardScores = new BigDecimal[hardLevelsSize];
        Arrays.fill(hardScores, BigDecimal.ZERO);
        softScores = new BigDecimal[softLevelsSize];
        Arrays.fill(softScores, BigDecimal.ZERO);
    }

    public int getHardLevelsSize() {
        return hardScores.length;
    }

    public BigDecimal getHardScore(int hardLevel) {
        return hardScores[hardLevel];
    }

    public int getSoftLevelsSize() {
        return softScores.length;
    }

    public BigDecimal getSoftScore(int softLevel) {
        return softScores[softLevel];
    }

    // ************************************************************************
    // Setup methods
    // ************************************************************************

    @Override
    public void putConstraintWeight(Rule rule, BendableBigDecimalScore constraintWeight) {
        BiConsumer<RuleContext, BigDecimal> matchExecutor;
        if (constraintWeight.equals(BendableBigDecimalScore.zero(hardScores.length, softScores.length))) {
            matchExecutor = (RuleContext kcontext, BigDecimal matchWeight) -> {};
        } else if (constraintWeight.getInitScore() != 0) {
            throw new IllegalStateException("The initScore (" + constraintWeight.getInitScore() + ") must be 0.");
        } else {
            Integer singleLevel = null;
            BigDecimal singleLevelWeight = null;
            for (int i = 0; i < constraintWeight.getLevelsSize(); i++) {
                BigDecimal levelWeight = constraintWeight.getHardOrSoftScore(i);
                if (!levelWeight.equals(BigDecimal.ZERO)) {
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
                BigDecimal levelWeight = singleLevelWeight;
                if (singleLevel < constraintWeight.getHardLevelsSize()) {
                    int level = singleLevel;
                    matchExecutor = (RuleContext kcontext, BigDecimal matchWeight)
                            -> addHardConstraintMatch(kcontext, level, levelWeight.multiply(matchWeight));
                } else {
                    int level = singleLevel - constraintWeight.getHardLevelsSize();
                    matchExecutor = (RuleContext kcontext, BigDecimal matchWeight)
                            -> addSoftConstraintMatch(kcontext, level, levelWeight.multiply(matchWeight));
                }
            } else {
                matchExecutor = (RuleContext kcontext, BigDecimal matchWeight)-> {
                    BigDecimal[] hardWeights = new BigDecimal[hardScores.length];
                    BigDecimal[] softWeights = new BigDecimal[softScores.length];
                    for (int i = 0; i < hardWeights.length; i++) {
                        hardWeights[i] = constraintWeight.getHardScore(i).multiply(matchWeight);
                    }
                    for (int i = 0; i < softWeights.length; i++) {
                        softWeights[i] = constraintWeight.getSoftScore(i).multiply(matchWeight);
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
     * @param weight never null, higher is better, negative for a penalty, positive for a reward
     */
    public void addHardConstraintMatch(RuleContext kcontext, int hardLevel, BigDecimal weight) {
        hardScores[hardLevel] = hardScores[hardLevel].add(weight);
        registerConstraintMatch(kcontext,
                () -> hardScores[hardLevel] = hardScores[hardLevel].subtract(weight),
                () -> {
                    BigDecimal[] newHardScores = new BigDecimal[hardScores.length];
                    Arrays.fill(newHardScores, BigDecimal.ZERO);
                    BigDecimal[] newSoftScores = new BigDecimal[softScores.length];
                    Arrays.fill(newSoftScores, BigDecimal.ZERO);
                    newHardScores[hardLevel] = weight;
                    return BendableBigDecimalScore.valueOf(newHardScores, newSoftScores);
                });
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param softLevel {@code 0 <= softLevel <} {@link #getSoftLevelsSize()}.
     * The {@code scoreLevel} is {@code hardLevel} for hard levels and {@code softLevel + hardLevelSize} for soft levels.
     * @param weight never null, higher is better, negative for a penalty, positive for a reward
     */
    public void addSoftConstraintMatch(RuleContext kcontext, int softLevel, BigDecimal weight) {
        softScores[softLevel] = softScores[softLevel].add(weight);
        registerConstraintMatch(kcontext,
                () -> softScores[softLevel] = softScores[softLevel].subtract(weight),
                () -> {
                    BigDecimal[] newHardScores = new BigDecimal[hardScores.length];
                    Arrays.fill(newHardScores, BigDecimal.ZERO);
                    BigDecimal[] newSoftScores = new BigDecimal[softScores.length];
                    Arrays.fill(newSoftScores, BigDecimal.ZERO);
                    newSoftScores[softLevel] = weight;
                    return BendableBigDecimalScore.valueOf(newHardScores, newSoftScores);
                });
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeights never null, array of length {@link #getHardLevelsSize()}, does not contain any nulls
     * @param softWeights never null, array of length {@link #getSoftLevelsSize()}, does not contain any nulls
     */
    public void addMultiConstraintMatch(RuleContext kcontext, BigDecimal[] hardWeights, BigDecimal[] softWeights) {
        if (hardScores.length != hardWeights.length) {
            throw new IllegalArgumentException("The hardScores length (" + hardScores.length
                    + ") is different than the hardWeights length (" + hardWeights.length + ").");
        }
        for (int i = 0; i < hardScores.length; i++) {
            hardScores[i] = hardScores[i].add(hardWeights[i]);
        }
        if (softScores.length != softWeights.length) {
            throw new IllegalArgumentException("The softScores length (" + softScores.length
                    + ") is different than the softWeights length (" + softWeights.length + ").");
        }
        for (int i = 0; i < softScores.length; i++) {
            softScores[i] = softScores[i].add(softWeights[i]);
        }
        registerConstraintMatch(kcontext,
                () -> {
                    for (int i = 0; i < hardScores.length; i++) {
                        hardScores[i] = hardScores[i].subtract(hardWeights[i]);
                    }
                    for (int i = 0; i < softScores.length; i++) {
                        softScores[i] = softScores[i].subtract(softWeights[i]);
                    }
                },
                () -> BendableBigDecimalScore.valueOf(hardWeights, softWeights));
    }

    @Override
    public BendableBigDecimalScore extractScore(int initScore) {
        return new BendableBigDecimalScore(initScore,
                Arrays.copyOf(hardScores, hardScores.length),
                Arrays.copyOf(softScores, softScores.length));
    }

}

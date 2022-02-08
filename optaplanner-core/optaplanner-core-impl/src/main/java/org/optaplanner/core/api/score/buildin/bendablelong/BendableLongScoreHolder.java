/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.holder.ScoreHolder;

/**
 * @see BendableLongScore
 */
public interface BendableLongScoreHolder extends ScoreHolder<BendableLongScore> {

    int getHardLevelsSize();

    int getSoftLevelsSize();

    /**
     * Penalize a match by the {@link ConstraintWeight} negated and multiplied with the weightMultiplier for all score levels.
     *
     * @param kcontext never null, the magic variable in DRL
     * @param weightMultiplier at least 0
     */
    void penalize(RuleContext kcontext, long weightMultiplier);

    /**
     * Penalize a match by the {@link ConstraintWeight} negated and multiplied with the specific weightMultiplier per score
     * level.
     * Slower than {@link #penalize(RuleContext, long)}.
     *
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeightsMultiplier elements at least 0
     * @param softWeightsMultiplier elements at least 0
     */
    void penalize(RuleContext kcontext, long[] hardWeightsMultiplier, long[] softWeightsMultiplier);

    /**
     * Reward a match by the {@link ConstraintWeight} multiplied with the weightMultiplier for all score levels.
     *
     * @param kcontext never null, the magic variable in DRL
     * @param weightMultiplier at least 0
     */
    void reward(RuleContext kcontext, long weightMultiplier);

    /**
     * Reward a match by the {@link ConstraintWeight} multiplied with the specific weightMultiplier per score level.
     * Slower than {@link #reward(RuleContext, long)}.
     *
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeightsMultiplier elements at least 0
     * @param softWeightsMultiplier elements at least 0
     */
    void reward(RuleContext kcontext, long[] hardWeightsMultiplier, long[] softWeightsMultiplier);

    void impactScore(RuleContext kcontext, long weightMultiplier);

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardLevel {@code 0 <= hardLevel <} {@link #getHardLevelsSize()}.
     *        The {@code scoreLevel} is {@code hardLevel} for hard levels and {@code softLevel + hardLevelSize} for soft levels.
     * @param weight higher is better, negative for a penalty, positive for a reward
     */
    void addHardConstraintMatch(RuleContext kcontext, int hardLevel, long weight);

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param softLevel {@code 0 <= softLevel <} {@link #getSoftLevelsSize()}.
     *        The {@code scoreLevel} is {@code hardLevel} for hard levels and {@code softLevel + hardLevelSize} for soft levels.
     * @param weight higher is better, negative for a penalty, positive for a reward
     */
    void addSoftConstraintMatch(RuleContext kcontext, int softLevel, long weight);

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeights never null, array of length {@link #getHardLevelsSize()}
     * @param softWeights never null, array of length {@link #getSoftLevelsSize()}
     */
    void addMultiConstraintMatch(RuleContext kcontext, long[] hardWeights, long[] softWeights);
}

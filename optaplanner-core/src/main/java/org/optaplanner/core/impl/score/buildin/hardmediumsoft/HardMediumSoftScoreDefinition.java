/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.core.impl.score.buildin.hardmediumsoft;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScoreHolder;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.holder.ScoreHolder;
import org.optaplanner.core.impl.score.definition.AbstractFeasibilityScoreDefinition;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrendLevel;

public class HardMediumSoftScoreDefinition extends AbstractFeasibilityScoreDefinition<HardMediumSoftScore> {

    private double hardScoreTimeGradientWeight = 0.50; // TODO this is a guess
    private double mediumScoreTimeGradientWeight = 0.30; // TODO this is a guess

    public double getHardScoreTimeGradientWeight() {
        return hardScoreTimeGradientWeight;
    }

    /**
     * It's recommended to use a number which can be exactly represented as a double,
     * such as 0.5, 0.25, 0.75, 0.125, ... but not 0.1, 0.2, ...
     * @param hardScoreTimeGradientWeight 0.0 <= hardScoreTimeGradientWeight <= 1.0
     */
    public void setHardScoreTimeGradientWeight(double hardScoreTimeGradientWeight) {
        this.hardScoreTimeGradientWeight = hardScoreTimeGradientWeight;
        if (hardScoreTimeGradientWeight < 0.0 || hardScoreTimeGradientWeight > 1.0) {
            throw new IllegalArgumentException("Property hardScoreTimeGradientWeight (" + hardScoreTimeGradientWeight
                    + ") must be greater or equal to 0.0 and smaller or equal to 1.0.");
        }
    }

    public double getMediumScoreTimeGradientWeight() {
        return mediumScoreTimeGradientWeight;
    }

    /**
     * It's recommended to use a number which can be exactly represented as a double,
     * such as 0.5, 0.25, 0.75, 0.125, ... but not 0.1, 0.2, ...
     * @param mediumScoreTimeGradientWeight 0.0 <= hardScoreTimeGradientWeight <= 1.0
     */
    public void setMediumScoreTimeGradientWeight(double mediumScoreTimeGradientWeight) {
        this.mediumScoreTimeGradientWeight = mediumScoreTimeGradientWeight;
        if (mediumScoreTimeGradientWeight < 0.0 || mediumScoreTimeGradientWeight > 1.0) {
            throw new IllegalArgumentException("Property mediumScoreTimeGradientWeight ("
                    + mediumScoreTimeGradientWeight + ") must be greater or equal to 0.0 and smaller or equal to 1.0.");
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public int getLevelCount() {
        return 3;
    }

    @Override
    public int getFeasibleLevelCount() {
        return 1;
    }

    public Class<HardMediumSoftScore> getScoreClass() {
        return HardMediumSoftScore.class;
    }

    public HardMediumSoftScore parseScore(String scoreString) {
        return HardMediumSoftScore.parseScore(scoreString);
    }

    public HardMediumSoftScoreHolder buildScoreHolder(boolean constraintMatchEnabled) {
        return new HardMediumSoftScoreHolder(constraintMatchEnabled);
    }

    public HardMediumSoftScore buildOptimisticBound(InitializingScoreTrend initializingScoreTrend, HardMediumSoftScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return HardMediumSoftScore.valueOf(
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_DOWN ? score.getHardScore() : Integer.MAX_VALUE,
                trendLevels[1] == InitializingScoreTrendLevel.ONLY_DOWN ? score.getMediumScore() : Integer.MAX_VALUE,
                trendLevels[2] == InitializingScoreTrendLevel.ONLY_DOWN ? score.getSoftScore() : Integer.MAX_VALUE);
    }

    public HardMediumSoftScore buildPessimisticBound(InitializingScoreTrend initializingScoreTrend, HardMediumSoftScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return HardMediumSoftScore.valueOf(
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_UP ? score.getHardScore() : Integer.MIN_VALUE,
                trendLevels[1] == InitializingScoreTrendLevel.ONLY_UP ? score.getMediumScore() : Integer.MIN_VALUE,
                trendLevels[2] == InitializingScoreTrendLevel.ONLY_UP ? score.getSoftScore() : Integer.MIN_VALUE);
    }

}

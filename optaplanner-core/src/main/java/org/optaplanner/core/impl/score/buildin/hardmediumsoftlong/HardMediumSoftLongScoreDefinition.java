/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.core.impl.score.buildin.hardmediumsoftlong;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;
import org.optaplanner.core.api.score.holder.ScoreHolder;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrendLevel;

public class HardMediumSoftLongScoreDefinition extends AbstractScoreDefinition<HardMediumSoftLongScore> {

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

    public int getLevelCount() {
        return 3;
    }

    public Class<HardMediumSoftLongScore> getScoreClass() {
        return HardMediumSoftLongScore.class;
    }

    public HardMediumSoftLongScore parseScore(String scoreString) {
        return HardMediumSoftLongScore.parseScore(scoreString);
    }

    public double calculateTimeGradient(HardMediumSoftLongScore startScore, HardMediumSoftLongScore endScore,
            HardMediumSoftLongScore score) {
        if (score.compareTo(endScore) > 0) {
            return 1.0;
        } else if (score.compareTo(startScore) < 0) {
            return 0.0;
        }
        double timeGradient = 0.0;
        double softScoreTimeGradientWeight = 1.0 - hardScoreTimeGradientWeight - mediumScoreTimeGradientWeight;
        if (startScore.getHardScore() == endScore.getHardScore()) {
            timeGradient += hardScoreTimeGradientWeight;
        } else {
            long hardScoreTotal = endScore.getHardScore() - startScore.getHardScore();
            long hardScoreDelta = score.getHardScore() - startScore.getHardScore();
            double hardTimeGradient = (double) hardScoreDelta / (double) hardScoreTotal;
            timeGradient += hardTimeGradient * hardScoreTimeGradientWeight;
        }

        if (score.getMediumScore() >= endScore.getMediumScore()) {
            timeGradient += mediumScoreTimeGradientWeight;
        } else if (score.getMediumScore() <= startScore.getMediumScore()) {
            // No change: timeGradient += 0.0
        } else {
            long mediumScoreTotal = endScore.getMediumScore() - startScore.getMediumScore();
            long mediumScoreDelta = score.getMediumScore() - startScore.getMediumScore();
            double mediumTimeGradient = (double) mediumScoreDelta / (double) mediumScoreTotal;
            timeGradient += mediumTimeGradient * mediumScoreTimeGradientWeight;
        }

        if (score.getSoftScore() >= endScore.getSoftScore()) {
            timeGradient += softScoreTimeGradientWeight;
        } else if (score.getSoftScore() <= startScore.getSoftScore()) {
            // No change: timeGradient += 0.0
        } else {
            long softScoreTotal = endScore.getSoftScore() - startScore.getSoftScore();
            long softScoreDelta = score.getSoftScore() - startScore.getSoftScore();
            double softTimeGradient = (double) softScoreDelta / (double) softScoreTotal;
            timeGradient += softTimeGradient * softScoreTimeGradientWeight;
        }
        return timeGradient;
    }

    public HardMediumSoftLongScoreHolder buildScoreHolder(boolean constraintMatchEnabled) {
        return new HardMediumSoftLongScoreHolder(constraintMatchEnabled);
    }

    public HardMediumSoftLongScore buildOptimisticBound(InitializingScoreTrend initializingScoreTrend, HardMediumSoftLongScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return HardMediumSoftLongScore.valueOf(
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_DOWN ? score.getHardScore() : Long.MAX_VALUE,
                trendLevels[1] == InitializingScoreTrendLevel.ONLY_DOWN ? score.getMediumScore() : Long.MAX_VALUE,
                trendLevels[2] == InitializingScoreTrendLevel.ONLY_DOWN ? score.getSoftScore() : Long.MAX_VALUE);
    }

    public HardMediumSoftLongScore buildPessimisticBound(InitializingScoreTrend initializingScoreTrend, HardMediumSoftLongScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return HardMediumSoftLongScore.valueOf(
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_UP ? score.getHardScore() : Long.MIN_VALUE,
                trendLevels[1] == InitializingScoreTrendLevel.ONLY_UP ? score.getMediumScore() : Long.MIN_VALUE,
                trendLevels[2] == InitializingScoreTrendLevel.ONLY_UP ? score.getSoftScore() : Long.MIN_VALUE);
    }

}

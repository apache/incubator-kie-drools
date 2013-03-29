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
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.api.score.holder.ScoreHolder;

public class HardMediumSoftScoreDefinition extends AbstractScoreDefinition<HardMediumSoftScore> {

    private double hardScoreTimeGradientWeight = 0.50; // TODO this is a guess
    private double mediumScoreTimeGradientWeight = 0.30; // TODO this is a guess

    private HardMediumSoftScore perfectMaximumScore = HardMediumSoftScore.valueOf(0, 0, 0);
    private HardMediumSoftScore perfectMinimumScore = HardMediumSoftScore.valueOf(
            Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

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

    @Override
    public HardMediumSoftScore getPerfectMaximumScore() {
        return perfectMaximumScore;
    }

    public void setPerfectMaximumScore(HardMediumSoftScore perfectMaximumScore) {
        this.perfectMaximumScore = perfectMaximumScore;
    }

    @Override
    public HardMediumSoftScore getPerfectMinimumScore() {
        return perfectMinimumScore;
    }

    public void setPerfectMinimumScore(HardMediumSoftScore perfectMinimumScore) {
        this.perfectMinimumScore = perfectMinimumScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public Class<HardMediumSoftScore> getScoreClass() {
        return HardMediumSoftScore.class;
    }

    public Score parseScore(String scoreString) {
        return HardMediumSoftScore.parseScore(scoreString);
    }

    public double calculateTimeGradient(HardMediumSoftScore startScore, HardMediumSoftScore endScore,
            HardMediumSoftScore score) {
        if (score.compareTo(endScore) > 0) {
            return 1.0;
        } else if (score.compareTo(startScore) < 0) {
            return 0.0;
        }
        double timeGradient = 0.0;
        double softScoreTimeGradientWeight = 1.0 - this.hardScoreTimeGradientWeight - this.mediumScoreTimeGradientWeight;
        if (startScore.getHardScore() == endScore.getHardScore()) {
            timeGradient += hardScoreTimeGradientWeight;
        } else {
            int hardScoreTotal = endScore.getHardScore() - startScore.getHardScore();
            int hardScoreDelta = score.getHardScore() - startScore.getHardScore();
            double hardTimeGradient = (double) hardScoreDelta / (double) hardScoreTotal;
            timeGradient += hardTimeGradient * hardScoreTimeGradientWeight;
        }

        if (score.getMediumScore() >= endScore.getMediumScore()) {
            timeGradient += mediumScoreTimeGradientWeight;
        } else if (score.getMediumScore() <= startScore.getMediumScore()) {
            // No change: timeGradient += 0.0
        } else {
            int mediumScoreTotal = endScore.getMediumScore() - startScore.getMediumScore();
            int mediumScoreDelta = score.getMediumScore() - startScore.getMediumScore();
            double mediumTimeGradient = (double) mediumScoreDelta / (double) mediumScoreTotal;
            timeGradient += mediumTimeGradient * mediumScoreTimeGradientWeight;
        }

        if (score.getSoftScore() >= endScore.getSoftScore()) {
            timeGradient += softScoreTimeGradientWeight;
        } else if (score.getSoftScore() <= startScore.getSoftScore()) {
            // No change: timeGradient += 0.0
        } else {
            int softScoreTotal = endScore.getSoftScore() - startScore.getSoftScore();
            int softScoreDelta = score.getSoftScore() - startScore.getSoftScore();
            double softTimeGradient = (double) softScoreDelta / (double) softScoreTotal;
            timeGradient += softTimeGradient * softScoreTimeGradientWeight;
        }
        return timeGradient;
    }

    public ScoreHolder buildScoreHolder(boolean constraintMatchEnabled) {
        return new HardMediumSoftScoreHolder(constraintMatchEnabled);
    }

}

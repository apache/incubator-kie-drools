/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.impl.score.buildin.hardsoftbigdecimal;

import java.math.BigDecimal;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScoreHolder;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.api.score.holder.ScoreHolder;

public class HardSoftBigDecimalScoreDefinition extends AbstractScoreDefinition<HardSoftBigDecimalScore> {

    private double hardScoreTimeGradientWeight = 0.75; // TODO this is a guess

    private HardSoftBigDecimalScore perfectMaximumScore = HardSoftBigDecimalScore.valueOf(
            BigDecimal.ZERO, BigDecimal.ZERO);
    private HardSoftBigDecimalScore perfectMinimumScore = null;

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

    @Override
    public HardSoftBigDecimalScore getPerfectMaximumScore() {
        return perfectMaximumScore;
    }

    public void setPerfectMaximumScore(HardSoftBigDecimalScore perfectMaximumScore) {
        this.perfectMaximumScore = perfectMaximumScore;
    }

    @Override
    public HardSoftBigDecimalScore getPerfectMinimumScore() {
        return perfectMinimumScore;
    }

    public void setPerfectMinimumScore(HardSoftBigDecimalScore perfectMinimumScore) {
        this.perfectMinimumScore = perfectMinimumScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public Class<HardSoftBigDecimalScore> getScoreClass() {
        return HardSoftBigDecimalScore.class;
    }

    public Score parseScore(String scoreString) {
        return HardSoftBigDecimalScore.parseScore(scoreString);
    }

    public double calculateTimeGradient(HardSoftBigDecimalScore startScore, HardSoftBigDecimalScore endScore,
            HardSoftBigDecimalScore score) {
        if (score.compareTo(endScore) > 0) {
            return 1.0;
        } else if (score.compareTo(startScore) < 0) {
            return 0.0;
        }
        double timeGradient = 0.0;
        double softScoreTimeGradientWeight = 1.0 - hardScoreTimeGradientWeight;
        if (startScore.getHardScore().compareTo(endScore.getHardScore()) == 0) {
            timeGradient += hardScoreTimeGradientWeight;
        } else {
            BigDecimal hardScoreTotal = endScore.getHardScore().subtract(startScore.getHardScore());
            BigDecimal hardScoreDelta = score.getHardScore().subtract(startScore.getHardScore());
            double hardTimeGradient = hardScoreDelta.divide(hardScoreTotal).doubleValue();
            timeGradient += hardTimeGradient * hardScoreTimeGradientWeight;
        }
        if (score.getSoftScore().compareTo(endScore.getSoftScore()) >= 0) {
            timeGradient += softScoreTimeGradientWeight;
        } else if (score.getSoftScore().compareTo(startScore.getSoftScore()) <= 0) {
            // No change: timeGradient += 0.0
        } else {
            BigDecimal softScoreTotal = endScore.getSoftScore().subtract(startScore.getSoftScore());
            BigDecimal softScoreDelta = score.getSoftScore().subtract(startScore.getSoftScore());
            double softTimeGradient = softScoreDelta.divide(softScoreTotal).doubleValue();
            timeGradient += softTimeGradient * softScoreTimeGradientWeight;
        }
        return timeGradient;
    }

    public ScoreHolder buildScoreHolder(boolean constraintMatchEnabled) {
        return new HardSoftBigDecimalScoreHolder(constraintMatchEnabled);
    }

}

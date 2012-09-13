/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.core.score.buildin.hardandsoftlong;

import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.holder.ScoreHolder;
import org.drools.planner.core.score.definition.AbstractScoreDefinition;

public class HardAndSoftLongScoreDefinition extends AbstractScoreDefinition<HardAndSoftLongScore> {

    private double hardScoreTimeGradientWeight = 0.75; // TODO this is a guess

    private HardAndSoftLongScore perfectMaximumScore = new DefaultHardAndSoftLongScore(0, 0);
    private HardAndSoftLongScore perfectMinimumScore = new DefaultHardAndSoftLongScore(Long.MIN_VALUE, Long.MIN_VALUE);

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

    public void setPerfectMaximumScore(HardAndSoftLongScore perfectMaximumScore) {
        this.perfectMaximumScore = perfectMaximumScore;
    }

    public void setPerfectMinimumScore(HardAndSoftLongScore perfectMinimumScore) {
        this.perfectMinimumScore = perfectMinimumScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public HardAndSoftLongScore getPerfectMaximumScore() {
        return perfectMaximumScore;
    }

    @Override
    public HardAndSoftLongScore getPerfectMinimumScore() {
        return perfectMinimumScore;
    }

    public Score parseScore(String scoreString) {
        return DefaultHardAndSoftLongScore.parseScore(scoreString);
    }

    public double calculateTimeGradient(HardAndSoftLongScore startScore, HardAndSoftLongScore endScore,
            HardAndSoftLongScore score) {
        if (score.getHardScore() > endScore.getHardScore()) {
            return 1.0;
        } else if (startScore.getHardScore() > score.getHardScore()) {
            return 0.0;
        }
        double softScoreTimeGradientWeight;
        double timeGradient;
        if (startScore.getHardScore() == endScore.getHardScore()) {
            softScoreTimeGradientWeight = 1.0;
            timeGradient = 0.0;
        } else {
            softScoreTimeGradientWeight = 1.0 - hardScoreTimeGradientWeight;
            long hardScoreTotal = endScore.getHardScore() - startScore.getHardScore();
            long hardScoreDelta = score.getHardScore() - startScore.getHardScore();
            double hardTimeGradient = (double) hardScoreDelta / (double) hardScoreTotal;
            timeGradient = hardTimeGradient * hardScoreTimeGradientWeight;
        }
        if (score.getSoftScore() >= endScore.getSoftScore()) {
            timeGradient += softScoreTimeGradientWeight;
        } else if (startScore.getSoftScore() >= score.getSoftScore()) {
            // No change: timeGradient += 0.0
        } else {
            long softScoreTotal = endScore.getSoftScore() - startScore.getSoftScore();
            long softScoreDelta = score.getSoftScore() - startScore.getSoftScore();
            double softTimeGradient = (double) softScoreDelta / (double) softScoreTotal;
            timeGradient += softTimeGradient * softScoreTimeGradientWeight;
        }
        return timeGradient;
    }

    public ScoreHolder buildScoreHolder() {
        return new HardAndSoftLongScoreHolder();
    }

}

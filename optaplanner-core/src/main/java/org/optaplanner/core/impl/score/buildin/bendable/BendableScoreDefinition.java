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

package org.optaplanner.core.impl.score.buildin.bendable;

import java.util.Arrays;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.bendable.BendableScoreHolder;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.api.score.holder.ScoreHolder;

public class BendableScoreDefinition extends AbstractScoreDefinition<BendableScore> {

    private final int hardLevelCount;
    private final int softLevelCount;

    private double recursiveTimeGradientWeight = 0.50; // TODO this is a guess

    private BendableScore perfectMaximumScore = null;
    private BendableScore perfectMinimumScore = null;

    public BendableScoreDefinition(int hardLevelCount, int softLevelCount) {
        this.hardLevelCount = hardLevelCount;
        this.softLevelCount = softLevelCount;
    }

    public int getHardLevelCount() {
        return hardLevelCount;
    }

    public int getSoftLevelCount() {
        return softLevelCount;
    }

    public double getRecursiveTimeGradientWeight() {
        return recursiveTimeGradientWeight;
    }

    /**
     * It's recommended to use a number which can be exactly represented as a double,
     * such as 0.5, 0.25, 0.75, 0.125, ... but not 0.1, 0.2, ...
     * @param recursiveTimeGradientWeight 0.0 <= recursiveTimeGradientWeight <= 1.0
     */
    public void setRecursiveTimeGradientWeight(double recursiveTimeGradientWeight) {
        this.recursiveTimeGradientWeight = recursiveTimeGradientWeight;
        if (recursiveTimeGradientWeight < 0.0 || recursiveTimeGradientWeight > 1.0) {
            throw new IllegalArgumentException("Property recursiveTimeGradientWeight (" + recursiveTimeGradientWeight
                    + ") must be greater or equal to 0.0 and smaller or equal to 1.0.");
        }
    }

    @Override
    public BendableScore getPerfectMaximumScore() {
        return perfectMaximumScore;
    }

    public void setPerfectMaximumScore(BendableScore perfectMaximumScore) {
        this.perfectMaximumScore = perfectMaximumScore;
    }

    @Override
    public BendableScore getPerfectMinimumScore() {
        return perfectMinimumScore;
    }

    public void setPerfectMinimumScore(BendableScore perfectMinimumScore) {
        this.perfectMinimumScore = perfectMinimumScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public Class<BendableScore> getScoreClass() {
        return BendableScore.class;
    }

    public Score parseScore(String scoreString) {
        return BendableScore.parseScore(hardLevelCount, softLevelCount, scoreString);
    }

    public BendableScore createScore(int... scores) {
        int levelCount = hardLevelCount + softLevelCount;
        if (scores.length != levelCount) {
            throw new IllegalArgumentException("The scores (" + Arrays.toString(scores)
                    + ")'s length (" + scores.length
                    + ") is not levelCount (" + levelCount + ").");
        }
        return BendableScore.valueOf(Arrays.copyOfRange(scores, 0, hardLevelCount),
                Arrays.copyOfRange(scores, hardLevelCount, levelCount));
    }

    public double calculateTimeGradient(BendableScore startScore, BendableScore endScore,
            BendableScore score) {
        startScore.validateCompatible(score);
        score.validateCompatible(endScore);
        if (score.compareTo(endScore) > 0) {
            return 1.0;
        } else if (score.compareTo(startScore) < 0) {
            return 0.0;
        }
        double timeGradient = 0.0;
        double levelTimeGradientWeight = 1.0;
        int levelCount = hardLevelCount + softLevelCount;
        for (int i = 0; i < levelCount; i++) {
            if (i != (levelCount - 1)) {
                levelTimeGradientWeight *= recursiveTimeGradientWeight;
            }
            int startScoreLevel = (i < hardLevelCount) ? startScore.getHardScore(i) : startScore.getSoftScore(i);
            int endScoreLevel = (i < hardLevelCount) ? endScore.getHardScore(i) : endScore.getSoftScore(i);
            int scoreLevel = (i < hardLevelCount) ? score.getHardScore(i) : score.getSoftScore(i);
            if (scoreLevel >= endScoreLevel) {
                timeGradient += levelTimeGradientWeight;
            } else {
                if (scoreLevel <= startScoreLevel) {
                    // No change: timeGradient += 0.0
                } else {
                    int levelTotal = endScoreLevel - startScoreLevel;
                    int levelDelta = scoreLevel - startScoreLevel;
                    double levelTimeGradient = (double) levelDelta / (double) levelTotal;
                    timeGradient += levelTimeGradient * levelTimeGradientWeight;
                }
            }

        }
        if (timeGradient > 1.0) {
            // Rounding error due to calculating with doubles
            timeGradient = 1.0;
        }
        return timeGradient;
    }

    public ScoreHolder buildScoreHolder() {
        return new BendableScoreHolder(hardLevelCount, softLevelCount);
    }

}

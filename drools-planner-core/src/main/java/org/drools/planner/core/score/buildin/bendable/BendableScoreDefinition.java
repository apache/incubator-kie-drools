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

package org.drools.planner.core.score.buildin.bendable;

import java.util.Arrays;

import org.drools.planner.core.score.AbstractScore;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.definition.AbstractScoreDefinition;
import org.drools.planner.core.score.holder.ScoreHolder;

public class BendableScoreDefinition extends AbstractScoreDefinition<BendableScore> {

    private final int hardScoresSize;
    private final int softScoresSize;

    private double recursiveTimeGradientWeight = 0.50; // TODO this is a guess

    private BendableScore perfectMaximumScore = null;
    private BendableScore perfectMinimumScore = null;

    public BendableScoreDefinition(int hardScoresSize, int softScoresSize) {
        this.hardScoresSize = hardScoresSize;
        this.softScoresSize = softScoresSize;
    }

    public int getHardScoresSize() {
        return hardScoresSize;
    }

    public int getSoftScoresSize() {
        return softScoresSize;
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
        int scoresSize = hardScoresSize + softScoresSize;
        String[] levelStrings = AbstractScore.parseLevelStrings(scoreString, scoresSize);
        int[] hardScores = new int[hardScoresSize];
        int[] softScores = new int[softScoresSize];
        for (int i = 0; i < hardScores.length; i++) {
            hardScores[i] = Integer.parseInt(levelStrings[i]);
        }
        for (int i = 0; i < softScores.length; i++) {
            softScores[i] = Integer.parseInt(levelStrings[hardScores.length + i]);
        }
        return new BendableScore(hardScores, softScores);
    }

    public BendableScore scoreValueOf(int[] hardScores, int[] softScores) {
        if (hardScores.length != hardScoresSize) {
            throw new IllegalArgumentException("The hardScores (" + Arrays.toString(hardScores)
                    + ")'s length (" + hardScores.length + ") is not hardScoresSize (" + hardScoresSize + ").");
        }
        if (softScores.length != softScoresSize) {
            throw new IllegalArgumentException("The softScores (" + Arrays.toString(softScores)
                    + ")'s length (" + softScores.length + ") is not softScoresSize (" + softScoresSize + ").");
        }
        return new BendableScore(hardScores, softScores);
    }

    public double calculateTimeGradient(BendableScore startScore, BendableScore endScore,
            BendableScore score) {
        if (score.compareTo(endScore) > 0) {
            return 1.0;
        } else if (score.compareTo(startScore) < 0) {
            return 0.0;
        }
        double timeGradient = 0.0;
        double levelTimeGradientWeight = 1.0;
        int scoresSize = hardScoresSize + softScoresSize;
        for (int i = 0; i < scoresSize; i++) {
            if (i != (scoresSize - 1)) {
                levelTimeGradientWeight *= recursiveTimeGradientWeight;
            }
            int startScoreLevel = (i < hardScoresSize) ? startScore.getHardScore(i) : startScore.getSoftScore(i);
            int endScoreLevel = (i < hardScoresSize) ? endScore.getHardScore(i) : endScore.getSoftScore(i);
            int scoreLevel = (i < hardScoresSize) ? score.getHardScore(i) : score.getSoftScore(i);
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
        return new BendableScoreHolder(hardScoresSize, softScoresSize);
    }

}

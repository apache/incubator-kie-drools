/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.core.impl.solver.termination;

import java.util.Arrays;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.phase.scope.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public class BestScoreTermination extends AbstractTermination {

    private final Score bestScoreLimit;
    private final double[] timeGradientWeightNumbers;

    public BestScoreTermination(Score bestScoreLimit, double[] timeGradientWeightNumbers) {
        this.bestScoreLimit = bestScoreLimit;
        if (bestScoreLimit == null) {
            throw new IllegalArgumentException("The bestScoreLimit (" + bestScoreLimit
                    + ") cannot be null.");
        }
        this.timeGradientWeightNumbers = timeGradientWeightNumbers;
    }

    // ************************************************************************
    // Terminated methods
    // ************************************************************************

    public boolean isSolverTerminated(DefaultSolverScope solverScope) {
        return isTerminated(solverScope.isBestSolutionInitialized(), solverScope.getBestScore());
    }

    public boolean isPhaseTerminated(AbstractSolverPhaseScope phaseScope) {
        return isTerminated(phaseScope.isBestSolutionInitialized(), phaseScope.getBestScore());
    }

    protected boolean isTerminated(boolean bestSolutionInitialized, Score bestScore) {
        return bestSolutionInitialized && bestScore.compareTo(bestScoreLimit) >= 0;
    }

    // ************************************************************************
    // Time gradient methods
    // ************************************************************************

    public double calculateSolverTimeGradient(DefaultSolverScope solverScope) {
        Score startingInitializedScore = solverScope.getStartingInitializedScore();
        Score bestScore = solverScope.getBestScore();
        return calculateTimeGradient(startingInitializedScore, bestScoreLimit, bestScore);
    }

    public double calculatePhaseTimeGradient(AbstractSolverPhaseScope phaseScope) {
        Score startingInitializedScore = phaseScope.getStartingScore();
        Score bestScore = phaseScope.getBestScore();
        return calculateTimeGradient(startingInitializedScore, bestScoreLimit, bestScore);
    }

    protected double calculateTimeGradient(Score startScore, Score endScore, Score score) {
        Score totalDiff = endScore.subtract(startScore);
        Number[] totalDiffNumbers = totalDiff.toLevelNumbers();
        Score scoreDiff = score.subtract(startScore);
        Number[] scoreDiffNumbers = scoreDiff.toLevelNumbers();
        if (scoreDiffNumbers.length != totalDiffNumbers.length) {
            throw new IllegalStateException("The startScore (" + startScore + "), endScore (" + endScore
                    + ") and score (" + score + ") don't have the same level count.");
        }
        int levelCount = scoreDiffNumbers.length;
        if (timeGradientWeightNumbers.length != levelCount - 1) {
            throw new IllegalStateException(
                    "The timeGradientWeightNumbers (" + Arrays.toString(timeGradientWeightNumbers)
                    + ")'s length (" + timeGradientWeightNumbers.length
                    + ") is not 1 less than the levelCount (" + levelCount
                    + ") of the startScore (" + startScore + "), endScore (" + endScore
                    + ") and score (" + score + ").");
        }

        double timeGradient = 0.0;
        double remainingTimeGradient = 1.0;
        for (int i = 0; i < levelCount; i++) {
            double levelTimeGradientWeight;
            if (i != (levelCount - 1)) {
                levelTimeGradientWeight = remainingTimeGradient * timeGradientWeightNumbers[i];
                remainingTimeGradient -= levelTimeGradientWeight;
            } else {
                levelTimeGradientWeight = remainingTimeGradient;
                remainingTimeGradient = 0.0;
            }
            double totalDiffLevel = totalDiffNumbers[i].doubleValue();
            double scoreDiffLevel = scoreDiffNumbers[i].doubleValue();
            if (scoreDiffLevel == totalDiffLevel) {
                // Max out this level
                timeGradient += levelTimeGradientWeight;
            } else if (scoreDiffLevel > totalDiffLevel) {
                // Max out this level and all softer levels too
                timeGradient += levelTimeGradientWeight + remainingTimeGradient;
                break;
            } else if (scoreDiffLevel == 0.0) {
                // Ignore this level
                // timeGradient += 0.0
            } else if (scoreDiffLevel < 0.0) {
                // Ignore this level and all softer levels too
                // timeGradient += 0.0
                break;
            } else {
                double levelTimeGradient = (double) scoreDiffLevel / (double) totalDiffLevel;
                timeGradient += levelTimeGradient * levelTimeGradientWeight;
            }

        }
        if (timeGradient > 1.0) {
            // Rounding error due to calculating with doubles
            timeGradient = 1.0;
        }
        return timeGradient;
    }

}

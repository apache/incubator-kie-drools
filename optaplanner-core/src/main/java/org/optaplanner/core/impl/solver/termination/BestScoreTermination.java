/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.score.ScoreUtils;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public class BestScoreTermination extends AbstractTermination {

    private final int levelsSize;
    private final Score bestScoreLimit;
    private final double[] timeGradientWeightNumbers;

    public BestScoreTermination(ScoreDefinition scoreDefinition, Score bestScoreLimit, double[] timeGradientWeightNumbers) {
        levelsSize = scoreDefinition.getLevelsSize();
        this.bestScoreLimit = bestScoreLimit;
        if (bestScoreLimit == null) {
            throw new IllegalArgumentException("The bestScoreLimit (" + bestScoreLimit
                    + ") cannot be null.");
        }
        this.timeGradientWeightNumbers = timeGradientWeightNumbers;
        if (timeGradientWeightNumbers.length != levelsSize - 1) {
            throw new IllegalStateException(
                    "The timeGradientWeightNumbers (" + Arrays.toString(timeGradientWeightNumbers)
                            + ")'s length (" + timeGradientWeightNumbers.length
                            + ") is not 1 less than the levelsSize (" + scoreDefinition.getLevelsSize() + ").");
        }
    }

    // ************************************************************************
    // Terminated methods
    // ************************************************************************

    @Override
    public boolean isSolverTerminated(DefaultSolverScope solverScope) {
        return isTerminated(solverScope.isBestSolutionInitialized(), solverScope.getBestScore());
    }

    @Override
    public boolean isPhaseTerminated(AbstractPhaseScope phaseScope) {
        return isTerminated(phaseScope.isBestSolutionInitialized(), phaseScope.getBestScore());
    }

    protected boolean isTerminated(boolean bestSolutionInitialized, Score bestScore) {
        return bestSolutionInitialized && bestScore.compareTo(bestScoreLimit) >= 0;
    }

    // ************************************************************************
    // Time gradient methods
    // ************************************************************************

    @Override
    public double calculateSolverTimeGradient(DefaultSolverScope solverScope) {
        Score startingInitializedScore = solverScope.getStartingInitializedScore();
        Score bestScore = solverScope.getBestScore();
        return calculateTimeGradient(startingInitializedScore, bestScoreLimit, bestScore);
    }

    @Override
    public double calculatePhaseTimeGradient(AbstractPhaseScope phaseScope) {
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
                    + ") and score (" + score + ") don't have the same levelsSize.");
        }
        return ScoreUtils.calculateTimeGradient(totalDiffNumbers, scoreDiffNumbers, timeGradientWeightNumbers,
                levelsSize);
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    @Override
    public Termination createChildThreadTermination(DefaultSolverScope solverScope, ChildThreadType childThreadType) {
        // TODO FIXME through some sort of solverlistener and async behaviour...
        throw new UnsupportedOperationException("This terminationClass (" + getClass()
                + ") does not yet support being used in child threads of type (" + childThreadType + ").");
    }

    @Override
    public String toString() {
        return "BestScore(" + bestScoreLimit + ")";
    }

}

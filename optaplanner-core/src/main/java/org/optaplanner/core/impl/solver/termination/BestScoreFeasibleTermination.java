/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.score.ScoreUtils;
import org.optaplanner.core.impl.score.definition.FeasibilityScoreDefinition;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public class BestScoreFeasibleTermination extends AbstractTermination {

    private final int feasibleLevelsSize;
    private final double[] timeGradientWeightFeasibleNumbers;

    public BestScoreFeasibleTermination(FeasibilityScoreDefinition scoreDefinition,
            double[] timeGradientWeightFeasibleNumbers) {
        feasibleLevelsSize = scoreDefinition.getFeasibleLevelsSize();
        this.timeGradientWeightFeasibleNumbers = timeGradientWeightFeasibleNumbers;
        if (timeGradientWeightFeasibleNumbers.length != feasibleLevelsSize - 1) {
            throw new IllegalStateException(
                    "The timeGradientWeightNumbers (" + Arrays.toString(timeGradientWeightFeasibleNumbers)
                            + ")'s length (" + timeGradientWeightFeasibleNumbers.length
                            + ") is not 1 less than the feasibleLevelsSize (" + scoreDefinition.getFeasibleLevelsSize()
                            + ").");
        }
    }

    @Override
    public boolean isSolverTerminated(DefaultSolverScope solverScope) {
        return isTerminated(solverScope.getBestScore());
    }

    @Override
    public boolean isPhaseTerminated(AbstractPhaseScope phaseScope) {
        return isTerminated(phaseScope.getBestScore());
    }

    protected boolean isTerminated(Score bestScore) {
        return ((FeasibilityScore) bestScore).isFeasible();
    }

    @Override
    public double calculateSolverTimeGradient(DefaultSolverScope solverScope) {
        return calculateFeasibilityTimeGradient(
                (FeasibilityScore) solverScope.getStartingInitializedScore(), (FeasibilityScore) solverScope.getBestScore());
    }

    @Override
    public double calculatePhaseTimeGradient(AbstractPhaseScope phaseScope) {
        return calculateFeasibilityTimeGradient(
                (FeasibilityScore) phaseScope.getStartingScore(), (FeasibilityScore) phaseScope.getBestScore());
    }

    protected double calculateFeasibilityTimeGradient(FeasibilityScore startScore, FeasibilityScore score) {
        if (startScore == null || !startScore.isSolutionInitialized()) {
            return 0.0;
        }
        Score totalDiff = startScore.negate();
        Number[] totalDiffNumbers = totalDiff.toLevelNumbers();
        Score scoreDiff = score.subtract(startScore);
        Number[] scoreDiffNumbers = scoreDiff.toLevelNumbers();
        if (scoreDiffNumbers.length != totalDiffNumbers.length) {
            throw new IllegalStateException("The startScore (" + startScore + ") and score (" + score
                    + ") don't have the same levelsSize.");
        }
        return ScoreUtils.calculateTimeGradient(totalDiffNumbers, scoreDiffNumbers, timeGradientWeightFeasibleNumbers,
                feasibleLevelsSize);
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
        return "BestScoreFeasible()";
    }

}

package org.optaplanner.core.impl.solver.termination;

import java.util.Arrays;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;

public class BestScoreTermination<Solution_> extends AbstractTermination<Solution_> {

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
    public boolean isSolverTerminated(SolverScope<Solution_> solverScope) {
        return isTerminated(solverScope.isBestSolutionInitialized(), solverScope.getBestScore());
    }

    @Override
    public boolean isPhaseTerminated(AbstractPhaseScope<Solution_> phaseScope) {
        return isTerminated(phaseScope.isBestSolutionInitialized(), (Score) phaseScope.getBestScore());
    }

    protected boolean isTerminated(boolean bestSolutionInitialized, Score bestScore) {
        return bestSolutionInitialized && bestScore.compareTo(bestScoreLimit) >= 0;
    }

    // ************************************************************************
    // Time gradient methods
    // ************************************************************************

    @Override
    public double calculateSolverTimeGradient(SolverScope<Solution_> solverScope) {
        Score startingInitializedScore = solverScope.getStartingInitializedScore();
        Score bestScore = solverScope.getBestScore();
        return calculateTimeGradient(startingInitializedScore, bestScoreLimit, bestScore);
    }

    @Override
    public double calculatePhaseTimeGradient(AbstractPhaseScope<Solution_> phaseScope) {
        Score startingInitializedScore = phaseScope.getStartingScore();
        Score bestScore = phaseScope.getBestScore();
        return calculateTimeGradient(startingInitializedScore, bestScoreLimit, bestScore);
    }

    protected <Score_ extends Score<Score_>> double calculateTimeGradient(Score_ startScore, Score_ endScore,
            Score_ score) {
        Score_ totalDiff = endScore.subtract(startScore);
        Number[] totalDiffNumbers = totalDiff.toLevelNumbers();
        Score_ scoreDiff = score.subtract(startScore);
        Number[] scoreDiffNumbers = scoreDiff.toLevelNumbers();
        if (scoreDiffNumbers.length != totalDiffNumbers.length) {
            throw new IllegalStateException("The startScore (" + startScore + "), endScore (" + endScore
                    + ") and score (" + score + ") don't have the same levelsSize.");
        }
        return calculateTimeGradient(totalDiffNumbers, scoreDiffNumbers, timeGradientWeightNumbers,
                levelsSize);
    }

    /**
     *
     * @param totalDiffNumbers never null
     * @param scoreDiffNumbers never null
     * @param timeGradientWeightNumbers never null
     * @param levelDepth The number of levels of the diffNumbers that are included
     * @return {@code 0.0 <= value <= 1.0}
     */
    static double calculateTimeGradient(Number[] totalDiffNumbers, Number[] scoreDiffNumbers,
            double[] timeGradientWeightNumbers, int levelDepth) {
        double timeGradient = 0.0;
        double remainingTimeGradient = 1.0;
        for (int i = 0; i < levelDepth; i++) {
            double levelTimeGradientWeight;
            if (i != (levelDepth - 1)) {
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
                double levelTimeGradient = scoreDiffLevel / totalDiffLevel;
                timeGradient += levelTimeGradient * levelTimeGradientWeight;
            }

        }
        if (timeGradient > 1.0) {
            // Rounding error due to calculating with doubles
            timeGradient = 1.0;
        }
        return timeGradient;
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    @Override
    public Termination<Solution_> createChildThreadTermination(SolverScope<Solution_> solverScope,
            ChildThreadType childThreadType) {
        // TODO FIXME through some sort of solverlistener and async behaviour...
        throw new UnsupportedOperationException("This terminationClass (" + getClass()
                + ") does not yet support being used in child threads of type (" + childThreadType + ").");
    }

    @Override
    public String toString() {
        return "BestScore(" + bestScoreLimit + ")";
    }

}

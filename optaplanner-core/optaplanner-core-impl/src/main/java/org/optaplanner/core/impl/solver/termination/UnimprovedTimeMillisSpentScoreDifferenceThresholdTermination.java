package org.optaplanner.core.impl.solver.termination;

import java.time.Clock;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;
import org.optaplanner.core.impl.util.Pair;

public class UnimprovedTimeMillisSpentScoreDifferenceThresholdTermination<Solution_>
        extends AbstractTermination<Solution_> {

    private final long unimprovedTimeMillisSpentLimit;
    private final Score unimprovedScoreDifferenceThreshold;
    private final Clock clock;

    private Queue<Pair<Long, Score>> bestScoreImprovementHistoryQueue;
    // safeTimeMillis is until when we're safe from termination
    private long solverSafeTimeMillis = -1L;
    private long phaseSafeTimeMillis = -1L;

    public UnimprovedTimeMillisSpentScoreDifferenceThresholdTermination(
            long unimprovedTimeMillisSpentLimit,
            Score unimprovedScoreDifferenceThreshold) {
        this(unimprovedTimeMillisSpentLimit, unimprovedScoreDifferenceThreshold, Clock.systemUTC());
    }

    protected UnimprovedTimeMillisSpentScoreDifferenceThresholdTermination(
            long unimprovedTimeMillisSpentLimit,
            Score unimprovedScoreDifferenceThreshold,
            Clock clock) {
        this.unimprovedTimeMillisSpentLimit = unimprovedTimeMillisSpentLimit;
        this.unimprovedScoreDifferenceThreshold = unimprovedScoreDifferenceThreshold;
        if (unimprovedTimeMillisSpentLimit < 0L) {
            throw new IllegalArgumentException("The unimprovedTimeMillisSpentLimit (" + unimprovedTimeMillisSpentLimit
                    + ") cannot be negative.");
        }
        this.clock = clock;
    }

    public long getUnimprovedTimeMillisSpentLimit() {
        return unimprovedTimeMillisSpentLimit;
    }

    public Score getUnimprovedScoreDifferenceThreshold() {
        return unimprovedScoreDifferenceThreshold;
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        bestScoreImprovementHistoryQueue = new ArrayDeque<>();
        solverSafeTimeMillis = solverScope.getBestSolutionTimeMillis() + unimprovedTimeMillisSpentLimit;
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        bestScoreImprovementHistoryQueue = null;
        solverSafeTimeMillis = -1L;
    }

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        phaseSafeTimeMillis = phaseScope.getStartingSystemTimeMillis() + unimprovedTimeMillisSpentLimit;
    }

    @Override
    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        phaseSafeTimeMillis = -1L;
    }

    @Override
    public void stepEnded(AbstractStepScope<Solution_> stepScope) {
        if (stepScope.getBestScoreImproved()) {
            SolverScope<Solution_> solverScope = stepScope.getPhaseScope().getSolverScope();
            long bestSolutionTimeMillis = solverScope.getBestSolutionTimeMillis();
            Score bestScore = solverScope.getBestScore();
            for (Iterator<Pair<Long, Score>> it = bestScoreImprovementHistoryQueue.iterator(); it.hasNext();) {
                Pair<Long, Score> bestScoreImprovement = it.next();
                Score scoreDifference = bestScore.subtract(bestScoreImprovement.getValue());
                boolean timeLimitNotYetReached = bestScoreImprovement.getKey()
                        + unimprovedTimeMillisSpentLimit >= bestSolutionTimeMillis;
                boolean scoreImprovedOverThreshold = scoreDifference.compareTo(unimprovedScoreDifferenceThreshold) >= 0;
                if (scoreImprovedOverThreshold && timeLimitNotYetReached) {
                    it.remove();
                    long safeTimeMillis = bestSolutionTimeMillis + unimprovedTimeMillisSpentLimit;
                    solverSafeTimeMillis = safeTimeMillis;
                    phaseSafeTimeMillis = safeTimeMillis;
                } else {
                    break;
                }
            }
            bestScoreImprovementHistoryQueue.add(Pair.of(bestSolutionTimeMillis, bestScore));
        }
    }

    // ************************************************************************
    // Terminated methods
    // ************************************************************************

    @Override
    public boolean isSolverTerminated(SolverScope<Solution_> solverScope) {
        return isTerminated(solverSafeTimeMillis);
    }

    @Override
    public boolean isPhaseTerminated(AbstractPhaseScope<Solution_> phaseScope) {
        return isTerminated(phaseSafeTimeMillis);
    }

    protected boolean isTerminated(long safeTimeMillis) {
        // It's possible that there is already an improving move in the forager
        // that will end up pushing the safeTimeMillis further
        // but that doesn't change the fact that the best score didn't improve enough in the specified time interval.
        // It just looks weird because it terminates even though the final step is a high enough score improvement.
        long now = clock.millis();
        return now > safeTimeMillis;
    }

    // ************************************************************************
    // Time gradient methods
    // ************************************************************************

    @Override
    public double calculateSolverTimeGradient(SolverScope<Solution_> solverScope) {
        return calculateTimeGradient(solverSafeTimeMillis);
    }

    @Override
    public double calculatePhaseTimeGradient(AbstractPhaseScope<Solution_> phaseScope) {
        return calculateTimeGradient(phaseSafeTimeMillis);
    }

    protected double calculateTimeGradient(long safeTimeMillis) {
        long now = clock.millis();
        long unimprovedTimeMillisSpent = now - (safeTimeMillis - unimprovedTimeMillisSpentLimit);
        double timeGradient = unimprovedTimeMillisSpent / ((double) unimprovedTimeMillisSpentLimit);
        return Math.min(timeGradient, 1.0);
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    @Override
    public UnimprovedTimeMillisSpentScoreDifferenceThresholdTermination<Solution_> createChildThreadTermination(
            SolverScope<Solution_> solverScope, ChildThreadType childThreadType) {
        return new UnimprovedTimeMillisSpentScoreDifferenceThresholdTermination<>(unimprovedTimeMillisSpentLimit,
                unimprovedScoreDifferenceThreshold);
    }

    @Override
    public String toString() {
        return "UnimprovedTimeMillisSpent(" + unimprovedTimeMillisSpentLimit + ")";
    }
}

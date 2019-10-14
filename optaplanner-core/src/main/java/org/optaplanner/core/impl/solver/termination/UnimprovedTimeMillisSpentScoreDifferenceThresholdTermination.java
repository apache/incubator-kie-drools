/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.time.Clock;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

import org.apache.commons.lang3.tuple.Pair;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public class UnimprovedTimeMillisSpentScoreDifferenceThresholdTermination extends AbstractTermination {

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
    public void solvingStarted(DefaultSolverScope solverScope) {
        bestScoreImprovementHistoryQueue = new ArrayDeque<>();
        solverSafeTimeMillis = solverScope.getBestSolutionTimeMillis() + unimprovedTimeMillisSpentLimit;
    }

    @Override
    public void solvingEnded(DefaultSolverScope solverScope) {
        bestScoreImprovementHistoryQueue = null;
        solverSafeTimeMillis = -1L;
    }

    @Override
    public void phaseStarted(AbstractPhaseScope phaseScope) {
        phaseSafeTimeMillis = phaseScope.getStartingSystemTimeMillis() + unimprovedTimeMillisSpentLimit;
    }

    @Override
    public void phaseEnded(AbstractPhaseScope phaseScope) {
        phaseSafeTimeMillis = -1L;
    }

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        if (stepScope.getBestScoreImproved()) {
            DefaultSolverScope solverScope = stepScope.getPhaseScope().getSolverScope();
            long bestSolutionTimeMillis = solverScope.getBestSolutionTimeMillis();
            Score bestScore = solverScope.getBestScore();
            for (Iterator<Pair<Long, Score>> it = bestScoreImprovementHistoryQueue.iterator(); it.hasNext(); ) {
                Pair<Long, Score> bestScoreImprovement = it.next();
                Score scoreDifference = bestScore.subtract(bestScoreImprovement.getValue());
                boolean timeLimitNotYetReached =
                        bestScoreImprovement.getKey() + unimprovedTimeMillisSpentLimit >= bestSolutionTimeMillis;
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
    public boolean isSolverTerminated(DefaultSolverScope solverScope) {
        return isTerminated(solverSafeTimeMillis);
    }

    @Override
    public boolean isPhaseTerminated(AbstractPhaseScope phaseScope) {
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
    public double calculateSolverTimeGradient(DefaultSolverScope solverScope) {
        return calculateTimeGradient(solverSafeTimeMillis);
    }

    @Override
    public double calculatePhaseTimeGradient(AbstractPhaseScope phaseScope) {
        return calculateTimeGradient(phaseSafeTimeMillis);
    }

    protected double calculateTimeGradient(long safeTimeMillis) {
        long now = clock.millis();
        long unimprovedTimeMillisSpent = now - (safeTimeMillis - unimprovedTimeMillisSpentLimit);
        double timeGradient = ((double) unimprovedTimeMillisSpent) / ((double) unimprovedTimeMillisSpentLimit);
        return Math.min(timeGradient, 1.0);
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    @Override
    public UnimprovedTimeMillisSpentScoreDifferenceThresholdTermination createChildThreadTermination(
            DefaultSolverScope solverScope, ChildThreadType childThreadType) {
        return new UnimprovedTimeMillisSpentScoreDifferenceThresholdTermination(
                unimprovedTimeMillisSpentLimit, unimprovedScoreDifferenceThreshold);
    }

    @Override
    public String toString() {
        return "UnimprovedTimeMillisSpent(" + unimprovedTimeMillisSpentLimit + ")";
    }
}

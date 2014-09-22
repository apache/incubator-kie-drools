/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      hhttp://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.phase.scope;

import java.util.List;
import java.util.Random;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPhaseScope {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected final DefaultSolverScope solverScope;

    protected long startingSystemTimeMillis;

    protected Score startingScore;

    protected int bestSolutionStepIndex;

    public AbstractPhaseScope(DefaultSolverScope solverScope) {
        this.solverScope = solverScope;
    }

    public DefaultSolverScope getSolverScope() {
        return solverScope;
    }

    public long getStartingSystemTimeMillis() {
        return startingSystemTimeMillis;
    }

    public Score getStartingScore() {
        return startingScore;
    }

    public int getBestSolutionStepIndex() {
        return bestSolutionStepIndex;
    }

    public void setBestSolutionStepIndex(int bestSolutionStepIndex) {
        this.bestSolutionStepIndex = bestSolutionStepIndex;
    }

    public abstract AbstractStepScope getLastCompletedStepScope();

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

    public void reset() {
        startingSystemTimeMillis = System.currentTimeMillis();
        bestSolutionStepIndex = -1;
        // TODO Usage of solverScope.getBestScore() would be better performance wise but is null with a uninitialized score
        startingScore = solverScope.calculateScore();
        if (getLastCompletedStepScope().getStepIndex() < 0) {
            getLastCompletedStepScope().setScore(startingScore);
        }
    }

    public SolutionDescriptor getSolutionDescriptor() {
        return solverScope.getSolutionDescriptor();
    }

    public ScoreDefinition getScoreDefinition() {
        return solverScope.getScoreDefinition();
    }

    public long calculateSolverTimeMillisSpent() {
        return solverScope.calculateTimeMillisSpent();
    }

    public long calculatePhaseTimeMillisSpent() {
        long now = System.currentTimeMillis();
        return now - startingSystemTimeMillis;
    }

    public InnerScoreDirector getScoreDirector() {
        return solverScope.getScoreDirector();
    }

    public Solution getWorkingSolution() {
        return solverScope.getWorkingSolution();
    }

    public int getWorkingEntityCount() {
        return solverScope.getWorkingEntityCount();
    }

    public List<Object> getWorkingEntityList() {
        return solverScope.getWorkingEntityList();
    }

    public int getWorkingValueCount() {
        return solverScope.getWorkingValueCount();
    }

    public Score calculateScore() {
        return solverScope.calculateScore();
    }

    public void assertExpectedWorkingScore(Score expectedWorkingScore, Object completedAction) {
        solverScope.assertExpectedWorkingScore(expectedWorkingScore, completedAction);
    }

    public void assertWorkingScoreFromScratch(Score workingScore, Object completedAction) {
        solverScope.assertWorkingScoreFromScratch(workingScore, completedAction);
    }

    public void assertExpectedUndoMoveScore(Move move, Move undoMove) {
        Score undoScore = calculateScore();
        Score lastCompletedStepScore = getLastCompletedStepScope().getScore();
        if (!undoScore.equals(lastCompletedStepScore)) {
            // First assert that are probably no corrupted score rules.
            getScoreDirector().assertWorkingScoreFromScratch(undoScore, undoMove);
            throw new IllegalStateException(
                    "The moveClass (" + move.getClass() + ")'s move (" + move
                            + ") probably has a corrupted undoMove (" + undoMove + ")." +
                            " Or maybe there are corrupted score rules.\n"
                            + "Check the Move.createUndoMove(...) method of that Move class" +
                            " and enable EnvironmentMode " + EnvironmentMode.FULL_ASSERT
                            + " to fail-faster on corrupted score rules.\n"
                            + "Score corruption: the lastCompletedStepScore (" + lastCompletedStepScore
                            + ") is not the undoScore (" + undoScore + ").");
        }
    }

    public Random getWorkingRandom() {
        return solverScope.getWorkingRandom();
    }

    public boolean isBestSolutionInitialized() {
        return solverScope.isBestSolutionInitialized();
    }

    public Score getBestScore() {
        return solverScope.getBestScore();
    }

    public String getBestScoreWithUninitializedPrefix() {
        return solverScope.getBestScoreWithUninitializedPrefix();
    }

    public long getPhaseBestSolutionTimeMillis() {
        long bestSolutionTimeMillis = solverScope.getBestSolutionTimeMillis();
        // If the termination is explicitly phase configured, previous phases must not affect it
        if (bestSolutionTimeMillis < startingSystemTimeMillis) {
            bestSolutionTimeMillis = startingSystemTimeMillis;
        }
        return bestSolutionTimeMillis;
    }

    public int getNextStepIndex() {
        return getLastCompletedStepScope().getStepIndex() + 1;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName(); // TODO add + "(" + phaseIndex + ")"
    }

}

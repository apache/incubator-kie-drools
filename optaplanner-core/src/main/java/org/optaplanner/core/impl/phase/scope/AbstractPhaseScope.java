/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.phase.scope;

import java.util.List;
import java.util.Random;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class AbstractPhaseScope<Solution_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected final SolverScope<Solution_> solverScope;

    protected Long startingSystemTimeMillis;
    protected Long startingScoreCalculationCount;
    protected Score startingScore;
    protected Long endingSystemTimeMillis;
    protected Long endingScoreCalculationCount;
    protected long childThreadsScoreCalculationCount = 0;

    protected int bestSolutionStepIndex;

    public AbstractPhaseScope(SolverScope<Solution_> solverScope) {
        this.solverScope = solverScope;
    }

    public SolverScope<Solution_> getSolverScope() {
        return solverScope;
    }

    public Long getStartingSystemTimeMillis() {
        return startingSystemTimeMillis;
    }

    public Score getStartingScore() {
        return startingScore;
    }

    public Long getEndingSystemTimeMillis() {
        return endingSystemTimeMillis;
    }

    public int getBestSolutionStepIndex() {
        return bestSolutionStepIndex;
    }

    public void setBestSolutionStepIndex(int bestSolutionStepIndex) {
        this.bestSolutionStepIndex = bestSolutionStepIndex;
    }

    public abstract AbstractStepScope<Solution_> getLastCompletedStepScope();

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

    public void reset() {
        bestSolutionStepIndex = -1;
        // TODO Usage of solverScope.getBestScore() would be better performance wise but is null with a uninitialized score
        startingScore = solverScope.calculateScore();
        if (getLastCompletedStepScope().getStepIndex() < 0) {
            getLastCompletedStepScope().setScore(startingScore);
        }
    }

    public void startingNow() {
        startingSystemTimeMillis = System.currentTimeMillis();
        startingScoreCalculationCount = getScoreDirector().getCalculationCount();
    }

    public void endingNow() {
        endingSystemTimeMillis = System.currentTimeMillis();
        endingScoreCalculationCount = getScoreDirector().getCalculationCount();
    }

    public SolutionDescriptor<Solution_> getSolutionDescriptor() {
        return solverScope.getSolutionDescriptor();
    }

    public ScoreDefinition getScoreDefinition() {
        return solverScope.getScoreDefinition();
    }

    public long calculateSolverTimeMillisSpentUpToNow() {
        return solverScope.calculateTimeMillisSpentUpToNow();
    }

    public long calculatePhaseTimeMillisSpentUpToNow() {
        long now = System.currentTimeMillis();
        return now - startingSystemTimeMillis;
    }

    public long getPhaseTimeMillisSpent() {
        return endingSystemTimeMillis - startingSystemTimeMillis;
    }

    public void addChildThreadsScoreCalculationCount(long addition) {
        solverScope.addChildThreadsScoreCalculationCount(addition);
        childThreadsScoreCalculationCount += addition;
    }

    public long getPhaseScoreCalculationCount() {
        return endingScoreCalculationCount - startingScoreCalculationCount + childThreadsScoreCalculationCount;
    }

    /**
     * @return at least 0, per second
     */
    public long getPhaseScoreCalculationSpeed() {
        long timeMillisSpent = getPhaseTimeMillisSpent();
        // Avoid divide by zero exception on a fast CPU
        return getPhaseScoreCalculationCount() * 1000L / (timeMillisSpent == 0L ? 1L : timeMillisSpent);
    }

    public InnerScoreDirector<Solution_> getScoreDirector() {
        return solverScope.getScoreDirector();
    }

    public Solution_ getWorkingSolution() {
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
        getScoreDirector().assertExpectedWorkingScore(expectedWorkingScore, completedAction);
    }

    public void assertWorkingScoreFromScratch(Score workingScore, Object completedAction) {
        getScoreDirector().assertWorkingScoreFromScratch(workingScore, completedAction);
    }

    public void assertPredictedScoreFromScratch(Score workingScore, Object completedAction) {
        getScoreDirector().assertPredictedScoreFromScratch(workingScore, completedAction);
    }

    public void assertShadowVariablesAreNotStale(Score workingScore, Object completedAction) {
        getScoreDirector().assertShadowVariablesAreNotStale(workingScore, completedAction);
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

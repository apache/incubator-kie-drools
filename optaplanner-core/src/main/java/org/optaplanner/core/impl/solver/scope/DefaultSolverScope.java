/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.solver.scope;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.ScoreUtils;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class DefaultSolverScope<Solution_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected int startingSolverCount;
    protected Random workingRandom;
    protected InnerScoreDirector<Solution_> scoreDirector;

    protected Long startingSystemTimeMillis;
    protected Long endingSystemTimeMillis;

    protected Score startingInitializedScore; // TODO after initialization => ambiguous with solve()'s planningProblem

    protected volatile Solution_ bestSolution;
    protected int bestUninitializedVariableCount; // TODO remove me by folding me into bestSolution.getScore(): https://issues.jboss.org/browse/PLANNER-405
    protected Score bestScore; // TODO remove me by folding me into bestSolution.getScore(): https://issues.jboss.org/browse/PLANNER-405
    protected Long bestSolutionTimeMillis;


    public int getStartingSolverCount() {
        return startingSolverCount;
    }

    public void setStartingSolverCount(int startingSolverCount) {
        this.startingSolverCount = startingSolverCount;
    }

    public Random getWorkingRandom() {
        return workingRandom;
    }

    public void setWorkingRandom(Random workingRandom) {
        this.workingRandom = workingRandom;
    }

    public InnerScoreDirector<Solution_> getScoreDirector() {
        return scoreDirector;
    }

    public void setScoreDirector(InnerScoreDirector<Solution_> scoreDirector) {
        this.scoreDirector = scoreDirector;
    }

    public Long getStartingSystemTimeMillis() {
        return startingSystemTimeMillis;
    }

    public void setStartingSystemTimeMillis(Long startingSystemTimeMillis) {
        this.startingSystemTimeMillis = startingSystemTimeMillis;
    }

    public Long getEndingSystemTimeMillis() {
        return endingSystemTimeMillis;
    }

    public void setEndingSystemTimeMillis(Long endingSystemTimeMillis) {
        this.endingSystemTimeMillis = endingSystemTimeMillis;
    }

    public SolutionDescriptor<Solution_> getSolutionDescriptor() {
        return scoreDirector.getSolutionDescriptor();
    }

    public ScoreDefinition getScoreDefinition() {
        return scoreDirector.getScoreDefinition();
    }

    public Solution_ getWorkingSolution() {
        return scoreDirector.getWorkingSolution();
    }

    public int getWorkingEntityCount() {
        return scoreDirector.getWorkingEntityCount();
    }

    public List<Object> getWorkingEntityList() {
        return scoreDirector.getWorkingEntityList();
    }

    public int getWorkingValueCount() {
        return scoreDirector.getWorkingValueCount();
    }

    public Score calculateScore() {
        return scoreDirector.calculateScore();
    }

    public void assertExpectedWorkingScore(Score expectedWorkingScore, Object completedAction) {
        scoreDirector.assertExpectedWorkingScore(expectedWorkingScore, completedAction);
    }

    public void assertWorkingScoreFromScratch(Score workingScore, Object completedAction) {
        scoreDirector.assertWorkingScoreFromScratch(workingScore, completedAction);
    }

    public void assertScoreFromScratch(Solution_ solution) {
        scoreDirector.getScoreDirectorFactory().assertScoreFromScratch(solution);
    }

    public Score getStartingInitializedScore() {
        return startingInitializedScore;
    }

    public void setStartingInitializedScore(Score startingInitializedScore) {
        this.startingInitializedScore = startingInitializedScore;
    }

    public long getCalculateCount() {
        return scoreDirector.getCalculateCount();
    }

    public Solution_ getBestSolution() {
        return bestSolution;
    }

    /**
     * The {@link PlanningSolution best solution} must never be the same instance
     * as the {@link PlanningSolution working solution}, it should be a (un)changed clone.
     * @param bestSolution never null
     */
    public void setBestSolution(Solution_ bestSolution) {
        this.bestSolution = bestSolution;
    }

    public int getBestUninitializedVariableCount() {
        return bestUninitializedVariableCount;
    }

    public void setBestUninitializedVariableCount(int bestUninitializedVariableCount) {
        if (bestUninitializedVariableCount < 0) {
            throw new IllegalArgumentException("The bestUninitializedVariableCount ("
                    + bestUninitializedVariableCount + ") cannot be negative.");
        }
        this.bestUninitializedVariableCount = bestUninitializedVariableCount;
    }

    public Score getBestScore() {
        return bestScore;
    }

    public void setBestScore(Score bestScore) {
        this.bestScore = bestScore;
    }

    public Long getBestSolutionTimeMillis() {
        return bestSolutionTimeMillis;
    }

    public void setBestSolutionTimeMillis(Long bestSolutionTimeMillis) {
        this.bestSolutionTimeMillis = bestSolutionTimeMillis;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

    public boolean isBestSolutionInitialized() {
        return bestUninitializedVariableCount == 0;
    }

    public long calculateTimeMillisSpent() {
        long now = System.currentTimeMillis();
        return now - startingSystemTimeMillis;
    }

    public void setWorkingSolutionFromBestSolution() {
        // The workingSolution must never be the same instance as the bestSolution.
        scoreDirector.setWorkingSolution(scoreDirector.cloneSolution(bestSolution));
    }

    public String getBestScoreWithUninitializedPrefix() {
        return ScoreUtils.getScoreWithUninitializedPrefix(bestUninitializedVariableCount, bestScore);
    }

}

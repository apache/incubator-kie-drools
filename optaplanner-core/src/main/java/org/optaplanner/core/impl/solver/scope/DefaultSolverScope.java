/*
 * Copyright 2011 JBoss Inc
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

import java.util.List;
import java.util.Random;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSolverScope {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected Long startingSystemTimeMillis;
    protected int startingSolverCount;
    protected Long endingSystemTimeMillis;
    protected InnerScoreDirector scoreDirector;
    protected Random workingRandom;

    protected Score startingInitializedScore; // TODO after initialization => ambiguous with solve()'s planningProblem

    protected Solution bestSolution;
    protected int bestUninitializedVariableCount; // TODO remove me by folding me into bestSolution.getScore()
    protected Score bestScore; // TODO remove me by folding me into bestSolution.getScore()
    protected Long bestSolutionTimeMillis;


    public Long getStartingSystemTimeMillis() {
        return startingSystemTimeMillis;
    }

    public void setStartingSystemTimeMillis(Long startingSystemTimeMillis) {
        this.startingSystemTimeMillis = startingSystemTimeMillis;
    }

    public int getStartingSolverCount() {
        return startingSolverCount;
    }

    public void setStartingSolverCount(int startingSolverCount) {
        this.startingSolverCount = startingSolverCount;
    }

    public Long getEndingSystemTimeMillis() {
        return endingSystemTimeMillis;
    }

    public void setEndingSystemTimeMillis(Long endingSystemTimeMillis) {
        this.endingSystemTimeMillis = endingSystemTimeMillis;
    }

    public InnerScoreDirector getScoreDirector() {
        return scoreDirector;
    }

    public void setScoreDirector(InnerScoreDirector scoreDirector) {
        // TODO remove HACK to fix memory leak of https://issues.jboss.org/browse/PLANNER-19
        if (this.scoreDirector != null) {
            this.scoreDirector.dispose();
        }
        this.scoreDirector = scoreDirector;
    }

    public SolutionDescriptor getSolutionDescriptor() {
        return scoreDirector.getSolutionDescriptor();
    }

    public ScoreDefinition getScoreDefinition() {
        return scoreDirector.getScoreDefinition();
    }

    public Solution getWorkingSolution() {
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

    public void assertScoreFromScratch(Solution solution) {
        scoreDirector.getScoreDirectorFactory().assertScoreFromScratch(solution);
    }

    public Random getWorkingRandom() {
        return workingRandom;
    }

    public void setWorkingRandom(Random workingRandom) {
        this.workingRandom = workingRandom;
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

    public Solution getBestSolution() {
        return bestSolution;
    }

    /**
     * The bestSolution must never be the same instance as the workingSolution, it should be a (un)changed clone.
     * @param bestSolution never null
     */
    public void setBestSolution(Solution bestSolution) {
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
        SolutionCloner cloner = scoreDirector.getSolutionDescriptor().getSolutionCloner();
        scoreDirector.setWorkingSolution(cloner.cloneSolution(getBestSolution()));
    }

    public String getBestScoreWithUninitializedPrefix() {
        return isBestSolutionInitialized() ? bestScore.toString() : "uninitialized/" + bestScore;
    }

}

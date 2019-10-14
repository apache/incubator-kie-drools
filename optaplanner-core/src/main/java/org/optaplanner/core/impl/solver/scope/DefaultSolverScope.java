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

import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class DefaultSolverScope<Solution_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected int startingSolverCount;
    protected Random workingRandom;
    protected InnerScoreDirector<Solution_> scoreDirector;
    /**
     * Used for capping CPU power usage in multithreaded scenarios.
     */
    protected Semaphore runnableThreadSemaphore = null;

    protected volatile Long startingSystemTimeMillis;
    protected volatile Long endingSystemTimeMillis;
    protected long childThreadsScoreCalculationCount = 0;

    protected Score startingInitializedScore;

    protected volatile Solution_ bestSolution;
    protected volatile Score bestScore;
    protected Long bestSolutionTimeMillis;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

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

    public void setRunnableThreadSemaphore(Semaphore runnableThreadSemaphore) {
        this.runnableThreadSemaphore = runnableThreadSemaphore;
    }

    public Long getStartingSystemTimeMillis() {
        return startingSystemTimeMillis;
    }

    public Long getEndingSystemTimeMillis() {
        return endingSystemTimeMillis;
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

    public void assertScoreFromScratch(Solution_ solution) {
        scoreDirector.getScoreDirectorFactory().assertScoreFromScratch(solution);
    }

    public Score getStartingInitializedScore() {
        return startingInitializedScore;
    }

    public void setStartingInitializedScore(Score startingInitializedScore) {
        this.startingInitializedScore = startingInitializedScore;
    }

    public void addChildThreadsScoreCalculationCount(long addition) {
        childThreadsScoreCalculationCount += addition;
    }

    public long getScoreCalculationCount() {
        return scoreDirector.getCalculationCount() + childThreadsScoreCalculationCount;
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

    public void startingNow() {
        startingSystemTimeMillis = System.currentTimeMillis();
        endingSystemTimeMillis = null;
    }

    public Long getBestSolutionTimeMillisSpent() {
        return bestSolutionTimeMillis - startingSystemTimeMillis;
    }

    public void endingNow() {
        endingSystemTimeMillis = System.currentTimeMillis();
    }

    public boolean isBestSolutionInitialized() {
        return bestScore.isSolutionInitialized();
    }

    public long calculateTimeMillisSpentUpToNow() {
        long now = System.currentTimeMillis();
        return now - startingSystemTimeMillis;
    }

    public long getTimeMillisSpent() {
        return endingSystemTimeMillis - startingSystemTimeMillis;
    }

    /**
     * @return at least 0, per second
     */
    public long getScoreCalculationSpeed() {
        long timeMillisSpent = getTimeMillisSpent();
        // Avoid divide by zero exception on a fast CPU
        return getScoreCalculationCount() * 1000L / (timeMillisSpent == 0L ? 1L : timeMillisSpent);
    }

    public void setWorkingSolutionFromBestSolution() {
        // The workingSolution must never be the same instance as the bestSolution.
        scoreDirector.setWorkingSolution(scoreDirector.cloneSolution(bestSolution));
    }

    public DefaultSolverScope<Solution_> createChildThreadSolverScope(ChildThreadType childThreadType) {
        DefaultSolverScope<Solution_> childThreadSolverScope = new DefaultSolverScope<>();
        childThreadSolverScope.startingSolverCount = startingSolverCount;
        // TODO FIXME use RandomFactory
        // Experiments show that this trick to attain reproducibility doesn't break uniform distribution
        childThreadSolverScope.workingRandom = new Random(workingRandom.nextLong());
        childThreadSolverScope.scoreDirector = scoreDirector.createChildThreadScoreDirector(childThreadType);
        childThreadSolverScope.startingSystemTimeMillis = startingSystemTimeMillis;
        childThreadSolverScope.endingSystemTimeMillis = null;
        childThreadSolverScope.startingInitializedScore = null;
        childThreadSolverScope.bestSolution = null;
        childThreadSolverScope.bestScore = null;
        childThreadSolverScope.bestSolutionTimeMillis = null;
        return childThreadSolverScope;
    }

    public void initializeYielding() {
        if (runnableThreadSemaphore != null) {
            try {
                runnableThreadSemaphore.acquire();
            } catch (InterruptedException e) {
                // TODO it will take a while before the BasicPlumbingTermination is called
                // The BasicPlumbingTermination will terminate the solver.
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Similar to {@link Thread#yield()}, but allows capping the number of active solver threads
     * at less than the CPU processor count, so other threads (for example servlet threads that handle REST calls)
     * and other processes (such as SSH) have access to uncontested CPUs and don't suffer any latency.
     * <p>
     * Needs to be called <b>before</b> {@link Termination#isPhaseTerminated(AbstractPhaseScope)},
     * so the decision to start a new iteration is after any yield waiting time has been consumed
     * (so {@link Solver#terminateEarly()} reacts immediately).
     */
    public void checkYielding() {
        if (runnableThreadSemaphore != null) {
            runnableThreadSemaphore.release();
            try {
                runnableThreadSemaphore.acquire();
            } catch (InterruptedException e) {
                // The BasicPlumbingTermination will terminate the solver.
                Thread.currentThread().interrupt();
            }
        }
    }

    public void destroyYielding() {
        if (runnableThreadSemaphore != null) {
            runnableThreadSemaphore.release();
        }
    }

}

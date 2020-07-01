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

package org.optaplanner.core.impl.solver;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.ProblemFactChange;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.phase.Phase;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.solver.random.RandomFactory;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.termination.BasicPlumbingTermination;
import org.optaplanner.core.impl.solver.termination.Termination;

/**
 * Default implementation for {@link Solver}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see Solver
 * @see AbstractSolver
 */
public class DefaultSolver<Solution_> extends AbstractSolver<Solution_> {

    protected EnvironmentMode environmentMode;
    protected RandomFactory randomFactory;

    protected BasicPlumbingTermination basicPlumbingTermination;

    protected final AtomicBoolean solving = new AtomicBoolean(false);

    protected final SolverScope<Solution_> solverScope;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public DefaultSolver(EnvironmentMode environmentMode, RandomFactory randomFactory,
            BestSolutionRecaller<Solution_> bestSolutionRecaller, BasicPlumbingTermination basicPlumbingTermination,
            Termination termination,
            List<Phase<Solution_>> phaseList,
            SolverScope<Solution_> solverScope) {
        super(bestSolutionRecaller, termination, phaseList);
        this.environmentMode = environmentMode;
        this.randomFactory = randomFactory;
        this.basicPlumbingTermination = basicPlumbingTermination;
        this.solverScope = solverScope;
    }

    public EnvironmentMode getEnvironmentMode() {
        return environmentMode;
    }

    public RandomFactory getRandomFactory() {
        return randomFactory;
    }

    public InnerScoreDirectorFactory<Solution_> getScoreDirectorFactory() {
        return solverScope.getScoreDirector().getScoreDirectorFactory();
    }

    public BestSolutionRecaller<Solution_> getBestSolutionRecaller() {
        return bestSolutionRecaller;
    }

    public List<Phase<Solution_>> getPhaseList() {
        return phaseList;
    }

    public SolverScope<Solution_> getSolverScope() {
        return solverScope;
    }

    // ************************************************************************
    // Complex getters
    // ************************************************************************

    public long getTimeMillisSpent() {
        Long startingSystemTimeMillis = solverScope.getStartingSystemTimeMillis();
        if (startingSystemTimeMillis == null) {
            // The solver hasn't started yet
            return 0L;
        }
        Long endingSystemTimeMillis = solverScope.getEndingSystemTimeMillis();
        if (endingSystemTimeMillis == null) {
            // The solver hasn't ended yet
            endingSystemTimeMillis = System.currentTimeMillis();
        }
        return endingSystemTimeMillis - startingSystemTimeMillis;
    }

    @Override
    public boolean isSolving() {
        return solving.get();
    }

    @Override
    public boolean terminateEarly() {
        boolean terminationEarlySuccessful = basicPlumbingTermination.terminateEarly();
        if (terminationEarlySuccessful) {
            logger.info("Terminating solver early.");
        }
        return terminationEarlySuccessful;
    }

    @Override
    public boolean isTerminateEarly() {
        return basicPlumbingTermination.isTerminateEarly();
    }

    @Override
    public boolean addProblemFactChange(ProblemFactChange<Solution_> problemFactChange) {
        return basicPlumbingTermination.addProblemFactChange(problemFactChange);
    }

    @Override
    public boolean addProblemFactChanges(List<ProblemFactChange<Solution_>> problemFactChangeList) {
        return basicPlumbingTermination.addProblemFactChanges(problemFactChangeList);
    }

    @Override
    public boolean isEveryProblemFactChangeProcessed() {
        return basicPlumbingTermination.isEveryProblemFactChangeProcessed();
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public final Solution_ solve(Solution_ problem) {
        if (problem == null) {
            throw new IllegalArgumentException("The problem (" + problem + ") must not be null.");
        }
        solverScope.setBestSolution(problem);
        outerSolvingStarted(solverScope);
        boolean restartSolver = true;
        while (restartSolver) {
            solvingStarted(solverScope);
            runPhases(solverScope);
            solvingEnded(solverScope);
            restartSolver = checkProblemFactChanges();
        }
        outerSolvingEnded(solverScope);
        return solverScope.getBestSolution();
    }

    public void outerSolvingStarted(SolverScope<Solution_> solverScope) {
        solving.set(true);
        basicPlumbingTermination.resetTerminateEarly();
        solverScope.setStartingSolverCount(0);
        solverScope.setWorkingRandom(randomFactory.createRandom());
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        solverScope.startingNow();
        solverScope.getScoreDirector().resetCalculationCount();
        super.solvingStarted(solverScope);
        int startingSolverCount = solverScope.getStartingSolverCount() + 1;
        solverScope.setStartingSolverCount(startingSolverCount);
        logger.info("Solving {}: time spent ({}), best score ({}), environment mode ({}), random ({}).",
                (startingSolverCount == 1 ? "started" : "restarted"),
                solverScope.calculateTimeMillisSpentUpToNow(),
                solverScope.getBestScore(),
                environmentMode.name(),
                (randomFactory != null ? randomFactory : "not fixed"));
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        super.solvingEnded(solverScope);
        solverScope.endingNow();
    }

    public void outerSolvingEnded(SolverScope<Solution_> solverScope) {
        // Must be kept open for doProblemFactChange
        solverScope.getScoreDirector().close();
        logger.info("Solving ended: time spent ({}), best score ({}), score calculation speed ({}/sec),"
                + " phase total ({}), environment mode ({}).",
                solverScope.getTimeMillisSpent(),
                solverScope.getBestScore(),
                solverScope.getScoreCalculationSpeed(),
                phaseList.size(),
                environmentMode.name());
        solving.set(false);
    }

    private boolean checkProblemFactChanges() {
        boolean restartSolver = basicPlumbingTermination.waitForRestartSolverDecision();
        if (!restartSolver) {
            return false;
        } else {
            BlockingQueue<ProblemFactChange> problemFactChangeQueue = basicPlumbingTermination
                    .startProblemFactChangesProcessing();
            solverScope.setWorkingSolutionFromBestSolution();
            Score score = null;
            int stepIndex = 0;
            ProblemFactChange<Solution_> problemFactChange = problemFactChangeQueue.poll();
            while (problemFactChange != null) {
                score = doProblemFactChange(problemFactChange, stepIndex);
                stepIndex++;
                problemFactChange = problemFactChangeQueue.poll();
            }
            // All PFCs are processed, fail fast if any of the new facts have null planning IDs.
            InnerScoreDirector<Solution_> scoreDirector = solverScope.getScoreDirector();
            scoreDirector.assertNonNullPlanningIds();
            // Everything is fine, proceed.
            basicPlumbingTermination.endProblemFactChangesProcessing();
            bestSolutionRecaller.updateBestSolution(solverScope);
            logger.info("Real-time problem fact changes done: step total ({}), new best score ({}).",
                    stepIndex, score);
            return true;
        }
    }

    private Score doProblemFactChange(ProblemFactChange<Solution_> problemFactChange, int stepIndex) {
        problemFactChange.doChange(solverScope.getScoreDirector());
        Score score = solverScope.calculateScore();
        logger.debug("    Step index ({}), new score ({}) for real-time problem fact change.", stepIndex, score);
        return score;
    }

}

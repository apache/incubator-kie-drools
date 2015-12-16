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

package org.optaplanner.core.impl.solver;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.phase.Phase;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.solver.event.SolverEventSupport;
import org.optaplanner.core.impl.solver.random.RandomFactory;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation for {@link Solver}.
 * @see Solver
 */
public class DefaultSolver<Solution_ extends Solution> implements Solver<Solution_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected SolverEventSupport<Solution_> solverEventSupport = new SolverEventSupport<Solution_>(this);

    protected EnvironmentMode environmentMode;
    protected RandomFactory randomFactory;
    protected boolean constraintMatchEnabledPreference = false;
    protected InnerScoreDirectorFactory scoreDirectorFactory;

    protected BasicPlumbingTermination basicPlumbingTermination;
    // Note that the basicPlumbingTermination is a component of this termination
    protected Termination termination;
    protected BestSolutionRecaller bestSolutionRecaller;
    protected List<Phase> phaseList;

    protected AtomicBoolean solving = new AtomicBoolean(false);

    protected DefaultSolverScope solverScope = new DefaultSolverScope();

    public EnvironmentMode getEnvironmentMode() {
        return environmentMode;
    }

    public void setEnvironmentMode(EnvironmentMode environmentMode) {
        this.environmentMode = environmentMode;
    }

    public RandomFactory getRandomFactory() {
        return randomFactory;
    }

    public void setRandomFactory(RandomFactory randomFactory) {
        this.randomFactory = randomFactory;
    }

    public boolean isConstraintMatchEnabledPreference() {
        return constraintMatchEnabledPreference;
    }

    public void setConstraintMatchEnabledPreference(boolean constraintMatchEnabledPreference) {
        this.constraintMatchEnabledPreference = constraintMatchEnabledPreference;
    }

    public InnerScoreDirectorFactory getScoreDirectorFactory() {
        return scoreDirectorFactory;
    }

    public void setScoreDirectorFactory(InnerScoreDirectorFactory scoreDirectorFactory) {
        this.scoreDirectorFactory = scoreDirectorFactory;
    }

    public void setBasicPlumbingTermination(BasicPlumbingTermination basicPlumbingTermination) {
        this.basicPlumbingTermination = basicPlumbingTermination;
    }

    public void setTermination(Termination termination) {
        this.termination = termination;
    }

    public BestSolutionRecaller getBestSolutionRecaller() {
        return bestSolutionRecaller;
    }

    public void setBestSolutionRecaller(BestSolutionRecaller bestSolutionRecaller) {
        this.bestSolutionRecaller = bestSolutionRecaller;
        this.bestSolutionRecaller.setSolverEventSupport(solverEventSupport);
    }

    public List<Phase> getPhaseList() {
        return phaseList;
    }

    public void setPhaseList(List<Phase> phaseList) {
        this.phaseList = phaseList;
    }

    public DefaultSolverScope getSolverScope() {
        return solverScope;
    }

    // ************************************************************************
    // Complex getters
    // ************************************************************************

    public Solution_ getBestSolution() {
        return (Solution_) solverScope.getBestSolution();
    }

    public long getTimeMillisSpent() {
        Long endingSystemTimeMillis = solverScope.getEndingSystemTimeMillis();
        if (endingSystemTimeMillis == null) {
            endingSystemTimeMillis = System.currentTimeMillis();
        }
        return endingSystemTimeMillis - solverScope.getStartingSystemTimeMillis();
    }

    public boolean isSolving() {
        return solving.get();
    }

    public boolean terminateEarly() {
        return basicPlumbingTermination.terminateEarly();
    }

    public boolean isTerminateEarly() {
        return basicPlumbingTermination.isTerminateEarly();
    }

    public boolean addProblemFactChange(ProblemFactChange problemFactChange) {
        return basicPlumbingTermination.addProblemFactChange(problemFactChange);
    }

    public boolean isEveryProblemFactChangeProcessed() {
        return basicPlumbingTermination.isEveryProblemFactChangeProcessed();
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public final Solution_ solve(Solution_ planningProblem) {
        if (planningProblem == null) {
            throw new IllegalArgumentException("The planningProblem (" + planningProblem
                    + ") must not be null.");
        }
        solverScope.setBestSolution(planningProblem);
        outerSolvingStarted(solverScope);
        boolean restartSolver = true;
        while (restartSolver) {
            solvingStarted(solverScope);
            runPhases();
            solvingEnded(solverScope);
            restartSolver = checkProblemFactChanges();
        }
        outerSolvingEnded(solverScope);
        return (Solution_) solverScope.getBestSolution();
    }

    public void outerSolvingStarted(DefaultSolverScope solverScope) {
        solving.set(true);
        basicPlumbingTermination.resetTerminateEarly();
        solverScope.setStartingSolverCount(0);
        solverScope.setWorkingRandom(randomFactory.createRandom());
        solverScope.setScoreDirector(scoreDirectorFactory.buildScoreDirector(constraintMatchEnabledPreference));
    }

    public void solvingStarted(DefaultSolverScope solverScope) {
        solverScope.setStartingSystemTimeMillis(System.currentTimeMillis());
        solverScope.setEndingSystemTimeMillis(null);
        solverScope.getScoreDirector().resetCalculateCount();
        solverScope.setWorkingSolutionFromBestSolution();
        bestSolutionRecaller.solvingStarted(solverScope);
        for (Phase phase : phaseList) {
            phase.solvingStarted(solverScope);
        }
        int startingSolverCount = solverScope.getStartingSolverCount() + 1;
        solverScope.setStartingSolverCount(startingSolverCount);
        logger.info("Solving {}: time spent ({}), best score ({}), environment mode ({}), random ({}).",
                (startingSolverCount == 1 ? "started" : "restarted"),
                solverScope.calculateTimeMillisSpent(),
                solverScope.getBestScoreWithUninitializedPrefix(),
                environmentMode.name(),
                (randomFactory != null ? randomFactory : "not fixed"));
    }

    protected void runPhases() {
        Iterator<Phase> it = phaseList.iterator();
        while (!termination.isSolverTerminated(solverScope) && it.hasNext()) {
            Phase phase = it.next();
            phase.solve(solverScope);
            if (it.hasNext()) {
                solverScope.setWorkingSolutionFromBestSolution();
            }
        }
        // TODO support doing round-robin of phases (only non-construction heuristics)
    }

    public void solvingEnded(DefaultSolverScope solverScope) {
        for (Phase phase : phaseList) {
            phase.solvingEnded(solverScope);
        }
        bestSolutionRecaller.solvingEnded(solverScope);
        solverScope.setEndingSystemTimeMillis(System.currentTimeMillis());
    }

    public void outerSolvingEnded(DefaultSolverScope solverScope) {
        // Must be kept open for doProblemFactChange
        solverScope.getScoreDirector().dispose();
        long timeMillisSpent = getTimeMillisSpent();
        // Avoid divide by zero exception on a fast CPU
        long averageCalculateCountPerSecond = solverScope.getCalculateCount() * 1000L
                / (timeMillisSpent == 0L ? 1L : timeMillisSpent);
        logger.info("Solving ended: time spent ({}), best score ({}), average calculate count per second ({}),"
                        + " environment mode ({}).",
                timeMillisSpent,
                solverScope.getBestScoreWithUninitializedPrefix(),
                averageCalculateCountPerSecond,
                environmentMode.name());
        solving.set(false);
    }

    private boolean checkProblemFactChanges() {
        boolean restartSolver = basicPlumbingTermination.waitForRestartSolverDecision();
        if (!restartSolver) {
            return false;
        } else {
            BlockingQueue<ProblemFactChange> problemFactChangeQueue
                    = basicPlumbingTermination.startProblemFactChangesProcessing();
            solverScope.setWorkingSolutionFromBestSolution();
            Score score = null;
            int stepIndex = 0;
            ProblemFactChange problemFactChange = problemFactChangeQueue.poll();
            while (problemFactChange != null) {
                score = doProblemFactChange(problemFactChange, stepIndex);
                stepIndex++;
                problemFactChange = problemFactChangeQueue.poll();
            }
            basicPlumbingTermination.endProblemFactChangesProcessing();
            Solution newBestSolution = solverScope.getScoreDirector().cloneWorkingSolution();
            // TODO BestSolutionRecaller.solverStarted() already calls countUninitializedVariables()
            int newBestUninitializedVariableCount = solverScope.getSolutionDescriptor()
                    .countUninitializedVariables(newBestSolution);
            bestSolutionRecaller.updateBestSolution(solverScope,
                    newBestSolution, newBestUninitializedVariableCount);
            logger.info("Real-time problem fact changes done: step total ({}), new {} best score ({}).",
                    stepIndex, (newBestUninitializedVariableCount <= 0 ? "initialized" : "uninitialized"), score);
            return true;
        }
    }

    private Score doProblemFactChange(ProblemFactChange problemFactChange, int stepIndex) {
        problemFactChange.doChange(solverScope.getScoreDirector());
        Score score = solverScope.calculateScore();
        logger.debug("    Step index ({}), new score ({}) for real-time problem fact change.", stepIndex, score);
        return score;
    }

    public void addEventListener(SolverEventListener<Solution_> eventListener) {
        solverEventSupport.addEventListener(eventListener);
    }

    public void removeEventListener(SolverEventListener<Solution_> eventListener) {
        solverEventSupport.removeEventListener(eventListener);
    }

    public void addPhaseLifecycleListener(PhaseLifecycleListener phaseLifecycleListener) {
        for (Phase phase : phaseList) {
            phase.addPhaseLifecycleListener(phaseLifecycleListener);
        }
    }

    public void removePhaseLifecycleListener(PhaseLifecycleListener phaseLifecycleListener) {
        for (Phase phase : phaseList) {
            phase.addPhaseLifecycleListener(phaseLifecycleListener);
        }
    }

}

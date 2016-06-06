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

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.phase.Phase;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleSupport;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
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
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see Solver
 */
public class DefaultSolver<Solution_> implements Solver<Solution_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected SolverEventSupport<Solution_> solverEventSupport = new SolverEventSupport<>(this);
    protected PhaseLifecycleSupport<Solution_> phaseLifecycleSupport = new PhaseLifecycleSupport<>();

    protected EnvironmentMode environmentMode;
    protected RandomFactory randomFactory;
    protected boolean constraintMatchEnabledPreference = false;
    protected InnerScoreDirectorFactory<Solution_> scoreDirectorFactory;

    protected BasicPlumbingTermination basicPlumbingTermination;
    // Note that the basicPlumbingTermination is a component of this termination
    protected Termination termination;
    protected BestSolutionRecaller<Solution_> bestSolutionRecaller;
    protected List<Phase> phaseList;

    protected AtomicBoolean solving = new AtomicBoolean(false);

    protected DefaultSolverScope<Solution_> solverScope = new DefaultSolverScope<>();

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

    @Override
    public InnerScoreDirectorFactory<Solution_> getScoreDirectorFactory() {
        return scoreDirectorFactory;
    }

    public void setScoreDirectorFactory(InnerScoreDirectorFactory<Solution_> scoreDirectorFactory) {
        this.scoreDirectorFactory = scoreDirectorFactory;
    }

    public void setBasicPlumbingTermination(BasicPlumbingTermination basicPlumbingTermination) {
        this.basicPlumbingTermination = basicPlumbingTermination;
    }

    public void setTermination(Termination termination) {
        this.termination = termination;
    }

    public BestSolutionRecaller<Solution_> getBestSolutionRecaller() {
        return bestSolutionRecaller;
    }

    public void setBestSolutionRecaller(BestSolutionRecaller<Solution_> bestSolutionRecaller) {
        this.bestSolutionRecaller = bestSolutionRecaller;
        this.bestSolutionRecaller.setSolverEventSupport(solverEventSupport);
    }

    public List<Phase> getPhaseList() {
        return phaseList;
    }

    public void setPhaseList(List<Phase> phaseList) {
        this.phaseList = phaseList;
        for (Phase<Solution_> phase : phaseList) {
            phase.setSolverPhaseLifecycleSupport(phaseLifecycleSupport);
        }
    }

    public DefaultSolverScope<Solution_> getSolverScope() {
        return solverScope;
    }

    // ************************************************************************
    // Complex getters
    // ************************************************************************

    @Override
    public Solution_ getBestSolution() {
        return solverScope.getBestSolution();
    }

    @Override
    public Score getBestScore() {
        return solverScope.getBestScore();
    }

    @Override
    public long getTimeMillisSpent() {
        Long endingSystemTimeMillis = solverScope.getEndingSystemTimeMillis();
        if (endingSystemTimeMillis == null) {
            endingSystemTimeMillis = System.currentTimeMillis();
        }
        return endingSystemTimeMillis - solverScope.getStartingSystemTimeMillis();
    }

    @Override
    public boolean isSolving() {
        return solving.get();
    }

    @Override
    public boolean terminateEarly() {
        return basicPlumbingTermination.terminateEarly();
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
    public boolean isEveryProblemFactChangeProcessed() {
        return basicPlumbingTermination.isEveryProblemFactChangeProcessed();
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
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
        return solverScope.getBestSolution();
    }

    public void outerSolvingStarted(DefaultSolverScope<Solution_> solverScope) {
        solving.set(true);
        basicPlumbingTermination.resetTerminateEarly();
        solverScope.setStartingSolverCount(0);
        solverScope.setWorkingRandom(randomFactory.createRandom());
        solverScope.setScoreDirector(scoreDirectorFactory.buildScoreDirector(constraintMatchEnabledPreference));
    }

    public void solvingStarted(DefaultSolverScope<Solution_> solverScope) {
        solverScope.startingNow();
        solverScope.getScoreDirector().resetCalculationCount();
        solverScope.setWorkingSolutionFromBestSolution();
        bestSolutionRecaller.solvingStarted(solverScope);
        phaseLifecycleSupport.fireSolvingStarted(solverScope);
        for (Phase<Solution_> phase : phaseList) {
            phase.solvingStarted(solverScope);
        }
        int startingSolverCount = solverScope.getStartingSolverCount() + 1;
        solverScope.setStartingSolverCount(startingSolverCount);
        logger.info("Solving {}: time spent ({}), best score ({}), environment mode ({}), random ({}).",
                (startingSolverCount == 1 ? "started" : "restarted"),
                solverScope.calculateTimeMillisSpentUpToNow(),
                solverScope.getBestScore(),
                environmentMode.name(),
                (randomFactory != null ? randomFactory : "not fixed"));
    }

    protected void runPhases() {
        Iterator<Phase> it = phaseList.iterator();
        while (!termination.isSolverTerminated(solverScope) && it.hasNext()) {
            Phase<Solution_> phase = it.next();
            phase.solve(solverScope);
            if (it.hasNext()) {
                solverScope.setWorkingSolutionFromBestSolution();
            }
        }
        // TODO support doing round-robin of phases (only non-construction heuristics)
    }

    public void solvingEnded(DefaultSolverScope<Solution_> solverScope) {
        for (Phase<Solution_> phase : phaseList) {
            phase.solvingEnded(solverScope);
        }
        phaseLifecycleSupport.fireSolvingEnded(solverScope);
        bestSolutionRecaller.solvingEnded(solverScope);
        solverScope.endingNow();
    }

    public void outerSolvingEnded(DefaultSolverScope<Solution_> solverScope) {
        // Must be kept open for doProblemFactChange
        solverScope.getScoreDirector().dispose();
        logger.info("Solving ended: time spent ({}), best score ({}), score calculation speed ({}/sec),"
                        + " environment mode ({}).",
                solverScope.getTimeMillisSpent(),
                solverScope.getBestScore(),
                solverScope.getScoreCalculationSpeed(),
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
            ProblemFactChange<Solution_> problemFactChange = problemFactChangeQueue.poll();
            while (problemFactChange != null) {
                score = doProblemFactChange(problemFactChange, stepIndex);
                stepIndex++;
                problemFactChange = problemFactChangeQueue.poll();
            }
            basicPlumbingTermination.endProblemFactChangesProcessing();
            Solution_ newBestSolution = solverScope.getScoreDirector().cloneWorkingSolution();
            bestSolutionRecaller.updateBestSolution(solverScope, newBestSolution);
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

    @Override
    public void addEventListener(SolverEventListener<Solution_> eventListener) {
        solverEventSupport.addEventListener(eventListener);
    }

    @Override
    public void removeEventListener(SolverEventListener<Solution_> eventListener) {
        solverEventSupport.removeEventListener(eventListener);
    }

    /**
     * Add a {@link PhaseLifecycleListener} that is notified
     * of {@link PhaseLifecycleListener#solvingStarted(DefaultSolverScope)} solving} events
     * and also of the {@link PhaseLifecycleListener#phaseStarted(AbstractPhaseScope) phase}
     * and the {@link PhaseLifecycleListener#stepStarted(AbstractStepScope)} step} starting/ending events of all phases.
     * <p>
     * To get notified for only 1 phase, use {@link Phase#addPhaseLifecycleListener(PhaseLifecycleListener)} instead.
     * @param phaseLifecycleListener never null
     */
    public void addPhaseLifecycleListener(PhaseLifecycleListener<Solution_> phaseLifecycleListener) {
        phaseLifecycleSupport.addEventListener(phaseLifecycleListener);
    }

    /**
     * @param phaseLifecycleListener never null
     * @see #addPhaseLifecycleListener(PhaseLifecycleListener)
     */
    public void removePhaseLifecycleListener(PhaseLifecycleListener<Solution_> phaseLifecycleListener) {
        phaseLifecycleSupport.removeEventListener(phaseLifecycleListener);
    }

}

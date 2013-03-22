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

package org.optaplanner.core.impl.solver;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.bestsolution.BestSolutionRecaller;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.event.SolverEventListener;
import org.optaplanner.core.impl.event.SolverEventSupport;
import org.optaplanner.core.impl.phase.SolverPhase;
import org.optaplanner.core.impl.phase.event.SolverPhaseLifecycleListener;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.termination.Termination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation for {@link Solver}.
 * @see Solver
 */
public class DefaultSolver implements Solver {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected SolverEventSupport solverEventSupport = new SolverEventSupport(this);

    protected ScoreDirectorFactory scoreDirectorFactory;
    protected Long randomSeed;

    protected BasicPlumbingTermination basicPlumbingTermination;
    // Note that the basicPlumbingTermination is a component of this termination
    protected Termination termination;
    protected BestSolutionRecaller bestSolutionRecaller;
    protected List<SolverPhase> solverPhaseList;

    protected AtomicBoolean solving = new AtomicBoolean(false);

    protected DefaultSolverScope solverScope = new DefaultSolverScope();
    
    public long getRandomSeed() {
        return this.randomSeed;
    }

    public void setRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
    }

    public ScoreDirectorFactory getScoreDirectorFactory() {
        return scoreDirectorFactory;
    }

    public void setScoreDirectorFactory(ScoreDirectorFactory scoreDirectorFactory) {
        this.scoreDirectorFactory = scoreDirectorFactory;
    }

    public void setBasicPlumbingTermination(BasicPlumbingTermination basicPlumbingTermination) {
        this.basicPlumbingTermination = basicPlumbingTermination;
    }

    public void setTermination(Termination termination) {
        this.termination = termination;
    }

    public void setBestSolutionRecaller(BestSolutionRecaller bestSolutionRecaller) {
        this.bestSolutionRecaller = bestSolutionRecaller;
        this.bestSolutionRecaller.setSolverEventSupport(solverEventSupport);
    }

    public List<SolverPhase> getSolverPhaseList() {
        return solverPhaseList;
    }

    public void setSolverPhaseList(List<SolverPhase> solverPhaseList) {
        this.solverPhaseList = solverPhaseList;
    }

    public void setPlanningProblem(Solution planningProblem) {
        solverScope.setBestSolution(planningProblem);
    }

    public Solution getBestSolution() {
        return solverScope.getBestSolution();
    }

    // TODO this shouldn't change after the solve is done
    public long getTimeMillisSpend() {
        return solverScope.calculateTimeMillisSpend();
    }

    public SolutionDescriptor getSolutionDescriptor() {
        return solverScope.getSolutionDescriptor();
    }

    public DefaultSolverScope getSolverScope() {
        return solverScope;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

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
        BlockingQueue<ProblemFactChange> problemFactChangeQueue
                = basicPlumbingTermination.getProblemFactChangeQueue();
        // TODO bug: the last ProblemFactChange might already been polled, but not processed yet
        return problemFactChangeQueue.isEmpty();
    }

    public final void solve() {
        solving.set(true);
        basicPlumbingTermination.resetTerminateEarly();
        solverScope.setRestartSolver(true);
        while (solverScope.isRestartSolver()) {
            solverScope.setRestartSolver(false);
            solvingStarted(solverScope);
            runSolverPhases();
            solvingEnded(solverScope);
            checkProblemFactChanges();
        }
        // Must be kept open for doProblemFactChange
        solverScope.getScoreDirector().dispose();
        solving.set(false);
    }

    public void solvingStarted(DefaultSolverScope solverScope) {
        if (solverScope.getBestSolution() == null) {
            throw new IllegalStateException("The planningProblem must not be null." +
                    " Use Solver.setPlanningProblem(Solution).");
        }
        solverScope.setStartingSystemTimeMillis(System.currentTimeMillis());
        solverScope.setScoreDirector(scoreDirectorFactory.buildScoreDirector());
        if (randomSeed != null) {
            solverScope.setWorkingRandom(new Random(randomSeed));
        } else {
            solverScope.setWorkingRandom(new Random());
        }
        solverScope.setWorkingSolutionFromBestSolution();
        bestSolutionRecaller.solvingStarted(solverScope);
        for (SolverPhase solverPhase : solverPhaseList) {
            solverPhase.solvingStarted(solverScope);
        }
        logger.info("Solving started: time spend ({}), score ({}), new best score ({}), random seed ({}).",
                solverScope.calculateTimeMillisSpend(), solverScope.getStartingInitializedScore(),
                solverScope.getBestScore(), (randomSeed != null ? randomSeed : "not fixed"));
    }

    protected void runSolverPhases() {
        Iterator<SolverPhase> it = solverPhaseList.iterator();
        while (!termination.isSolverTerminated(solverScope) && it.hasNext()) {
            SolverPhase solverPhase = it.next();
            solverPhase.solve(solverScope);
            if (it.hasNext()) {
                solverScope.setWorkingSolutionFromBestSolution();
            }
        }
        // TODO support doing round-robin of phases (only non-construction heuristics)
    }

    public void solvingEnded(DefaultSolverScope solverScope) {
        for (SolverPhase solverPhase : solverPhaseList) {
            solverPhase.solvingEnded(solverScope);
        }
        bestSolutionRecaller.solvingEnded(solverScope);
        long timeMillisSpend = solverScope.calculateTimeMillisSpend();
        if (timeMillisSpend == 0L) {
            // Avoid divide by zero exception on a fast CPU
            timeMillisSpend = 1L;
        }
        long averageCalculateCountPerSecond = solverScope.getCalculateCount() * 1000L / timeMillisSpend;
        logger.info("Solving ended: time spend ({}), best score ({}), average calculate count per second ({}).",
                timeMillisSpend,
                solverScope.getBestScoreWithUninitializedPrefix(),
                averageCalculateCountPerSecond);
    }

    private void checkProblemFactChanges() {
        BlockingQueue<ProblemFactChange> problemFactChangeQueue
                = basicPlumbingTermination.getProblemFactChangeQueue();
        if (!problemFactChangeQueue.isEmpty()) {
            solverScope.setRestartSolver(true);
            solverScope.setWorkingSolutionFromBestSolution();
            Score score = null;
            int count = 0;
            ProblemFactChange problemFactChange = problemFactChangeQueue.poll();
            while (problemFactChange != null) {
                score = doProblemFactChange(problemFactChange);
                count++;
                problemFactChange = problemFactChangeQueue.poll();
            }
            Solution newBestSolution = solverScope.getScoreDirector().cloneWorkingSolution();
            // TODO BestSolutionRecaller.solverStarted() already calls countUninitializedVariables()
            int newBestUninitializedVariableCount = solverScope.getSolutionDescriptor()
                    .countUninitializedVariables(newBestSolution);
            bestSolutionRecaller.updateBestSolution(solverScope,
                    newBestSolution, newBestUninitializedVariableCount);
            logger.info("Done {} ProblemFactChange(s): new score ({}) possibly uninitialized. Restarting solver.",
                    count, score);
        }
    }

    private Score doProblemFactChange(ProblemFactChange problemFactChange) {
        problemFactChange.doChange(solverScope.getScoreDirector());
        Score score = solverScope.calculateScore();
        logger.debug("    Done ProblemFactChange: new score ({}).", score);
        return score;
    }

    public void addEventListener(SolverEventListener eventListener) {
        solverEventSupport.addEventListener(eventListener);
    }

    public void removeEventListener(SolverEventListener eventListener) {
        solverEventSupport.removeEventListener(eventListener);
    }

    public void addSolverPhaseLifecycleListener(SolverPhaseLifecycleListener solverPhaseLifecycleListener) {
        for (SolverPhase solverPhase : solverPhaseList) {
            solverPhase.addSolverPhaseLifecycleListener(solverPhaseLifecycleListener);
        }
    }

    public void removeSolverPhaseLifecycleListener(SolverPhaseLifecycleListener solverPhaseLifecycleListener) {
        for (SolverPhase solverPhase : solverPhaseList) {
            solverPhase.addSolverPhaseLifecycleListener(solverPhaseLifecycleListener);
        }
    }

}

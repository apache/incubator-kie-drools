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

package org.drools.planner.core.solver;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.planner.core.Solver;
import org.drools.planner.core.bestsolution.BestSolutionRecaller;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.event.SolverEventListener;
import org.drools.planner.core.event.SolverEventSupport;
import org.drools.planner.core.phase.SolverPhase;
import org.drools.planner.core.phase.event.SolverPhaseLifecycleListener;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solution.director.DefaultSolutionDirector;
import org.drools.planner.core.termination.Termination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation for {@link Solver}.
 * @see Solver
 */
public class DefaultSolver implements Solver {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected SolverEventSupport solverEventSupport = new SolverEventSupport(this);

    protected Long randomSeed;

    protected BasicPlumbingTermination basicPlumbingTermination;
    // Note that the basicPlumbingTermination is a component of this termination
    protected Termination termination;
    protected BestSolutionRecaller bestSolutionRecaller;
    protected List<SolverPhase> solverPhaseList;

    protected AtomicBoolean solving = new AtomicBoolean(false);

    protected DefaultSolverScope solverScope = new DefaultSolverScope();

    public void setRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
    }

    public void setSolutionDirector(DefaultSolutionDirector solutionDirector) {
        solverScope.setSolutionDirector(solutionDirector);
    }

    public ScoreDefinition getScoreDefinition() {
        return solverScope.getScoreDefinition();
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
        // TODO Should this be a clone or not? Currently not, but that changes what the user gave us.
        solverScope.getSolutionDirector().setWorkingSolution(planningProblem);
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
        solving.set(false);
    }

    public void solvingStarted(DefaultSolverScope solverScope) {
        if (solverScope.getSolutionDirector().getWorkingSolution() == null) {
            throw new IllegalStateException("The planningProblem must not be null." +
                    " Use Solver.setPlanningProblem(Solution).");
        }
        solverScope.reset();
        if (randomSeed != null) {
            solverScope.setWorkingRandom(new Random(randomSeed));
        } else {
            solverScope.setWorkingRandom(new Random());
        }
        bestSolutionRecaller.solvingStarted(solverScope);
        logger.info("Solver started: time spend ({}), score ({}), new best score ({}), random seed ({}).",
                new Object[]{solverScope.calculateTimeMillisSpend(), solverScope.getStartingInitializedScore(),
                        solverScope.getBestScore(), (randomSeed != null ? randomSeed : "not fixed")});
    }

    protected void runSolverPhases() {
        Iterator<SolverPhase> it = solverPhaseList.iterator();
        while (!termination.isSolverTerminated(solverScope) && it.hasNext()) {
            SolverPhase solverPhase = it.next();
            solverPhase.solve(solverScope);
        }
        // TODO support doing round-robin of phases (only non-construction heuristics)
    }

    public void solvingEnded(DefaultSolverScope solverScope) {
        bestSolutionRecaller.solvingEnded(solverScope);;
        long timeMillisSpend = solverScope.calculateTimeMillisSpend();
        if (timeMillisSpend == 0L) {
            // Avoid divide by zero exception on a fast CPU
            timeMillisSpend = 1L;
        }
        long averageCalculateCountPerSecond = solverScope.getCalculateCount() * 1000L / timeMillisSpend;
        logger.info("Solved: time spend ({}), best score ({}), average calculate count per second ({}).",
                new Object[]{timeMillisSpend,
                solverScope.getBestScore(),
                averageCalculateCountPerSecond});
    }

    private void checkProblemFactChanges() {
        BlockingQueue<ProblemFactChange> problemFactChangeQueue
                = basicPlumbingTermination.getProblemFactChangeQueue();
        if (!problemFactChangeQueue.isEmpty()) {
            solverScope.setRestartSolver(true);
            Score score = null;
            int count = 0;
            ProblemFactChange problemFactChange = problemFactChangeQueue.poll();
            while (problemFactChange != null) {
                score = doProblemFactChange(problemFactChange);
                count++;
                problemFactChange = problemFactChangeQueue.poll();
            }
            logger.info("Done {} ProblemFactChange(s): new score ({}) possibly uninitialized. Restarting solver.",
                    count, score);
        }
    }

    private Score doProblemFactChange(ProblemFactChange problemFactChange) {
        problemFactChange.doChange(solverScope.getSolutionDirector());
        Score score = solverScope.calculateScoreFromWorkingMemory();
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

/*
 * Copyright 2010 JBoss Inc
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

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.RuleBase;
import org.drools.planner.core.Solver;
import org.drools.planner.core.bestsolution.BestSolutionRecaller;
import org.drools.planner.core.domain.meta.SolutionDescriptor;
import org.drools.planner.core.event.SolverEventListener;
import org.drools.planner.core.event.SolverEventSupport;
import org.drools.planner.core.localsearch.DefaultLocalSearchSolver;
import org.drools.planner.core.score.calculator.ScoreCalculator;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solution.initializer.StartingSolutionInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for {@link Solver}.
 * @see Solver
 * @see DefaultLocalSearchSolver
 */
public abstract class AbstractSolver implements Solver {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected SolverEventSupport solverEventSupport = new SolverEventSupport(this);

    // TODO atomic enum with values NOT_STARTED, RUNNING, DONE, CANCELLED
    // TODO introduce a solver factory and make a solver one time use
    protected final AtomicBoolean terminatedEarly = new AtomicBoolean(false);

    protected Long randomSeed;

    protected StartingSolutionInitializer startingSolutionInitializer = null;
    protected BestSolutionRecaller bestSolutionRecaller;

    public void setRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
    }

    public void setSolutionDescriptor(SolutionDescriptor solutionDescriptor) {
        getAbstractSolverScope().setSolutionDescriptor(solutionDescriptor);
    }

    public void setRuleBase(RuleBase ruleBase) {
        getAbstractSolverScope().setRuleBase(ruleBase);
    }

    public ScoreDefinition getScoreDefinition() {
        return getAbstractSolverScope().getScoreDefinition();
    }

    public void setScoreDefinition(ScoreDefinition scoreDefinition) {
        getAbstractSolverScope().setScoreDefinition(scoreDefinition);
    }

    public void setScoreCalculator(ScoreCalculator scoreCalculator) {
        getAbstractSolverScope().setWorkingScoreCalculator(scoreCalculator);
    }

    public StartingSolutionInitializer getStartingSolutionInitializer() {
        return startingSolutionInitializer;
    }

    public void setStartingSolutionInitializer(StartingSolutionInitializer startingSolutionInitializer) {
        this.startingSolutionInitializer = startingSolutionInitializer;
    }

    public void setBestSolutionRecaller(BestSolutionRecaller bestSolutionRecaller) {
        this.bestSolutionRecaller = bestSolutionRecaller;
        this.bestSolutionRecaller.setSolverEventSupport(solverEventSupport);
    }

    public void setStartingSolution(Solution startingSolution) {
        getAbstractSolverScope().setWorkingSolution(startingSolution);
    }

    public Solution getBestSolution() {
        return getAbstractSolverScope().getBestSolution();
    }

    public long getTimeMillisSpend() {
        return getAbstractSolverScope().calculateTimeMillisSpend();
    }

    public abstract AbstractSolverScope getAbstractSolverScope();

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean terminateEarly() {
        boolean terminationEarlySuccessful = !terminatedEarly.getAndSet(true);
        if (terminationEarlySuccessful) {
            logger.info("Terminating solver early.");
        }
        return terminationEarlySuccessful;
    }

    public boolean isTerminatedEarly() {
        return terminatedEarly.get();
    }

    public final void solve() {
        terminatedEarly.set(false);
        solveImplementation();
    }

    protected abstract void solveImplementation();

    public void solvingStarted(AbstractSolverScope abstractSolverScope) {
        if (abstractSolverScope.getWorkingSolution() == null) {
            throw new IllegalStateException("The startingSolution must not be null." +
                    " Use Solver.setStartingSolution(Solution).");
        }
        abstractSolverScope.reset();
        if (randomSeed != null) {
            logger.info("Solving with random seed ({}).", randomSeed);
            abstractSolverScope.setWorkingRandom(new Random(randomSeed));
        } else {
            logger.info("Solving with a non-fixed random seed.");
            abstractSolverScope.setWorkingRandom(new Random());
        }
        if (startingSolutionInitializer != null) {
            if (!startingSolutionInitializer.isSolutionInitialized(abstractSolverScope)) {
                logger.info("Initializing solution.");
                startingSolutionInitializer.initializeSolution(abstractSolverScope);
            } else {
                logger.debug("Solution is already initialized.");
            }
        }
        bestSolutionRecaller.solvingStarted(abstractSolverScope);
        logger.info("Starting with time spend ({}), score ({}), new best score ({}).",
                new Object[]{abstractSolverScope.calculateTimeMillisSpend(), abstractSolverScope.getStartingScore(),
                        abstractSolverScope.getBestScore()});
    }

    public void addEventListener(SolverEventListener eventListener) {
        solverEventSupport.addEventListener(eventListener);
    }

    public void removeEventListener(SolverEventListener eventListener) {
        solverEventSupport.removeEventListener(eventListener);
    }

}

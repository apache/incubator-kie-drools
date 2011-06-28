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
import org.drools.planner.core.localsearch.DefaultLocalSearchSolverPhase;
import org.drools.planner.core.localsearch.event.LocalSearchSolverPhaseLifecycleListener;
import org.drools.planner.core.score.calculator.ScoreCalculator;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solution.initializer.StartingSolutionInitializer;
import org.drools.planner.core.solver.event.SolverPhaseLifecycleListener;
import org.drools.planner.core.solver.event.SolverPhaseLifecycleSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see DefaultLocalSearchSolverPhase
 */
public abstract class AbstractSolverPhase implements SolverPhase, SolverPhaseLifecycleListener {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected AtomicBoolean terminatedEarlyHolder;

    protected BestSolutionRecaller bestSolutionRecaller;

    protected SolverPhaseLifecycleSupport solverPhaseLifecycleSupport = new SolverPhaseLifecycleSupport();

    public void setTerminatedEarlyHolder(AtomicBoolean terminatedEarlyHolder) {
        this.terminatedEarlyHolder = terminatedEarlyHolder;
    }

    public void setBestSolutionRecaller(BestSolutionRecaller bestSolutionRecaller) {
        this.bestSolutionRecaller = bestSolutionRecaller;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void phaseStarted(AbstractSolverPhaseScope solverPhaseScope) {
        solverPhaseScope.setStartingSystemTimeMillis(System.currentTimeMillis());
        solverPhaseScope.setBestSolutionStepIndex(-1);
        solverPhaseScope.setStartingScore(solverPhaseScope.getBestScore());
        solverPhaseLifecycleSupport.firePhaseStarted(solverPhaseScope);
    }

    public void beforeDeciding(AbstractStepScope stepScope) {
        solverPhaseLifecycleSupport.fireBeforeDeciding(stepScope);
    }

    public void stepDecided(AbstractStepScope stepScope) {
        solverPhaseLifecycleSupport.fireStepDecided(stepScope);
    }

    public void stepTaken(AbstractStepScope stepScope) {
        bestSolutionRecaller.extractBestSolution(stepScope);
        solverPhaseLifecycleSupport.fireStepTaken(stepScope);
    }

    public void phaseEnded(AbstractSolverPhaseScope solverPhaseScope) {
        solverPhaseLifecycleSupport.firePhaseEnded(solverPhaseScope);
    }

    public void addSolverPhaseLifecycleListener(SolverPhaseLifecycleListener lifecycleListener) {
        solverPhaseLifecycleSupport.addEventListener(lifecycleListener);
    }

    public void removeSolverPhaseLifecycleListener(SolverPhaseLifecycleListener lifecycleListener) {
        solverPhaseLifecycleSupport.removeEventListener(lifecycleListener);
    }

}

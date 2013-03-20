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

package org.optaplanner.core.impl.phase;

import org.optaplanner.core.impl.bestsolution.BestSolutionRecaller;
import org.optaplanner.core.impl.localsearch.DefaultLocalSearchSolverPhase;
import org.optaplanner.core.impl.phase.event.SolverPhaseLifecycleListener;
import org.optaplanner.core.impl.phase.event.SolverPhaseLifecycleSupport;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.termination.Termination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see DefaultLocalSearchSolverPhase
 */
public abstract class AbstractSolverPhase implements SolverPhase {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected int phaseIndex = -1;

    protected Termination termination;
    protected BestSolutionRecaller bestSolutionRecaller;

    protected SolverPhaseLifecycleSupport solverPhaseLifecycleSupport = new SolverPhaseLifecycleSupport();

    public Termination getTermination() {
        return termination;
    }

    public int getPhaseIndex() {
        return phaseIndex;
    }

    public void setPhaseIndex(int phaseIndex) {
        this.phaseIndex = phaseIndex;
    }

    public void setTermination(Termination termination) {
        this.termination = termination;
    }

    public void setBestSolutionRecaller(BestSolutionRecaller bestSolutionRecaller) {
        this.bestSolutionRecaller = bestSolutionRecaller;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void solvingStarted(DefaultSolverScope solverScope) {
        // bestSolutionRecaller.solvingStarted(...) is called by DefaultSolver
        termination.solvingStarted(solverScope);
        solverPhaseLifecycleSupport.fireSolvingStarted(solverScope);
    }

    public void solvingEnded(DefaultSolverScope solverScope) {
        // bestSolutionRecaller.solvingStarted(...) is called by DefaultSolver
        termination.solvingEnded(solverScope);
        solverPhaseLifecycleSupport.fireSolvingStarted(solverScope);
    }

    public void phaseStarted(AbstractSolverPhaseScope phaseScope) {
        phaseScope.reset();
        bestSolutionRecaller.phaseStarted(phaseScope);
        termination.phaseStarted(phaseScope);
        solverPhaseLifecycleSupport.firePhaseStarted(phaseScope);
    }

    public void stepStarted(AbstractStepScope stepScope) {
        bestSolutionRecaller.stepStarted(stepScope);
        termination.stepStarted(stepScope);
        solverPhaseLifecycleSupport.fireStepStarted(stepScope);
    }

    public void stepEnded(AbstractStepScope stepScope) {
        bestSolutionRecaller.stepEnded(stepScope);
        termination.stepEnded(stepScope);
        solverPhaseLifecycleSupport.fireStepEnded(stepScope);
    }

    public void phaseEnded(AbstractSolverPhaseScope phaseScope) {
        bestSolutionRecaller.phaseEnded(phaseScope);
        termination.phaseEnded(phaseScope);
        solverPhaseLifecycleSupport.firePhaseEnded(phaseScope);
    }

    public void addSolverPhaseLifecycleListener(SolverPhaseLifecycleListener lifecycleListener) {
        solverPhaseLifecycleSupport.addEventListener(lifecycleListener);
    }

    public void removeSolverPhaseLifecycleListener(SolverPhaseLifecycleListener lifecycleListener) {
        solverPhaseLifecycleSupport.removeEventListener(lifecycleListener);
    }

}

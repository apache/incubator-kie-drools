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

import org.optaplanner.core.impl.localsearch.DefaultLocalSearchPhase;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleSupport;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see DefaultLocalSearchPhase
 */
public abstract class AbstractPhase implements Phase {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected int phaseIndex = -1;

    protected Termination termination;
    protected BestSolutionRecaller bestSolutionRecaller;

    protected PhaseLifecycleSupport phaseLifecycleSupport = new PhaseLifecycleSupport();

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
        phaseLifecycleSupport.fireSolvingStarted(solverScope);
    }

    public void solvingEnded(DefaultSolverScope solverScope) {
        // bestSolutionRecaller.solvingEnded(...) is called by DefaultSolver
        termination.solvingEnded(solverScope);
        phaseLifecycleSupport.fireSolvingEnded(solverScope);
    }

    public void phaseStarted(AbstractPhaseScope phaseScope) {
        phaseScope.reset();
        bestSolutionRecaller.phaseStarted(phaseScope);
        termination.phaseStarted(phaseScope);
        phaseLifecycleSupport.firePhaseStarted(phaseScope);
    }

    public void stepStarted(AbstractStepScope stepScope) {
        bestSolutionRecaller.stepStarted(stepScope);
        termination.stepStarted(stepScope);
        phaseLifecycleSupport.fireStepStarted(stepScope);
    }

    public void stepEnded(AbstractStepScope stepScope) {
        bestSolutionRecaller.stepEnded(stepScope);
        termination.stepEnded(stepScope);
        phaseLifecycleSupport.fireStepEnded(stepScope);
    }

    public void phaseEnded(AbstractPhaseScope phaseScope) {
        bestSolutionRecaller.phaseEnded(phaseScope);
        termination.phaseEnded(phaseScope);
        phaseLifecycleSupport.firePhaseEnded(phaseScope);
    }

    public void addPhaseLifecycleListener(PhaseLifecycleListener phaseLifecycleListener) {
        phaseLifecycleSupport.addEventListener(phaseLifecycleListener);
    }

    public void removePhaseLifecycleListener(PhaseLifecycleListener phaseLifecycleListener) {
        phaseLifecycleSupport.removeEventListener(phaseLifecycleListener);
    }

}

/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.impl.phase.Phase;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleSupport;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.event.SolverEventSupport;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see Solver
 * @see DefaultSolver
 */
public abstract class AbstractSolver<Solution_> implements Solver<Solution_> {

    protected final SolverEventSupport<Solution_> solverEventSupport = new SolverEventSupport<>(this);
    protected final PhaseLifecycleSupport<Solution_> phaseLifecycleSupport = new PhaseLifecycleSupport<>();

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************


    // ************************************************************************
    // Event listeners
    // ************************************************************************

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

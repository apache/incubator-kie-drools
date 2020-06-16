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

package org.optaplanner.core.impl.phase;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleSupport;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * A phase of a {@link Solver}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see AbstractPhase
 */
public interface Phase<Solution_> extends PhaseLifecycleListener<Solution_> {

    /**
     * Used to make {@link DefaultSolver#addPhaseLifecycleListener(PhaseLifecycleListener)} work.
     *
     * @param solverPhaseLifecycleSupport never null
     */
    void setSolverPhaseLifecycleSupport(PhaseLifecycleSupport<Solution_> solverPhaseLifecycleSupport);

    /**
     * Add a {@link PhaseLifecycleListener} that is only notified
     * of the {@link PhaseLifecycleListener#phaseStarted(AbstractPhaseScope) phase}
     * and the {@link PhaseLifecycleListener#stepStarted(AbstractStepScope)} step} starting/ending events from this phase
     * (and the {@link PhaseLifecycleListener#solvingStarted(SolverScope)} solving} events too of course).
     * <p>
     * To get notified for all phases, use {@link DefaultSolver#addPhaseLifecycleListener(PhaseLifecycleListener)} instead.
     *
     * @param phaseLifecycleListener never null
     */
    void addPhaseLifecycleListener(PhaseLifecycleListener<Solution_> phaseLifecycleListener);

    /**
     * @param phaseLifecycleListener never null
     * @see #addPhaseLifecycleListener(PhaseLifecycleListener)
     */
    void removePhaseLifecycleListener(PhaseLifecycleListener<Solution_> phaseLifecycleListener);

    void solve(SolverScope<Solution_> solverScope);

}

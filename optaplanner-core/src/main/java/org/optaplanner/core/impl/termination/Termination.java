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

package org.optaplanner.core.impl.termination;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.localsearch.decider.acceptor.simulatedannealing.SimulatedAnnealingAcceptor;
import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.SolverPhase;
import org.optaplanner.core.impl.phase.event.SolverPhaseLifecycleListener;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * A Termination determines when a {@link Solver} or a {@link SolverPhase} should stop.
 */
public interface Termination extends SolverPhaseLifecycleListener {

    /**
     * Called by the {@link Solver} after every phase to determine if the search should stop.
     * @param solverScope never null
     * @return true if the search should terminate.
     */
    boolean isSolverTerminated(DefaultSolverScope solverScope);

    /**
     * Called by the {@link SolverPhase} after every step and every move to determine if the search should stop.
     * @param phaseScope never null
     * @return true if the search should terminate.
     */
    boolean isPhaseTerminated(AbstractSolverPhaseScope phaseScope);

    /**
     * A timeGradient is a relative estimate of how long the search will continue.
     * </p>
     * Clients that use a timeGradient should cache it at the start of a single step
     * because some implementations are not time-stable.
     * </p>
     * If a timeGradient can not be calculated, it should return -1.0.
     * Several implementations (such a {@link SimulatedAnnealingAcceptor}) require a correctly implemented timeGradient.
     * <p/>
     * A Termination's timeGradient can be requested after they are terminated, so implementations
     * should be careful not to return a timeGradient above 1.0.
     * @param solverScope never null
     * @return timeGradient t for which 0.0 &lt;= t &lt;= 1.0 or -1.0 when it is not supported.
     *         At the start of a solver t is 0.0 and at the end t would be 1.0.
     */
    double calculateSolverTimeGradient(DefaultSolverScope solverScope);

    /**
     * See {@link #calculateSolverTimeGradient(DefaultSolverScope)}.
     * @param phaseScope never null
     * @return timeGradient t for which 0.0 &lt;= t &lt;= 1.0 or -1.0 when it is not supported.
     *         At the start of a phase t is 0.0 and at the end t would be 1.0.
     */
    double calculatePhaseTimeGradient(AbstractSolverPhaseScope phaseScope);

}

/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.solver.termination;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.localsearch.decider.acceptor.simulatedannealing.SimulatedAnnealingAcceptor;
import org.optaplanner.core.impl.phase.Phase;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * A Termination determines when a {@link Solver} or a {@link Phase} should stop.
 * <p>
 * An implementation must extend {@link AbstractTermination} to ensure backwards compatibility in future versions.
 * @see AbstractTermination
 */
public interface Termination extends PhaseLifecycleListener {

    /**
     * Called by the {@link Solver} after every phase to determine if the search should stop.
     * @param solverScope never null
     * @return true if the search should terminate.
     */
    boolean isSolverTerminated(DefaultSolverScope solverScope);

    /**
     * Called by the {@link Phase} after every step and every move to determine if the search should stop.
     * @param phaseScope never null
     * @return true if the search should terminate.
     */
    boolean isPhaseTerminated(AbstractPhaseScope phaseScope);

    /**
     * A timeGradient is a relative estimate of how long the search will continue.
     * <p>
     * Clients that use a timeGradient should cache it at the start of a single step
     * because some implementations are not time-stable.
     * <p>
     * If a timeGradient can not be calculated, it should return -1.0.
     * Several implementations (such a {@link SimulatedAnnealingAcceptor}) require a correctly implemented timeGradient.
     * <p>
     * A Termination's timeGradient can be requested after they are terminated, so implementations
     * should be careful not to return a timeGradient above 1.0.
     * @param solverScope never null
     * @return timeGradient t for which {@code 0.0 <= t <= 1.0 or -1.0} when it is not supported.
     *         At the start of a solver t is 0.0 and at the end t would be 1.0.
     */
    double calculateSolverTimeGradient(DefaultSolverScope solverScope);

    /**
     * See {@link #calculateSolverTimeGradient(DefaultSolverScope)}.
     * @param phaseScope never null
     * @return timeGradient t for which {@code 0.0 <= t <= 1.0 or -1.0} when it is not supported.
     *         At the start of a phase t is 0.0 and at the end t would be 1.0.
     */
    double calculatePhaseTimeGradient(AbstractPhaseScope phaseScope);

    /**
     * Create a {@link Termination} for a child {@link Thread} of the {@link Solver}.
     * @param solverScope never null
     * @param childThreadType never null
     * @return not null
     * @throws UnsupportedOperationException if not supported by this termination
     */
    Termination createChildThreadTermination(DefaultSolverScope solverScope, ChildThreadType childThreadType);

}

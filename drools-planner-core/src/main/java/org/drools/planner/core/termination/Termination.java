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

package org.drools.planner.core.termination;

import org.drools.planner.core.Solver;
import org.drools.planner.core.localsearch.decider.acceptor.simulatedannealing.SimulatedAnnealingAcceptor;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.SolverPhase;
import org.drools.planner.core.phase.event.SolverPhaseLifecycleListener;
import org.drools.planner.core.phase.step.AbstractStepScope;
import org.drools.planner.core.solver.event.SolverLifecycleListener;

/**
 * A Termination determines when a {@link Solver} or a {@link SolverPhase} should stop.
 */
public interface Termination extends SolverLifecycleListener, SolverPhaseLifecycleListener {

    /**
     * Called by the {@link Solver} after every phase to determine if the search should stop.
     * @param lastSolverPhaseScope never null
     * @return true if the search should terminate.
     */
    boolean isSolverTerminated(AbstractSolverPhaseScope lastSolverPhaseScope);

    /**
     * Called by the {@link SolverPhase} after every step to determine if the search should stop.
     * @param stepScope never null
     * @return true if the search should terminate.
     */
    boolean isPhaseTerminated(AbstractStepScope stepScope);

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
     * @param lastSolverPhaseScope never null
     * @return timeGradient t for which 0.0 &lt;= t &lt;= 1.0 or -1.0 when it is not supported.
     *         At the start of a solver t is 0.0 and at the end t would be 1.0.
     */
    double calculateSolverTimeGradient(AbstractSolverPhaseScope lastSolverPhaseScope);

    /**
     * See {@link #calculateSolverTimeGradient(AbstractSolverPhaseScope)}.
     * @param stepScope never null
     * @return timeGradient t for which 0.0 &lt;= t &lt;= 1.0 or -1.0 when it is not supported.
     *         At the start of a phase t is 0.0 and at the end t would be 1.0.
     */
    double calculatePhaseTimeGradient(AbstractStepScope stepScope);

}

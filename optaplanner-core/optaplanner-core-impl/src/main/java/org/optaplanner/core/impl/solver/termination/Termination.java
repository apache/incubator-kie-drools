package org.optaplanner.core.impl.solver.termination;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.localsearch.decider.acceptor.simulatedannealing.SimulatedAnnealingAcceptor;
import org.optaplanner.core.impl.phase.Phase;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;

/**
 * A Termination determines when a {@link Solver} or a {@link Phase} should stop.
 * <p>
 * An implementation must extend {@link AbstractTermination} to ensure backwards compatibility in future versions.
 *
 * @see AbstractTermination
 */
public interface Termination<Solution_> extends PhaseLifecycleListener<Solution_> {

    /**
     * Called by the {@link Solver} after every phase to determine if the search should stop.
     *
     * @param solverScope never null
     * @return true if the search should terminate.
     */
    boolean isSolverTerminated(SolverScope<Solution_> solverScope);

    /**
     * Called by the {@link Phase} after every step and every move to determine if the search should stop.
     *
     * @param phaseScope never null
     * @return true if the search should terminate.
     */
    boolean isPhaseTerminated(AbstractPhaseScope<Solution_> phaseScope);

    /**
     * A timeGradient is a relative estimate of how long the search will continue.
     * <p>
     * Clients that use a timeGradient should cache it at the start of a single step
     * because some implementations are not time-stable.
     * <p>
     * If a timeGradient cannot be calculated, it should return -1.0.
     * Several implementations (such a {@link SimulatedAnnealingAcceptor}) require a correctly implemented timeGradient.
     * <p>
     * A Termination's timeGradient can be requested after they are terminated, so implementations
     * should be careful not to return a timeGradient above 1.0.
     *
     * @param solverScope never null
     * @return timeGradient t for which {@code 0.0 <= t <= 1.0 or -1.0} when it is not supported.
     *         At the start of a solver t is 0.0 and at the end t would be 1.0.
     */
    double calculateSolverTimeGradient(SolverScope<Solution_> solverScope);

    /**
     * See {@link #calculateSolverTimeGradient(SolverScope)}.
     *
     * @param phaseScope never null
     * @return timeGradient t for which {@code 0.0 <= t <= 1.0 or -1.0} when it is not supported.
     *         At the start of a phase t is 0.0 and at the end t would be 1.0.
     */
    double calculatePhaseTimeGradient(AbstractPhaseScope<Solution_> phaseScope);

    /**
     * Create a {@link Termination} for a child {@link Thread} of the {@link Solver}.
     *
     * @param solverScope never null
     * @param childThreadType never null
     * @return not null
     * @throws UnsupportedOperationException if not supported by this termination
     */
    Termination<Solution_> createChildThreadTermination(SolverScope<Solution_> solverScope, ChildThreadType childThreadType);

}

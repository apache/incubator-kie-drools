package org.optaplanner.core.api.solver;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.config.solver.SolverManagerConfig;

/**
 * The status of {@link PlanningSolution problem} submitted to the {@link SolverManager}.
 * Retrieve this status with {@link SolverManager#getSolverStatus(Object)} or {@link SolverJob#getSolverStatus()}.
 */
public enum SolverStatus {
    /**
     * No solver thread started solving this problem yet, but sooner or later a solver thread will solve it.
     * <p>
     * For example, submitting 7 problems to a {@link SolverManager}
     * with a {@link SolverManagerConfig#getParallelSolverCount()} of 4,
     * puts 3 into this state for non-trivial amount of time.
     * <p>
     * Transitions into {@link #SOLVING_ACTIVE} (or {@link #NOT_SOLVING} if it is
     * {@link SolverManager#terminateEarly(Object) terminated early}, before it starts).
     */
    SOLVING_SCHEDULED,
    /**
     * A solver thread started solving the problem, but hasn't finished yet.
     * <p>
     * If CPU resource are scarce and that solver thread is waiting for CPU time,
     * the state doesn't change, it's still considered solving active.
     * <p>
     * Transitions into {@link #NOT_SOLVING} when terminated.
     */
    SOLVING_ACTIVE,
    /**
     * The problem's solving has terminated or the problem was never submitted to the {@link SolverManager}.
     * {@link SolverManager#getSolverStatus(Object)} cannot tell the difference,
     * but {@link SolverJob#getSolverStatus()} can.
     */
    NOT_SOLVING;
}

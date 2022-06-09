package org.optaplanner.core.impl.phase;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
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
     * Add a {@link PhaseLifecycleListener} that is only notified
     * of the {@link PhaseLifecycleListener#phaseStarted(AbstractPhaseScope) phase}
     * and the {@link PhaseLifecycleListener#stepStarted(AbstractStepScope) step} starting/ending events from this phase
     * (and the {@link PhaseLifecycleListener#solvingStarted(SolverScope) solving} events too of course).
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

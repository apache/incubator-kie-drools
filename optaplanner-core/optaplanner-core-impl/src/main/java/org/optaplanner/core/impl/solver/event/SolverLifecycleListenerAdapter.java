package org.optaplanner.core.impl.solver.event;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * An adapter for {@link SolverLifecycleListener}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class SolverLifecycleListenerAdapter<Solution_> implements SolverLifecycleListener<Solution_> {

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        // Hook method
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        // Hook method
    }

}

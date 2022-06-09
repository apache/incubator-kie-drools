package org.optaplanner.core.impl.localsearch.event;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.event.SolverLifecycleListenerAdapter;

/**
 * An adapter for {@link LocalSearchPhaseLifecycleListener}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class LocalSearchPhaseLifecycleListenerAdapter<Solution_> extends SolverLifecycleListenerAdapter<Solution_>
        implements LocalSearchPhaseLifecycleListener<Solution_> {

    @Override
    public void phaseStarted(LocalSearchPhaseScope<Solution_> phaseScope) {
        // Hook method
    }

    @Override
    public void stepStarted(LocalSearchStepScope<Solution_> stepScope) {
        // Hook method
    }

    @Override
    public void stepEnded(LocalSearchStepScope<Solution_> stepScope) {
        // Hook method
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope<Solution_> phaseScope) {
        // Hook method
    }

}

package org.optaplanner.core.impl.phase.event;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.event.SolverLifecycleListenerAdapter;

/**
 * An adapter for {@link PhaseLifecycleListener}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class PhaseLifecycleListenerAdapter<Solution_> extends SolverLifecycleListenerAdapter<Solution_>
        implements PhaseLifecycleListener<Solution_> {

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        // Hook method
    }

    @Override
    public void stepStarted(AbstractStepScope<Solution_> stepScope) {
        // Hook method
    }

    @Override
    public void stepEnded(AbstractStepScope<Solution_> stepScope) {
        // Hook method
    }

    @Override
    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        // Hook method
    }

}

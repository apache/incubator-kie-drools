package org.optaplanner.core.impl.phase.event;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.event.SolverLifecycleListener;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see PhaseLifecycleListenerAdapter
 */
public interface PhaseLifecycleListener<Solution_> extends SolverLifecycleListener<Solution_> {

    void phaseStarted(AbstractPhaseScope<Solution_> phaseScope);

    void stepStarted(AbstractStepScope<Solution_> stepScope);

    void stepEnded(AbstractStepScope<Solution_> stepScope);

    void phaseEnded(AbstractPhaseScope<Solution_> phaseScope);

}

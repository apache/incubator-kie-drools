package org.optaplanner.core.impl.localsearch.event;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.event.SolverLifecycleListener;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see LocalSearchPhaseLifecycleListenerAdapter
 */
public interface LocalSearchPhaseLifecycleListener<Solution_> extends SolverLifecycleListener<Solution_> {

    void phaseStarted(LocalSearchPhaseScope<Solution_> phaseScope);

    void stepStarted(LocalSearchStepScope<Solution_> stepScope);

    void stepEnded(LocalSearchStepScope<Solution_> stepScope);

    void phaseEnded(LocalSearchPhaseScope<Solution_> phaseScope);

}

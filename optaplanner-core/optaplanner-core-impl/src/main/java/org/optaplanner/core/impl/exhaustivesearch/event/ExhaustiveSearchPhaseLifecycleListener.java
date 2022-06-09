package org.optaplanner.core.impl.exhaustivesearch.event;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.exhaustivesearch.scope.ExhaustiveSearchPhaseScope;
import org.optaplanner.core.impl.exhaustivesearch.scope.ExhaustiveSearchStepScope;
import org.optaplanner.core.impl.solver.event.SolverLifecycleListener;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public interface ExhaustiveSearchPhaseLifecycleListener<Solution_> extends SolverLifecycleListener<Solution_> {

    void phaseStarted(ExhaustiveSearchPhaseScope<Solution_> phaseScope);

    void stepStarted(ExhaustiveSearchStepScope<Solution_> stepScope);

    void stepEnded(ExhaustiveSearchStepScope<Solution_> stepScope);

    void phaseEnded(ExhaustiveSearchPhaseScope<Solution_> phaseScope);

}

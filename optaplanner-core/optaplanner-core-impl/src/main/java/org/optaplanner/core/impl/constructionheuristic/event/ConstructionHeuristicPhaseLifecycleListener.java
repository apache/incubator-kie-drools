package org.optaplanner.core.impl.constructionheuristic.event;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicPhaseScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicStepScope;
import org.optaplanner.core.impl.solver.event.SolverLifecycleListener;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see ConstructionHeuristicPhaseLifecycleListenerAdapter
 */
public interface ConstructionHeuristicPhaseLifecycleListener<Solution_> extends SolverLifecycleListener<Solution_> {

    void phaseStarted(ConstructionHeuristicPhaseScope<Solution_> phaseScope);

    void stepStarted(ConstructionHeuristicStepScope<Solution_> stepScope);

    void stepEnded(ConstructionHeuristicStepScope<Solution_> stepScope);

    void phaseEnded(ConstructionHeuristicPhaseScope<Solution_> phaseScope);

}

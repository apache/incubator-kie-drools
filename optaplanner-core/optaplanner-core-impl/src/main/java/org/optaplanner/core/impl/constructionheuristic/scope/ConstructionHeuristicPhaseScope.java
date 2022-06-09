package org.optaplanner.core.impl.constructionheuristic.scope;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class ConstructionHeuristicPhaseScope<Solution_> extends AbstractPhaseScope<Solution_> {

    private ConstructionHeuristicStepScope<Solution_> lastCompletedStepScope;

    public ConstructionHeuristicPhaseScope(SolverScope<Solution_> solverScope) {
        super(solverScope);
        lastCompletedStepScope = new ConstructionHeuristicStepScope<>(this, -1);
    }

    @Override
    public ConstructionHeuristicStepScope<Solution_> getLastCompletedStepScope() {
        return lastCompletedStepScope;
    }

    public void setLastCompletedStepScope(ConstructionHeuristicStepScope<Solution_> lastCompletedStepScope) {
        this.lastCompletedStepScope = lastCompletedStepScope;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

}

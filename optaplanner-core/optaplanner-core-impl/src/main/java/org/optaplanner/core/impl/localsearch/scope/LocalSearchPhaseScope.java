package org.optaplanner.core.impl.localsearch.scope;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class LocalSearchPhaseScope<Solution_> extends AbstractPhaseScope<Solution_> {

    private LocalSearchStepScope<Solution_> lastCompletedStepScope;

    public LocalSearchPhaseScope(SolverScope<Solution_> solverScope) {
        super(solverScope);
        lastCompletedStepScope = new LocalSearchStepScope<>(this, -1);
        lastCompletedStepScope.setTimeGradient(0.0);
    }

    @Override
    public LocalSearchStepScope<Solution_> getLastCompletedStepScope() {
        return lastCompletedStepScope;
    }

    public void setLastCompletedStepScope(LocalSearchStepScope<Solution_> lastCompletedStepScope) {
        this.lastCompletedStepScope = lastCompletedStepScope;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

}

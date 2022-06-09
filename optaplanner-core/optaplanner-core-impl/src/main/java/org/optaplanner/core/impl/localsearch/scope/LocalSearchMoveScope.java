package org.optaplanner.core.impl.localsearch.scope;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.phase.scope.AbstractMoveScope;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class LocalSearchMoveScope<Solution_> extends AbstractMoveScope<Solution_> {

    private final LocalSearchStepScope<Solution_> stepScope;

    private Boolean accepted = null;

    public LocalSearchMoveScope(LocalSearchStepScope<Solution_> stepScope, int moveIndex, Move<Solution_> move) {
        super(moveIndex, move);
        this.stepScope = stepScope;
    }

    @Override
    public LocalSearchStepScope<Solution_> getStepScope() {
        return stepScope;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

}

package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu;

import java.util.Collection;

import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

public class ValueTabuAcceptor<Solution_> extends AbstractTabuAcceptor<Solution_> {

    public ValueTabuAcceptor(String logIndentation) {
        super(logIndentation);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    protected Collection<? extends Object> findTabu(LocalSearchMoveScope<Solution_> moveScope) {
        return moveScope.getMove().getPlanningValues();
    }

    @Override
    protected Collection<? extends Object> findNewTabu(LocalSearchStepScope<Solution_> stepScope) {
        return stepScope.getStep().getPlanningValues();
    }

}

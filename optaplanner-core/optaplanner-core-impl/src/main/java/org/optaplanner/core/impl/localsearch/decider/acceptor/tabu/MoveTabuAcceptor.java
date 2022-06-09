package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu;

import java.util.Collection;
import java.util.Collections;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

public class MoveTabuAcceptor<Solution_> extends AbstractTabuAcceptor<Solution_> {

    protected boolean useUndoMoveAsTabuMove = true;

    public MoveTabuAcceptor(String logIndentation) {
        super(logIndentation);
    }

    public void setUseUndoMoveAsTabuMove(boolean useUndoMoveAsTabuMove) {
        this.useUndoMoveAsTabuMove = useUndoMoveAsTabuMove;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    protected Collection<? extends Object> findTabu(LocalSearchMoveScope<Solution_> moveScope) {
        return Collections.singletonList(moveScope.getMove());
    }

    @Override
    protected Collection<? extends Object> findNewTabu(LocalSearchStepScope<Solution_> stepScope) {
        Move<?> tabuMove;
        if (useUndoMoveAsTabuMove) {
            tabuMove = stepScope.getUndoStep();
        } else {
            tabuMove = stepScope.getStep();
        }
        return Collections.singletonList(tabuMove);
    }

}

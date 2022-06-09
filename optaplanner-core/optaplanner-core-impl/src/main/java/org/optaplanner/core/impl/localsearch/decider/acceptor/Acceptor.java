package org.optaplanner.core.impl.localsearch.decider.acceptor;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.localsearch.decider.forager.LocalSearchForager;
import org.optaplanner.core.impl.localsearch.event.LocalSearchPhaseLifecycleListener;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;

/**
 * An Acceptor accepts or rejects a selected {@link Move}.
 * Note that the {@link LocalSearchForager} can still ignore the advice of the {@link Acceptor}.
 *
 * @see AbstractAcceptor
 */
public interface Acceptor<Solution_> extends LocalSearchPhaseLifecycleListener<Solution_> {

    /**
     * @param moveScope not null
     * @return true if accepted
     */
    boolean isAccepted(LocalSearchMoveScope<Solution_> moveScope);

}

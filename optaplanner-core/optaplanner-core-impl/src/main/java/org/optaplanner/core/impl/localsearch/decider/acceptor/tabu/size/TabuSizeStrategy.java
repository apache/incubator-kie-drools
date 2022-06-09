package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size;

import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

public interface TabuSizeStrategy<Solution_> {

    /**
     * @param stepScope never null
     * @return {@code >= 0}
     */
    int determineTabuSize(LocalSearchStepScope<Solution_> stepScope);

}

package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size;

import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

public class FixedTabuSizeStrategy<Solution_> extends AbstractTabuSizeStrategy<Solution_> {

    protected final int tabuSize;

    public FixedTabuSizeStrategy(int tabuSize) {
        this.tabuSize = tabuSize;
        if (tabuSize < 0) {
            throw new IllegalArgumentException("The tabuSize (" + tabuSize
                    + ") cannot be negative.");
        }
    }

    @Override
    public int determineTabuSize(LocalSearchStepScope<Solution_> stepScope) {
        return tabuSize;
    }

}

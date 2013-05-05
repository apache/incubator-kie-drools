package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu;

import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;

public abstract class AbstractAbsoluteSizeTabuAcceptor extends AbstractTabuAcceptor {

    protected int tabuSize = -1;
    protected int fadingTabuSize = 0;

    public void setTabuSize(int tabuSize) {
        this.tabuSize = tabuSize;
    }

    public void setFadingTabuSize(int fadingTabuSize) {
        this.fadingTabuSize = fadingTabuSize;
    }
    
    protected void validate() {
        if (tabuSize < 0) {
            throw new IllegalArgumentException("The tabuSize (" + tabuSize
                    + ") cannot be negative.");
        }
        if (fadingTabuSize < 0) {
            throw new IllegalArgumentException("The fadingTabuSize (" + fadingTabuSize
                    + ") cannot be negative.");
        }
        if (tabuSize + fadingTabuSize == 0) {
            throw new IllegalArgumentException("The sum of tabuSize and fadingTabuSize should be at least 1.");
        }
    }

    @Override
    protected int calculateActualMaximumSize(LocalSearchSolverPhaseScope scope) {
        return calculateFadingTabuSize(scope) + calculateRegularTabuSize(scope);
    }
    
    @Override
    protected int calculateFadingTabuSize(LocalSearchSolverPhaseScope scope) {
        return fadingTabuSize;
    }

    @Override
    protected int calculateRegularTabuSize(LocalSearchSolverPhaseScope scope) {
        return tabuSize;
    }

}

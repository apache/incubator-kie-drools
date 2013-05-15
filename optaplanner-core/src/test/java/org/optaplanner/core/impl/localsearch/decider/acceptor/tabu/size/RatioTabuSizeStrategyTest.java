package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size;

import org.junit.Test;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RatioTabuSizeStrategyTest {

    @Test
    public void tabuSize() {
        LocalSearchSolverPhaseScope phaseScope = mock(LocalSearchSolverPhaseScope.class);
        when(phaseScope.getWorkingEntityListSize()).thenReturn(20);
        LocalSearchStepScope stepScope = new LocalSearchStepScope(phaseScope);
        assertEquals(2, new RatioTabuSizeStrategy(0.1).determineTabuSize(stepScope));
        assertEquals(10, new RatioTabuSizeStrategy(0.5).determineTabuSize(stepScope));
    }

}

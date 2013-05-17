package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size;

import org.junit.Test;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EntityRatioTabuSizeStrategyTest {

    @Test
    public void tabuSize() {
        LocalSearchSolverPhaseScope phaseScope = new LocalSearchSolverPhaseScope(mock(DefaultSolverScope.class));
        when(phaseScope.getWorkingEntityCount()).thenReturn(20);
        LocalSearchStepScope stepScope = new LocalSearchStepScope(phaseScope);
        assertEquals(2, new EntityRatioTabuSizeStrategy(0.1).determineTabuSize(stepScope));
        assertEquals(10, new EntityRatioTabuSizeStrategy(0.5).determineTabuSize(stepScope));
    }

}

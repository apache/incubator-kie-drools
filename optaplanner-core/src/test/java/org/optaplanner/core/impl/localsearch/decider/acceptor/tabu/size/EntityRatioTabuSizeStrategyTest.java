package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size;

import org.junit.Test;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EntityRatioTabuSizeStrategyTest {

    @Test
    public void tabuSize() {
        LocalSearchPhaseScope phaseScope = new LocalSearchPhaseScope(mock(DefaultSolverScope.class));
        when(phaseScope.getWorkingEntityCount()).thenReturn(100);
        LocalSearchStepScope stepScope = new LocalSearchStepScope(phaseScope);
        assertEquals(10, new EntityRatioTabuSizeStrategy(0.1).determineTabuSize(stepScope));
        assertEquals(50, new EntityRatioTabuSizeStrategy(0.5).determineTabuSize(stepScope));
        // Rounding
        assertEquals(11, new EntityRatioTabuSizeStrategy(0.1051).determineTabuSize(stepScope));
        assertEquals(10, new EntityRatioTabuSizeStrategy(0.1049).determineTabuSize(stepScope));
        // Corner cases
        assertEquals(1, new EntityRatioTabuSizeStrategy(0.0000001).determineTabuSize(stepScope));
        assertEquals(99, new EntityRatioTabuSizeStrategy(0.9999999).determineTabuSize(stepScope));
    }

}

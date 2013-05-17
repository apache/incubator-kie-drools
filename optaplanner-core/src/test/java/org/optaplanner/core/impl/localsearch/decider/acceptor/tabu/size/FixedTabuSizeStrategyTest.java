package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size;

import org.junit.Test;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FixedTabuSizeStrategyTest {

    @Test
    public void tabuSize() {
        LocalSearchStepScope stepScope = mock(LocalSearchStepScope.class);
        assertEquals(5, new FixedTabuSizeStrategy(5).determineTabuSize(stepScope));
        assertEquals(17, new FixedTabuSizeStrategy(17).determineTabuSize(stepScope));
    }

}

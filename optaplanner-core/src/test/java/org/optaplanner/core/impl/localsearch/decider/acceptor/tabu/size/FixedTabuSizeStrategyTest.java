package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.PlanningEntityTabuAcceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;

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

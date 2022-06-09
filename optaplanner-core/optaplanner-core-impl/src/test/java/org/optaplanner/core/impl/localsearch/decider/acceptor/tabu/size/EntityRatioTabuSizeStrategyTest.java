package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;

class EntityRatioTabuSizeStrategyTest {

    @Test
    void tabuSize() {
        LocalSearchPhaseScope phaseScope = new LocalSearchPhaseScope(mock(SolverScope.class));
        when(phaseScope.getWorkingEntityCount()).thenReturn(100);
        LocalSearchStepScope stepScope = new LocalSearchStepScope(phaseScope);
        assertThat(new EntityRatioTabuSizeStrategy(0.1).determineTabuSize(stepScope)).isEqualTo(10);
        assertThat(new EntityRatioTabuSizeStrategy(0.5).determineTabuSize(stepScope)).isEqualTo(50);
        // Rounding
        assertThat(new EntityRatioTabuSizeStrategy(0.1051).determineTabuSize(stepScope)).isEqualTo(11);
        assertThat(new EntityRatioTabuSizeStrategy(0.1049).determineTabuSize(stepScope)).isEqualTo(10);
        // Corner cases
        assertThat(new EntityRatioTabuSizeStrategy(0.0000001).determineTabuSize(stepScope)).isEqualTo(1);
        assertThat(new EntityRatioTabuSizeStrategy(0.9999999).determineTabuSize(stepScope)).isEqualTo(99);
    }

}

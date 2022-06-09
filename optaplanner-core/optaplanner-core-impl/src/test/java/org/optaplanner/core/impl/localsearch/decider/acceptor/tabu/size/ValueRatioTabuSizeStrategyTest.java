package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;

class ValueRatioTabuSizeStrategyTest {

    @Test
    void tabuSize() {
        LocalSearchPhaseScope phaseScope = new LocalSearchPhaseScope(mock(SolverScope.class));
        when(phaseScope.getWorkingValueCount()).thenReturn(100);
        LocalSearchStepScope stepScope = new LocalSearchStepScope(phaseScope);
        assertThat(new ValueRatioTabuSizeStrategy(0.1).determineTabuSize(stepScope)).isEqualTo(10);
        assertThat(new ValueRatioTabuSizeStrategy(0.5).determineTabuSize(stepScope)).isEqualTo(50);
        // Rounding
        assertThat(new ValueRatioTabuSizeStrategy(0.1051).determineTabuSize(stepScope)).isEqualTo(11);
        assertThat(new ValueRatioTabuSizeStrategy(0.1049).determineTabuSize(stepScope)).isEqualTo(10);
        // Corner cases
        assertThat(new ValueRatioTabuSizeStrategy(0.0000001).determineTabuSize(stepScope)).isEqualTo(1);
        assertThat(new ValueRatioTabuSizeStrategy(0.9999999).determineTabuSize(stepScope)).isEqualTo(99);
    }

}

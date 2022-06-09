package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

class FixedTabuSizeStrategyTest {

    @Test
    void tabuSize() {
        LocalSearchStepScope stepScope = mock(LocalSearchStepScope.class);
        assertThat(new FixedTabuSizeStrategy(5).determineTabuSize(stepScope)).isEqualTo(5);
        assertThat(new FixedTabuSizeStrategy(17).determineTabuSize(stepScope)).isEqualTo(17);
    }

}

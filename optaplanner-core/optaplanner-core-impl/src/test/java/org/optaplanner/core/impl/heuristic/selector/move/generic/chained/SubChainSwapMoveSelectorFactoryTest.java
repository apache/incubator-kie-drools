package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.chained.SubChainSelectorConfig;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;

class SubChainSwapMoveSelectorFactoryTest {

    @Test
    void buildBaseMoveSelector() {
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig("chainedObject");
        SubChainSelectorConfig leftSubChainSelectorConfig = new SubChainSelectorConfig();
        leftSubChainSelectorConfig.setValueSelectorConfig(valueSelectorConfig);
        SubChainSelectorConfig rightSubChainSelectorConfig = new SubChainSelectorConfig();
        rightSubChainSelectorConfig.setValueSelectorConfig(valueSelectorConfig);
        SubChainSwapMoveSelectorConfig config = new SubChainSwapMoveSelectorConfig();
        config.setSubChainSelectorConfig(leftSubChainSelectorConfig);
        config.setSecondarySubChainSelectorConfig(rightSubChainSelectorConfig);
        SubChainSwapMoveSelectorFactory factory = new SubChainSwapMoveSelectorFactory(config);

        HeuristicConfigPolicy heuristicConfigPolicy = mock(HeuristicConfigPolicy.class);
        when(heuristicConfigPolicy.getSolutionDescriptor()).thenReturn(TestdataChainedSolution.buildSolutionDescriptor());

        SubChainSwapMoveSelector selector = (SubChainSwapMoveSelector) factory.buildBaseMoveSelector(heuristicConfigPolicy,
                SelectionCacheType.JUST_IN_TIME, true);
        assertThat(selector.leftSubChainSelector).isNotNull();
        assertThat(selector.rightSubChainSelector).isNotNull();
        assertThat(selector.variableDescriptor).isNotNull();
        assertThat(selector.randomSelection).isTrue();
    }
}

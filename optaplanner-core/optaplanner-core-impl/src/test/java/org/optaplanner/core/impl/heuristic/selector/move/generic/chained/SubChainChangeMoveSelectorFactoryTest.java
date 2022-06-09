package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.chained.SubChainSelectorConfig;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;

class SubChainChangeMoveSelectorFactoryTest {

    @Test
    void buildBaseMoveSelector() {
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig("chainedObject");
        SubChainSelectorConfig subChainSelectorConfig = new SubChainSelectorConfig();
        subChainSelectorConfig.setValueSelectorConfig(valueSelectorConfig);

        SubChainChangeMoveSelectorConfig config = new SubChainChangeMoveSelectorConfig();
        config.setSubChainSelectorConfig(subChainSelectorConfig);
        config.setValueSelectorConfig(valueSelectorConfig);
        SubChainChangeMoveSelectorFactory factory = new SubChainChangeMoveSelectorFactory(config);

        HeuristicConfigPolicy heuristicConfigPolicy = mock(HeuristicConfigPolicy.class);
        when(heuristicConfigPolicy.getSolutionDescriptor()).thenReturn(TestdataChainedSolution.buildSolutionDescriptor());

        SubChainChangeMoveSelector selector = (SubChainChangeMoveSelector) factory.buildBaseMoveSelector(heuristicConfigPolicy,
                SelectionCacheType.JUST_IN_TIME, true);
        assertThat(selector.subChainSelector).isNotNull();
        assertThat(selector.valueSelector).isNotNull();
        assertThat(selector.randomSelection).isTrue();
    }

}

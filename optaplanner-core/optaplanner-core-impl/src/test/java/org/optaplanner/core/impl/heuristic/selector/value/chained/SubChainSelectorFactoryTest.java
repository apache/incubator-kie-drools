package org.optaplanner.core.impl.heuristic.selector.value.chained;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.chained.SubChainSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;

class SubChainSelectorFactoryTest {

    @Test
    void buildSubChainSelector() {
        SubChainSelectorConfig config = new SubChainSelectorConfig();
        config.setMinimumSubChainSize(2);
        config.setMaximumSubChainSize(3);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig("chainedObject");
        config.setValueSelectorConfig(valueSelectorConfig);
        HeuristicConfigPolicy heuristicConfigPolicy = mock(HeuristicConfigPolicy.class);
        EntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        DefaultSubChainSelector subChainSelector =
                (DefaultSubChainSelector) SubChainSelectorFactory.create(config).buildSubChainSelector(heuristicConfigPolicy,
                        entityDescriptor, SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(subChainSelector.maximumSubChainSize).isEqualTo(config.getMaximumSubChainSize());
        assertThat(subChainSelector.minimumSubChainSize).isEqualTo(config.getMinimumSubChainSize());
        assertThat(subChainSelector.randomSelection).isTrue();
    }
}

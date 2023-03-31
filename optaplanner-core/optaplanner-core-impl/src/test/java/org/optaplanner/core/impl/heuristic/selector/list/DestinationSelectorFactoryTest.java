package org.optaplanner.core.impl.heuristic.selector.list;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static org.optaplanner.core.impl.heuristic.HeuristicConfigPolicyTestUtils.buildHeuristicConfigPolicy;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.nearby.NearbySelectionConfig;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.list.DestinationSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;

class DestinationSelectorFactoryTest {

    @Test
    void failFast_ifNearbyDoesNotHaveOriginSubListOrValueSelector() {
        DestinationSelectorConfig destinationSelectorConfig = new DestinationSelectorConfig()
                .withEntitySelectorConfig(new EntitySelectorConfig())
                .withValueSelectorConfig(new ValueSelectorConfig())
                .withNearbySelectionConfig(new NearbySelectionConfig()
                        .withOriginEntitySelectorConfig(new EntitySelectorConfig().withMimicSelectorRef("x"))
                        .withNearbyDistanceMeterClass(mock(NearbyDistanceMeter.class).getClass()));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> DestinationSelectorFactory.<TestdataListSolution> create(destinationSelectorConfig)
                        .buildDestinationSelector(buildHeuristicConfigPolicy(TestdataListSolution.buildSolutionDescriptor()),
                                SelectionCacheType.JUST_IN_TIME, true))
                .withMessageContaining("requires an originSubListSelector or an originValueSelector");
    }
}

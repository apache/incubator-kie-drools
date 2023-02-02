package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.heuristic.HeuristicConfigPolicyTestUtils.buildHeuristicConfigPolicy;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.SubListChangeMoveSelectorConfig;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils;

class SubListSelectorFactoryTest {

    @Test
    void buildSubListSelector() {
        SubListChangeMoveSelectorConfig config = new SubListChangeMoveSelectorConfig();

        config.setMinimumSubListSize(2);
        config.setMaximumSubListSize(3);

        SubListSelectorFactory<TestdataListSolution> factory = new SubListSelectorFactory<>(config);

        HeuristicConfigPolicy<TestdataListSolution> heuristicConfigPolicy =
                buildHeuristicConfigPolicy(TestdataListSolution.buildSolutionDescriptor());

        ListVariableDescriptor<TestdataListSolution> listVariableDescriptor =
                TestdataListEntity.buildVariableDescriptorForValueList();
        EntitySelector<TestdataListSolution> entitySelector = TestdataListUtils.mockEntitySelector();
        when(entitySelector.getEntityDescriptor()).thenReturn(listVariableDescriptor.getEntityDescriptor());
        RandomSubListSelector<TestdataListSolution> subListSelector =
                factory.buildSubListSelector(heuristicConfigPolicy, listVariableDescriptor,
                        entitySelector, SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);

        assertThat(subListSelector.getMinimumSubListSize()).isEqualTo(config.getMinimumSubListSize());
        assertThat(subListSelector.getMaximumSubListSize()).isEqualTo(config.getMaximumSubListSize());
    }
}

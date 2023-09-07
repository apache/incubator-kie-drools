/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.list;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.heuristic.HeuristicConfigPolicyTestUtils.buildHeuristicConfigPolicy;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.common.nearby.NearbySelectionConfig;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.list.SubListSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.list.mimic.MimicRecordingSubListSelector;
import org.optaplanner.core.impl.heuristic.selector.list.mimic.MimicReplayingSubListSelector;
import org.optaplanner.core.impl.heuristic.selector.list.mimic.SubListMimicRecorder;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListUtils;

class SubListSelectorFactoryTest {

    @Test
    void buildSubListSelector() {
        SubListSelectorConfig config = new SubListSelectorConfig()
                .withMinimumSubListSize(2)
                .withMaximumSubListSize(3)
                .withValueSelectorConfig(new ValueSelectorConfig());

        SubListSelectorFactory<TestdataListSolution> selectorFactory = SubListSelectorFactory.create(config);

        HeuristicConfigPolicy<TestdataListSolution> heuristicConfigPolicy =
                buildHeuristicConfigPolicy(TestdataListSolution.buildSolutionDescriptor());

        ListVariableDescriptor<TestdataListSolution> listVariableDescriptor =
                TestdataListEntity.buildVariableDescriptorForValueList();
        EntitySelector<TestdataListSolution> entitySelector = TestdataListUtils.mockEntitySelector();
        when(entitySelector.getEntityDescriptor()).thenReturn(listVariableDescriptor.getEntityDescriptor());

        RandomSubListSelector<TestdataListSolution> subListSelector =
                (RandomSubListSelector<TestdataListSolution>) selectorFactory.buildSubListSelector(heuristicConfigPolicy,
                        entitySelector, SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);

        assertThat(subListSelector.getMinimumSubListSize()).isEqualTo(config.getMinimumSubListSize());
        assertThat(subListSelector.getMaximumSubListSize()).isEqualTo(config.getMaximumSubListSize());
    }

    @Test
    void buildMimicRecordingSelector() {
        SubListSelectorConfig config = new SubListSelectorConfig()
                .withId("someSelectorId")
                .withMinimumSubListSize(3)
                .withMaximumSubListSize(10)
                .withValueSelectorConfig(new ValueSelectorConfig());

        HeuristicConfigPolicy<TestdataListSolution> heuristicConfigPolicy =
                buildHeuristicConfigPolicy(TestdataListSolution.buildSolutionDescriptor());

        ListVariableDescriptor<TestdataListSolution> listVariableDescriptor =
                TestdataListEntity.buildVariableDescriptorForValueList();
        EntitySelector<TestdataListSolution> entitySelector = TestdataListUtils.mockEntitySelector();
        when(entitySelector.getEntityDescriptor()).thenReturn(listVariableDescriptor.getEntityDescriptor());

        SubListSelectorFactory<TestdataListSolution> selectorFactory = SubListSelectorFactory.create(config);
        SubListSelector<TestdataListSolution> subListSelector = selectorFactory.buildSubListSelector(heuristicConfigPolicy,
                entitySelector, SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);

        assertThat(subListSelector).isInstanceOf(MimicRecordingSubListSelector.class);
    }

    @Test
    void buildMimicReplayingSelector() {
        String selectorId = "someSelectorId";
        SubListSelectorConfig config = new SubListSelectorConfig()
                .withMimicSelectorRef(selectorId);

        ListVariableDescriptor<TestdataListSolution> listVariableDescriptor =
                TestdataListEntity.buildVariableDescriptorForValueList();
        EntitySelector<TestdataListSolution> entitySelector = TestdataListUtils.mockEntitySelector();
        when(entitySelector.getEntityDescriptor()).thenReturn(listVariableDescriptor.getEntityDescriptor());

        SubListMimicRecorder<TestdataListSolution> subListMimicRecorder = mock(SubListMimicRecorder.class);
        HeuristicConfigPolicy<TestdataListSolution> heuristicConfigPolicy =
                buildHeuristicConfigPolicy(TestdataListSolution.buildSolutionDescriptor());
        heuristicConfigPolicy.addSubListMimicRecorder(selectorId, subListMimicRecorder);
        when(subListMimicRecorder.getVariableDescriptor()).thenReturn(listVariableDescriptor);

        SubListSelectorFactory<TestdataListSolution> selectorFactory = SubListSelectorFactory.create(config);
        SubListSelector<TestdataListSolution> subListSelector = selectorFactory.buildSubListSelector(heuristicConfigPolicy,
                entitySelector, SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);

        assertThat(subListSelector).isInstanceOf(MimicReplayingSubListSelector.class);
    }

    @Test
    void failFast_ifMimicRecordingIsUsedWithOtherProperty() {
        SubListSelectorConfig config = new SubListSelectorConfig()
                .withMaximumSubListSize(10)
                .withMimicSelectorRef("someSelectorId");

        assertThatIllegalArgumentException().isThrownBy(
                () -> SubListSelectorFactory.<TestdataListSolution> create(config)
                        .buildMimicReplaying(buildHeuristicConfigPolicy(TestdataListSolution.buildSolutionDescriptor())))
                .withMessageContaining("has another property");
    }

    static Stream<Arguments> limitedDistributionConfigs() {
        return Stream.of(
                arguments(new NearbySelectionConfig().withBlockDistributionSizeRatio(0.5), "blockDistributionSizeRatio"),
                arguments(new NearbySelectionConfig().withBlockDistributionSizeMaximum(10), "blockDistributionSizeMaximum"),
                arguments(new NearbySelectionConfig().withLinearDistributionSizeMaximum(10), "linearDistributionSizeMaximum"),
                arguments(new NearbySelectionConfig().withParabolicDistributionSizeMaximum(10),
                        "parabolicDistributionSizeMaximum"));
    }

    @ParameterizedTest
    @MethodSource("limitedDistributionConfigs")
    void failFast_ifMinimumSubListSizeUsedTogetherWithLimitedDistribution(NearbySelectionConfig nearbySelectionConfig,
            String violatingPropertyName) {
        SubListSelectorConfig config = new SubListSelectorConfig()
                .withMinimumSubListSize(2)
                .withNearbySelectionConfig(nearbySelectionConfig);

        SubListSelectorFactory<TestdataListSolution> selectorFactory = SubListSelectorFactory.create(config);

        HeuristicConfigPolicy<TestdataListSolution> heuristicConfigPolicy =
                buildHeuristicConfigPolicy(TestdataListSolution.buildSolutionDescriptor());

        ListVariableDescriptor<TestdataListSolution> listVariableDescriptor =
                TestdataListEntity.buildVariableDescriptorForValueList();
        EntitySelector<TestdataListSolution> entitySelector = TestdataListUtils.mockEntitySelector();
        when(entitySelector.getEntityDescriptor()).thenReturn(listVariableDescriptor.getEntityDescriptor());

        assertThatIllegalArgumentException().isThrownBy(
                () -> selectorFactory.buildSubListSelector(heuristicConfigPolicy, entitySelector,
                        SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM))
                .withMessageContaining(violatingPropertyName);
    }

    @Test
    void failFast_ifNearbyDoesNotHaveOriginSubListSelector() {
        SubListSelectorConfig subListSelectorConfig = new SubListSelectorConfig()
                .withNearbySelectionConfig(new NearbySelectionConfig()
                        .withOriginEntitySelectorConfig(new EntitySelectorConfig().withMimicSelectorRef("x"))
                        .withNearbyDistanceMeterClass(mock(NearbyDistanceMeter.class).getClass()));

        EntitySelector<TestdataListSolution> entitySelector = TestdataListUtils.mockEntitySelector();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> SubListSelectorFactory.<TestdataListSolution> create(subListSelectorConfig)
                        .buildSubListSelector(buildHeuristicConfigPolicy(TestdataListSolution.buildSolutionDescriptor()),
                                entitySelector, SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM))
                .withMessageContaining("requires an originSubListSelector");
    }

    @Test
    void requiresListVariable() {
        SubListSelectorConfig subListSelectorConfig = new SubListSelectorConfig();

        EntitySelector<TestdataSolution> entitySelector =
                SelectorTestUtils.mockEntitySelector(TestdataEntity.buildEntityDescriptor());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> SubListSelectorFactory.<TestdataSolution> create(subListSelectorConfig)
                        .buildSubListSelector(buildHeuristicConfigPolicy(TestdataSolution.buildSolutionDescriptor()),
                                entitySelector, SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM))
                .withMessageContaining("@" + PlanningListVariable.class.getSimpleName());
    }
}

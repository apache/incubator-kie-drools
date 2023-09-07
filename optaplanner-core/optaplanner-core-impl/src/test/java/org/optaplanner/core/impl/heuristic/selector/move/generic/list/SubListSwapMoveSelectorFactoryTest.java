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

package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.optaplanner.core.impl.heuristic.HeuristicConfigPolicyTestUtils.buildHeuristicConfigPolicy;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.list.SubListSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.SubListSwapMoveSelectorConfig;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.list.RandomSubListSelector;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;

class SubListSwapMoveSelectorFactoryTest {

    @Test
    void buildBaseMoveSelector() {
        SubListSwapMoveSelectorConfig config = new SubListSwapMoveSelectorConfig();
        SubListSwapMoveSelectorFactory<TestdataListSolution> factory =
                new SubListSwapMoveSelectorFactory<>(config);

        HeuristicConfigPolicy<TestdataListSolution> heuristicConfigPolicy =
                buildHeuristicConfigPolicy(TestdataListSolution.buildSolutionDescriptor());

        RandomSubListSwapMoveSelector<TestdataListSolution> selector =
                (RandomSubListSwapMoveSelector<TestdataListSolution>) factory.buildBaseMoveSelector(heuristicConfigPolicy,
                        SelectionCacheType.JUST_IN_TIME, true);

        assertThat(selector.isCountable()).isTrue();
        assertThat(selector.isNeverEnding()).isTrue();
        assertThat(selector.isSelectReversingMoveToo()).isTrue();
    }

    @Test
    void disableSelectReversingMoveToo() {
        SubListSwapMoveSelectorConfig config = new SubListSwapMoveSelectorConfig();
        config.setSelectReversingMoveToo(false);
        SubListSwapMoveSelectorFactory<TestdataListSolution> factory =
                new SubListSwapMoveSelectorFactory<>(config);

        HeuristicConfigPolicy<TestdataListSolution> heuristicConfigPolicy =
                buildHeuristicConfigPolicy(TestdataListSolution.buildSolutionDescriptor());

        RandomSubListSwapMoveSelector<TestdataListSolution> selector =
                (RandomSubListSwapMoveSelector<TestdataListSolution>) factory.buildBaseMoveSelector(heuristicConfigPolicy,
                        SelectionCacheType.JUST_IN_TIME, true);

        assertThat(selector.isSelectReversingMoveToo()).isFalse();
    }

    static SubListSwapMoveSelectorConfig minimumSize_SubListSelector() {
        SubListSwapMoveSelectorConfig config = new SubListSwapMoveSelectorConfig()
                .withSubListSelectorConfig(new SubListSelectorConfig().withMinimumSubListSize(10));
        config.setMinimumSubListSize(10);
        return config;
    }

    static SubListSwapMoveSelectorConfig maximumSize_SubListSelector() {
        SubListSwapMoveSelectorConfig config = new SubListSwapMoveSelectorConfig()
                .withSubListSelectorConfig(new SubListSelectorConfig().withMaximumSubListSize(10));
        config.setMaximumSubListSize(10);
        return config;
    }

    static SubListSwapMoveSelectorConfig minimumSize_SecondarySubListSelector() {
        SubListSwapMoveSelectorConfig config = new SubListSwapMoveSelectorConfig()
                .withSecondarySubListSelectorConfig(new SubListSelectorConfig().withMinimumSubListSize(10));
        config.setMinimumSubListSize(10);
        return config;
    }

    static SubListSwapMoveSelectorConfig maximumSize_SecondarySubListSelector() {
        SubListSwapMoveSelectorConfig config = new SubListSwapMoveSelectorConfig()
                .withSecondarySubListSelectorConfig(new SubListSelectorConfig().withMaximumSubListSize(10));
        config.setMaximumSubListSize(10);
        return config;
    }

    static Stream<Arguments> wrongConfigurations() {
        return Stream.of(
                arguments(minimumSize_SubListSelector(), "minimumSubListSize", "subListSelector"),
                arguments(maximumSize_SubListSelector(), "maximumSubListSize", "subListSelector"),
                arguments(minimumSize_SecondarySubListSelector(), "minimumSubListSize", "secondarySubListSelector"),
                arguments(maximumSize_SecondarySubListSelector(), "maximumSubListSize", "secondarySubListSelector"));
    }

    @ParameterizedTest(name = "{1} + {2}")
    @MethodSource("wrongConfigurations")
    void failFast_ifSubListSizeOnBothMoveSelectorAndSubListSelector(
            SubListSwapMoveSelectorConfig config, String propertyName, String childConfigName) {
        SubListSwapMoveSelectorFactory<TestdataListSolution> factory = new SubListSwapMoveSelectorFactory<>(config);

        HeuristicConfigPolicy<TestdataListSolution> heuristicConfigPolicy =
                buildHeuristicConfigPolicy(TestdataListSolution.buildSolutionDescriptor());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> factory.buildBaseMoveSelector(heuristicConfigPolicy, SelectionCacheType.JUST_IN_TIME, true))
                .withMessageContainingAll(propertyName, childConfigName);
    }

    @Test
    void transferDeprecatedSubListSizeToChildSelectors() {
        int minimumSubListSize = 21;
        int maximumSubListSize = 445;
        SubListSwapMoveSelectorConfig config = new SubListSwapMoveSelectorConfig();
        config.setMinimumSubListSize(minimumSubListSize);
        config.setMaximumSubListSize(maximumSubListSize);

        SubListSwapMoveSelectorFactory<TestdataListSolution> factory = new SubListSwapMoveSelectorFactory<>(config);

        HeuristicConfigPolicy<TestdataListSolution> heuristicConfigPolicy =
                buildHeuristicConfigPolicy(TestdataListSolution.buildSolutionDescriptor());

        RandomSubListSwapMoveSelector<?> moveSelector =
                (RandomSubListSwapMoveSelector<?>) factory.buildBaseMoveSelector(heuristicConfigPolicy,
                        SelectionCacheType.JUST_IN_TIME, true);
        assertThat(((RandomSubListSelector<?>) moveSelector.getLeftSubListSelector()).getMinimumSubListSize())
                .isEqualTo(minimumSubListSize);
        assertThat(((RandomSubListSelector<?>) moveSelector.getLeftSubListSelector()).getMaximumSubListSize())
                .isEqualTo(maximumSubListSize);
        assertThat(((RandomSubListSelector<?>) moveSelector.getRightSubListSelector()).getMinimumSubListSize())
                .isEqualTo(minimumSubListSize);
        assertThat(((RandomSubListSelector<?>) moveSelector.getRightSubListSelector()).getMaximumSubListSize())
                .isEqualTo(maximumSubListSize);
    }
}

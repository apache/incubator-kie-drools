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

package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.heuristic.HeuristicConfigPolicyTestUtils.buildHeuristicConfigPolicy;

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
        SubChainSwapMoveSelectorFactory<TestdataChainedSolution> factory =
                new SubChainSwapMoveSelectorFactory<>(config);

        HeuristicConfigPolicy<TestdataChainedSolution> heuristicConfigPolicy =
                buildHeuristicConfigPolicy(TestdataChainedSolution.buildSolutionDescriptor());

        SubChainSwapMoveSelector<TestdataChainedSolution> selector = (SubChainSwapMoveSelector<TestdataChainedSolution>) factory
                .buildBaseMoveSelector(heuristicConfigPolicy, SelectionCacheType.JUST_IN_TIME, true);
        assertThat(selector.leftSubChainSelector).isNotNull();
        assertThat(selector.rightSubChainSelector).isNotNull();
        assertThat(selector.variableDescriptor).isNotNull();
        assertThat(selector.randomSelection).isTrue();
    }
}

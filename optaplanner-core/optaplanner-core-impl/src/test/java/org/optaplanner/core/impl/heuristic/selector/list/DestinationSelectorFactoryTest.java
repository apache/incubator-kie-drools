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

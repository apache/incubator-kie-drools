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

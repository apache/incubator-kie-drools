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

import java.util.Objects;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.chained.SubChainSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelectorFactory;

public class SubChainSwapMoveSelectorFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, SubChainSwapMoveSelectorConfig> {

    public SubChainSwapMoveSelectorFactory(SubChainSwapMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    protected MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        EntityDescriptor<Solution_> entityDescriptor = deduceEntityDescriptor(configPolicy, config.getEntityClass());
        SubChainSelectorConfig subChainSelectorConfig =
                Objects.requireNonNullElseGet(config.getSubChainSelectorConfig(), SubChainSelectorConfig::new);
        SubChainSelectorConfig secondarySubChainSelectorConfig =
                Objects.requireNonNullElse(config.getSecondarySubChainSelectorConfig(), subChainSelectorConfig);
        SelectionOrder selectionOrder = SelectionOrder.fromRandomSelectionBoolean(randomSelection);
        SubChainSelector<Solution_> leftSubChainSelector =
                SubChainSelectorFactory.<Solution_> create(subChainSelectorConfig)
                        .buildSubChainSelector(configPolicy, entityDescriptor, minimumCacheType, selectionOrder);
        SubChainSelector<Solution_> rightSubChainSelector =
                SubChainSelectorFactory.<Solution_> create(secondarySubChainSelectorConfig)
                        .buildSubChainSelector(configPolicy, entityDescriptor, minimumCacheType, selectionOrder);
        return new SubChainSwapMoveSelector<>(leftSubChainSelector, rightSubChainSelector, randomSelection,
                Objects.requireNonNullElse(config.getSelectReversingMoveToo(), true));
    }
}

/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

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

public class SubChainSwapMoveSelectorFactory extends AbstractMoveSelectorFactory<SubChainSwapMoveSelectorConfig> {

    public SubChainSwapMoveSelectorFactory(SubChainSwapMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    protected MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        EntityDescriptor entityDescriptor =
                config.getEntityClass() == null ? deduceEntityDescriptor(configPolicy.getSolutionDescriptor())
                        : deduceEntityDescriptor(configPolicy.getSolutionDescriptor(), config.getEntityClass());
        SubChainSelectorConfig subChainSelectorConfig_ =
                config.getSubChainSelectorConfig() == null ? new SubChainSelectorConfig() : config.getSubChainSelectorConfig();
        SubChainSelector leftSubChainSelector =
                SubChainSelectorFactory.create(subChainSelectorConfig_).buildSubChainSelector(configPolicy,
                        entityDescriptor, minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        SubChainSelectorConfig rightSubChainSelectorConfig =
                defaultIfNull(config.getSecondarySubChainSelectorConfig(), subChainSelectorConfig_);
        SubChainSelector rightSubChainSelector =
                SubChainSelectorFactory.create(rightSubChainSelectorConfig).buildSubChainSelector(configPolicy,
                        entityDescriptor, minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        return new SubChainSwapMoveSelector(leftSubChainSelector, rightSubChainSelector, randomSelection,
                defaultIfNull(config.getSelectReversingMoveToo(), true));
    }
}

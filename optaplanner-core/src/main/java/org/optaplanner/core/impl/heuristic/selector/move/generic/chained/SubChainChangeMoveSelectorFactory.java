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

import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.chained.SubChainSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelector;

public class SubChainChangeMoveSelectorFactory extends AbstractMoveSelectorFactory<SubChainChangeMoveSelectorConfig> {

    public SubChainChangeMoveSelectorFactory(SubChainChangeMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    public MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        EntityDescriptor entityDescriptor =
                configPolicy.getSolutionDescriptor().deduceEntityDescriptor(moveSelectorConfig.getEntityClass());
        SubChainSelectorConfig subChainSelectorConfig_ =
                moveSelectorConfig.getSubChainSelectorConfig() == null ? new SubChainSelectorConfig()
                        : moveSelectorConfig.getSubChainSelectorConfig();
        SubChainSelector subChainSelector = subChainSelectorConfig_.buildSubChainSelector(configPolicy,
                entityDescriptor,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        ValueSelectorConfig valueSelectorConfig_ =
                moveSelectorConfig.getValueSelectorConfig() == null ? new ValueSelectorConfig()
                        : moveSelectorConfig.getValueSelectorConfig();
        ValueSelector valueSelector = valueSelectorConfig_.buildValueSelector(configPolicy,
                entityDescriptor,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        if (!(valueSelector instanceof EntityIndependentValueSelector)) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + moveSelectorConfig
                    + ") needs to be based on an EntityIndependentValueSelector (" + valueSelector + ")."
                    + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");
        }
        return new SubChainChangeMoveSelector(subChainSelector, (EntityIndependentValueSelector) valueSelector,
                randomSelection, defaultIfNull(moveSelectorConfig.getSelectReversingMoveToo(), true));
    }
}

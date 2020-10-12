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

package org.optaplanner.core.impl.heuristic.selector.value.chained;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.chained.SubChainSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelectorFactory;

public class SubChainSelectorFactory<Solution_> {

    /**
     * Defaults to 1, even if it partially duplicates {@link ChangeMoveSelectorConfig},
     * because otherwise the default would not include
     * swapping a pillar of size 1 with another pillar of size 2 or greater.
     */
    private static final int DEFAULT_MINIMUM_SUB_CHAIN_SIZE = 1;
    private static final int DEFAULT_MAXIMUM_SUB_CHAIN_SIZE = Integer.MAX_VALUE;

    public static <Solution_> SubChainSelectorFactory<Solution_>
            create(SubChainSelectorConfig subChainSelectorConfig) {
        return new SubChainSelectorFactory<>(subChainSelectorConfig);
    }

    private final SubChainSelectorConfig config;

    public SubChainSelectorFactory(SubChainSelectorConfig subChainSelectorConfig) {
        this.config = subChainSelectorConfig;
    }

    /**
     *
     * @param configPolicy never null
     * @param entityDescriptor never null
     * @param minimumCacheType never null, If caching is used (different from {@link SelectionCacheType#JUST_IN_TIME}),
     *        then it should be at least this {@link SelectionCacheType} because an ancestor already uses such caching
     *        and less would be pointless.
     * @param inheritedSelectionOrder never null
     * @return never null
     */
    public SubChainSelector<Solution_> buildSubChainSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            EntityDescriptor<Solution_> entityDescriptor, SelectionCacheType minimumCacheType,
            SelectionOrder inheritedSelectionOrder) {
        if (minimumCacheType.compareTo(SelectionCacheType.STEP) > 0) {
            throw new IllegalArgumentException("The subChainSelectorConfig (" + this
                    + ")'s minimumCacheType (" + minimumCacheType
                    + ") must not be higher than " + SelectionCacheType.STEP
                    + " because the chains change every step.");
        }
        ValueSelectorConfig valueSelectorConfig_ = config.getValueSelectorConfig() == null ? new ValueSelectorConfig()
                : config.getValueSelectorConfig();
        // ValueSelector uses SelectionOrder.ORIGINAL because a SubChainSelector STEP caches the values
        ValueSelector<Solution_> valueSelector =
                ValueSelectorFactory.<Solution_> create(valueSelectorConfig_)
                        .buildValueSelector(configPolicy, entityDescriptor, minimumCacheType, SelectionOrder.ORIGINAL);
        if (!(valueSelector instanceof EntityIndependentValueSelector)) {
            throw new IllegalArgumentException("The minimumCacheType (" + this
                    + ") needs to be based on an EntityIndependentValueSelector (" + valueSelector + ")."
                    + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");
        }
        return new DefaultSubChainSelector<>((EntityIndependentValueSelector<Solution_>) valueSelector,
                inheritedSelectionOrder.toRandomSelectionBoolean(),
                defaultIfNull(config.getMinimumSubChainSize(), DEFAULT_MINIMUM_SUB_CHAIN_SIZE),
                defaultIfNull(config.getMaximumSubChainSize(), DEFAULT_MAXIMUM_SUB_CHAIN_SIZE));
    }
}

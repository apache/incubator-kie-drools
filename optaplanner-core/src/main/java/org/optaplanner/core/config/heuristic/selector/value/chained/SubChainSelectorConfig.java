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

package org.optaplanner.core.config.heuristic.selector.value.chained;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import javax.xml.bind.annotation.XmlElement;

import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.config.heuristic.selector.SelectorConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.DefaultSubChainSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelector;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("subChainSelector")
public class SubChainSelectorConfig extends SelectorConfig<SubChainSelectorConfig> {

    /**
     * Defaults to 1, even if it partially duplicates {@link ChangeMoveSelectorConfig},
     * because otherwise the default would not include
     * swapping a pillar of size 1 with another pillar of size 2 or greater.
     */
    private static final int DEFAULT_MINIMUM_SUB_CHAIN_SIZE = 1;
    private static final int DEFAULT_MAXIMUM_SUB_CHAIN_SIZE = Integer.MAX_VALUE;

    @XmlElement(name = "valueSelector")
    @XStreamAlias("valueSelector")
    protected ValueSelectorConfig valueSelectorConfig = null;

    protected Integer minimumSubChainSize = null;
    protected Integer maximumSubChainSize = null;

    public ValueSelectorConfig getValueSelectorConfig() {
        return valueSelectorConfig;
    }

    public void setValueSelectorConfig(ValueSelectorConfig valueSelectorConfig) {
        this.valueSelectorConfig = valueSelectorConfig;
    }

    /**
     * @return sometimes null
     */
    public Integer getMinimumSubChainSize() {
        return minimumSubChainSize;
    }

    public void setMinimumSubChainSize(Integer minimumSubChainSize) {
        this.minimumSubChainSize = minimumSubChainSize;
    }

    public Integer getMaximumSubChainSize() {
        return maximumSubChainSize;
    }

    public void setMaximumSubChainSize(Integer maximumSubChainSize) {
        this.maximumSubChainSize = maximumSubChainSize;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

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
    public SubChainSelector buildSubChainSelector(HeuristicConfigPolicy configPolicy,
            EntityDescriptor entityDescriptor,
            SelectionCacheType minimumCacheType, SelectionOrder inheritedSelectionOrder) {
        if (minimumCacheType.compareTo(SelectionCacheType.STEP) > 0) {
            throw new IllegalArgumentException("The subChainSelectorConfig (" + this
                    + ")'s minimumCacheType (" + minimumCacheType
                    + ") must not be higher than " + SelectionCacheType.STEP
                    + " because the chains change every step.");
        }
        ValueSelectorConfig valueSelectorConfig_ = valueSelectorConfig == null ? new ValueSelectorConfig()
                : valueSelectorConfig;
        // ValueSelector uses SelectionOrder.ORIGINAL because a SubChainSelector STEP caches the values
        ValueSelector valueSelector = valueSelectorConfig_.buildValueSelector(configPolicy,
                entityDescriptor,
                minimumCacheType, SelectionOrder.ORIGINAL);
        if (!(valueSelector instanceof EntityIndependentValueSelector)) {
            throw new IllegalArgumentException("The minimumCacheType (" + this
                    + ") needs to be based on an EntityIndependentValueSelector (" + valueSelector + ")."
                    + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");
        }
        return new DefaultSubChainSelector((EntityIndependentValueSelector) valueSelector,
                inheritedSelectionOrder.toRandomSelectionBoolean(),
                defaultIfNull(minimumSubChainSize, DEFAULT_MINIMUM_SUB_CHAIN_SIZE),
                defaultIfNull(maximumSubChainSize, DEFAULT_MAXIMUM_SUB_CHAIN_SIZE));
    }

    @Override
    public SubChainSelectorConfig inherit(SubChainSelectorConfig inheritedConfig) {
        valueSelectorConfig = ConfigUtils.inheritConfig(valueSelectorConfig, inheritedConfig.getValueSelectorConfig());
        minimumSubChainSize = ConfigUtils.inheritOverwritableProperty(minimumSubChainSize,
                inheritedConfig.getMinimumSubChainSize());
        maximumSubChainSize = ConfigUtils.inheritOverwritableProperty(maximumSubChainSize,
                inheritedConfig.getMaximumSubChainSize());
        return this;
    }

    @Override
    public SubChainSelectorConfig copyConfig() {
        return new SubChainSelectorConfig().inherit(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + valueSelectorConfig + ")";
    }

}

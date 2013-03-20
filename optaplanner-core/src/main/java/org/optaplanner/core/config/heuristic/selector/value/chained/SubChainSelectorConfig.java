/*
 * Copyright 2012 JBoss Inc
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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.api.domain.value.ValueRange;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.heuristic.selector.SelectorConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.DefaultSubChainSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelector;

@XStreamAlias("subChainSelector")
public class SubChainSelectorConfig extends SelectorConfig {

    private static final int DEFAULT_MINIMUM_SUB_CHAIN_SIZE = 2;
    private static final int DEFAULT_MAXIMUM_SUB_CHAIN_SIZE = Integer.MAX_VALUE;

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
     * Defaults to {@value #DEFAULT_MINIMUM_SUB_CHAIN_SIZE} because other {@link MoveSelector}s
     * s(uch as {@link ChangeMoveSelector} and {@link SwapMoveSelector}) already handle 1-sized chains.
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
     * @param environmentMode never null
     * @param solutionDescriptor never null
     * @param entityDescriptor never null
     * @param minimumCacheType never null, If caching is used (different from {@link SelectionCacheType#JUST_IN_TIME}),
     * then it should be at least this {@link SelectionCacheType} because an ancestor already uses such caching
     * and less would be pointless.
     * @param inheritedSelectionOrder never null
     * @return never null
     */
    public SubChainSelector buildSubChainSelector(EnvironmentMode environmentMode,
            SolutionDescriptor solutionDescriptor, PlanningEntityDescriptor entityDescriptor,
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
        ValueSelector valueSelector = valueSelectorConfig_.buildValueSelector(environmentMode,
                solutionDescriptor, entityDescriptor,
                minimumCacheType, SelectionOrder.ORIGINAL);
        if (!(valueSelector instanceof EntityIndependentValueSelector)) {
            throw new IllegalArgumentException("The minimumCacheType (" + this
                    + ") needs to be based on a EntityIndependentValueSelector."
                    + " Check your @" + ValueRange.class.getSimpleName() + " annotations.");
        }
        return new DefaultSubChainSelector((EntityIndependentValueSelector) valueSelector,
                inheritedSelectionOrder.toRandomSelectionBoolean(),
                minimumSubChainSize == null ? DEFAULT_MINIMUM_SUB_CHAIN_SIZE : minimumSubChainSize,
                maximumSubChainSize == null ? DEFAULT_MAXIMUM_SUB_CHAIN_SIZE : maximumSubChainSize);
    }

    public void inherit(SubChainSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (valueSelectorConfig == null) {
            valueSelectorConfig = inheritedConfig.getValueSelectorConfig();
        } else if (inheritedConfig.getValueSelectorConfig() != null) {
            valueSelectorConfig.inherit(inheritedConfig.getValueSelectorConfig());
        }
        minimumSubChainSize = ConfigUtils.inheritOverwritableProperty(minimumSubChainSize,
                inheritedConfig.getMinimumSubChainSize());
        maximumSubChainSize = ConfigUtils.inheritOverwritableProperty(maximumSubChainSize,
                inheritedConfig.getMaximumSubChainSize());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + valueSelectorConfig + ")";
    }

}

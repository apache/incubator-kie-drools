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

package org.drools.planner.config.heuristic.selector.value.chained;

import java.util.Collection;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.heuristic.selector.SelectorConfig;
import org.drools.planner.config.heuristic.selector.common.SelectionOrder;
import org.drools.planner.config.heuristic.selector.value.ValueSelectorConfig;
import org.drools.planner.config.util.ConfigUtils;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.heuristic.selector.move.generic.ChangeMoveSelector;
import org.drools.planner.core.heuristic.selector.move.generic.SwapMoveSelector;
import org.drools.planner.core.heuristic.selector.move.generic.chained.SubChainChangeMoveSelector;
import org.drools.planner.core.heuristic.selector.value.FromSolutionPropertyValueSelector;
import org.drools.planner.core.heuristic.selector.value.ValueSelector;
import org.drools.planner.core.heuristic.selector.value.chained.DefaultSubChainSelector;
import org.drools.planner.core.heuristic.selector.value.chained.SubChainSelector;
import org.drools.planner.core.heuristic.selector.value.decorator.ProbabilityValueSelector;

@XStreamAlias("subChainSelector")
public class SubChainSelectorConfig extends SelectorConfig {

    private static final int DEFAULT_MINIMUM_SUB_CHAIN_SIZE = 2;

    @XStreamAlias("valueSelector")
    protected ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();

    protected Integer minimumSubChainSize = null;

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

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    /**
     * @param environmentMode never null
     * @param solutionDescriptor never null
     * @param inheritedSelectionOrder never null
     * @param minimumCacheType never null, If caching is used (different from {@link SelectionCacheType#JUST_IN_TIME}),
     * then it should be at least this {@link SelectionCacheType} because an ancestor already uses such caching
     * and less would be pointless.
     * @param entityDescriptor never null
     * @return never null
     */
    public SubChainSelector buildSubChainSelector(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            SelectionOrder inheritedSelectionOrder, SelectionCacheType minimumCacheType,
            PlanningEntityDescriptor entityDescriptor) {
        if (minimumCacheType.compareTo(SelectionCacheType.STEP) > 0) {
            throw new IllegalArgumentException("The subChainChangeMoveSelector's minimumCacheType (" + minimumCacheType
                    + ") must not be higher than " + SelectionCacheType.STEP
                    + " because the chains change every step.");
        }
        // ValueSelector uses SelectionOrder.ORIGINAL because a SubChainSelector STEP caches the values
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(environmentMode, solutionDescriptor,
                SelectionOrder.ORIGINAL, minimumCacheType, entityDescriptor);
        return new DefaultSubChainSelector(valueSelector, inheritedSelectionOrder == SelectionOrder.RANDOM,
                minimumSubChainSize == null ? DEFAULT_MINIMUM_SUB_CHAIN_SIZE : minimumSubChainSize);
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
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + valueSelectorConfig + ")";
    }

}

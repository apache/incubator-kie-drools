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

package org.drools.planner.config.heuristic.selector.move.generic.chained;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.heuristic.selector.common.SelectionOrder;
import org.drools.planner.config.heuristic.selector.move.MoveSelectorConfig;
import org.drools.planner.config.heuristic.selector.value.ValueSelectorConfig;
import org.drools.planner.config.heuristic.selector.value.chained.SubChainSelectorConfig;
import org.drools.planner.config.util.ConfigUtils;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.heuristic.selector.move.generic.chained.SubChainChangeMoveSelector;
import org.drools.planner.core.heuristic.selector.value.ValueSelector;
import org.drools.planner.core.heuristic.selector.value.chained.SubChainSelector;

@XStreamAlias("subChainChangeMoveSelector")
public class SubChainChangeMoveSelectorConfig extends MoveSelectorConfig {

    private Class<?> planningEntityClass = null;
    @XStreamAlias("subChainSelector")
    private SubChainSelectorConfig subChainSelectorConfig = new SubChainSelectorConfig();
    @XStreamAlias("valueSelector")
    private ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();

    private Boolean selectReversingMoveToo = null;

    public Class<?> getPlanningEntityClass() {
        return planningEntityClass;
    }

    public void setPlanningEntityClass(Class<?> planningEntityClass) {
        this.planningEntityClass = planningEntityClass;
    }

    public SubChainSelectorConfig getSubChainSelectorConfig() {
        return subChainSelectorConfig;
    }

    public void setSubChainSelectorConfig(SubChainSelectorConfig subChainSelectorConfig) {
        this.subChainSelectorConfig = subChainSelectorConfig;
    }

    public ValueSelectorConfig getValueSelectorConfig() {
        return valueSelectorConfig;
    }

    public void setValueSelectorConfig(ValueSelectorConfig valueSelectorConfig) {
        this.valueSelectorConfig = valueSelectorConfig;
    }

    public Boolean getSelectReversingMoveToo() {
        return selectReversingMoveToo;
    }

    public void setSelectReversingMoveToo(Boolean selectReversingMoveToo) {
        this.selectReversingMoveToo = selectReversingMoveToo;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public MoveSelector buildBaseMoveSelector(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        PlanningEntityDescriptor entityDescriptor = deduceEntityDescriptor(solutionDescriptor, planningEntityClass);
        SubChainSelector subChainSelector = subChainSelectorConfig.buildSubChainSelector(environmentMode,
                solutionDescriptor, entityDescriptor,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(environmentMode,
                solutionDescriptor, entityDescriptor,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        return new SubChainChangeMoveSelector(subChainSelector, valueSelector, randomSelection,
                selectReversingMoveToo == null ? true : selectReversingMoveToo);
    }

    public void inherit(SubChainChangeMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        planningEntityClass = ConfigUtils.inheritOverwritableProperty(planningEntityClass,
                inheritedConfig.getPlanningEntityClass());
        if (subChainSelectorConfig == null) {
            subChainSelectorConfig = inheritedConfig.getSubChainSelectorConfig();
        } else if (inheritedConfig.getSubChainSelectorConfig() != null) {
            subChainSelectorConfig.inherit(inheritedConfig.getSubChainSelectorConfig());
        }
        if (valueSelectorConfig == null) {
            valueSelectorConfig = inheritedConfig.getValueSelectorConfig();
        } else if (inheritedConfig.getValueSelectorConfig() != null) {
            valueSelectorConfig.inherit(inheritedConfig.getValueSelectorConfig());
        }
        selectReversingMoveToo = ConfigUtils.inheritOverwritableProperty(selectReversingMoveToo,
                inheritedConfig.getSelectReversingMoveToo());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + subChainSelectorConfig + ", " + valueSelectorConfig + ")";
    }

}

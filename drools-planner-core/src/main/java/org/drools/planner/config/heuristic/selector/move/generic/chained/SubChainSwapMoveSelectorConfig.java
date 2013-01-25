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

import java.util.Collection;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.heuristic.selector.common.SelectionOrder;
import org.drools.planner.config.heuristic.selector.move.MoveSelectorConfig;
import org.drools.planner.config.heuristic.selector.value.chained.SubChainSelectorConfig;
import org.drools.planner.config.util.ConfigUtils;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.heuristic.selector.move.generic.chained.SubChainSwapMoveSelector;
import org.drools.planner.core.heuristic.selector.value.chained.SubChainSelector;

@XStreamAlias("subChainSwapMoveSelector")
public class SubChainSwapMoveSelectorConfig extends MoveSelectorConfig {

    private Class<?> planningEntityClass = null;
    @XStreamAlias("subChainSelector")
    private SubChainSelectorConfig subChainSelectorConfig = new SubChainSelectorConfig();
    @XStreamAlias("secondarySubChainSelector")
    private SubChainSelectorConfig secondarySubChainSelectorConfig = null;

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

    public SubChainSelectorConfig getSecondarySubChainSelectorConfig() {
        return secondarySubChainSelectorConfig;
    }

    public void setSecondarySubChainSelectorConfig(SubChainSelectorConfig secondarySubChainSelectorConfig) {
        this.secondarySubChainSelectorConfig = secondarySubChainSelectorConfig;
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
        PlanningEntityDescriptor entityDescriptor = fetchEntityDescriptor(solutionDescriptor, planningEntityClass);
        SubChainSelector leftSubChainSelector = subChainSelectorConfig.buildSubChainSelector(environmentMode,
                solutionDescriptor, entityDescriptor,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        SubChainSelectorConfig rightSubChainSelectorConfig = secondarySubChainSelectorConfig == null
                ? subChainSelectorConfig : secondarySubChainSelectorConfig;
        SubChainSelector rightSubChainSelector = rightSubChainSelectorConfig.buildSubChainSelector(environmentMode,
                solutionDescriptor, entityDescriptor,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        return new SubChainSwapMoveSelector(leftSubChainSelector, rightSubChainSelector, randomSelection,
                selectReversingMoveToo == null ? true : selectReversingMoveToo);
    }

    public void inherit(SubChainSwapMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        planningEntityClass = ConfigUtils.inheritOverwritableProperty(planningEntityClass,
                inheritedConfig.getPlanningEntityClass());
        if (subChainSelectorConfig == null) {
            subChainSelectorConfig = inheritedConfig.getSubChainSelectorConfig();
        } else if (inheritedConfig.getSubChainSelectorConfig() != null) {
            subChainSelectorConfig.inherit(inheritedConfig.getSubChainSelectorConfig());
        }
        if (secondarySubChainSelectorConfig == null) {
            secondarySubChainSelectorConfig = inheritedConfig.getSecondarySubChainSelectorConfig();
        } else if (inheritedConfig.getSecondarySubChainSelectorConfig() != null) {
            secondarySubChainSelectorConfig.inherit(inheritedConfig.getSecondarySubChainSelectorConfig());
        }
        selectReversingMoveToo = ConfigUtils.inheritOverwritableProperty(selectReversingMoveToo,
                inheritedConfig.getSelectReversingMoveToo());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + subChainSelectorConfig
                + (secondarySubChainSelectorConfig == null ? "" : ", " + secondarySubChainSelectorConfig) + ")";
    }

}

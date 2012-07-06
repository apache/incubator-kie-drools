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

package org.drools.planner.config.heuristic.selector.move.generic;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.heuristic.selector.common.SelectionOrder;
import org.drools.planner.config.heuristic.selector.entity.EntitySelectorConfig;
import org.drools.planner.config.heuristic.selector.move.MoveSelectorConfig;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.heuristic.selector.move.generic.SwapMoveSelector;

@XStreamAlias("swapMoveSelector")
public class SwapMoveSelectorConfig extends MoveSelectorConfig {

    @XStreamAlias("leftEntitySelector")
    private EntitySelectorConfig leftEntitySelectorConfig = new EntitySelectorConfig();
    @XStreamAlias("rightEntitySelector")
    private EntitySelectorConfig rightEntitySelectorConfig = new EntitySelectorConfig();

    public EntitySelectorConfig getLeftEntitySelectorConfig() {
        return leftEntitySelectorConfig;
    }

    public void setLeftEntitySelectorConfig(EntitySelectorConfig leftEntitySelectorConfig) {
        this.leftEntitySelectorConfig = leftEntitySelectorConfig;
    }

    public EntitySelectorConfig getRightEntitySelectorConfig() {
        return rightEntitySelectorConfig;
    }

    public void setRightEntitySelectorConfig(EntitySelectorConfig rightEntitySelectorConfig) {
        this.rightEntitySelectorConfig = rightEntitySelectorConfig;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public MoveSelector buildBaseMoveSelector(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            SelectionOrder resolvedSelectionOrder, SelectionCacheType resolvedCacheType) {

        EntitySelector leftEntitySelector = leftEntitySelectorConfig.buildEntitySelector(
                environmentMode, solutionDescriptor,
                resolvedSelectionOrder, resolvedCacheType);
        EntitySelector rightEntitySelector = rightEntitySelectorConfig.buildEntitySelector(
                environmentMode, solutionDescriptor,
                resolvedSelectionOrder, resolvedCacheType);
        return new SwapMoveSelector(leftEntitySelector, rightEntitySelector,
                resolvedSelectionOrder == SelectionOrder.RANDOM);
    }

    public void inherit(SwapMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (leftEntitySelectorConfig == null) {
            leftEntitySelectorConfig = inheritedConfig.getLeftEntitySelectorConfig();
        } else if (inheritedConfig.getLeftEntitySelectorConfig() != null) {
            leftEntitySelectorConfig.inherit(inheritedConfig.getLeftEntitySelectorConfig());
        }
        if (rightEntitySelectorConfig == null) {
            rightEntitySelectorConfig = inheritedConfig.getRightEntitySelectorConfig();
        } else if (inheritedConfig.getRightEntitySelectorConfig() != null) {
            rightEntitySelectorConfig.inherit(inheritedConfig.getRightEntitySelectorConfig());
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + leftEntitySelectorConfig + ", " + rightEntitySelectorConfig + ")";
    }

}

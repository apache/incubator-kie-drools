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

import java.util.Collection;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.heuristic.selector.common.SelectionOrder;
import org.drools.planner.config.heuristic.selector.entity.EntitySelectorConfig;
import org.drools.planner.config.heuristic.selector.move.MoveSelectorConfig;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.heuristic.selector.move.generic.SwapMoveSelector;

@XStreamAlias("swapMoveSelector")
public class SwapMoveSelectorConfig extends MoveSelectorConfig {

    @XStreamAlias("entitySelector")
    private EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
    @XStreamAlias("secondaryEntitySelector")
    private EntitySelectorConfig secondaryEntitySelectorConfig = null;

    public EntitySelectorConfig getEntitySelectorConfig() {
        return entitySelectorConfig;
    }

    public void setEntitySelectorConfig(EntitySelectorConfig entitySelectorConfig) {
        this.entitySelectorConfig = entitySelectorConfig;
    }

    public EntitySelectorConfig getSecondaryEntitySelectorConfig() {
        return secondaryEntitySelectorConfig;
    }

    public void setSecondaryEntitySelectorConfig(EntitySelectorConfig secondaryEntitySelectorConfig) {
        this.secondaryEntitySelectorConfig = secondaryEntitySelectorConfig;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public MoveSelector buildBaseMoveSelector(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            SelectionOrder resolvedSelectionOrder, SelectionCacheType resolvedCacheType) {
        EntitySelector leftEntitySelector = entitySelectorConfig.buildEntitySelector(
                environmentMode, solutionDescriptor, resolvedSelectionOrder, resolvedCacheType);
        EntitySelectorConfig rightEntitySelectorConfig = secondaryEntitySelectorConfig == null
                ? entitySelectorConfig : secondaryEntitySelectorConfig;
        EntitySelector rightEntitySelector = rightEntitySelectorConfig.buildEntitySelector(
                        environmentMode, solutionDescriptor, resolvedSelectionOrder, resolvedCacheType);
        Collection<PlanningVariableDescriptor> variableDescriptors = leftEntitySelector.getEntityDescriptor()
                .getPlanningVariableDescriptors();
        return new SwapMoveSelector(leftEntitySelector, rightEntitySelector, variableDescriptors,
                resolvedSelectionOrder == SelectionOrder.RANDOM);
    }

    public void inherit(SwapMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (entitySelectorConfig == null) {
            entitySelectorConfig = inheritedConfig.getEntitySelectorConfig();
        } else if (inheritedConfig.getEntitySelectorConfig() != null) {
            entitySelectorConfig.inherit(inheritedConfig.getEntitySelectorConfig());
        }
        if (secondaryEntitySelectorConfig == null) {
            secondaryEntitySelectorConfig = inheritedConfig.getSecondaryEntitySelectorConfig();
        } else if (inheritedConfig.getSecondaryEntitySelectorConfig() != null) {
            secondaryEntitySelectorConfig.inherit(inheritedConfig.getSecondaryEntitySelectorConfig());
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelectorConfig
                + (secondaryEntitySelectorConfig == null ? "" : ", " + secondaryEntitySelectorConfig) + ")";
    }

}

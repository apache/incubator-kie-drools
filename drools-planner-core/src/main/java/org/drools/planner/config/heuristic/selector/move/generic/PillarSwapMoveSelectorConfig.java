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
import org.drools.planner.config.heuristic.selector.entity.pillar.PillarSelectorConfig;
import org.drools.planner.config.heuristic.selector.move.MoveSelectorConfig;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.entity.pillar.PillarSelector;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.heuristic.selector.move.generic.PillarSwapMoveSelector;

@XStreamAlias("pillarSwapMoveSelector")
public class PillarSwapMoveSelectorConfig extends MoveSelectorConfig {

    @XStreamAlias("pillarSelector")
    private PillarSelectorConfig pillarSelectorConfig = new PillarSelectorConfig();
    @XStreamAlias("secondaryPillarSelector")
    private PillarSelectorConfig secondaryPillarSelectorConfig = null;

    public PillarSelectorConfig getPillarSelectorConfig() {
        return pillarSelectorConfig;
    }

    public void setPillarSelectorConfig(PillarSelectorConfig pillarSelectorConfig) {
        this.pillarSelectorConfig = pillarSelectorConfig;
    }

    public PillarSelectorConfig getSecondaryPillarSelectorConfig() {
        return secondaryPillarSelectorConfig;
    }

    public void setSecondaryPillarSelectorConfig(PillarSelectorConfig secondaryPillarSelectorConfig) {
        this.secondaryPillarSelectorConfig = secondaryPillarSelectorConfig;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public MoveSelector buildBaseMoveSelector(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            SelectionOrder resolvedSelectionOrder, SelectionCacheType minimumCacheType) {
        PillarSelector leftPillarSelector = pillarSelectorConfig.buildPillarSelector(
                environmentMode, solutionDescriptor, resolvedSelectionOrder, minimumCacheType);
        PillarSelectorConfig rightPillarSelectorConfig = secondaryPillarSelectorConfig == null
                ? pillarSelectorConfig : secondaryPillarSelectorConfig;
        PillarSelector rightPillarSelector = rightPillarSelectorConfig.buildPillarSelector(
                environmentMode, solutionDescriptor, resolvedSelectionOrder, minimumCacheType);
        Collection<PlanningVariableDescriptor> variableDescriptors = leftPillarSelector.getEntityDescriptor()
                .getPlanningVariableDescriptors();
        return new PillarSwapMoveSelector(leftPillarSelector, rightPillarSelector, variableDescriptors,
                resolvedSelectionOrder == SelectionOrder.RANDOM);
    }

    public void inherit(PillarSwapMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (pillarSelectorConfig == null) {
            pillarSelectorConfig = inheritedConfig.getPillarSelectorConfig();
        } else if (inheritedConfig.getPillarSelectorConfig() != null) {
            pillarSelectorConfig.inherit(inheritedConfig.getPillarSelectorConfig());
        }
        if (secondaryPillarSelectorConfig == null) {
            secondaryPillarSelectorConfig = inheritedConfig.getSecondaryPillarSelectorConfig();
        } else if (inheritedConfig.getSecondaryPillarSelectorConfig() != null) {
            secondaryPillarSelectorConfig.inherit(inheritedConfig.getSecondaryPillarSelectorConfig());
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + pillarSelectorConfig
                + (secondaryPillarSelectorConfig == null ? "" : ", " + secondaryPillarSelectorConfig) + ")";
    }

}

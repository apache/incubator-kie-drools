/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.core.config.heuristic.selector.move.generic.chained;

import java.util.Collection;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.TwoOptMoveSelector;

@XStreamAlias("twoOptMoveSelector")
public class TwoOptMoveSelectorConfig extends MoveSelectorConfig {

    @XStreamAlias("entitySelector")
    private EntitySelectorConfig entitySelectorConfig = null;
    @XStreamAlias("secondaryEntitySelector")
    private EntitySelectorConfig secondaryEntitySelectorConfig = null;

    private String variableName = null;

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

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        EntitySelectorConfig entitySelectorConfig_ = entitySelectorConfig == null ? new EntitySelectorConfig()
                : entitySelectorConfig;
        EntitySelector leftEntitySelector = entitySelectorConfig_.buildEntitySelector(
                configPolicy,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        EntitySelectorConfig rightEntitySelectorConfig = secondaryEntitySelectorConfig == null
                ? entitySelectorConfig_ : secondaryEntitySelectorConfig;
        EntitySelector rightEntitySelector = rightEntitySelectorConfig.buildEntitySelector(
                configPolicy,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        GenuineVariableDescriptor variableDescriptor = deduceVariableDescriptor(
                leftEntitySelector.getEntityDescriptor(), variableName);
        return new TwoOptMoveSelector(leftEntitySelector, rightEntitySelector, variableDescriptor,
                randomSelection);
    }

    public void inherit(TwoOptMoveSelectorConfig inheritedConfig) {
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
        variableName = ConfigUtils.inheritOverwritableProperty(variableName, inheritedConfig.getVariableName());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelectorConfig
                + (secondaryEntitySelectorConfig == null ? "" : ", " + secondaryEntitySelectorConfig) + ")";
    }

}

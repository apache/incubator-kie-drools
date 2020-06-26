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

package org.optaplanner.core.config.heuristic.selector.move.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("changeMoveSelector")
public class ChangeMoveSelectorConfig extends MoveSelectorConfig<ChangeMoveSelectorConfig> {

    public static final String XML_ELEMENT_NAME = "changeMoveSelector";

    @XmlElement(name = "entitySelector")
    @XStreamAlias("entitySelector")
    private EntitySelectorConfig entitySelectorConfig = null;

    @XmlElement(name = "valueSelector")
    @XStreamAlias("valueSelector")
    private ValueSelectorConfig valueSelectorConfig = null;

    public ChangeMoveSelectorConfig() {
    }

    public EntitySelectorConfig getEntitySelectorConfig() {
        return entitySelectorConfig;
    }

    public void setEntitySelectorConfig(EntitySelectorConfig entitySelectorConfig) {
        this.entitySelectorConfig = entitySelectorConfig;
    }

    public ValueSelectorConfig getValueSelectorConfig() {
        return valueSelectorConfig;
    }

    public void setValueSelectorConfig(ValueSelectorConfig valueSelectorConfig) {
        this.valueSelectorConfig = valueSelectorConfig;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        if (entitySelectorConfig == null) {
            throw new IllegalStateException("The entitySelectorConfig (" + entitySelectorConfig
                    + ") should haven been initialized during unfolding.");
        }
        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(configPolicy,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        if (valueSelectorConfig == null) {
            throw new IllegalStateException("The valueSelectorConfig (" + valueSelectorConfig
                    + ") should haven been initialized during unfolding.");
        }
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(configPolicy,
                entitySelector.getEntityDescriptor(),
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        return new ChangeMoveSelector(entitySelector, valueSelector, randomSelection);
    }

    @Override
    protected MoveSelectorConfig buildUnfoldedMoveSelectorConfig(HeuristicConfigPolicy configPolicy) {
        Collection<EntityDescriptor> entityDescriptors;
        EntityDescriptor onlyEntityDescriptor = entitySelectorConfig == null ? null
                : entitySelectorConfig.extractEntityDescriptor(configPolicy);
        if (onlyEntityDescriptor != null) {
            entityDescriptors = Collections.singletonList(onlyEntityDescriptor);
        } else {
            entityDescriptors = configPolicy.getSolutionDescriptor().getGenuineEntityDescriptors();
        }
        List<GenuineVariableDescriptor> variableDescriptorList = new ArrayList<>();
        for (EntityDescriptor entityDescriptor : entityDescriptors) {
            GenuineVariableDescriptor onlyVariableDescriptor = valueSelectorConfig == null ? null
                    : valueSelectorConfig.extractVariableDescriptor(configPolicy, entityDescriptor);
            if (onlyVariableDescriptor != null) {
                if (onlyEntityDescriptor != null) {
                    // No need for unfolding or deducing
                    return null;
                }
                variableDescriptorList.add(onlyVariableDescriptor);
            } else {
                variableDescriptorList.addAll(entityDescriptor.getGenuineVariableDescriptors());
            }
        }
        return buildUnfoldedMoveSelectorConfig(variableDescriptorList);
    }

    protected MoveSelectorConfig buildUnfoldedMoveSelectorConfig(
            List<GenuineVariableDescriptor> variableDescriptorList) {
        List<MoveSelectorConfig> moveSelectorConfigList = new ArrayList<>(variableDescriptorList.size());
        for (GenuineVariableDescriptor variableDescriptor : variableDescriptorList) {
            // No childMoveSelectorConfig.inherit() because of unfoldedMoveSelectorConfig.inheritFolded()
            ChangeMoveSelectorConfig childMoveSelectorConfig = new ChangeMoveSelectorConfig();
            // Different EntitySelector per child because it is a union
            EntitySelectorConfig childEntitySelectorConfig = new EntitySelectorConfig(entitySelectorConfig);
            if (childEntitySelectorConfig.getMimicSelectorRef() == null) {
                childEntitySelectorConfig.setEntityClass(variableDescriptor.getEntityDescriptor().getEntityClass());
            }
            childMoveSelectorConfig.setEntitySelectorConfig(childEntitySelectorConfig);
            ValueSelectorConfig childValueSelectorConfig = new ValueSelectorConfig(valueSelectorConfig);
            if (childValueSelectorConfig.getMimicSelectorRef() == null) {
                childValueSelectorConfig.setVariableName(variableDescriptor.getVariableName());
            }
            childMoveSelectorConfig.setValueSelectorConfig(childValueSelectorConfig);
            moveSelectorConfigList.add(childMoveSelectorConfig);
        }

        MoveSelectorConfig unfoldedMoveSelectorConfig;
        if (moveSelectorConfigList.size() == 1) {
            unfoldedMoveSelectorConfig = moveSelectorConfigList.get(0);
        } else {
            unfoldedMoveSelectorConfig = new UnionMoveSelectorConfig(moveSelectorConfigList);
        }
        unfoldedMoveSelectorConfig.inheritFolded(this);
        return unfoldedMoveSelectorConfig;
    }

    @Override
    public ChangeMoveSelectorConfig inherit(ChangeMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        entitySelectorConfig = ConfigUtils.inheritConfig(entitySelectorConfig, inheritedConfig.getEntitySelectorConfig());
        valueSelectorConfig = ConfigUtils.inheritConfig(valueSelectorConfig, inheritedConfig.getValueSelectorConfig());
        return this;
    }

    @Override
    public ChangeMoveSelectorConfig copyConfig() {
        return new ChangeMoveSelectorConfig().inherit(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelectorConfig + ", " + valueSelectorConfig + ")";
    }

}

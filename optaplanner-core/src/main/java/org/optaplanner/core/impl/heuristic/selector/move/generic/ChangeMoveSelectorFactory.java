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

package org.optaplanner.core.impl.heuristic.selector.move.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;

public class ChangeMoveSelectorFactory extends AbstractMoveSelectorFactory<ChangeMoveSelectorConfig> {

    public ChangeMoveSelectorFactory(ChangeMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    public MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        if (moveSelectorConfig.getEntitySelectorConfig() == null) {
            throw new IllegalStateException("The entitySelectorConfig (" + moveSelectorConfig.getEntitySelectorConfig()
                    + ") should haven been initialized during unfolding.");
        }
        EntitySelector entitySelector = moveSelectorConfig.getEntitySelectorConfig().buildEntitySelector(configPolicy,
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        if (moveSelectorConfig.getValueSelectorConfig() == null) {
            throw new IllegalStateException("The valueSelectorConfig (" + moveSelectorConfig.getValueSelectorConfig()
                    + ") should haven been initialized during unfolding.");
        }
        ValueSelector valueSelector = moveSelectorConfig.getValueSelectorConfig().buildValueSelector(configPolicy,
                entitySelector.getEntityDescriptor(),
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        return new ChangeMoveSelector(entitySelector, valueSelector, randomSelection);
    }

    @Override
    protected MoveSelectorConfig buildUnfoldedMoveSelectorConfig(HeuristicConfigPolicy configPolicy) {
        Collection<EntityDescriptor> entityDescriptors;
        EntityDescriptor onlyEntityDescriptor = moveSelectorConfig.getEntitySelectorConfig() == null ? null
                : moveSelectorConfig.getEntitySelectorConfig().extractEntityDescriptor(configPolicy);
        if (onlyEntityDescriptor != null) {
            entityDescriptors = Collections.singletonList(onlyEntityDescriptor);
        } else {
            entityDescriptors = configPolicy.getSolutionDescriptor().getGenuineEntityDescriptors();
        }
        List<GenuineVariableDescriptor> variableDescriptorList = new ArrayList<>();
        for (EntityDescriptor entityDescriptor : entityDescriptors) {
            GenuineVariableDescriptor onlyVariableDescriptor = moveSelectorConfig.getValueSelectorConfig() == null ? null
                    : moveSelectorConfig.getValueSelectorConfig().extractVariableDescriptor(configPolicy, entityDescriptor);
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

    protected MoveSelectorConfig buildUnfoldedMoveSelectorConfig(List<GenuineVariableDescriptor> variableDescriptorList) {
        List<MoveSelectorConfig> moveSelectorConfigList = new ArrayList<>(variableDescriptorList.size());
        for (GenuineVariableDescriptor variableDescriptor : variableDescriptorList) {
            // No childMoveSelectorConfig.inherit() because of unfoldedMoveSelectorConfig.inheritFolded()
            ChangeMoveSelectorConfig childMoveSelectorConfig = new ChangeMoveSelectorConfig();
            // Different EntitySelector per child because it is a union
            EntitySelectorConfig childEntitySelectorConfig =
                    new EntitySelectorConfig(moveSelectorConfig.getEntitySelectorConfig());
            if (childEntitySelectorConfig.getMimicSelectorRef() == null) {
                childEntitySelectorConfig.setEntityClass(variableDescriptor.getEntityDescriptor().getEntityClass());
            }
            childMoveSelectorConfig.setEntitySelectorConfig(childEntitySelectorConfig);
            ValueSelectorConfig childValueSelectorConfig = new ValueSelectorConfig(moveSelectorConfig.getValueSelectorConfig());
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
        unfoldedMoveSelectorConfig.inheritFolded(moveSelectorConfig);
        return unfoldedMoveSelectorConfig;
    }
}

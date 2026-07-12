/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.list.DestinationSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.ListChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.list.DestinationSelector;
import org.optaplanner.core.impl.heuristic.selector.list.DestinationSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelectorFactory;

public class ListChangeMoveSelectorFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, ListChangeMoveSelectorConfig> {

    public ListChangeMoveSelectorFactory(ListChangeMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    protected MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        checkUnfolded("valueSelectorConfig", config.getValueSelectorConfig());
        checkUnfolded("destinationSelectorConfig", config.getDestinationSelectorConfig());
        checkUnfolded("destinationEntitySelectorConfig", config.getDestinationSelectorConfig().getEntitySelectorConfig());
        checkUnfolded("destinationValueSelectorConfig", config.getDestinationSelectorConfig().getValueSelectorConfig());

        SelectionOrder selectionOrder = SelectionOrder.fromRandomSelectionBoolean(randomSelection);

        EntityDescriptor<Solution_> entityDescriptor = EntitySelectorFactory
                .<Solution_> create(config.getDestinationSelectorConfig().getEntitySelectorConfig())
                .extractEntityDescriptor(configPolicy);

        ValueSelector<Solution_> sourceValueSelector = ValueSelectorFactory
                .<Solution_> create(config.getValueSelectorConfig())
                .buildValueSelector(configPolicy, entityDescriptor, minimumCacheType, selectionOrder);

        if (!(sourceValueSelector instanceof EntityIndependentValueSelector)) {
            throw new IllegalArgumentException("The listChangeMoveSelector (" + config
                    + ") for a list variable needs to be based on an "
                    + EntityIndependentValueSelector.class.getSimpleName() + " (" + sourceValueSelector + ")."
                    + " Check your valueSelectorConfig.");
        }

        DestinationSelector<Solution_> destinationSelector = DestinationSelectorFactory
                .<Solution_> create(config.getDestinationSelectorConfig())
                .buildDestinationSelector(configPolicy, minimumCacheType, randomSelection);

        return new ListChangeMoveSelector<>(
                (EntityIndependentValueSelector<Solution_>) sourceValueSelector,
                destinationSelector,
                randomSelection);
    }

    @Override
    protected MoveSelectorConfig<?> buildUnfoldedMoveSelectorConfig(HeuristicConfigPolicy<Solution_> configPolicy) {
        Collection<EntityDescriptor<Solution_>> entityDescriptors;
        EntityDescriptor<Solution_> onlyEntityDescriptor = config.getDestinationSelectorConfig() == null ? null
                : config.getDestinationSelectorConfig().getEntitySelectorConfig() == null ? null
                        : EntitySelectorFactory
                                .<Solution_> create(config.getDestinationSelectorConfig().getEntitySelectorConfig())
                                .extractEntityDescriptor(configPolicy);
        if (onlyEntityDescriptor != null) {
            entityDescriptors = Collections.singletonList(onlyEntityDescriptor);
        } else {
            entityDescriptors = configPolicy.getSolutionDescriptor().getGenuineEntityDescriptors();
        }
        if (entityDescriptors.size() > 1) {
            throw new IllegalArgumentException("The listChangeMoveSelector (" + config
                    + ") cannot unfold when there are multiple entities (" + entityDescriptors + ")."
                    + " Please use one listChangeMoveSelector per each planning list variable.");
        }
        EntityDescriptor<Solution_> entityDescriptor = entityDescriptors.iterator().next();

        List<ListVariableDescriptor<Solution_>> variableDescriptorList = new ArrayList<>();
        GenuineVariableDescriptor<Solution_> onlyVariableDescriptor = config.getValueSelectorConfig() == null ? null
                : ValueSelectorFactory.<Solution_> create(config.getValueSelectorConfig())
                        .extractVariableDescriptor(configPolicy, entityDescriptor);
        GenuineVariableDescriptor<Solution_> onlyDestinationVariableDescriptor =
                config.getDestinationSelectorConfig() == null ? null
                        : config.getDestinationSelectorConfig().getValueSelectorConfig() == null ? null
                                : ValueSelectorFactory
                                        .<Solution_> create(config.getDestinationSelectorConfig().getValueSelectorConfig())
                                        .extractVariableDescriptor(configPolicy, entityDescriptor);
        if (onlyVariableDescriptor != null && onlyDestinationVariableDescriptor != null) {
            if (!onlyVariableDescriptor.isListVariable()) {
                throw new IllegalArgumentException("The listChangeMoveSelector (" + config
                        + ") is configured to use a planning variable (" + onlyVariableDescriptor
                        + "), which is not a planning list variable."
                        + " Either fix your annotations and use a @" + PlanningListVariable.class.getSimpleName()
                        + " on the variable to make it work with listChangeMoveSelector"
                        + " or use a changeMoveSelector instead.");
            }
            if (!onlyDestinationVariableDescriptor.isListVariable()) {
                throw new IllegalArgumentException("The destinationSelector (" + config.getDestinationSelectorConfig()
                        + ") is configured to use a planning variable (" + onlyDestinationVariableDescriptor
                        + "), which is not a planning list variable.");
            }
            if (onlyVariableDescriptor != onlyDestinationVariableDescriptor) {
                throw new IllegalArgumentException("The listChangeMoveSelector's valueSelector ("
                        + config.getValueSelectorConfig()
                        + ") and destinationSelector's valueSelector ("
                        + config.getDestinationSelectorConfig().getValueSelectorConfig()
                        + ") must be configured for the same planning variable.");
            }
            if (onlyEntityDescriptor != null) {
                // No need for unfolding or deducing
                return null;
            }
            variableDescriptorList.add((ListVariableDescriptor<Solution_>) onlyVariableDescriptor);
        } else {
            variableDescriptorList.addAll(
                    entityDescriptor.getGenuineVariableDescriptorList().stream()
                            .filter(GenuineVariableDescriptor::isListVariable)
                            .map(variableDescriptor -> ((ListVariableDescriptor<Solution_>) variableDescriptor))
                            .collect(Collectors.toList()));
        }
        if (variableDescriptorList.isEmpty()) {
            throw new IllegalArgumentException("The listChangeMoveSelector (" + config
                    + ") cannot unfold because there are no planning list variables.");
        }
        if (variableDescriptorList.size() > 1) {
            throw new IllegalArgumentException("The listChangeMoveSelector (" + config
                    + ") cannot unfold because there are multiple planning list variables.");
        }
        ListChangeMoveSelectorConfig listChangeMoveSelectorConfig = buildChildMoveSelectorConfig(
                variableDescriptorList.get(0),
                config.getValueSelectorConfig(),
                config.getDestinationSelectorConfig());
        listChangeMoveSelectorConfig.inheritFolded(config);
        return listChangeMoveSelectorConfig;
    }

    public static ListChangeMoveSelectorConfig buildChildMoveSelectorConfig(
            ListVariableDescriptor<?> variableDescriptor,
            ValueSelectorConfig inheritedValueSelectorConfig,
            DestinationSelectorConfig inheritedDestinationSelectorConfig) {

        ValueSelectorConfig childValueSelectorConfig = new ValueSelectorConfig(inheritedValueSelectorConfig);
        if (childValueSelectorConfig.getMimicSelectorRef() == null) {
            childValueSelectorConfig.setVariableName(variableDescriptor.getVariableName());
        }

        return new ListChangeMoveSelectorConfig()
                .withValueSelectorConfig(childValueSelectorConfig)
                .withDestinationSelectorConfig(new DestinationSelectorConfig(inheritedDestinationSelectorConfig)
                        .withEntitySelectorConfig(
                                Optional.ofNullable(inheritedDestinationSelectorConfig)
                                        .map(DestinationSelectorConfig::getEntitySelectorConfig)
                                        .map(EntitySelectorConfig::new) // use copy constructor if inherited not null
                                        .orElseGet(EntitySelectorConfig::new) // otherwise create new instance
                                        // override entity class (destination entity selector is never replaying)
                                        .withEntityClass(variableDescriptor.getEntityDescriptor().getEntityClass()))
                        .withValueSelectorConfig(
                                Optional.ofNullable(inheritedDestinationSelectorConfig)
                                        .map(DestinationSelectorConfig::getValueSelectorConfig)
                                        .map(ValueSelectorConfig::new) // use copy constructor if inherited not null
                                        .orElseGet(ValueSelectorConfig::new) // otherwise create new instance
                                        // override variable name (destination value selector is never replaying)
                                        .withVariableName(variableDescriptor.getVariableName())));
    }
}

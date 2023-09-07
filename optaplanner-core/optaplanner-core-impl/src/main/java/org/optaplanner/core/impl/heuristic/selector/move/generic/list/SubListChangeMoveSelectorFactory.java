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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.list.DestinationSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.list.SubListSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.SubListChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.list.DestinationSelector;
import org.optaplanner.core.impl.heuristic.selector.list.DestinationSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.list.SubListSelector;
import org.optaplanner.core.impl.heuristic.selector.list.SubListSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelectorFactory;

public class SubListChangeMoveSelectorFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, SubListChangeMoveSelectorConfig> {

    public SubListChangeMoveSelectorFactory(SubListChangeMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    protected MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        checkUnfolded("subListSelectorConfig", config.getSubListSelectorConfig());
        checkUnfolded("destinationSelectorConfig", config.getDestinationSelectorConfig());
        if (!randomSelection) {
            throw new IllegalArgumentException("The subListChangeMoveSelector (" + config
                    + ") only supports random selection order.");
        }

        SelectionOrder selectionOrder = SelectionOrder.fromRandomSelectionBoolean(randomSelection);

        EntitySelector<Solution_> entitySelector = EntitySelectorFactory
                .<Solution_> create(config.getDestinationSelectorConfig().getEntitySelectorConfig())
                .buildEntitySelector(configPolicy, minimumCacheType, selectionOrder);

        SubListSelector<Solution_> subListSelector = SubListSelectorFactory
                .<Solution_> create(config.getSubListSelectorConfig())
                .buildSubListSelector(configPolicy, entitySelector, minimumCacheType, selectionOrder);

        DestinationSelector<Solution_> destinationSelector = DestinationSelectorFactory
                .<Solution_> create(config.getDestinationSelectorConfig())
                .buildDestinationSelector(configPolicy, minimumCacheType, randomSelection);

        boolean selectReversingMoveToo = Objects.requireNonNullElse(config.getSelectReversingMoveToo(), true);

        return new RandomSubListChangeMoveSelector<>(subListSelector, destinationSelector, selectReversingMoveToo);
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
            throw new IllegalArgumentException("The subListChangeMoveSelector (" + config
                    + ") cannot unfold when there are multiple entities (" + entityDescriptors + ")."
                    + " Please use one subListChangeMoveSelector per each planning list variable.");
        }
        EntityDescriptor<Solution_> entityDescriptor = entityDescriptors.iterator().next();

        List<ListVariableDescriptor<Solution_>> variableDescriptorList = new ArrayList<>();
        GenuineVariableDescriptor<Solution_> onlySubListVariableDescriptor =
                config.getSubListSelectorConfig() == null ? null
                        : config.getSubListSelectorConfig().getValueSelectorConfig() == null ? null
                                : ValueSelectorFactory
                                        .<Solution_> create(config.getSubListSelectorConfig().getValueSelectorConfig())
                                        .extractVariableDescriptor(configPolicy, entityDescriptor);
        GenuineVariableDescriptor<Solution_> onlyDestinationVariableDescriptor =
                config.getDestinationSelectorConfig() == null ? null
                        : config.getDestinationSelectorConfig().getValueSelectorConfig() == null ? null
                                : ValueSelectorFactory
                                        .<Solution_> create(config.getDestinationSelectorConfig().getValueSelectorConfig())
                                        .extractVariableDescriptor(configPolicy, entityDescriptor);
        if (onlySubListVariableDescriptor != null && onlyDestinationVariableDescriptor != null) {
            if (!onlySubListVariableDescriptor.isListVariable()) {
                throw new IllegalArgumentException("The subListChangeMoveSelector (" + config
                        + ") is configured to use a planning variable (" + onlySubListVariableDescriptor
                        + "), which is not a planning list variable.");
            }
            if (!onlyDestinationVariableDescriptor.isListVariable()) {
                throw new IllegalArgumentException("The subListChangeMoveSelector (" + config
                        + ") is configured to use a planning variable (" + onlyDestinationVariableDescriptor
                        + "), which is not a planning list variable.");
            }
            if (onlySubListVariableDescriptor != onlyDestinationVariableDescriptor) {
                throw new IllegalArgumentException("The subListSelector's valueSelector ("
                        + config.getSubListSelectorConfig().getValueSelectorConfig()
                        + ") and destinationSelector's valueSelector ("
                        + config.getDestinationSelectorConfig().getValueSelectorConfig()
                        + ") must be configured for the same planning variable.");
            }
            if (onlyEntityDescriptor != null) {
                // No need for unfolding or deducing
                return null;
            }
            variableDescriptorList.add((ListVariableDescriptor<Solution_>) onlySubListVariableDescriptor);
        } else {
            variableDescriptorList.addAll(
                    entityDescriptor.getGenuineVariableDescriptorList().stream()
                            .filter(GenuineVariableDescriptor::isListVariable)
                            .map(variableDescriptor -> ((ListVariableDescriptor<Solution_>) variableDescriptor))
                            .collect(Collectors.toList()));
        }
        if (variableDescriptorList.isEmpty()) {
            throw new IllegalArgumentException("The subListChangeMoveSelector (" + config
                    + ") cannot unfold because there are no planning list variables.");
        }
        if (variableDescriptorList.size() > 1) {
            throw new IllegalArgumentException("The subListChangeMoveSelector (" + config
                    + ") cannot unfold because there are multiple planning list variables.");
        }
        return buildChildMoveSelectorConfig(variableDescriptorList.get(0));
    }

    private SubListChangeMoveSelectorConfig buildChildMoveSelectorConfig(ListVariableDescriptor<?> variableDescriptor) {
        SubListChangeMoveSelectorConfig subListChangeMoveSelectorConfig = config.copyConfig()
                .withSubListSelectorConfig(new SubListSelectorConfig(config.getSubListSelectorConfig())
                        .withValueSelectorConfig(Optional.ofNullable(config.getSubListSelectorConfig())
                                .map(SubListSelectorConfig::getValueSelectorConfig)
                                .map(ValueSelectorConfig::new) // use copy constructor if inherited not null
                                .orElseGet(ValueSelectorConfig::new)))
                .withDestinationSelectorConfig(new DestinationSelectorConfig(config.getDestinationSelectorConfig())
                        .withEntitySelectorConfig(
                                Optional.ofNullable(config.getDestinationSelectorConfig())
                                        .map(DestinationSelectorConfig::getEntitySelectorConfig)
                                        .map(EntitySelectorConfig::new) // use copy constructor if inherited not null
                                        .orElseGet(EntitySelectorConfig::new) // otherwise create new instance
                                        // override entity class (destination entity selector is never replaying)
                                        .withEntityClass(variableDescriptor.getEntityDescriptor().getEntityClass()))
                        .withValueSelectorConfig(
                                Optional.ofNullable(config.getDestinationSelectorConfig())
                                        .map(DestinationSelectorConfig::getValueSelectorConfig)
                                        .map(ValueSelectorConfig::new) // use copy constructor if inherited not null
                                        .orElseGet(ValueSelectorConfig::new) // otherwise create new instance
                                        // override variable name (destination value selector is never replaying)
                                        .withVariableName(variableDescriptor.getVariableName())));

        SubListSelectorConfig subListSelectorConfig = subListChangeMoveSelectorConfig.getSubListSelectorConfig();
        SubListConfigUtil.transferDeprecatedMinimumSubListSize(
                subListChangeMoveSelectorConfig,
                SubListChangeMoveSelectorConfig::getMinimumSubListSize,
                "subListSelector",
                subListSelectorConfig);
        SubListConfigUtil.transferDeprecatedMaximumSubListSize(
                subListChangeMoveSelectorConfig,
                SubListChangeMoveSelectorConfig::getMaximumSubListSize,
                "subListSelector",
                subListSelectorConfig);

        if (subListSelectorConfig.getMimicSelectorRef() == null) {
            subListSelectorConfig.getValueSelectorConfig().setVariableName(variableDescriptor.getVariableName());
        }

        return subListChangeMoveSelectorConfig;
    }
}

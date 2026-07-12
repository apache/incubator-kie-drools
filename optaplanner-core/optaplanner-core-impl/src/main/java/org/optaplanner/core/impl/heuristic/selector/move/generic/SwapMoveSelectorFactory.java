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

package org.optaplanner.core.impl.heuristic.selector.move.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.ListSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwapMoveSelectorFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, SwapMoveSelectorConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwapMoveSelectorFactory.class);

    public SwapMoveSelectorFactory(SwapMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    protected MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        EntitySelectorConfig entitySelectorConfig =
                Objects.requireNonNullElseGet(config.getEntitySelectorConfig(), EntitySelectorConfig::new);
        EntitySelectorConfig secondaryEntitySelectorConfig =
                Objects.requireNonNullElse(config.getSecondaryEntitySelectorConfig(), entitySelectorConfig);
        SelectionOrder selectionOrder = SelectionOrder.fromRandomSelectionBoolean(randomSelection);
        EntitySelector<Solution_> leftEntitySelector =
                EntitySelectorFactory.<Solution_> create(entitySelectorConfig)
                        .buildEntitySelector(configPolicy, minimumCacheType, selectionOrder);
        EntitySelector<Solution_> rightEntitySelector =
                EntitySelectorFactory.<Solution_> create(secondaryEntitySelectorConfig)
                        .buildEntitySelector(configPolicy, minimumCacheType, selectionOrder);
        EntityDescriptor<Solution_> entityDescriptor = leftEntitySelector.getEntityDescriptor();
        List<GenuineVariableDescriptor<Solution_>> variableDescriptorList =
                deduceVariableDescriptorList(entityDescriptor, config.getVariableNameIncludeList());

        return new SwapMoveSelector<>(leftEntitySelector, rightEntitySelector, variableDescriptorList,
                randomSelection);
    }

    @Override
    protected MoveSelectorConfig<?> buildUnfoldedMoveSelectorConfig(HeuristicConfigPolicy<Solution_> configPolicy) {
        EntityDescriptor<Solution_> onlyEntityDescriptor = config.getEntitySelectorConfig() == null ? null
                : EntitySelectorFactory.<Solution_> create(config.getEntitySelectorConfig())
                        .extractEntityDescriptor(configPolicy);
        if (config.getSecondaryEntitySelectorConfig() != null) {
            EntityDescriptor<Solution_> onlySecondaryEntityDescriptor =
                    EntitySelectorFactory.<Solution_> create(config.getSecondaryEntitySelectorConfig())
                            .extractEntityDescriptor(configPolicy);
            if (onlyEntityDescriptor != onlySecondaryEntityDescriptor) {
                throw new IllegalArgumentException("The entitySelector (" + config.getEntitySelectorConfig()
                        + ")'s entityClass (" + (onlyEntityDescriptor == null ? null : onlyEntityDescriptor.getEntityClass())
                        + ") and secondaryEntitySelectorConfig (" + config.getSecondaryEntitySelectorConfig()
                        + ")'s entityClass ("
                        + (onlySecondaryEntityDescriptor == null ? null : onlySecondaryEntityDescriptor.getEntityClass())
                        + ") must be the same entity class.");
            }
        }
        if (onlyEntityDescriptor != null) {
            List<GenuineVariableDescriptor<Solution_>> variableDescriptorList =
                    onlyEntityDescriptor.getGenuineVariableDescriptorList();
            // If there is a single list variable, unfold to list swap move selector config.
            if (variableDescriptorList.size() == 1 && variableDescriptorList.get(0).isListVariable()) {
                return buildListSwapMoveSelectorConfig(variableDescriptorList.get(0), true);
            }
            // Otherwise, make sure there is no list variable, because SwapMove is not supposed to swap list variables.
            failIfHasAnyGenuineListVariables(onlyEntityDescriptor);
            // No need for unfolding or deducing
            return null;
        }
        Collection<EntityDescriptor<Solution_>> entityDescriptors =
                configPolicy.getSolutionDescriptor().getGenuineEntityDescriptors();
        return buildUnfoldedMoveSelectorConfig(entityDescriptors);
    }

    protected MoveSelectorConfig<?>
            buildUnfoldedMoveSelectorConfig(Collection<EntityDescriptor<Solution_>> entityDescriptors) {
        List<MoveSelectorConfig> moveSelectorConfigList = new ArrayList<>(entityDescriptors.size());

        List<GenuineVariableDescriptor<Solution_>> variableDescriptorList =
                entityDescriptors.iterator().next().getGenuineVariableDescriptorList();

        // Only unfold into list swap move selector for the basic scenario with 1 entity and 1 list variable.
        if (entityDescriptors.size() == 1 && variableDescriptorList.size() == 1
                && variableDescriptorList.get(0).isListVariable()) {
            // No childMoveSelectorConfig.inherit() because of unfoldedMoveSelectorConfig.inheritFolded()
            ListSwapMoveSelectorConfig childMoveSelectorConfig =
                    buildListSwapMoveSelectorConfig(variableDescriptorList.get(0), false);
            moveSelectorConfigList.add(childMoveSelectorConfig);
        } else {
            // More complex scenarios do not support unfolding into list swap => fail fast if there is any list variable.
            for (EntityDescriptor<Solution_> entityDescriptor : entityDescriptors) {
                failIfHasAnyGenuineListVariables(entityDescriptor);

                // No childMoveSelectorConfig.inherit() because of unfoldedMoveSelectorConfig.inheritFolded()
                SwapMoveSelectorConfig childMoveSelectorConfig = new SwapMoveSelectorConfig();
                EntitySelectorConfig childEntitySelectorConfig = new EntitySelectorConfig(config.getEntitySelectorConfig());
                if (childEntitySelectorConfig.getMimicSelectorRef() == null) {
                    childEntitySelectorConfig.setEntityClass(entityDescriptor.getEntityClass());
                }
                childMoveSelectorConfig.setEntitySelectorConfig(childEntitySelectorConfig);
                if (config.getSecondaryEntitySelectorConfig() != null) {
                    EntitySelectorConfig childSecondaryEntitySelectorConfig =
                            new EntitySelectorConfig(config.getSecondaryEntitySelectorConfig());
                    if (childSecondaryEntitySelectorConfig.getMimicSelectorRef() == null) {
                        childSecondaryEntitySelectorConfig.setEntityClass(entityDescriptor.getEntityClass());
                    }
                    childMoveSelectorConfig.setSecondaryEntitySelectorConfig(childSecondaryEntitySelectorConfig);
                }
                childMoveSelectorConfig.setVariableNameIncludeList(config.getVariableNameIncludeList());
                moveSelectorConfigList.add(childMoveSelectorConfig);
            }
        }

        MoveSelectorConfig<?> unfoldedMoveSelectorConfig;
        if (moveSelectorConfigList.size() == 1) {
            unfoldedMoveSelectorConfig = moveSelectorConfigList.get(0);
        } else {
            unfoldedMoveSelectorConfig = new UnionMoveSelectorConfig(moveSelectorConfigList);
        }
        unfoldedMoveSelectorConfig.inheritFolded(config);
        return unfoldedMoveSelectorConfig;
    }

    private static void failIfHasAnyGenuineListVariables(EntityDescriptor<?> entityDescriptor) {
        if (entityDescriptor.hasAnyGenuineListVariables()) {
            throw new IllegalArgumentException(
                    "The variableDescriptorList (" + entityDescriptor.getGenuineVariableDescriptorList()
                            + ") has multiple variables and one or more of them is a @"
                            + PlanningListVariable.class.getSimpleName()
                            + ", which is currently not supported.");
        }

    }

    private ListSwapMoveSelectorConfig buildListSwapMoveSelectorConfig(VariableDescriptor<?> variableDescriptor,
            boolean inheritFoldedConfig) {
        LOGGER.warn("The swapMoveSelectorConfig ({}) is being used for a list variable."
                + " This was the only available option when the planning list variable was introduced."
                + " We are keeping this option through the 8.x release stream for backward compatibility reasons"
                + " but it will be removed in the next major release.\n"
                + "Please update your solver config to use ListSwapMoveSelectorConfig now.", config);
        ListSwapMoveSelectorConfig listSwapMoveSelectorConfig = new ListSwapMoveSelectorConfig();
        ValueSelectorConfig childValueSelectorConfig = new ValueSelectorConfig(
                new ValueSelectorConfig(variableDescriptor.getVariableName()));
        listSwapMoveSelectorConfig.setValueSelectorConfig(childValueSelectorConfig);
        if (inheritFoldedConfig) {
            listSwapMoveSelectorConfig.inheritFolded(config);
        }
        return listSwapMoveSelectorConfig;
    }
}

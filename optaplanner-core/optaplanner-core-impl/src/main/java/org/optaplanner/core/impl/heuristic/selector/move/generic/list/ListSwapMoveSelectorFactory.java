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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.ListSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelectorFactory;

public class ListSwapMoveSelectorFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, ListSwapMoveSelectorConfig> {

    public ListSwapMoveSelectorFactory(ListSwapMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    protected MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        ValueSelectorConfig valueSelectorConfig =
                Objects.requireNonNullElseGet(config.getValueSelectorConfig(), ValueSelectorConfig::new);
        ValueSelectorConfig secondaryValueSelectorConfig =
                Objects.requireNonNullElse(config.getSecondaryValueSelectorConfig(), valueSelectorConfig);
        SelectionOrder selectionOrder = SelectionOrder.fromRandomSelectionBoolean(randomSelection);
        EntityDescriptor<Solution_> entityDescriptor = getTheOnlyEntityDescriptor(configPolicy.getSolutionDescriptor());
        EntityIndependentValueSelector<Solution_> leftValueSelector = buildEntityIndependentValueSelector(configPolicy,
                entityDescriptor, valueSelectorConfig, minimumCacheType, selectionOrder);
        EntityIndependentValueSelector<Solution_> rightValueSelector = buildEntityIndependentValueSelector(configPolicy,
                entityDescriptor, secondaryValueSelectorConfig, minimumCacheType, selectionOrder);

        GenuineVariableDescriptor<Solution_> variableDescriptor = leftValueSelector.getVariableDescriptor();
        // This may be redundant but emphasizes that the ListSwapMove is not designed to swap elements
        // on multiple list variables, unlike the SwapMove, which swaps all (basic) variables between left and right entities.
        if (variableDescriptor != rightValueSelector.getVariableDescriptor()) {
            throw new IllegalStateException("Impossible state: the leftValueSelector (" + leftValueSelector
                    + ") and the rightValueSelector (" + rightValueSelector
                    + ") have different variable descriptors. This should have failed fast during config unfolding.");
        }

        return new ListSwapMoveSelector<>(
                leftValueSelector,
                rightValueSelector,
                randomSelection);
    }

    private EntityIndependentValueSelector<Solution_> buildEntityIndependentValueSelector(
            HeuristicConfigPolicy<Solution_> configPolicy,
            EntityDescriptor<Solution_> entityDescriptor,
            ValueSelectorConfig valueSelectorConfig,
            SelectionCacheType minimumCacheType,
            SelectionOrder inheritedSelectionOrder) {
        ValueSelector<Solution_> valueSelector = ValueSelectorFactory.<Solution_> create(valueSelectorConfig)
                .buildValueSelector(configPolicy, entityDescriptor, minimumCacheType, inheritedSelectionOrder);
        if (!(valueSelector instanceof EntityIndependentValueSelector)) {
            throw new IllegalArgumentException("The listSwapMoveSelector (" + config
                    + ") for a list variable needs to be based on an "
                    + EntityIndependentValueSelector.class.getSimpleName() + " (" + valueSelector + ")."
                    + " Check your valueSelectorConfig.");

        }
        return (EntityIndependentValueSelector<Solution_>) valueSelector;
    }

    @Override
    protected MoveSelectorConfig<?> buildUnfoldedMoveSelectorConfig(HeuristicConfigPolicy<Solution_> configPolicy) {
        EntityDescriptor<Solution_> entityDescriptor = getTheOnlyEntityDescriptor(configPolicy.getSolutionDescriptor());
        GenuineVariableDescriptor<Solution_> onlyVariableDescriptor = config.getValueSelectorConfig() == null ? null
                : ValueSelectorFactory.<Solution_> create(config.getValueSelectorConfig())
                        .extractVariableDescriptor(configPolicy, entityDescriptor);
        if (config.getSecondaryValueSelectorConfig() != null) {
            GenuineVariableDescriptor<Solution_> onlySecondaryVariableDescriptor =
                    ValueSelectorFactory.<Solution_> create(config.getSecondaryValueSelectorConfig())
                            .extractVariableDescriptor(configPolicy, entityDescriptor);
            if (onlyVariableDescriptor != onlySecondaryVariableDescriptor) {
                throw new IllegalArgumentException("The valueSelector (" + config.getValueSelectorConfig()
                        + ")'s variableName ("
                        + (onlyVariableDescriptor == null ? null : onlyVariableDescriptor.getVariableName())
                        + ") and secondaryValueSelectorConfig (" + config.getSecondaryValueSelectorConfig()
                        + ")'s variableName ("
                        + (onlySecondaryVariableDescriptor == null ? null : onlySecondaryVariableDescriptor.getVariableName())
                        + ") must be the same planning list variable.");
            }
        }
        if (onlyVariableDescriptor != null) {
            if (!onlyVariableDescriptor.isListVariable()) {
                throw new IllegalArgumentException("The listSwapMoveSelector (" + config
                        + ") is configured to use a planning variable (" + onlyVariableDescriptor
                        + "), which is not a planning list variable."
                        + " Either fix your annotations and use a @" + PlanningListVariable.class.getSimpleName()
                        + " on the variable to make it work with listSwapMoveSelector"
                        + " or use a swapMoveSelector instead.");
            }
            // No need for unfolding or deducing
            return null;
        }
        List<ListVariableDescriptor<Solution_>> variableDescriptorList =
                entityDescriptor.getGenuineVariableDescriptorList().stream()
                        .filter(GenuineVariableDescriptor::isListVariable)
                        .map(variableDescriptor -> ((ListVariableDescriptor<Solution_>) variableDescriptor))
                        .collect(Collectors.toList());
        if (variableDescriptorList.isEmpty()) {
            throw new IllegalArgumentException("The listSwapMoveSelector (" + config
                    + ") cannot unfold because there are no planning list variables for the only entity (" + entityDescriptor
                    + ") or no planning list variables at all.");
        }
        return buildUnfoldedMoveSelectorConfig(variableDescriptorList);
    }

    protected MoveSelectorConfig<?>
            buildUnfoldedMoveSelectorConfig(List<ListVariableDescriptor<Solution_>> variableDescriptorList) {
        List<MoveSelectorConfig> moveSelectorConfigList = new ArrayList<>(variableDescriptorList.size());
        for (ListVariableDescriptor<Solution_> variableDescriptor : variableDescriptorList) {
            // No childMoveSelectorConfig.inherit() because of unfoldedMoveSelectorConfig.inheritFolded()
            ListSwapMoveSelectorConfig childMoveSelectorConfig = new ListSwapMoveSelectorConfig();
            ValueSelectorConfig childValueSelectorConfig = new ValueSelectorConfig(config.getValueSelectorConfig());
            if (childValueSelectorConfig.getMimicSelectorRef() == null) {
                childValueSelectorConfig.setVariableName(variableDescriptor.getVariableName());
            }
            childMoveSelectorConfig.setValueSelectorConfig(childValueSelectorConfig);
            if (config.getSecondaryValueSelectorConfig() != null) {
                ValueSelectorConfig childSecondaryValueSelectorConfig =
                        new ValueSelectorConfig(config.getSecondaryValueSelectorConfig());
                if (childSecondaryValueSelectorConfig.getMimicSelectorRef() == null) {
                    childSecondaryValueSelectorConfig.setVariableName(variableDescriptor.getVariableName());
                }
                childMoveSelectorConfig.setSecondaryValueSelectorConfig(childSecondaryValueSelectorConfig);
            }
            moveSelectorConfigList.add(childMoveSelectorConfig);
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
}

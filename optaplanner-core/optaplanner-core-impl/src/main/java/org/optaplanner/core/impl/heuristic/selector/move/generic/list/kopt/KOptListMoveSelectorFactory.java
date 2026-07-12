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

package org.optaplanner.core.impl.heuristic.selector.move.generic.list.kopt;

import java.util.Objects;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.kopt.KOptListMoveSelectorConfig;
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

public final class KOptListMoveSelectorFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, KOptListMoveSelectorConfig> {

    private static final int DEFAULT_MINIMUM_K = 2;
    private static final int DEFAULT_MAXIMUM_K = 2;

    public KOptListMoveSelectorFactory(KOptListMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    protected MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        ValueSelectorConfig originSelectorConfig =
                Objects.requireNonNullElseGet(config.getOriginSelectorConfig(), ValueSelectorConfig::new);
        ValueSelectorConfig valueSelectorConfig =
                Objects.requireNonNullElseGet(config.getValueSelectorConfig(), ValueSelectorConfig::new);

        EntityDescriptor<Solution_> entityDescriptor = getTheOnlyEntityDescriptor(configPolicy.getSolutionDescriptor());

        EntityIndependentValueSelector<Solution_> originSelector =
                buildEntityIndependentValueSelector(configPolicy, entityDescriptor, originSelectorConfig, minimumCacheType,
                        SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        EntityIndependentValueSelector<Solution_> valueSelector =
                buildEntityIndependentValueSelector(configPolicy, entityDescriptor, valueSelectorConfig, minimumCacheType,
                        SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        // TODO support coexistence of list and basic variables https://issues.redhat.com/browse/PLANNER-2755
        GenuineVariableDescriptor<Solution_> variableDescriptor = getTheOnlyVariableDescriptor(entityDescriptor);
        if (!variableDescriptor.isListVariable()) {
            throw new IllegalArgumentException("The kOptListMoveSelector (" + config
                    + ") can only be used when the domain model has a list variable."
                    + " Check your @" + PlanningEntity.class.getSimpleName()
                    + " and make sure it has a @" + PlanningListVariable.class.getSimpleName() + ".");
        }

        int minimumK = Objects.requireNonNullElse(config.getMinimumK(), DEFAULT_MINIMUM_K);
        if (minimumK < 2) {
            throw new IllegalArgumentException("minimumK (" + minimumK + ") must be at least 2.");
        }
        int maximumK = Objects.requireNonNullElse(config.getMaximumK(), DEFAULT_MAXIMUM_K);
        if (maximumK < minimumK) {
            throw new IllegalArgumentException("maximumK (" + maximumK + ") must be at least minimumK (" + minimumK + ").");
        }

        int[] pickedKDistribution = new int[maximumK - minimumK + 1];
        // Each prior k is 8 times more likely to be picked than the subsequent k
        int total = 1;
        for (int i = minimumK; i < maximumK; i++) {
            total *= 8;
        }
        for (int i = 0; i < pickedKDistribution.length - 1; i++) {
            int remainder = total / 8;
            pickedKDistribution[i] = total - remainder;
            total = remainder;
        }
        pickedKDistribution[pickedKDistribution.length - 1] = total;
        return new KOptListMoveSelector<>(((ListVariableDescriptor<Solution_>) variableDescriptor),
                originSelector, valueSelector, minimumK, maximumK, pickedKDistribution);
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
            throw new IllegalArgumentException("The kOptListMoveSelector (" + config
                    + ") for a list variable needs to be based on an "
                    + EntityIndependentValueSelector.class.getSimpleName() + " (" + valueSelector + ")."
                    + " Check your valueSelectorConfig.");

        }
        return (EntityIndependentValueSelector<Solution_>) valueSelector;
    }
}

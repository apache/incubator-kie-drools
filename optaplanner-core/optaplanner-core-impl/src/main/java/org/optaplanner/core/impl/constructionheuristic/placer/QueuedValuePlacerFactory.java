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

package org.optaplanner.core.impl.constructionheuristic.placer;

import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.config.constructionheuristic.placer.QueuedValuePlacerConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelectorFactory;

public class QueuedValuePlacerFactory<Solution_>
        extends AbstractEntityPlacerFactory<Solution_, QueuedValuePlacerConfig> {

    public static QueuedValuePlacerConfig unfoldNew(MoveSelectorConfig templateMoveSelectorConfig) {
        throw new UnsupportedOperationException("The <constructionHeuristic> contains a moveSelector ("
                + templateMoveSelectorConfig + ") and the <queuedValuePlacer> does not support unfolding those yet.");
    }

    public QueuedValuePlacerFactory(QueuedValuePlacerConfig placerConfig) {
        super(placerConfig);
    }

    @Override
    public QueuedValuePlacer<Solution_> buildEntityPlacer(HeuristicConfigPolicy<Solution_> configPolicy) {
        EntityDescriptor<Solution_> entityDescriptor = deduceEntityDescriptor(configPolicy, config.getEntityClass());
        ValueSelectorConfig valueSelectorConfig_ = buildValueSelectorConfig(configPolicy, entityDescriptor);
        // TODO improve the ValueSelectorFactory API (avoid the boolean flags).
        ValueSelector<Solution_> valueSelector = ValueSelectorFactory.<Solution_> create(valueSelectorConfig_)
                .buildValueSelector(configPolicy, entityDescriptor, SelectionCacheType.PHASE, SelectionOrder.ORIGINAL,
                        false, // override applyReinitializeVariableFiltering
                        ValueSelectorFactory.ListValueFilteringType.ACCEPT_UNASSIGNED);

        MoveSelectorConfig<?> moveSelectorConfig_ = config.getMoveSelectorConfig() == null
                ? buildChangeMoveSelectorConfig(configPolicy, valueSelectorConfig_.getId(),
                        valueSelector.getVariableDescriptor())
                : config.getMoveSelectorConfig();

        MoveSelector<Solution_> moveSelector = MoveSelectorFactory.<Solution_> create(moveSelectorConfig_)
                .buildMoveSelector(configPolicy, SelectionCacheType.JUST_IN_TIME, SelectionOrder.ORIGINAL);
        if (!(valueSelector instanceof EntityIndependentValueSelector)) {
            throw new IllegalArgumentException("The queuedValuePlacer (" + this
                    + ") needs to be based on an "
                    + EntityIndependentValueSelector.class.getSimpleName() + " (" + valueSelector + ")."
                    + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");

        }
        return new QueuedValuePlacer<>((EntityIndependentValueSelector<Solution_>) valueSelector, moveSelector);
    }

    private ValueSelectorConfig buildValueSelectorConfig(HeuristicConfigPolicy<Solution_> configPolicy,
            EntityDescriptor<Solution_> entityDescriptor) {
        ValueSelectorConfig valueSelectorConfig_;
        if (config.getValueSelectorConfig() == null) {
            Class<?> entityClass = entityDescriptor.getEntityClass();
            GenuineVariableDescriptor<Solution_> variableDescriptor = getTheOnlyVariableDescriptor(entityDescriptor);
            valueSelectorConfig_ = new ValueSelectorConfig()
                    .withId(entityClass.getName() + "." + variableDescriptor.getVariableName())
                    .withVariableName(variableDescriptor.getVariableName());
            if (ValueSelectorConfig.hasSorter(configPolicy.getValueSorterManner(), variableDescriptor)) {
                valueSelectorConfig_ = valueSelectorConfig_.withCacheType(SelectionCacheType.PHASE)
                        .withSelectionOrder(SelectionOrder.SORTED)
                        .withSorterManner(configPolicy.getValueSorterManner());
            }
        } else {
            valueSelectorConfig_ = config.getValueSelectorConfig();
        }
        if (valueSelectorConfig_.getCacheType() != null
                && valueSelectorConfig_.getCacheType().compareTo(SelectionCacheType.PHASE) < 0) {
            throw new IllegalArgumentException("The queuedValuePlacer (" + this
                    + ") cannot have a valueSelectorConfig (" + valueSelectorConfig_
                    + ") with a cacheType (" + valueSelectorConfig_.getCacheType()
                    + ") lower than " + SelectionCacheType.PHASE + ".");
        }
        return valueSelectorConfig_;
    }

    @Override
    protected ChangeMoveSelectorConfig buildChangeMoveSelectorConfig(
            HeuristicConfigPolicy<Solution_> configPolicy, String valueSelectorConfigId,
            GenuineVariableDescriptor<Solution_> variableDescriptor) {
        ChangeMoveSelectorConfig changeMoveSelectorConfig = new ChangeMoveSelectorConfig();
        EntityDescriptor<Solution_> entityDescriptor = variableDescriptor.getEntityDescriptor();
        EntitySelectorConfig changeEntitySelectorConfig = new EntitySelectorConfig()
                .withEntityClass(entityDescriptor.getEntityClass());
        if (EntitySelectorConfig.hasSorter(configPolicy.getEntitySorterManner(), entityDescriptor)) {
            changeEntitySelectorConfig = changeEntitySelectorConfig.withCacheType(SelectionCacheType.PHASE)
                    .withSelectionOrder(SelectionOrder.SORTED)
                    .withSorterManner(configPolicy.getEntitySorterManner());
        }
        ValueSelectorConfig changeValueSelectorConfig = new ValueSelectorConfig()
                .withMimicSelectorRef(valueSelectorConfigId);
        return changeMoveSelectorConfig.withEntitySelectorConfig(changeEntitySelectorConfig)
                .withValueSelectorConfig(changeValueSelectorConfig);
    }
}

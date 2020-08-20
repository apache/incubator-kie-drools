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

public class QueuedValuePlacerFactory extends AbstractEntityPlacerFactory<QueuedValuePlacerConfig> {

    public static QueuedValuePlacerConfig unfoldNew(MoveSelectorConfig templateMoveSelectorConfig) {
        throw new UnsupportedOperationException("The <constructionHeuristic> contains a moveSelector ("
                + templateMoveSelectorConfig + ") and the <queuedValuePlacer> does not support unfolding those yet.");
    }

    public QueuedValuePlacerFactory(QueuedValuePlacerConfig placerConfig) {
        super(placerConfig);
    }

    @Override
    public QueuedValuePlacer buildEntityPlacer(HeuristicConfigPolicy configPolicy) {
        EntityDescriptor entityDescriptor =
                configPolicy.getSolutionDescriptor().deduceEntityDescriptor(placerConfig.getEntityClass());
        ValueSelectorConfig valueSelectorConfig_ = buildValueSelectorConfig(configPolicy, entityDescriptor);
        ValueSelector valueSelector = valueSelectorConfig_.buildValueSelector(configPolicy, entityDescriptor,
                SelectionCacheType.PHASE, SelectionOrder.ORIGINAL, false);

        MoveSelectorConfig moveSelectorConfig_ = placerConfig.getMoveSelectorConfig() == null
                ? buildChangeMoveSelectorConfig(configPolicy, valueSelectorConfig_.getId(),
                        valueSelector.getVariableDescriptor())
                : placerConfig.getMoveSelectorConfig();

        MoveSelector moveSelector = MoveSelectorFactory.create(moveSelectorConfig_)
                .buildMoveSelector(configPolicy, SelectionCacheType.JUST_IN_TIME, SelectionOrder.ORIGINAL);
        if (!(valueSelector instanceof EntityIndependentValueSelector)) {
            throw new IllegalArgumentException("The queuedValuePlacer (" + this
                    + ") needs to be based on an EntityIndependentValueSelector (" + valueSelector + ")."
                    + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");

        }
        return new QueuedValuePlacer((EntityIndependentValueSelector) valueSelector, moveSelector);
    }

    private ValueSelectorConfig buildValueSelectorConfig(HeuristicConfigPolicy configPolicy,
            EntityDescriptor entityDescriptor) {
        ValueSelectorConfig valueSelectorConfig_;
        if (placerConfig.getValueSelectorConfig() == null) {
            valueSelectorConfig_ = new ValueSelectorConfig();
            Class<?> entityClass = entityDescriptor.getEntityClass();
            GenuineVariableDescriptor variableDescriptor = entityDescriptor.deduceVariableDescriptor(null);
            valueSelectorConfig_.setId(entityClass.getName() + "." + variableDescriptor.getVariableName());
            valueSelectorConfig_.setVariableName(variableDescriptor.getVariableName());
            if (ValueSelectorConfig.hasSorter(configPolicy.getValueSorterManner(), variableDescriptor)) {
                valueSelectorConfig_.setCacheType(SelectionCacheType.PHASE);
                valueSelectorConfig_.setSelectionOrder(SelectionOrder.SORTED);
                valueSelectorConfig_.setSorterManner(configPolicy.getValueSorterManner());
            }
        } else {
            valueSelectorConfig_ = placerConfig.getValueSelectorConfig();
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
    protected ChangeMoveSelectorConfig buildChangeMoveSelectorConfig(HeuristicConfigPolicy configPolicy,
            String valueSelectorConfigId, GenuineVariableDescriptor variableDescriptor) {
        ChangeMoveSelectorConfig changeMoveSelectorConfig = new ChangeMoveSelectorConfig();
        EntitySelectorConfig changeEntitySelectorConfig = new EntitySelectorConfig();
        EntityDescriptor entityDescriptor = variableDescriptor.getEntityDescriptor();
        changeEntitySelectorConfig.setEntityClass(entityDescriptor.getEntityClass());
        if (EntitySelectorConfig.hasSorter(configPolicy.getEntitySorterManner(), entityDescriptor)) {
            changeEntitySelectorConfig.setCacheType(SelectionCacheType.PHASE);
            changeEntitySelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
            changeEntitySelectorConfig.setSorterManner(configPolicy.getEntitySorterManner());
        }
        changeMoveSelectorConfig.setEntitySelectorConfig(changeEntitySelectorConfig);
        ValueSelectorConfig changeValueSelectorConfig = new ValueSelectorConfig();
        changeValueSelectorConfig.setMimicSelectorRef(valueSelectorConfigId);
        changeMoveSelectorConfig.setValueSelectorConfig(changeValueSelectorConfig);
        return changeMoveSelectorConfig;
    }
}

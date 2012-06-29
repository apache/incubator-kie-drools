/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.config.heuristic.selector.move.generic;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.heuristic.selector.common.SelectionOrder;
import org.drools.planner.config.heuristic.selector.entity.EntitySelectorConfig;
import org.drools.planner.config.heuristic.selector.move.MoveSelectorConfig;
import org.drools.planner.config.heuristic.selector.value.ValueSelectorConfig;
import org.drools.planner.config.util.ConfigUtils;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.cached.SelectionFilter;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.heuristic.selector.entity.cached.FilteringEntitySelector;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.heuristic.selector.move.cached.FilteringMoveSelector;
import org.drools.planner.core.heuristic.selector.move.cached.ShufflingMoveSelector;
import org.drools.planner.core.heuristic.selector.move.generic.ChangeMoveSelector;
import org.drools.planner.core.heuristic.selector.value.ValueSelector;

@XStreamAlias("changeMoveSelector")
public class ChangeMoveSelectorConfig extends MoveSelectorConfig {

    @XStreamAlias("entitySelector")
    private EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
    @XStreamAlias("valueSelector")
    private ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig();

    private SelectionOrder selectionOrder = null;
    private SelectionCacheType cacheType = null;
    private Class<? extends SelectionFilter> moveFilterClass = null;
    // TODO moveSorterClass
    // TODO moveProbabilityWeightFactoryClass

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

    public SelectionOrder getSelectionOrder() {
        return selectionOrder;
    }

    public void setSelectionOrder(SelectionOrder selectionOrder) {
        this.selectionOrder = selectionOrder;
    }

    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    public void setCacheType(SelectionCacheType cacheType) {
        this.cacheType = cacheType;
    }

    public Class<? extends SelectionFilter> getMoveFilterClass() {
        return moveFilterClass;
    }

    public void setMoveFilterClass(Class<? extends SelectionFilter> moveFilterClass) {
        this.moveFilterClass = moveFilterClass;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public MoveSelector buildMoveSelector(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            SelectionOrder inheritedSelectionOrder, SelectionCacheType inheritedCacheType) {
        SelectionOrder resolvedSelectionOrder = SelectionOrder.resolve(selectionOrder, inheritedSelectionOrder);
        SelectionCacheType resolvedCacheType = SelectionCacheType.resolve(cacheType, inheritedCacheType);

        boolean randomSelection;
        boolean shuffled;
        if (resolvedSelectionOrder != SelectionOrder.RANDOM) {
            randomSelection = false;
            shuffled = false;
        } else {
            if (resolvedCacheType.compareTo(SelectionCacheType.STEP) >= 0) {
                randomSelection = false;
                shuffled = true;
                resolvedSelectionOrder = SelectionOrder.ORIGINAL;
            } else {
                randomSelection = true;
                shuffled = false;
            }
        }
        // TODO && moveProbabilityWeightFactoryClass == null;
        // TODO if probability and random==true then put random=false to entity and value selectors

        EntitySelector entitySelector = entitySelectorConfig.buildEntitySelector(environmentMode, solutionDescriptor,
                resolvedSelectionOrder, resolvedCacheType);
        ValueSelector valueSelector = valueSelectorConfig.buildValueSelector(environmentMode, solutionDescriptor,
                resolvedSelectionOrder, resolvedCacheType, entitySelector.getEntityDescriptor());
        MoveSelector moveSelector = new ChangeMoveSelector(entitySelector, valueSelector, randomSelection);

        if (moveFilterClass != null) {
            SelectionFilter moveFilter;
            try {
                moveFilter = moveFilterClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("moveFilterClass ("
                        + moveFilterClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("moveFilterClass ("
                        + moveFilterClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
            FilteringMoveSelector filteringMoveSelector = new FilteringMoveSelector(moveSelector,
                    resolvedCacheType, moveFilter);
            moveSelector = filteringMoveSelector;
        }
        // TODO moveSorterClass
        // TODO moveProbabilityWeightFactoryClass
        if (shuffled) {
            moveSelector = new ShufflingMoveSelector(moveSelector, resolvedCacheType);
        }
        return moveSelector;
    }

    public void inherit(ChangeMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (entitySelectorConfig == null) {
            entitySelectorConfig = inheritedConfig.getEntitySelectorConfig();
        } else if (inheritedConfig.getEntitySelectorConfig() != null) {
            entitySelectorConfig.inherit(inheritedConfig.getEntitySelectorConfig());
        }
        if (valueSelectorConfig == null) {
            valueSelectorConfig = inheritedConfig.getValueSelectorConfig();
        } else if (inheritedConfig.getValueSelectorConfig() != null) {
            valueSelectorConfig.inherit(inheritedConfig.getValueSelectorConfig());
        }
        selectionOrder = ConfigUtils.inheritOverwritableProperty(selectionOrder, inheritedConfig.getSelectionOrder());
        cacheType = ConfigUtils.inheritOverwritableProperty(cacheType, inheritedConfig.getCacheType());
        moveFilterClass = ConfigUtils.inheritOverwritableProperty(
                moveFilterClass, inheritedConfig.getMoveFilterClass());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelectorConfig + ", " + valueSelectorConfig + ")";
    }

}

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

package org.drools.planner.config.heuristic.selector.move;

import com.thoughtworks.xstream.annotations.XStreamInclude;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.heuristic.selector.SelectorConfig;
import org.drools.planner.config.heuristic.selector.common.SelectionOrder;
import org.drools.planner.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.drools.planner.config.heuristic.selector.move.factory.MoveIteratorFactoryConfig;
import org.drools.planner.config.heuristic.selector.move.factory.MoveListFactoryConfig;
import org.drools.planner.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.drools.planner.config.heuristic.selector.move.generic.PillarSwapMoveSelectorConfig;
import org.drools.planner.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.drools.planner.config.heuristic.selector.move.generic.chained.SubChainChangeMoveSelectorConfig;
import org.drools.planner.config.heuristic.selector.move.generic.chained.SubChainSwapMoveSelectorConfig;
import org.drools.planner.config.util.ConfigUtils;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionFilter;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.heuristic.selector.move.decorator.CachingFilteringMoveSelector;
import org.drools.planner.core.heuristic.selector.move.decorator.JustInTimeFilteringMoveSelector;
import org.drools.planner.core.heuristic.selector.move.decorator.ShufflingMoveSelector;

/**
 * General superclass for {@link ChangeMoveSelectorConfig}, etc.
 */
@XStreamInclude({
        UnionMoveSelectorConfig.class,
        ChangeMoveSelectorConfig.class, SwapMoveSelectorConfig.class, PillarSwapMoveSelectorConfig.class,
        SubChainChangeMoveSelectorConfig.class, SubChainSwapMoveSelectorConfig.class,
        MoveListFactoryConfig.class, MoveIteratorFactoryConfig.class
})
public abstract class MoveSelectorConfig extends SelectorConfig {

    protected SelectionOrder selectionOrder = null;
    protected SelectionCacheType cacheType = null;
    protected Class<? extends SelectionFilter> moveFilterClass = null;
    // TODO moveSorterClass
    // TODO moveProbabilityWeightFactoryClass

    private Double fixedProbabilityWeight = null;

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

    public Double getFixedProbabilityWeight() {
        return fixedProbabilityWeight;
    }

    public void setFixedProbabilityWeight(Double fixedProbabilityWeight) {
        this.fixedProbabilityWeight = fixedProbabilityWeight;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public MoveSelector buildMoveSelector(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            SelectionOrder inheritedSelectionOrder, SelectionCacheType inheritedCacheType) {
        SelectionOrder resolvedSelectionOrder = SelectionOrder.resolve(selectionOrder, inheritedSelectionOrder);
        SelectionCacheType resolvedCacheType = SelectionCacheType.resolve(cacheType, inheritedCacheType);

        boolean shuffled;
        if (resolvedSelectionOrder != SelectionOrder.RANDOM) {
            shuffled = false;
        } else {
            if (resolvedCacheType.compareTo(SelectionCacheType.STEP) >= 0) {
                shuffled = true;
                // the baseMoveSelector and lower should not be random as they are going to get cached completely
                resolvedSelectionOrder = SelectionOrder.ORIGINAL;
            } else {
                shuffled = false;
            }
        }
        // TODO && moveProbabilityWeightFactoryClass == null;
        // TODO if probability and random==true then put random=false to entity and value selectors

        MoveSelector moveSelector = buildBaseMoveSelector(environmentMode, solutionDescriptor,
                resolvedSelectionOrder, resolvedCacheType);

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
            MoveSelector filteringMoveSelector;
            if (resolvedCacheType == SelectionCacheType.JUST_IN_TIME) {
                filteringMoveSelector = new JustInTimeFilteringMoveSelector(moveSelector,
                        resolvedCacheType, moveFilter);
            } else {
                filteringMoveSelector = new CachingFilteringMoveSelector(moveSelector,
                        resolvedCacheType, moveFilter);
            }
            moveSelector = filteringMoveSelector;
        }
        // TODO moveSorterClass
        // TODO moveProbabilityWeightFactoryClass
        if (shuffled) {
            moveSelector = new ShufflingMoveSelector(moveSelector, resolvedCacheType);
        }
        // TODO this is broken because it introduces unneeded caching on level 2 and 3 deep
//        if (!alreadyCached && resolvedCacheType.compareTo(SelectionCacheType.JUST_IN_TIME) > 0) {
//            moveSelector = new CachingMoveSelector(moveSelector, resolvedCacheType);
//        }
        return moveSelector;
    }

    protected abstract MoveSelector buildBaseMoveSelector(
            EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            SelectionOrder resolvedSelectionOrder, SelectionCacheType resolvedCacheType);

    protected void inherit(MoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        selectionOrder = ConfigUtils.inheritOverwritableProperty(selectionOrder, inheritedConfig.getSelectionOrder());
        cacheType = ConfigUtils.inheritOverwritableProperty(cacheType, inheritedConfig.getCacheType());
        moveFilterClass = ConfigUtils.inheritOverwritableProperty(
                moveFilterClass, inheritedConfig.getMoveFilterClass());

        fixedProbabilityWeight = ConfigUtils.inheritOverwritableProperty(
                fixedProbabilityWeight, inheritedConfig.getFixedProbabilityWeight());
    }

}

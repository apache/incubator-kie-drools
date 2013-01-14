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

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamInclude;
import org.apache.commons.collections.CollectionUtils;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.heuristic.selector.SelectorConfig;
import org.drools.planner.config.heuristic.selector.common.SelectionOrder;
import org.drools.planner.config.heuristic.selector.move.composite.CartesianProductMoveSelectorConfig;
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
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.heuristic.selector.move.decorator.CachingMoveSelector;
import org.drools.planner.core.heuristic.selector.move.decorator.FilteringMoveSelector;
import org.drools.planner.core.heuristic.selector.move.decorator.ProbabilityMoveSelector;
import org.drools.planner.core.heuristic.selector.move.decorator.ShufflingMoveSelector;

/**
 * General superclass for {@link ChangeMoveSelectorConfig}, etc.
 */
@XStreamInclude({
        UnionMoveSelectorConfig.class, CartesianProductMoveSelectorConfig.class,
        ChangeMoveSelectorConfig.class, SwapMoveSelectorConfig.class, PillarSwapMoveSelectorConfig.class,
        SubChainChangeMoveSelectorConfig.class, SubChainSwapMoveSelectorConfig.class,
        MoveListFactoryConfig.class, MoveIteratorFactoryConfig.class
})
public abstract class MoveSelectorConfig extends SelectorConfig {

    protected SelectionCacheType cacheType = null;
    protected SelectionOrder selectionOrder = null;
    @XStreamImplicit(itemFieldName = "moveFilterClass")
    protected List<Class<? extends SelectionFilter>> moveFilterClassList = null;
    protected Class<? extends SelectionProbabilityWeightFactory> moveProbabilityWeightFactoryClass = null;
    // TODO moveSorterClass

    private Double fixedProbabilityWeight = null;

    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    public void setCacheType(SelectionCacheType cacheType) {
        this.cacheType = cacheType;
    }

    public SelectionOrder getSelectionOrder() {
        return selectionOrder;
    }

    public void setSelectionOrder(SelectionOrder selectionOrder) {
        this.selectionOrder = selectionOrder;
    }

    public List<Class<? extends SelectionFilter>> getMoveFilterClassList() {
        return moveFilterClassList;
    }

    public void setMoveFilterClassList(List<Class<? extends SelectionFilter>> moveFilterClassList) {
        this.moveFilterClassList = moveFilterClassList;
    }

    public Class<? extends SelectionProbabilityWeightFactory> getMoveProbabilityWeightFactoryClass() {
        return moveProbabilityWeightFactoryClass;
    }

    public void setMoveProbabilityWeightFactoryClass(Class<? extends SelectionProbabilityWeightFactory> moveProbabilityWeightFactoryClass) {
        this.moveProbabilityWeightFactoryClass = moveProbabilityWeightFactoryClass;
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

    /**
     *
     * @param environmentMode never null
     * @param solutionDescriptor never null
     * @param minimumCacheType never null, If caching is used (different from {@link SelectionCacheType#JUST_IN_TIME}),
     * then it should be at least this {@link SelectionCacheType} because an ancestor already uses such caching
     * and less would be pointless.
     * @param inheritedSelectionOrder never null
     * @return never null
     */
    public MoveSelector buildMoveSelector(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            SelectionCacheType minimumCacheType, SelectionOrder inheritedSelectionOrder) {
        SelectionCacheType resolvedCacheType = SelectionCacheType.resolve(cacheType, minimumCacheType);
        minimumCacheType = SelectionCacheType.max(minimumCacheType, resolvedCacheType);
        SelectionOrder resolvedSelectionOrder = SelectionOrder.resolve(selectionOrder, inheritedSelectionOrder);

        // baseMoveSelector and lower should be SelectionOrder.ORIGINAL if they are going to get cached completely
        MoveSelector moveSelector = buildBaseMoveSelector(environmentMode, solutionDescriptor,
                minimumCacheType, resolvedCacheType.isCached() ? SelectionOrder.ORIGINAL : resolvedSelectionOrder);

        moveSelector = applyFiltering(moveSelector);
        // TODO applySorting
        moveSelector = applyProbability(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        moveSelector = applyShuffling(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        moveSelector = applyCaching(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        return moveSelector;
    }

    private MoveSelector applyFiltering(MoveSelector moveSelector) {
        if (!CollectionUtils.isEmpty(moveFilterClassList)) {
            List<SelectionFilter> moveFilterList = new ArrayList<SelectionFilter>(moveFilterClassList.size());
            for (Class<? extends SelectionFilter> moveFilterClass : moveFilterClassList) {
                moveFilterList.add(ConfigUtils.newInstance(this, "moveFilterClass", moveFilterClass));
            }
            moveSelector = new FilteringMoveSelector(moveSelector, moveFilterList);
        }
        return moveSelector;
    }

    private MoveSelector applyProbability(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            MoveSelector moveSelector) {
        if (moveProbabilityWeightFactoryClass != null) {
            if (resolvedSelectionOrder != SelectionOrder.RANDOM) {
                throw new IllegalArgumentException("The moveSelectorConfig (" + this
                        + ") with moveProbabilityWeightFactoryClass ("
                        + moveProbabilityWeightFactoryClass + ") has a resolvedSelectionOrder ("
                        + resolvedSelectionOrder + ") that is not " + SelectionOrder.RANDOM + ".");
            }
            SelectionProbabilityWeightFactory probabilityWeightFactory = ConfigUtils.newInstance(this,
                    "moveProbabilityWeightFactoryClass", moveProbabilityWeightFactoryClass);
            moveSelector = new ProbabilityMoveSelector(moveSelector,
                    resolvedCacheType, probabilityWeightFactory);
        }
        return moveSelector;
    }

    private MoveSelector applyShuffling(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            MoveSelector moveSelector) {
        if (resolvedSelectionOrder == SelectionOrder.SHUFFLED) {
            if (resolvedCacheType.isNotCached()) {
                throw new IllegalArgumentException("The moveSelectorConfig (" + this
                        + ") with resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") has a resolvedCacheType (" + resolvedCacheType + ") that is not cached.");
            }
            moveSelector = new ShufflingMoveSelector(moveSelector, resolvedCacheType);
        }
        return moveSelector;
    }

    private MoveSelector applyCaching(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            MoveSelector moveSelector) {
        if (resolvedCacheType.isCached() && resolvedCacheType.compareTo(moveSelector.getCacheType()) > 0) {
            moveSelector = new CachingMoveSelector(moveSelector, resolvedCacheType,
                    resolvedSelectionOrder == SelectionOrder.RANDOM);
        }
        return moveSelector;
    }

    /**
     *
     * @param environmentMode never null
     * @param solutionDescriptor never null
     * @param minimumCacheType never null, If caching is used (different from {@link SelectionCacheType#JUST_IN_TIME}),
     * then it should be at least this {@link SelectionCacheType} because an ancestor already uses such caching
     * and less would be pointless.
     * @param resolvedSelectionOrder never null
     * @return never null
     */
    protected abstract MoveSelector buildBaseMoveSelector(
            EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            SelectionCacheType minimumCacheType, SelectionOrder resolvedSelectionOrder);

    protected void inherit(MoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        cacheType = ConfigUtils.inheritOverwritableProperty(cacheType, inheritedConfig.getCacheType());
        selectionOrder = ConfigUtils.inheritOverwritableProperty(selectionOrder, inheritedConfig.getSelectionOrder());
        moveFilterClassList = ConfigUtils.inheritOverwritableProperty(
                moveFilterClassList, inheritedConfig.getMoveFilterClassList());
        moveProbabilityWeightFactoryClass = ConfigUtils.inheritOverwritableProperty(
                moveProbabilityWeightFactoryClass, inheritedConfig.getMoveProbabilityWeightFactoryClass());

        fixedProbabilityWeight = ConfigUtils.inheritOverwritableProperty(
                fixedProbabilityWeight, inheritedConfig.getFixedProbabilityWeight());
    }

}

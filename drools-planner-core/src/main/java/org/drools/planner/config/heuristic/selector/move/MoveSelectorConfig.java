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
import org.drools.planner.core.heuristic.selector.move.decorator.JustInTimeFilteringMoveSelector;
import org.drools.planner.core.heuristic.selector.move.decorator.ProbabilityMoveSelector;
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

        // baseMoveSelector and lower should not be SelectionOrder.RANDOM as they are going to get cached completely
        MoveSelector moveSelector = buildBaseMoveSelector(environmentMode, solutionDescriptor,
                minimumCacheType, resolvedCacheType.isCached() ? SelectionOrder.ORIGINAL : resolvedSelectionOrder);

        boolean alreadyCached = false;
        if (!CollectionUtils.isEmpty(moveFilterClassList)) {
            List<SelectionFilter> moveFilterList = new ArrayList<SelectionFilter>(moveFilterClassList.size());
            for (Class<? extends SelectionFilter> moveFilterClass : moveFilterClassList) {
                moveFilterList.add(ConfigUtils.newInstance(this, "moveFilterClass", moveFilterClass));
            }
            moveSelector = new JustInTimeFilteringMoveSelector(moveSelector, moveFilterList);
        }
        // TODO moveSorterClass
        if (moveProbabilityWeightFactoryClass != null) {
            if (resolvedSelectionOrder != SelectionOrder.RANDOM) {
                throw new IllegalArgumentException("The entitySelectorConfig (" + this
                        + ") with moveProbabilityWeightFactoryClass ("
                        + moveProbabilityWeightFactoryClass + ") has a non-random resolvedSelectionOrder ("
                        + resolvedSelectionOrder + ").");
            }
            SelectionProbabilityWeightFactory entityProbabilityWeightFactory = ConfigUtils.newInstance(this,
                    "moveProbabilityWeightFactoryClass", moveProbabilityWeightFactoryClass);
            moveSelector = new ProbabilityMoveSelector(moveSelector,
                    resolvedCacheType, entityProbabilityWeightFactory);
            alreadyCached = true;
        }
        if (resolvedCacheType.isCached() && !alreadyCached) {
            if (resolvedSelectionOrder != SelectionOrder.RANDOM) {
                // TODO this is pretty pointless for MoveListFactoryConfig, because MoveListFactory caches
                moveSelector = new CachingMoveSelector(moveSelector, resolvedCacheType);
            } else {
                moveSelector = new ShufflingMoveSelector(moveSelector, resolvedCacheType);
            }
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

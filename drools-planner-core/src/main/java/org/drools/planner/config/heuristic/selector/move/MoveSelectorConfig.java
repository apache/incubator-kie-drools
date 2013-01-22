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
import java.util.Comparator;
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
import org.drools.planner.core.heuristic.selector.common.decorator.ComparatorSelectionSorter;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionFilter;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionSorter;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.drools.planner.core.heuristic.selector.common.decorator.WeightFactorySelectionSorter;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.heuristic.selector.entity.decorator.SortingEntitySelector;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.heuristic.selector.move.decorator.CachingMoveSelector;
import org.drools.planner.core.heuristic.selector.move.decorator.FilteringMoveSelector;
import org.drools.planner.core.heuristic.selector.move.decorator.ProbabilityMoveSelector;
import org.drools.planner.core.heuristic.selector.move.decorator.ShufflingMoveSelector;
import org.drools.planner.core.heuristic.selector.move.decorator.SortingMoveSelector;

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

    @XStreamImplicit(itemFieldName = "filterClass")
    protected List<Class<? extends SelectionFilter>> filterClassList = null;

    protected Class<? extends Comparator> sorterComparatorClass = null;
    protected Class<? extends SelectionSorterWeightFactory> sorterWeightFactoryClass = null;
    protected SelectionSorterOrder sorterOrder = null;
    protected Class<? extends SelectionSorter> sorterClass = null;

    protected Class<? extends SelectionProbabilityWeightFactory> probabilityWeightFactoryClass = null;

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

    public List<Class<? extends SelectionFilter>> getFilterClassList() {
        return filterClassList;
    }

    public void setFilterClassList(List<Class<? extends SelectionFilter>> filterClassList) {
        this.filterClassList = filterClassList;
    }

    public Class<? extends Comparator> getSorterComparatorClass() {
        return sorterComparatorClass;
    }

    public void setSorterComparatorClass(Class<? extends Comparator> sorterComparatorClass) {
        this.sorterComparatorClass = sorterComparatorClass;
    }

    public Class<? extends SelectionSorterWeightFactory> getSorterWeightFactoryClass() {
        return sorterWeightFactoryClass;
    }

    public void setSorterWeightFactoryClass(Class<? extends SelectionSorterWeightFactory> sorterWeightFactoryClass) {
        this.sorterWeightFactoryClass = sorterWeightFactoryClass;
    }

    public SelectionSorterOrder getSorterOrder() {
        return sorterOrder;
    }

    public void setSorterOrder(SelectionSorterOrder sorterOrder) {
        this.sorterOrder = sorterOrder;
    }

    public Class<? extends SelectionSorter> getSorterClass() {
        return sorterClass;
    }

    public void setSorterClass(Class<? extends SelectionSorter> sorterClass) {
        this.sorterClass = sorterClass;
    }

    public Class<? extends SelectionProbabilityWeightFactory> getProbabilityWeightFactoryClass() {
        return probabilityWeightFactoryClass;
    }

    public void setProbabilityWeightFactoryClass(Class<? extends SelectionProbabilityWeightFactory> probabilityWeightFactoryClass) {
        this.probabilityWeightFactoryClass = probabilityWeightFactoryClass;
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

        moveSelector = applyFiltering(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        moveSelector = applySorting(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        moveSelector = applyProbability(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        moveSelector = applyShuffling(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        moveSelector = applyCaching(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        return moveSelector;
    }

    private MoveSelector applyFiltering(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            MoveSelector moveSelector) {
        if (!CollectionUtils.isEmpty(filterClassList)) {
            List<SelectionFilter> filterList = new ArrayList<SelectionFilter>(filterClassList.size());
            for (Class<? extends SelectionFilter> filterClass : filterClassList) {
                filterList.add(ConfigUtils.newInstance(this, "filterClass", filterClass));
            }
            moveSelector = new FilteringMoveSelector(moveSelector, filterList);
        }
        return moveSelector;
    }

    private MoveSelector applySorting(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            MoveSelector moveSelector) {
        if (sorterComparatorClass != null || sorterWeightFactoryClass != null || sorterClass != null) {
            SelectionSorter sorter = null;
            if (sorterComparatorClass != null) {
                if (resolvedSelectionOrder != SelectionOrder.ORIGINAL) {
                    throw new IllegalArgumentException("The moveSelectorConfig (" + this
                            + ") with sorterComparatorClass (" + sorterComparatorClass
                            + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                            + ") that is not " + SelectionOrder.ORIGINAL + ".");
                }
                Comparator<Object> sorterComparator = ConfigUtils.newInstance(this,
                        "sorterComparatorClass", sorterComparatorClass);
                sorter = new ComparatorSelectionSorter(sorterComparator,
                        SelectionSorterOrder.resolve(sorterOrder));
            }
            if (sorterWeightFactoryClass != null) {
                if (sorterComparatorClass != null) {
                    throw new IllegalArgumentException("The moveSelectorConfig (" + this
                            + ") has both an sorterComparatorClass (" + sorterComparatorClass
                            + ") and a sorterWeightFactoryClass (" + sorterWeightFactoryClass + ").");
                }
                if (resolvedSelectionOrder != SelectionOrder.ORIGINAL) {
                    throw new IllegalArgumentException("The moveSelectorConfig (" + this
                            + ") with sorterWeightFactoryClass (" + sorterWeightFactoryClass
                            + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                            + ") that is not " + SelectionOrder.ORIGINAL + ".");
                }
                SelectionSorterWeightFactory sorterWeightFactory = ConfigUtils.newInstance(this,
                        "sorterWeightFactoryClass", sorterWeightFactoryClass);
                sorter = new WeightFactorySelectionSorter(sorterWeightFactory,
                        SelectionSorterOrder.resolve(sorterOrder));
            }
            if (sorterClass != null) {
                if (sorterComparatorClass != null) {
                    throw new IllegalArgumentException("The moveSelectorConfig (" + this
                            + ") has both an sorterComparatorClass (" + sorterComparatorClass
                            + ") and a sorterClass (" + sorterClass + ").");
                }
                if (sorterWeightFactoryClass != null) {
                    throw new IllegalArgumentException("The moveSelectorConfig (" + this
                            + ") has both an sorterWeightFactoryClass (" + sorterWeightFactoryClass
                            + ") and a sorterClass (" + sorterClass + ").");
                }
                if (sorterOrder != null) {
                    throw new IllegalArgumentException("The moveSelectorConfig (" + this
                            + ") has both an sorterClass (" + sorterClass
                            + ") but the sorterOrder (" + sorterOrder + ") should be null.");
                }
                if (resolvedSelectionOrder != SelectionOrder.ORIGINAL) {
                    throw new IllegalArgumentException("The moveSelectorConfig (" + this
                            + ") with sorterClass (" + sorterClass
                            + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                            + ") that is not " + SelectionOrder.ORIGINAL + ".");
                }
                sorter = ConfigUtils.newInstance(this, "sorterClass", sorterClass);
            }
            moveSelector = new SortingMoveSelector(moveSelector, resolvedCacheType, sorter);
        } else {
            if (sorterOrder != null) {
                if (sorterOrder != null) {
                    throw new IllegalArgumentException("The moveSelectorConfig (" + this
                            + ") has a sorterOrder (" + sorterOrder
                            + "), but no sorterComparatorClass (" + sorterComparatorClass
                            + ") or sorterWeightFactoryClass (" + sorterWeightFactoryClass + ").");
                }
            }
        }
        return moveSelector;
    }

    private MoveSelector applyProbability(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            MoveSelector moveSelector) {
        if (probabilityWeightFactoryClass != null) {
            if (resolvedSelectionOrder != SelectionOrder.RANDOM) {
                throw new IllegalArgumentException("The moveSelectorConfig (" + this
                        + ") with probabilityWeightFactoryClass ("
                        + probabilityWeightFactoryClass + ") has a resolvedSelectionOrder ("
                        + resolvedSelectionOrder + ") that is not " + SelectionOrder.RANDOM + ".");
            }
            SelectionProbabilityWeightFactory probabilityWeightFactory = ConfigUtils.newInstance(this,
                    "probabilityWeightFactoryClass", probabilityWeightFactoryClass);
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
        filterClassList = ConfigUtils.inheritOverwritableProperty(
                filterClassList, inheritedConfig.getFilterClassList());
        sorterComparatorClass = ConfigUtils.inheritOverwritableProperty(
                sorterComparatorClass, inheritedConfig.getSorterComparatorClass());
        sorterWeightFactoryClass = ConfigUtils.inheritOverwritableProperty(
                sorterWeightFactoryClass, inheritedConfig.getSorterWeightFactoryClass());
        sorterOrder = ConfigUtils.inheritOverwritableProperty(
                sorterOrder, inheritedConfig.getSorterOrder());
        sorterClass = ConfigUtils.inheritOverwritableProperty(
                sorterClass, inheritedConfig.getSorterClass());
        probabilityWeightFactoryClass = ConfigUtils.inheritOverwritableProperty(
                probabilityWeightFactoryClass, inheritedConfig.getProbabilityWeightFactoryClass());

        fixedProbabilityWeight = ConfigUtils.inheritOverwritableProperty(
                fixedProbabilityWeight, inheritedConfig.getFixedProbabilityWeight());
    }

}

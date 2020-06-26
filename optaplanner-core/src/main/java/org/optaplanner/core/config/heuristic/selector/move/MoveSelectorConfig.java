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

package org.optaplanner.core.config.heuristic.selector.move;

import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.optaplanner.core.config.heuristic.selector.SelectorConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.optaplanner.core.config.heuristic.selector.move.composite.CartesianProductMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveIteratorFactoryConfig;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveListFactoryConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.KOptMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.TailChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.ComparatorSelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.WeightFactorySelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.CachingMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.FilteringMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.ProbabilityMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.SelectedCountLimitMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.ShufflingMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.SortingMoveSelector;

import com.thoughtworks.xstream.annotations.XStreamInclude;

/**
 * General superclass for {@link ChangeMoveSelectorConfig}, etc.
 */

@XmlSeeAlso({
        UnionMoveSelectorConfig.class, CartesianProductMoveSelectorConfig.class,
        ChangeMoveSelectorConfig.class, SwapMoveSelectorConfig.class,
        PillarChangeMoveSelectorConfig.class, PillarSwapMoveSelectorConfig.class,
        TailChainSwapMoveSelectorConfig.class, KOptMoveSelectorConfig.class,
        SubChainChangeMoveSelectorConfig.class, SubChainSwapMoveSelectorConfig.class,
        MoveListFactoryConfig.class, MoveIteratorFactoryConfig.class })
@XStreamInclude({
        UnionMoveSelectorConfig.class, CartesianProductMoveSelectorConfig.class,
        ChangeMoveSelectorConfig.class, SwapMoveSelectorConfig.class,
        PillarChangeMoveSelectorConfig.class, PillarSwapMoveSelectorConfig.class,
        TailChainSwapMoveSelectorConfig.class, KOptMoveSelectorConfig.class,
        SubChainChangeMoveSelectorConfig.class, SubChainSwapMoveSelectorConfig.class,
        MoveListFactoryConfig.class, MoveIteratorFactoryConfig.class
})
public abstract class MoveSelectorConfig<C extends MoveSelectorConfig> extends SelectorConfig<C> {

    protected SelectionCacheType cacheType = null;
    protected SelectionOrder selectionOrder = null;

    protected Class<? extends SelectionFilter> filterClass = null;

    protected Class<? extends Comparator> sorterComparatorClass = null;
    protected Class<? extends SelectionSorterWeightFactory> sorterWeightFactoryClass = null;
    protected SelectionSorterOrder sorterOrder = null;
    protected Class<? extends SelectionSorter> sorterClass = null;

    protected Class<? extends SelectionProbabilityWeightFactory> probabilityWeightFactoryClass = null;

    protected Long selectedCountLimit = null;

    private Double fixedProbabilityWeight = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

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

    public Class<? extends SelectionFilter> getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(Class<? extends SelectionFilter> filterClass) {
        this.filterClass = filterClass;
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

    public void setProbabilityWeightFactoryClass(
            Class<? extends SelectionProbabilityWeightFactory> probabilityWeightFactoryClass) {
        this.probabilityWeightFactoryClass = probabilityWeightFactoryClass;
    }

    public Long getSelectedCountLimit() {
        return selectedCountLimit;
    }

    public void setSelectedCountLimit(Long selectedCountLimit) {
        this.selectedCountLimit = selectedCountLimit;
    }

    public Double getFixedProbabilityWeight() {
        return fixedProbabilityWeight;
    }

    public void setFixedProbabilityWeight(Double fixedProbabilityWeight) {
        this.fixedProbabilityWeight = fixedProbabilityWeight;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public MoveSelectorConfig withCacheType(SelectionCacheType cacheType) {
        this.cacheType = cacheType;
        return this;
    }

    public MoveSelectorConfig withSelectionOrder(SelectionOrder selectionOrder) {
        this.selectionOrder = selectionOrder;
        return this;
    }

    public MoveSelectorConfig withFilterClass(Class<? extends SelectionFilter> filterClass) {
        this.filterClass = filterClass;
        return this;
    }

    public MoveSelectorConfig withSorterComparatorClass(Class<? extends Comparator> sorterComparatorClass) {
        this.sorterComparatorClass = sorterComparatorClass;
        return this;
    }

    public MoveSelectorConfig withSorterWeightFactoryClass(
            Class<? extends SelectionSorterWeightFactory> sorterWeightFactoryClass) {
        this.sorterWeightFactoryClass = sorterWeightFactoryClass;
        return this;
    }

    public MoveSelectorConfig withSorterOrder(SelectionSorterOrder sorterOrder) {
        this.sorterOrder = sorterOrder;
        return this;
    }

    public MoveSelectorConfig withSorterClass(Class<? extends SelectionSorter> sorterClass) {
        this.sorterClass = sorterClass;
        return this;
    }

    public MoveSelectorConfig withProbabilityWeightFactoryClass(
            Class<? extends SelectionProbabilityWeightFactory> probabilityWeightFactoryClass) {
        this.probabilityWeightFactoryClass = probabilityWeightFactoryClass;
        return this;
    }

    public MoveSelectorConfig withSelectedCountLimit(Long selectedCountLimit) {
        this.selectedCountLimit = selectedCountLimit;
        return this;
    }

    public MoveSelectorConfig withFixedProbabilityWeight(Double fixedProbabilityWeight) {
        this.fixedProbabilityWeight = fixedProbabilityWeight;
        return this;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    /**
     * @param configPolicy never null
     * @param minimumCacheType never null, If caching is used (different from {@link SelectionCacheType#JUST_IN_TIME}),
     *        then it should be at least this {@link SelectionCacheType} because an ancestor already uses such caching
     *        and less would be pointless.
     * @param inheritedSelectionOrder never null
     * @return never null
     */
    public MoveSelector buildMoveSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, SelectionOrder inheritedSelectionOrder) {
        MoveSelectorConfig unfoldedMoveSelectorConfig = buildUnfoldedMoveSelectorConfig(configPolicy);
        if (unfoldedMoveSelectorConfig != null) {
            return unfoldedMoveSelectorConfig.buildMoveSelector(configPolicy, minimumCacheType, inheritedSelectionOrder);
        }

        SelectionCacheType resolvedCacheType = SelectionCacheType.resolve(cacheType, minimumCacheType);
        SelectionOrder resolvedSelectionOrder = SelectionOrder.resolve(selectionOrder, inheritedSelectionOrder);

        validateCacheTypeVersusSelectionOrder(resolvedCacheType, resolvedSelectionOrder);
        validateSorting(resolvedSelectionOrder);
        validateProbability(resolvedSelectionOrder);
        validateSelectedLimit(minimumCacheType);

        MoveSelector moveSelector = buildBaseMoveSelector(configPolicy,
                SelectionCacheType.max(minimumCacheType, resolvedCacheType),
                determineBaseRandomSelection(resolvedCacheType, resolvedSelectionOrder));
        validateResolvedCacheType(resolvedCacheType, moveSelector);

        moveSelector = applyFiltering(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        moveSelector = applySorting(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        moveSelector = applyProbability(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        moveSelector = applyShuffling(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        moveSelector = applyCaching(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        moveSelector = applySelectedLimit(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        return moveSelector;
    }

    private void validateResolvedCacheType(SelectionCacheType resolvedCacheType, MoveSelector moveSelector) {
        if (!moveSelector.supportsPhaseAndSolverCaching()
                && resolvedCacheType.compareTo(SelectionCacheType.PHASE) >= 0) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + this
                    + ") has a resolvedCacheType (" + resolvedCacheType + ") that is not supported.\n"
                    + "Maybe don't use a <cacheType> on this type of moveSelector.");
        }
    }

    /**
     * @param configPolicy never null
     * @return null if no unfolding is needed
     */
    protected MoveSelectorConfig buildUnfoldedMoveSelectorConfig(HeuristicConfigPolicy configPolicy) {
        return null;
    }

    protected boolean determineBaseRandomSelection(
            SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder) {
        switch (resolvedSelectionOrder) {
            case ORIGINAL:
                return false;
            case SORTED:
            case SHUFFLED:
            case PROBABILISTIC:
                // baseValueSelector and lower should be ORIGINAL if they are going to get cached completely
                return false;
            case RANDOM:
                // Predict if caching will occur
                return resolvedCacheType.isNotCached() || (isBaseInherentlyCached() && !hasFiltering());
            default:
                throw new IllegalStateException("The selectionOrder (" + resolvedSelectionOrder
                        + ") is not implemented.");
        }
    }

    protected boolean isBaseInherentlyCached() {
        return false;
    }

    /**
     *
     * @param configPolicy never null
     * @param minimumCacheType never null, If caching is used (different from {@link SelectionCacheType#JUST_IN_TIME}),
     *        then it should be at least this {@link SelectionCacheType} because an ancestor already uses such caching
     *        and less would be pointless.
     * @param randomSelection true is equivalent to {@link SelectionOrder#RANDOM},
     *        false is equivalent to {@link SelectionOrder#ORIGINAL}
     * @return never null
     */
    protected abstract MoveSelector buildBaseMoveSelector(
            HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection);

    private boolean hasFiltering() {
        return filterClass != null;
    }

    private MoveSelector applyFiltering(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            MoveSelector moveSelector) {
        if (hasFiltering()) {
            SelectionFilter selectionFilter = ConfigUtils.newInstance(this, "filterClass", filterClass);
            moveSelector = new FilteringMoveSelector(moveSelector, selectionFilter);
        }
        return moveSelector;
    }

    private void validateSorting(SelectionOrder resolvedSelectionOrder) {
        if ((sorterComparatorClass != null || sorterWeightFactoryClass != null
                || sorterOrder != null || sorterClass != null)
                && resolvedSelectionOrder != SelectionOrder.SORTED) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + this
                    + ") with sorterComparatorClass (" + sorterComparatorClass
                    + ") and sorterWeightFactoryClass (" + sorterWeightFactoryClass
                    + ") and sorterOrder (" + sorterOrder
                    + ") and sorterClass (" + sorterClass
                    + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                    + ") that is not " + SelectionOrder.SORTED + ".");
        }
        if (sorterComparatorClass != null && sorterWeightFactoryClass != null) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + this
                    + ") has both a sorterComparatorClass (" + sorterComparatorClass
                    + ") and a sorterWeightFactoryClass (" + sorterWeightFactoryClass + ").");
        }
        if (sorterComparatorClass != null && sorterClass != null) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + this
                    + ") has both a sorterComparatorClass (" + sorterComparatorClass
                    + ") and a sorterClass (" + sorterClass + ").");
        }
        if (sorterWeightFactoryClass != null && sorterClass != null) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + this
                    + ") has both a sorterWeightFactoryClass (" + sorterWeightFactoryClass
                    + ") and a sorterClass (" + sorterClass + ").");
        }
        if (sorterClass != null && sorterOrder != null) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + this
                    + ") with sorterClass (" + sorterClass
                    + ") has a non-null sorterOrder (" + sorterOrder + ").");
        }
    }

    private MoveSelector applySorting(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            MoveSelector moveSelector) {
        if (resolvedSelectionOrder == SelectionOrder.SORTED) {
            SelectionSorter sorter;
            if (sorterComparatorClass != null) {
                Comparator<Object> sorterComparator = ConfigUtils.newInstance(this,
                        "sorterComparatorClass", sorterComparatorClass);
                sorter = new ComparatorSelectionSorter(sorterComparator,
                        SelectionSorterOrder.resolve(sorterOrder));
            } else if (sorterWeightFactoryClass != null) {
                SelectionSorterWeightFactory sorterWeightFactory = ConfigUtils.newInstance(this,
                        "sorterWeightFactoryClass", sorterWeightFactoryClass);
                sorter = new WeightFactorySelectionSorter(sorterWeightFactory,
                        SelectionSorterOrder.resolve(sorterOrder));
            } else if (sorterClass != null) {
                sorter = ConfigUtils.newInstance(this, "sorterClass", sorterClass);
            } else {
                throw new IllegalArgumentException("The moveSelectorConfig (" + this
                        + ") with resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs a sorterComparatorClass (" + sorterComparatorClass
                        + ") or a sorterWeightFactoryClass (" + sorterWeightFactoryClass
                        + ") or a sorterClass (" + sorterClass + ").");
            }
            moveSelector = new SortingMoveSelector(moveSelector, resolvedCacheType, sorter);
        }
        return moveSelector;
    }

    private void validateProbability(SelectionOrder resolvedSelectionOrder) {
        if (probabilityWeightFactoryClass != null
                && resolvedSelectionOrder != SelectionOrder.PROBABILISTIC) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + this
                    + ") with probabilityWeightFactoryClass (" + probabilityWeightFactoryClass
                    + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                    + ") that is not " + SelectionOrder.PROBABILISTIC + ".");
        }
    }

    private MoveSelector applyProbability(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            MoveSelector moveSelector) {
        if (resolvedSelectionOrder == SelectionOrder.PROBABILISTIC) {
            if (probabilityWeightFactoryClass == null) {
                throw new IllegalArgumentException("The moveSelectorConfig (" + this
                        + ") with resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs a probabilityWeightFactoryClass ("
                        + probabilityWeightFactoryClass + ").");
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
            moveSelector = new ShufflingMoveSelector(moveSelector, resolvedCacheType);
        }
        return moveSelector;
    }

    private MoveSelector applyCaching(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            MoveSelector moveSelector) {
        if (resolvedCacheType.isCached() && resolvedCacheType.compareTo(moveSelector.getCacheType()) > 0) {
            moveSelector = new CachingMoveSelector(moveSelector, resolvedCacheType,
                    resolvedSelectionOrder.toRandomSelectionBoolean());
        }
        return moveSelector;
    }

    private void validateSelectedLimit(SelectionCacheType minimumCacheType) {
        if (selectedCountLimit != null
                && minimumCacheType.compareTo(SelectionCacheType.JUST_IN_TIME) > 0) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + this
                    + ") with selectedCountLimit (" + selectedCountLimit
                    + ") has a minimumCacheType (" + minimumCacheType
                    + ") that is higher than " + SelectionCacheType.JUST_IN_TIME + ".");
        }
    }

    private MoveSelector applySelectedLimit(
            SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            MoveSelector moveSelector) {
        if (selectedCountLimit != null) {
            moveSelector = new SelectedCountLimitMoveSelector(moveSelector, selectedCountLimit);
        }
        return moveSelector;
    }

    /**
     * Gather a list of all descendant {@link MoveSelectorConfig}s
     * except for {@link UnionMoveSelectorConfig} and {@link CartesianProductMoveSelectorConfig}.
     *
     * @param leafMoveSelectorConfigList not null
     */
    public void extractLeafMoveSelectorConfigsIntoList(List<MoveSelectorConfig> leafMoveSelectorConfigList) {
        leafMoveSelectorConfigList.add(this);
    }

    @Override
    public C inherit(C inheritedConfig) {
        inheritCommon(inheritedConfig);
        return (C) this;
    }

    /**
     * Does not inherit subclass properties because this class and {@code foldedConfig} can be of a different type.
     *
     * @param foldedConfig never null
     */
    public void inheritFolded(MoveSelectorConfig foldedConfig) {
        inheritCommon(foldedConfig);
    }

    private void inheritCommon(MoveSelectorConfig inheritedConfig) {
        cacheType = ConfigUtils.inheritOverwritableProperty(cacheType, inheritedConfig.getCacheType());
        selectionOrder = ConfigUtils.inheritOverwritableProperty(selectionOrder, inheritedConfig.getSelectionOrder());
        filterClass = ConfigUtils.inheritOverwritableProperty(filterClass, inheritedConfig.getFilterClass());
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
        selectedCountLimit = ConfigUtils.inheritOverwritableProperty(
                selectedCountLimit, inheritedConfig.getSelectedCountLimit());

        fixedProbabilityWeight = ConfigUtils.inheritOverwritableProperty(
                fixedProbabilityWeight, inheritedConfig.getFixedProbabilityWeight());
    }

}

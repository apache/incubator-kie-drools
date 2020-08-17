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

package org.optaplanner.core.impl.heuristic.selector.move;

import java.util.Comparator;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.ComparatorSelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.WeightFactorySelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.CachingMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.FilteringMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.ProbabilityMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.SelectedCountLimitMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.ShufflingMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.decorator.SortingMoveSelector;

public abstract class AbstractMoveSelectorFactory<MoveSelectorConfig_ extends MoveSelectorConfig<MoveSelectorConfig_>>
        implements MoveSelectorFactory {

    protected final MoveSelectorConfig_ moveSelectorConfig;

    public AbstractMoveSelectorFactory(MoveSelectorConfig_ moveSelectorConfig) {
        this.moveSelectorConfig = moveSelectorConfig;
    }

    /**
     * Builds a base {@link MoveSelector} without any advanced capabilities (filtering, sorting, ...).
     * 
     * @param configPolicy never null
     * @param minimumCacheType never null, If caching is used (different from {@link SelectionCacheType#JUST_IN_TIME}),
     *        then it should be at least this {@link SelectionCacheType} because an ancestor already uses such caching
     *        and less would be pointless.
     * @param randomSelection true is equivalent to {@link SelectionOrder#RANDOM},
     *        false is equivalent to {@link SelectionOrder#ORIGINAL}
     * @return never null
     */
    protected abstract MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection);

    /**
     * {@inheritDoc}
     */
    @Override
    public MoveSelector buildMoveSelector(HeuristicConfigPolicy configPolicy, SelectionCacheType minimumCacheType,
            SelectionOrder inheritedSelectionOrder) {
        MoveSelectorConfig unfoldedMoveSelectorConfig = buildUnfoldedMoveSelectorConfig(configPolicy);
        if (unfoldedMoveSelectorConfig != null) {
            return MoveSelectorFactory.create(unfoldedMoveSelectorConfig).buildMoveSelector(configPolicy,
                    minimumCacheType, inheritedSelectionOrder);
        }

        SelectionCacheType resolvedCacheType = SelectionCacheType.resolve(moveSelectorConfig.getCacheType(), minimumCacheType);
        SelectionOrder resolvedSelectionOrder =
                SelectionOrder.resolve(moveSelectorConfig.getSelectionOrder(), inheritedSelectionOrder);

        validateCacheTypeVersusSelectionOrder(resolvedCacheType, resolvedSelectionOrder);
        validateSorting(resolvedSelectionOrder);
        validateProbability(resolvedSelectionOrder);
        validateSelectedLimit(minimumCacheType);

        boolean randomMoveSelection = determineBaseRandomSelection(resolvedCacheType, resolvedSelectionOrder);
        SelectionCacheType selectionCacheType = SelectionCacheType.max(minimumCacheType, resolvedCacheType);
        MoveSelector moveSelector = buildBaseMoveSelector(configPolicy, selectionCacheType, randomMoveSelection);
        validateResolvedCacheType(resolvedCacheType, moveSelector);

        moveSelector = applyFiltering(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        moveSelector = applySorting(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        moveSelector = applyProbability(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        moveSelector = applyShuffling(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        moveSelector = applyCaching(resolvedCacheType, resolvedSelectionOrder, moveSelector);
        moveSelector = applySelectedLimit(moveSelector);
        return moveSelector;
    }

    /**
     * To provide unfolded MoveSelectorConfig, override this method in a subclass.
     *
     * @param configPolicy never null
     * @return null if no unfolding is needed
     */
    protected MoveSelectorConfig buildUnfoldedMoveSelectorConfig(HeuristicConfigPolicy configPolicy) {
        return null;
    }

    private void validateCacheTypeVersusSelectionOrder(SelectionCacheType resolvedCacheType,
            SelectionOrder resolvedSelectionOrder) {
        switch (resolvedSelectionOrder) {
            case INHERIT:
                throw new IllegalArgumentException("The moveSelectorConfig (" + moveSelectorConfig
                        + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") which should have been resolved by now.");
            case ORIGINAL:
            case RANDOM:
                break;
            case SORTED:
            case SHUFFLED:
            case PROBABILISTIC:
                if (resolvedCacheType.isNotCached()) {
                    throw new IllegalArgumentException("The moveSelectorConfig (" + moveSelectorConfig
                            + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                            + ") which does not support the resolvedCacheType (" + resolvedCacheType + ").");
                }
                break;
            default:
                throw new IllegalStateException("The resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") is not implemented.");
        }
    }

    private void validateResolvedCacheType(SelectionCacheType resolvedCacheType, MoveSelector moveSelector) {
        if (!moveSelector.supportsPhaseAndSolverCaching()
                && resolvedCacheType.compareTo(SelectionCacheType.PHASE) >= 0) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + moveSelectorConfig
                    + ") has a resolvedCacheType (" + resolvedCacheType + ") that is not supported.\n"
                    + "Maybe don't use a <cacheType> on this type of moveSelector.");
        }
    }

    protected boolean determineBaseRandomSelection(SelectionCacheType resolvedCacheType,
            SelectionOrder resolvedSelectionOrder) {
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

    private boolean hasFiltering() {
        return moveSelectorConfig.getFilterClass() != null;
    }

    private MoveSelector applyFiltering(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            MoveSelector moveSelector) {
        if (hasFiltering()) {
            SelectionFilter selectionFilter =
                    ConfigUtils.newInstance(moveSelectorConfig, "filterClass", moveSelectorConfig.getFilterClass());
            moveSelector = new FilteringMoveSelector(moveSelector, selectionFilter);
        }
        return moveSelector;
    }

    protected void validateSorting(SelectionOrder resolvedSelectionOrder) {
        if ((moveSelectorConfig.getSorterComparatorClass() != null || moveSelectorConfig.getSorterWeightFactoryClass() != null
                || moveSelectorConfig.getSorterOrder() != null || moveSelectorConfig.getSorterClass() != null)
                && resolvedSelectionOrder != SelectionOrder.SORTED) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + moveSelectorConfig
                    + ") with sorterComparatorClass (" + moveSelectorConfig.getSorterComparatorClass()
                    + ") and sorterWeightFactoryClass (" + moveSelectorConfig.getSorterWeightFactoryClass()
                    + ") and sorterOrder (" + moveSelectorConfig.getSorterOrder()
                    + ") and sorterClass (" + moveSelectorConfig.getSorterClass()
                    + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                    + ") that is not " + SelectionOrder.SORTED + ".");
        }
        if (moveSelectorConfig.getSorterComparatorClass() != null && moveSelectorConfig.getSorterWeightFactoryClass() != null) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + moveSelectorConfig
                    + ") has both a sorterComparatorClass (" + moveSelectorConfig.getSorterComparatorClass()
                    + ") and a sorterWeightFactoryClass (" + moveSelectorConfig.getSorterWeightFactoryClass() + ").");
        }
        if (moveSelectorConfig.getSorterComparatorClass() != null && moveSelectorConfig.getSorterClass() != null) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + moveSelectorConfig
                    + ") has both a sorterComparatorClass (" + moveSelectorConfig.getSorterComparatorClass()
                    + ") and a sorterClass (" + moveSelectorConfig.getSorterClass() + ").");
        }
        if (moveSelectorConfig.getSorterWeightFactoryClass() != null && moveSelectorConfig.getSorterClass() != null) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + moveSelectorConfig
                    + ") has both a sorterWeightFactoryClass (" + moveSelectorConfig.getSorterWeightFactoryClass()
                    + ") and a sorterClass (" + moveSelectorConfig.getSorterClass() + ").");
        }
        if (moveSelectorConfig.getSorterClass() != null && moveSelectorConfig.getSorterOrder() != null) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + moveSelectorConfig
                    + ") with sorterClass (" + moveSelectorConfig.getSorterClass()
                    + ") has a non-null sorterOrder (" + moveSelectorConfig.getSorterOrder() + ").");
        }
    }

    protected MoveSelector applySorting(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            MoveSelector moveSelector) {
        if (resolvedSelectionOrder == SelectionOrder.SORTED) {
            SelectionSorter sorter;
            if (moveSelectorConfig.getSorterComparatorClass() != null) {
                Comparator<Object> sorterComparator = ConfigUtils.newInstance(moveSelectorConfig,
                        "sorterComparatorClass", moveSelectorConfig.getSorterComparatorClass());
                sorter = new ComparatorSelectionSorter(sorterComparator,
                        SelectionSorterOrder.resolve(moveSelectorConfig.getSorterOrder()));
            } else if (moveSelectorConfig.getSorterWeightFactoryClass() != null) {
                SelectionSorterWeightFactory sorterWeightFactory = ConfigUtils.newInstance(moveSelectorConfig,
                        "sorterWeightFactoryClass", moveSelectorConfig.getSorterWeightFactoryClass());
                sorter = new WeightFactorySelectionSorter(sorterWeightFactory,
                        SelectionSorterOrder.resolve(moveSelectorConfig.getSorterOrder()));
            } else if (moveSelectorConfig.getSorterClass() != null) {
                sorter = ConfigUtils.newInstance(moveSelectorConfig, "sorterClass", moveSelectorConfig.getSorterClass());
            } else {
                throw new IllegalArgumentException("The moveSelectorConfig (" + moveSelectorConfig
                        + ") with resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs a sorterComparatorClass (" + moveSelectorConfig.getSorterComparatorClass()
                        + ") or a sorterWeightFactoryClass (" + moveSelectorConfig.getSorterWeightFactoryClass()
                        + ") or a sorterClass (" + moveSelectorConfig.getSorterClass() + ").");
            }
            moveSelector = new SortingMoveSelector(moveSelector, resolvedCacheType, sorter);
        }
        return moveSelector;
    }

    private void validateProbability(SelectionOrder resolvedSelectionOrder) {
        if (moveSelectorConfig.getProbabilityWeightFactoryClass() != null
                && resolvedSelectionOrder != SelectionOrder.PROBABILISTIC) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + moveSelectorConfig
                    + ") with probabilityWeightFactoryClass (" + moveSelectorConfig.getProbabilityWeightFactoryClass()
                    + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                    + ") that is not " + SelectionOrder.PROBABILISTIC + ".");
        }
    }

    private MoveSelector applyProbability(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder,
            MoveSelector moveSelector) {
        if (resolvedSelectionOrder == SelectionOrder.PROBABILISTIC) {
            if (moveSelectorConfig.getProbabilityWeightFactoryClass() == null) {
                throw new IllegalArgumentException("The moveSelectorConfig (" + moveSelectorConfig
                        + ") with resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs a probabilityWeightFactoryClass ("
                        + moveSelectorConfig.getProbabilityWeightFactoryClass() + ").");
            }
            SelectionProbabilityWeightFactory probabilityWeightFactory = ConfigUtils.newInstance(moveSelectorConfig,
                    "probabilityWeightFactoryClass", moveSelectorConfig.getProbabilityWeightFactoryClass());
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
        if (moveSelectorConfig.getSelectedCountLimit() != null
                && minimumCacheType.compareTo(SelectionCacheType.JUST_IN_TIME) > 0) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + moveSelectorConfig
                    + ") with selectedCountLimit (" + moveSelectorConfig.getSelectedCountLimit()
                    + ") has a minimumCacheType (" + minimumCacheType
                    + ") that is higher than " + SelectionCacheType.JUST_IN_TIME + ".");
        }
    }

    private MoveSelector applySelectedLimit(MoveSelector moveSelector) {
        if (moveSelectorConfig.getSelectedCountLimit() != null) {
            moveSelector = new SelectedCountLimitMoveSelector(moveSelector, moveSelectorConfig.getSelectedCountLimit());
        }
        return moveSelector;
    }
}

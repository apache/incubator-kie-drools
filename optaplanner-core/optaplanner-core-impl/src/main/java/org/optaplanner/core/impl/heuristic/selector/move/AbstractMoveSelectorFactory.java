package org.optaplanner.core.impl.heuristic.selector.move;

import java.util.Comparator;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.AbstractSelectorFactory;
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

public abstract class AbstractMoveSelectorFactory<Solution_, MoveSelectorConfig_ extends MoveSelectorConfig<MoveSelectorConfig_>>
        extends AbstractSelectorFactory<Solution_, MoveSelectorConfig_> implements MoveSelectorFactory<Solution_> {

    public AbstractMoveSelectorFactory(MoveSelectorConfig_ moveSelectorConfig) {
        super(moveSelectorConfig);
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
    protected abstract MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection);

    /**
     * {@inheritDoc}
     */
    @Override
    public MoveSelector<Solution_> buildMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, SelectionOrder inheritedSelectionOrder) {
        MoveSelectorConfig<?> unfoldedMoveSelectorConfig = buildUnfoldedMoveSelectorConfig(configPolicy);
        if (unfoldedMoveSelectorConfig != null) {
            return MoveSelectorFactory.<Solution_> create(unfoldedMoveSelectorConfig)
                    .buildMoveSelector(configPolicy, minimumCacheType, inheritedSelectionOrder);
        }

        SelectionCacheType resolvedCacheType = SelectionCacheType.resolve(config.getCacheType(), minimumCacheType);
        SelectionOrder resolvedSelectionOrder =
                SelectionOrder.resolve(config.getSelectionOrder(), inheritedSelectionOrder);

        validateCacheTypeVersusSelectionOrder(resolvedCacheType, resolvedSelectionOrder);
        validateSorting(resolvedSelectionOrder);
        validateProbability(resolvedSelectionOrder);
        validateSelectedLimit(minimumCacheType);

        boolean randomMoveSelection = determineBaseRandomSelection(resolvedCacheType, resolvedSelectionOrder);
        SelectionCacheType selectionCacheType = SelectionCacheType.max(minimumCacheType, resolvedCacheType);
        MoveSelector<Solution_> moveSelector = buildBaseMoveSelector(configPolicy, selectionCacheType, randomMoveSelection);
        validateResolvedCacheType(resolvedCacheType, moveSelector);

        moveSelector = applyFiltering(moveSelector);
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
    protected MoveSelectorConfig<?> buildUnfoldedMoveSelectorConfig(
            HeuristicConfigPolicy<Solution_> configPolicy) {
        return null;
    }

    private void validateResolvedCacheType(SelectionCacheType resolvedCacheType, MoveSelector<Solution_> moveSelector) {
        if (!moveSelector.supportsPhaseAndSolverCaching() && resolvedCacheType.compareTo(SelectionCacheType.PHASE) >= 0) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + config
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
        return config.getFilterClass() != null;
    }

    private MoveSelector<Solution_> applyFiltering(MoveSelector<Solution_> moveSelector) {
        if (hasFiltering()) {
            SelectionFilter<Solution_, Move<Solution_>> selectionFilter =
                    ConfigUtils.newInstance(config, "filterClass", config.getFilterClass());
            moveSelector = new FilteringMoveSelector<>(moveSelector, selectionFilter);
        }
        return moveSelector;
    }

    protected void validateSorting(SelectionOrder resolvedSelectionOrder) {
        if ((config.getSorterComparatorClass() != null || config.getSorterWeightFactoryClass() != null
                || config.getSorterOrder() != null || config.getSorterClass() != null)
                && resolvedSelectionOrder != SelectionOrder.SORTED) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + config
                    + ") with sorterComparatorClass (" + config.getSorterComparatorClass()
                    + ") and sorterWeightFactoryClass (" + config.getSorterWeightFactoryClass()
                    + ") and sorterOrder (" + config.getSorterOrder()
                    + ") and sorterClass (" + config.getSorterClass()
                    + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                    + ") that is not " + SelectionOrder.SORTED + ".");
        }
        if (config.getSorterComparatorClass() != null && config.getSorterWeightFactoryClass() != null) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + config
                    + ") has both a sorterComparatorClass (" + config.getSorterComparatorClass()
                    + ") and a sorterWeightFactoryClass (" + config.getSorterWeightFactoryClass() + ").");
        }
        if (config.getSorterComparatorClass() != null && config.getSorterClass() != null) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + config
                    + ") has both a sorterComparatorClass (" + config.getSorterComparatorClass()
                    + ") and a sorterClass (" + config.getSorterClass() + ").");
        }
        if (config.getSorterWeightFactoryClass() != null && config.getSorterClass() != null) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + config
                    + ") has both a sorterWeightFactoryClass (" + config.getSorterWeightFactoryClass()
                    + ") and a sorterClass (" + config.getSorterClass() + ").");
        }
        if (config.getSorterClass() != null && config.getSorterOrder() != null) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + config
                    + ") with sorterClass (" + config.getSorterClass()
                    + ") has a non-null sorterOrder (" + config.getSorterOrder() + ").");
        }
    }

    protected MoveSelector<Solution_> applySorting(SelectionCacheType resolvedCacheType,
            SelectionOrder resolvedSelectionOrder, MoveSelector<Solution_> moveSelector) {
        if (resolvedSelectionOrder == SelectionOrder.SORTED) {
            SelectionSorter<Solution_, Move<Solution_>> sorter;
            if (config.getSorterComparatorClass() != null) {
                Comparator<Move<Solution_>> sorterComparator = ConfigUtils.newInstance(config,
                        "sorterComparatorClass", config.getSorterComparatorClass());
                sorter = new ComparatorSelectionSorter<>(sorterComparator,
                        SelectionSorterOrder.resolve(config.getSorterOrder()));
            } else if (config.getSorterWeightFactoryClass() != null) {
                SelectionSorterWeightFactory<Solution_, Move<Solution_>> sorterWeightFactory =
                        ConfigUtils.newInstance(config, "sorterWeightFactoryClass",
                                config.getSorterWeightFactoryClass());
                sorter = new WeightFactorySelectionSorter<>(sorterWeightFactory,
                        SelectionSorterOrder.resolve(config.getSorterOrder()));
            } else if (config.getSorterClass() != null) {
                sorter = ConfigUtils.newInstance(config, "sorterClass", config.getSorterClass());
            } else {
                throw new IllegalArgumentException("The moveSelectorConfig (" + config
                        + ") with resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs a sorterComparatorClass (" + config.getSorterComparatorClass()
                        + ") or a sorterWeightFactoryClass (" + config.getSorterWeightFactoryClass()
                        + ") or a sorterClass (" + config.getSorterClass() + ").");
            }
            moveSelector = new SortingMoveSelector<>(moveSelector, resolvedCacheType, sorter);
        }
        return moveSelector;
    }

    private void validateProbability(SelectionOrder resolvedSelectionOrder) {
        if (config.getProbabilityWeightFactoryClass() != null && resolvedSelectionOrder != SelectionOrder.PROBABILISTIC) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + config
                    + ") with probabilityWeightFactoryClass (" + config.getProbabilityWeightFactoryClass()
                    + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                    + ") that is not " + SelectionOrder.PROBABILISTIC + ".");
        }
    }

    private MoveSelector<Solution_> applyProbability(SelectionCacheType resolvedCacheType,
            SelectionOrder resolvedSelectionOrder, MoveSelector<Solution_> moveSelector) {
        if (resolvedSelectionOrder == SelectionOrder.PROBABILISTIC) {
            if (config.getProbabilityWeightFactoryClass() == null) {
                throw new IllegalArgumentException("The moveSelectorConfig (" + config
                        + ") with resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs a probabilityWeightFactoryClass ("
                        + config.getProbabilityWeightFactoryClass() + ").");
            }
            SelectionProbabilityWeightFactory<Solution_, Move<Solution_>> probabilityWeightFactory =
                    ConfigUtils.newInstance(config, "probabilityWeightFactoryClass",
                            config.getProbabilityWeightFactoryClass());
            moveSelector = new ProbabilityMoveSelector<>(moveSelector, resolvedCacheType, probabilityWeightFactory);
        }
        return moveSelector;
    }

    private MoveSelector<Solution_> applyShuffling(SelectionCacheType resolvedCacheType,
            SelectionOrder resolvedSelectionOrder, MoveSelector<Solution_> moveSelector) {
        if (resolvedSelectionOrder == SelectionOrder.SHUFFLED) {
            moveSelector = new ShufflingMoveSelector<>(moveSelector, resolvedCacheType);
        }
        return moveSelector;
    }

    private MoveSelector<Solution_> applyCaching(SelectionCacheType resolvedCacheType,
            SelectionOrder resolvedSelectionOrder, MoveSelector<Solution_> moveSelector) {
        if (resolvedCacheType.isCached() && resolvedCacheType.compareTo(moveSelector.getCacheType()) > 0) {
            moveSelector =
                    new CachingMoveSelector<>(moveSelector, resolvedCacheType,
                            resolvedSelectionOrder.toRandomSelectionBoolean());
        }
        return moveSelector;
    }

    private void validateSelectedLimit(SelectionCacheType minimumCacheType) {
        if (config.getSelectedCountLimit() != null
                && minimumCacheType.compareTo(SelectionCacheType.JUST_IN_TIME) > 0) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + config
                    + ") with selectedCountLimit (" + config.getSelectedCountLimit()
                    + ") has a minimumCacheType (" + minimumCacheType
                    + ") that is higher than " + SelectionCacheType.JUST_IN_TIME + ".");
        }
    }

    private MoveSelector<Solution_> applySelectedLimit(MoveSelector<Solution_> moveSelector) {
        if (config.getSelectedCountLimit() != null) {
            moveSelector = new SelectedCountLimitMoveSelector<>(moveSelector, config.getSelectedCountLimit());
        }
        return moveSelector;
    }
}

package org.optaplanner.core.impl.heuristic.selector.move.composite;

import java.util.List;
import java.util.stream.Collectors;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelectorFactory;

abstract class AbstractCompositeMoveSelectorFactory<Solution_, MoveSelectorConfig_ extends MoveSelectorConfig<MoveSelectorConfig_>>
        extends AbstractMoveSelectorFactory<Solution_, MoveSelectorConfig_> {

    public AbstractCompositeMoveSelectorFactory(MoveSelectorConfig_ moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    protected List<MoveSelector<Solution_>> buildInnerMoveSelectors(List<MoveSelectorConfig> innerMoveSelectorList,
            HeuristicConfigPolicy<Solution_> configPolicy, SelectionCacheType minimumCacheType,
            boolean randomSelection) {
        return innerMoveSelectorList.stream()
                .map(moveSelectorConfig -> {
                    MoveSelectorFactory<Solution_> innerMoveSelectorFactory =
                            MoveSelectorFactory.create(moveSelectorConfig);
                    SelectionOrder selectionOrder = SelectionOrder.fromRandomSelectionBoolean(randomSelection);
                    return innerMoveSelectorFactory.buildMoveSelector(configPolicy, minimumCacheType, selectionOrder);
                }).collect(Collectors.toList());
    }
}

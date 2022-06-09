package org.optaplanner.core.impl.heuristic.selector.move;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
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
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.composite.CartesianProductMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.composite.UnionMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactoryFactory;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactoryFactory;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.generic.PillarChangeMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.generic.PillarSwapMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.KOptMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.SubChainChangeMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.SubChainSwapMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.TailChainSwapMoveSelectorFactory;

public interface MoveSelectorFactory<Solution_> {

    static <Solution_> MoveSelectorFactory<Solution_> create(MoveSelectorConfig<?> moveSelectorConfig) {
        if (ChangeMoveSelectorConfig.class.isAssignableFrom(moveSelectorConfig.getClass())) {
            return new ChangeMoveSelectorFactory<>((ChangeMoveSelectorConfig) moveSelectorConfig);
        } else if (SwapMoveSelectorConfig.class.isAssignableFrom(moveSelectorConfig.getClass())) {
            return new SwapMoveSelectorFactory<>((SwapMoveSelectorConfig) moveSelectorConfig);
        } else if (PillarChangeMoveSelectorConfig.class.isAssignableFrom(moveSelectorConfig.getClass())) {
            return new PillarChangeMoveSelectorFactory<>((PillarChangeMoveSelectorConfig) moveSelectorConfig);
        } else if (PillarSwapMoveSelectorConfig.class.isAssignableFrom(moveSelectorConfig.getClass())) {
            return new PillarSwapMoveSelectorFactory<>((PillarSwapMoveSelectorConfig) moveSelectorConfig);
        } else if (UnionMoveSelectorConfig.class.isAssignableFrom(moveSelectorConfig.getClass())) {
            return new UnionMoveSelectorFactory<>((UnionMoveSelectorConfig) moveSelectorConfig);
        } else if (CartesianProductMoveSelectorConfig.class.isAssignableFrom(moveSelectorConfig.getClass())) {
            return new CartesianProductMoveSelectorFactory<>(
                    (CartesianProductMoveSelectorConfig) moveSelectorConfig);
        } else if (SubChainChangeMoveSelectorConfig.class.isAssignableFrom(moveSelectorConfig.getClass())) {
            return new SubChainChangeMoveSelectorFactory<>((SubChainChangeMoveSelectorConfig) moveSelectorConfig);
        } else if (SubChainSwapMoveSelectorConfig.class.isAssignableFrom(moveSelectorConfig.getClass())) {
            return new SubChainSwapMoveSelectorFactory<>((SubChainSwapMoveSelectorConfig) moveSelectorConfig);
        } else if (TailChainSwapMoveSelectorConfig.class.isAssignableFrom(moveSelectorConfig.getClass())) {
            return new TailChainSwapMoveSelectorFactory<>((TailChainSwapMoveSelectorConfig) moveSelectorConfig);
        } else if (MoveIteratorFactoryConfig.class.isAssignableFrom(moveSelectorConfig.getClass())) {
            return new MoveIteratorFactoryFactory<>((MoveIteratorFactoryConfig) moveSelectorConfig);
        } else if (MoveListFactoryConfig.class.isAssignableFrom(moveSelectorConfig.getClass())) {
            return new MoveListFactoryFactory<>((MoveListFactoryConfig) moveSelectorConfig);
        } else if (KOptMoveSelectorConfig.class.isAssignableFrom(moveSelectorConfig.getClass())) {
            return new KOptMoveSelectorFactory<>((KOptMoveSelectorConfig) moveSelectorConfig);
        } else {
            throw new IllegalArgumentException(String.format("Unknown %s type: (%s).",
                    MoveSelectorConfig.class.getSimpleName(), moveSelectorConfig.getClass().getName()));
        }
    }

    /**
     * Builds {@link MoveSelector} from the {@link MoveSelectorConfig} and provided parameters.
     *
     * @param configPolicy never null
     * @param minimumCacheType never null, If caching is used (different from {@link SelectionCacheType#JUST_IN_TIME}),
     *        then it should be at least this {@link SelectionCacheType} because an ancestor already uses such caching
     *        and less would be pointless.
     * @param inheritedSelectionOrder never null
     * @return never null
     */
    MoveSelector<Solution_> buildMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, SelectionOrder inheritedSelectionOrder);
}

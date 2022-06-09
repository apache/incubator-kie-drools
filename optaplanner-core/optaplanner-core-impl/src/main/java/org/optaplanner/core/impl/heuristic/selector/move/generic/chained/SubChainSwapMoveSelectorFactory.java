package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import java.util.Objects;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.chained.SubChainSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelectorFactory;

public class SubChainSwapMoveSelectorFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, SubChainSwapMoveSelectorConfig> {

    public SubChainSwapMoveSelectorFactory(SubChainSwapMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    protected MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        EntityDescriptor<Solution_> entityDescriptor =
                config.getEntityClass() == null ? deduceEntityDescriptor(configPolicy.getSolutionDescriptor())
                        : deduceEntityDescriptor(configPolicy.getSolutionDescriptor(), config.getEntityClass());
        SubChainSelectorConfig subChainSelectorConfig_ =
                config.getSubChainSelectorConfig() == null ? new SubChainSelectorConfig()
                        : config.getSubChainSelectorConfig();
        SubChainSelector<Solution_> leftSubChainSelector =
                SubChainSelectorFactory.<Solution_> create(subChainSelectorConfig_)
                        .buildSubChainSelector(configPolicy, entityDescriptor, minimumCacheType,
                                SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        SubChainSelectorConfig rightSubChainSelectorConfig =
                Objects.requireNonNullElse(config.getSecondarySubChainSelectorConfig(), subChainSelectorConfig_);
        SubChainSelector<Solution_> rightSubChainSelector =
                SubChainSelectorFactory.<Solution_> create(rightSubChainSelectorConfig)
                        .buildSubChainSelector(configPolicy, entityDescriptor, minimumCacheType,
                                SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        return new SubChainSwapMoveSelector<>(leftSubChainSelector, rightSubChainSelector, randomSelection,
                Objects.requireNonNullElse(config.getSelectReversingMoveToo(), true));
    }
}

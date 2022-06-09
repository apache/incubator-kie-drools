package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import java.util.Objects;

import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.chained.SubChainSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelectorFactory;

public class SubChainChangeMoveSelectorFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, SubChainChangeMoveSelectorConfig> {

    public SubChainChangeMoveSelectorFactory(SubChainChangeMoveSelectorConfig moveSelectorConfig) {
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
        SubChainSelector<Solution_> subChainSelector =
                SubChainSelectorFactory.<Solution_> create(subChainSelectorConfig_)
                        .buildSubChainSelector(configPolicy, entityDescriptor, minimumCacheType,
                                SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        ValueSelectorConfig valueSelectorConfig_ =
                config.getValueSelectorConfig() == null ? new ValueSelectorConfig() : config.getValueSelectorConfig();
        ValueSelector<Solution_> valueSelector = ValueSelectorFactory.<Solution_> create(valueSelectorConfig_)
                .buildValueSelector(configPolicy, entityDescriptor, minimumCacheType,
                        SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        if (!(valueSelector instanceof EntityIndependentValueSelector)) {
            throw new IllegalArgumentException("The moveSelectorConfig (" + config
                    + ") needs to be based on an "
                    + EntityIndependentValueSelector.class.getSimpleName() + " (" + valueSelector + ")."
                    + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");
        }
        return new SubChainChangeMoveSelector<>(subChainSelector,
                (EntityIndependentValueSelector<Solution_>) valueSelector, randomSelection,
                Objects.requireNonNullElse(config.getSelectReversingMoveToo(), true));
    }
}

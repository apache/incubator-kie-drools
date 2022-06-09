package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.KOptMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelectorFactory;

public class KOptMoveSelectorFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, KOptMoveSelectorConfig> {

    private static final int K = 3;

    public KOptMoveSelectorFactory(KOptMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    protected MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        EntitySelectorConfig entitySelectorConfig_ =
                config.getEntitySelectorConfig() == null ? new EntitySelectorConfig() : config.getEntitySelectorConfig();
        EntitySelector<Solution_> entitySelector =
                EntitySelectorFactory.<Solution_> create(entitySelectorConfig_)
                        .buildEntitySelector(configPolicy, minimumCacheType,
                                SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        ValueSelectorConfig valueSelectorConfig_ =
                config.getValueSelectorConfig() == null ? new ValueSelectorConfig() : config.getValueSelectorConfig();
        ValueSelector<Solution_>[] valueSelectors = new ValueSelector[K - 1];
        for (int i = 0; i < valueSelectors.length; i++) {
            valueSelectors[i] = ValueSelectorFactory.<Solution_> create(valueSelectorConfig_)
                    .buildValueSelector(configPolicy, entitySelector.getEntityDescriptor(), minimumCacheType,
                            SelectionOrder.fromRandomSelectionBoolean(randomSelection));

        }
        return new KOptMoveSelector<>(entitySelector, valueSelectors, randomSelection);
    }
}

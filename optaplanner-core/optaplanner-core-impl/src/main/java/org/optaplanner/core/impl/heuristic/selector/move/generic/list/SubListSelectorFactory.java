package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Objects;

import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.SubListSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelectorFactory;

public class SubListSelectorFactory<Solution_> {

    private static final int DEFAULT_MINIMUM_SUB_LIST_SIZE = 1;
    private static final int DEFAULT_MAXIMUM_SUB_LIST_SIZE = Integer.MAX_VALUE;

    public SubListSelectorFactory(SubListSelectorConfig config) {
        this.config = config;
    }

    public static <Solution_> SubListSelectorFactory<Solution_> create(SubListSelectorConfig subListSelectorConfig) {
        return new SubListSelectorFactory<>(subListSelectorConfig);
    }

    private final SubListSelectorConfig config;

    public RandomSubListSelector<Solution_> buildSubListSelector(
            HeuristicConfigPolicy<Solution_> configPolicy,
            ListVariableDescriptor<Solution_> listVariableDescriptor,
            EntitySelector<Solution_> entitySelector,
            SelectionCacheType minimumCacheType,
            SelectionOrder inheritedSelectionOrder) {
        EntityIndependentValueSelector<Solution_> valueSelector = buildEntityIndependentValueSelector(configPolicy,
                entitySelector.getEntityDescriptor(), minimumCacheType, inheritedSelectionOrder);
        int minimumSubListSize = Objects.requireNonNullElse(config.getMinimumSubListSize(), DEFAULT_MINIMUM_SUB_LIST_SIZE);
        int maximumSubListSize = Objects.requireNonNullElse(config.getMaximumSubListSize(), DEFAULT_MAXIMUM_SUB_LIST_SIZE);
        return new RandomSubListSelector<>(listVariableDescriptor, entitySelector, valueSelector,
                minimumSubListSize, maximumSubListSize);
    }

    private EntityIndependentValueSelector<Solution_> buildEntityIndependentValueSelector(
            HeuristicConfigPolicy<Solution_> configPolicy, EntityDescriptor<Solution_> entityDescriptor,
            SelectionCacheType minimumCacheType, SelectionOrder inheritedSelectionOrder) {
        ValueSelector<Solution_> valueSelector = ValueSelectorFactory.<Solution_> create(new ValueSelectorConfig())
                .buildValueSelector(configPolicy, entityDescriptor, minimumCacheType, inheritedSelectionOrder);
        if (!(valueSelector instanceof EntityIndependentValueSelector)) {
            throw new IllegalArgumentException("The subListChangeMoveSelector or subListSwapMoveSelector (" + config
                    + ") for a list variable needs to be based on an "
                    + EntityIndependentValueSelector.class.getSimpleName() + " (" + valueSelector + ")."
                    + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");

        }
        return (EntityIndependentValueSelector<Solution_>) valueSelector;
    }
}

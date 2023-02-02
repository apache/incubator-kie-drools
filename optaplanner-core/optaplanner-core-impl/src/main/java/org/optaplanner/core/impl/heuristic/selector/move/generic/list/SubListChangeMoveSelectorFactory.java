package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Objects;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.SubListChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelectorFactory;

public class SubListChangeMoveSelectorFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, SubListChangeMoveSelectorConfig> {

    public SubListChangeMoveSelectorFactory(SubListChangeMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    protected MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        SelectionOrder selectionOrder = SelectionOrder.fromRandomSelectionBoolean(randomSelection);
        EntitySelector<Solution_> entitySelector =
                EntitySelectorFactory.<Solution_> create(new EntitySelectorConfig())
                        .buildEntitySelector(configPolicy, minimumCacheType, selectionOrder);
        // TODO support coexistence of list and basic variables https://issues.redhat.com/browse/PLANNER-2755
        GenuineVariableDescriptor<Solution_> variableDescriptor =
                getTheOnlyVariableDescriptor(entitySelector.getEntityDescriptor());
        if (!variableDescriptor.isListVariable()) {
            throw new IllegalArgumentException("The subListChangeMoveSelector (" + config
                    + ") can only be used when the domain model has a list variable."
                    + " Check your @" + PlanningEntity.class.getSimpleName()
                    + " and make sure it has a @" + PlanningListVariable.class.getSimpleName() + ".");
        }

        ListVariableDescriptor<Solution_> listVariableDescriptor = (ListVariableDescriptor<Solution_>) variableDescriptor;

        RandomSubListSelector<Solution_> subListSelector = SubListSelectorFactory.<Solution_> create(config)
                .buildSubListSelector(configPolicy, listVariableDescriptor, entitySelector, minimumCacheType, selectionOrder);

        EntityIndependentValueSelector<Solution_> valueSelector = buildEntityIndependentValueSelector(configPolicy,
                entitySelector.getEntityDescriptor(), minimumCacheType, selectionOrder);

        ElementDestinationSelector<Solution_> destinationSelector =
                new ElementDestinationSelector<>(listVariableDescriptor, entitySelector, valueSelector, true);

        boolean selectReversingMoveToo = Objects.requireNonNullElse(config.getSelectReversingMoveToo(), true);

        return new RandomSubListChangeMoveSelector<>(listVariableDescriptor, subListSelector, destinationSelector,
                selectReversingMoveToo);
    }

    private EntityIndependentValueSelector<Solution_> buildEntityIndependentValueSelector(
            HeuristicConfigPolicy<Solution_> configPolicy, EntityDescriptor<Solution_> entityDescriptor,
            SelectionCacheType minimumCacheType, SelectionOrder inheritedSelectionOrder) {
        ValueSelector<Solution_> valueSelector =
                ValueSelectorFactory.<Solution_> create(new ValueSelectorConfig())
                        .buildValueSelector(configPolicy, entityDescriptor, minimumCacheType, inheritedSelectionOrder);
        if (!(valueSelector instanceof EntityIndependentValueSelector)) {
            throw new IllegalArgumentException("The subListChangeMoveSelector (" + config
                    + ") for a list variable needs to be based on an "
                    + EntityIndependentValueSelector.class.getSimpleName() + " (" + valueSelector + ")."
                    + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");

        }
        return (EntityIndependentValueSelector<Solution_>) valueSelector;
    }
}

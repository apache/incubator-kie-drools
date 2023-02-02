package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Objects;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.SubListSwapMoveSelectorConfig;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class SubListSwapMoveSelectorFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, SubListSwapMoveSelectorConfig> {

    public SubListSwapMoveSelectorFactory(SubListSwapMoveSelectorConfig moveSelectorConfig) {
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
            throw new IllegalArgumentException("The subListSwapMoveSelector (" + config
                    + ") can only be used when the domain model has a list variable."
                    + " Check your @" + PlanningEntity.class.getSimpleName()
                    + " and make sure it has a @" + PlanningListVariable.class.getSimpleName() + ".");
        }

        ListVariableDescriptor<Solution_> listVariableDescriptor = (ListVariableDescriptor<Solution_>) variableDescriptor;

        SubListSelectorFactory<Solution_> subListSelectorFactory = SubListSelectorFactory.create(config);
        RandomSubListSelector<Solution_> leftSubListSelector = subListSelectorFactory
                .buildSubListSelector(configPolicy, listVariableDescriptor, entitySelector, minimumCacheType, selectionOrder);
        RandomSubListSelector<Solution_> rightSubListSelector = subListSelectorFactory
                .buildSubListSelector(configPolicy, listVariableDescriptor, entitySelector, minimumCacheType, selectionOrder);

        boolean selectReversingMoveToo = Objects.requireNonNullElse(config.getSelectReversingMoveToo(), true);

        return new RandomSubListSwapMoveSelector<>(listVariableDescriptor, leftSubListSelector, rightSubListSelector,
                selectReversingMoveToo);
    }
}

package org.optaplanner.core.impl.heuristic.selector.move.generic.list.kopt;

import java.util.Objects;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.kopt.KOptListMoveSelectorConfig;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public final class KOptListMoveSelectorFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, KOptListMoveSelectorConfig> {

    private static final int DEFAULT_MINIMUM_K = 2;
    private static final int DEFAULT_MAXIMUM_K = 2;

    public KOptListMoveSelectorFactory(KOptListMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    protected MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        SelectionOrder selectionOrder = SelectionOrder.fromRandomSelectionBoolean(randomSelection);
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        EntitySelector<Solution_> entitySelector =
                EntitySelectorFactory.<Solution_> create(entitySelectorConfig)
                        .buildEntitySelector(configPolicy, minimumCacheType, selectionOrder);
        // TODO support coexistence of list and basic variables https://issues.redhat.com/browse/PLANNER-2755
        GenuineVariableDescriptor<Solution_> variableDescriptor =
                getTheOnlyVariableDescriptor(entitySelector.getEntityDescriptor());
        if (!variableDescriptor.isListVariable()) {
            throw new IllegalArgumentException("The kOptListMoveSelector (" + config
                    + ") can only be used when the domain model has a list variable."
                    + " Check your @" + PlanningEntity.class.getSimpleName()
                    + " and make sure it has a @" + PlanningListVariable.class.getSimpleName() + ".");
        }

        int minimumK = Objects.requireNonNullElse(config.getMinimumK(), DEFAULT_MINIMUM_K);
        if (minimumK < 2) {
            throw new IllegalArgumentException("minimumK (" + minimumK + ") must be at least 2.");
        }
        int maximumK = Objects.requireNonNullElse(config.getMaximumK(), DEFAULT_MAXIMUM_K);
        if (maximumK < minimumK) {
            throw new IllegalArgumentException("maximumK (" + maximumK + ") must be at least minimumK (" + minimumK + ").");
        }
        return new KOptListMoveSelector<>(((ListVariableDescriptor<Solution_>) variableDescriptor), entitySelector,
                minimumK, maximumK);
    }
}

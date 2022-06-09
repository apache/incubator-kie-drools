package org.optaplanner.core.impl.heuristic.selector.move.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.list.ListSwapMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelectorFactory;

public class SwapMoveSelectorFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, SwapMoveSelectorConfig> {

    public SwapMoveSelectorFactory(SwapMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    protected MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        EntitySelectorConfig entitySelectorConfig_ =
                config.getEntitySelectorConfig() == null ? new EntitySelectorConfig() : config.getEntitySelectorConfig();
        EntitySelector<Solution_> leftEntitySelector = EntitySelectorFactory.<Solution_> create(entitySelectorConfig_)
                .buildEntitySelector(configPolicy, minimumCacheType,
                        SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        EntitySelectorConfig rightEntitySelectorConfig = Objects.requireNonNullElse(config.getSecondaryEntitySelectorConfig(),
                entitySelectorConfig_);
        EntitySelector<Solution_> rightEntitySelector =
                EntitySelectorFactory.<Solution_> create(rightEntitySelectorConfig)
                        .buildEntitySelector(configPolicy, minimumCacheType,
                                SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        List<GenuineVariableDescriptor<Solution_>> variableDescriptorList =
                deduceVariableDescriptorList(leftEntitySelector.getEntityDescriptor(), config.getVariableNameIncludeList());
        if (variableDescriptorList.size() == 1 && variableDescriptorList.get(0).isListVariable()) {
            // TODO add ValueSelector to the config
            ValueSelectorFactory<Solution_> valueSelectorFactory = ValueSelectorFactory.create(new ValueSelectorConfig());
            EntityIndependentValueSelector<Solution_> leftValueSelector = buildEntityIndependentValueSelector(
                    valueSelectorFactory, configPolicy, leftEntitySelector, minimumCacheType, randomSelection);
            EntityIndependentValueSelector<Solution_> rightValueSelector = buildEntityIndependentValueSelector(
                    valueSelectorFactory, configPolicy, rightEntitySelector, minimumCacheType, randomSelection);
            return new ListSwapMoveSelector<>(
                    (ListVariableDescriptor<Solution_>) variableDescriptorList.get(0),
                    leftValueSelector,
                    rightValueSelector,
                    randomSelection);
        }
        if (variableDescriptorList.stream().noneMatch(GenuineVariableDescriptor::isListVariable)) {
            return new SwapMoveSelector<>(leftEntitySelector, rightEntitySelector, variableDescriptorList,
                    randomSelection);
        }
        throw new IllegalArgumentException("The variableDescriptorList (" + variableDescriptorList
                + ") has multiple variables and one or more of them is a @" + PlanningListVariable.class.getSimpleName()
                + ", which is currently not supported.");
    }

    private EntityIndependentValueSelector<Solution_> buildEntityIndependentValueSelector(
            ValueSelectorFactory<Solution_> valueSelectorFactory, HeuristicConfigPolicy<Solution_> configPolicy,
            EntitySelector<Solution_> entitySelector, SelectionCacheType minimumCacheType,
            boolean randomSelection) {
        ValueSelector<Solution_> valueSelector = valueSelectorFactory.buildValueSelector(configPolicy,
                entitySelector.getEntityDescriptor(),
                minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection));
        if (!(valueSelector instanceof EntityIndependentValueSelector)) {
            throw new IllegalArgumentException("The changeMoveSelector (" + this
                    + ") for a list variable needs to be based on an "
                    + EntityIndependentValueSelector.class.getSimpleName() + " (" + valueSelector + ")."
                    + " Check your valueSelectorConfig.");

        }
        return (EntityIndependentValueSelector<Solution_>) valueSelector;
    }

    @Override
    protected MoveSelectorConfig<?> buildUnfoldedMoveSelectorConfig(
            HeuristicConfigPolicy<Solution_> configPolicy) {
        EntityDescriptor<Solution_> onlyEntityDescriptor = config.getEntitySelectorConfig() == null ? null
                : EntitySelectorFactory.<Solution_> create(config.getEntitySelectorConfig())
                        .extractEntityDescriptor(configPolicy);
        if (config.getSecondaryEntitySelectorConfig() != null) {
            EntityDescriptor<Solution_> onlySecondaryEntityDescriptor =
                    EntitySelectorFactory.<Solution_> create(config.getSecondaryEntitySelectorConfig())
                            .extractEntityDescriptor(configPolicy);
            if (onlyEntityDescriptor != onlySecondaryEntityDescriptor) {
                throw new IllegalArgumentException("The entitySelector (" + config.getEntitySelectorConfig()
                        + ")'s entityClass (" + (onlyEntityDescriptor == null ? null : onlyEntityDescriptor.getEntityClass())
                        + ") and secondaryEntitySelectorConfig (" + config.getSecondaryEntitySelectorConfig()
                        + ")'s entityClass ("
                        + (onlySecondaryEntityDescriptor == null ? null : onlySecondaryEntityDescriptor.getEntityClass())
                        + ") must be the same entity class.");
            }
        }
        if (onlyEntityDescriptor != null) {
            return null;
        }
        Collection<EntityDescriptor<Solution_>> entityDescriptors =
                configPolicy.getSolutionDescriptor().getGenuineEntityDescriptors();
        return buildUnfoldedMoveSelectorConfig(entityDescriptors);
    }

    protected MoveSelectorConfig<?>
            buildUnfoldedMoveSelectorConfig(Collection<EntityDescriptor<Solution_>> entityDescriptors) {
        List<MoveSelectorConfig> moveSelectorConfigList = new ArrayList<>(entityDescriptors.size());
        for (EntityDescriptor<Solution_> entityDescriptor : entityDescriptors) {
            // No childMoveSelectorConfig.inherit() because of unfoldedMoveSelectorConfig.inheritFolded()
            SwapMoveSelectorConfig childMoveSelectorConfig = new SwapMoveSelectorConfig();
            EntitySelectorConfig childEntitySelectorConfig = new EntitySelectorConfig(config.getEntitySelectorConfig());
            if (childEntitySelectorConfig.getMimicSelectorRef() == null) {
                childEntitySelectorConfig.setEntityClass(entityDescriptor.getEntityClass());
            }
            childMoveSelectorConfig.setEntitySelectorConfig(childEntitySelectorConfig);
            if (config.getSecondaryEntitySelectorConfig() != null) {
                EntitySelectorConfig childSecondaryEntitySelectorConfig =
                        new EntitySelectorConfig(config.getSecondaryEntitySelectorConfig());
                if (childSecondaryEntitySelectorConfig.getMimicSelectorRef() == null) {
                    childSecondaryEntitySelectorConfig.setEntityClass(entityDescriptor.getEntityClass());
                }
                childMoveSelectorConfig.setSecondaryEntitySelectorConfig(childSecondaryEntitySelectorConfig);
            }
            childMoveSelectorConfig.setVariableNameIncludeList(config.getVariableNameIncludeList());
            moveSelectorConfigList.add(childMoveSelectorConfig);
        }

        MoveSelectorConfig unfoldedMoveSelectorConfig;
        if (moveSelectorConfigList.size() == 1) {
            unfoldedMoveSelectorConfig = moveSelectorConfigList.get(0);
        } else {
            unfoldedMoveSelectorConfig = new UnionMoveSelectorConfig(moveSelectorConfigList);
        }
        unfoldedMoveSelectorConfig.inheritFolded(config);
        return unfoldedMoveSelectorConfig;
    }
}

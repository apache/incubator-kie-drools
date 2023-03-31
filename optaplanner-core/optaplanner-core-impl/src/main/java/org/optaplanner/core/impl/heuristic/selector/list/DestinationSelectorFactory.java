package org.optaplanner.core.impl.heuristic.selector.list;

import java.util.Objects;

import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.common.nearby.NearbySelectionConfig;
import org.optaplanner.core.config.heuristic.selector.list.DestinationSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.AbstractSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandomFactory;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.list.nearby.NearSubListNearbyDestinationSelector;
import org.optaplanner.core.impl.heuristic.selector.list.nearby.NearValueNearbyDestinationSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelectorFactory;

public final class DestinationSelectorFactory<Solution_> extends AbstractSelectorFactory<Solution_, DestinationSelectorConfig> {

    public static <Solution_> DestinationSelectorFactory<Solution_>
            create(DestinationSelectorConfig destinationSelectorConfig) {
        return new DestinationSelectorFactory<>(destinationSelectorConfig);
    }

    private DestinationSelectorFactory(DestinationSelectorConfig destinationSelectorConfig) {
        super(destinationSelectorConfig);
    }

    public DestinationSelector<Solution_> buildDestinationSelector(
            HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType,
            boolean randomSelection) {
        SelectionOrder selectionOrder = SelectionOrder.fromRandomSelectionBoolean(randomSelection);

        ElementDestinationSelector<Solution_> baseDestinationSelector =
                buildBaseDestinationSelector(configPolicy, minimumCacheType, selectionOrder);

        return applyNearbySelection(configPolicy, minimumCacheType, selectionOrder, baseDestinationSelector);
    }

    private ElementDestinationSelector<Solution_> buildBaseDestinationSelector(
            HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType,
            SelectionOrder selectionOrder) {
        EntitySelector<Solution_> entitySelector = EntitySelectorFactory
                .<Solution_> create(Objects.requireNonNull(config.getEntitySelectorConfig()))
                .buildEntitySelector(configPolicy, minimumCacheType, selectionOrder);

        EntityIndependentValueSelector<Solution_> valueSelector = buildEntityIndependentValueSelector(configPolicy,
                entitySelector.getEntityDescriptor(), minimumCacheType, selectionOrder);

        return new ElementDestinationSelector<>(
                entitySelector,
                valueSelector,
                selectionOrder.toRandomSelectionBoolean());
    }

    private EntityIndependentValueSelector<Solution_> buildEntityIndependentValueSelector(
            HeuristicConfigPolicy<Solution_> configPolicy, EntityDescriptor<Solution_> entityDescriptor,
            SelectionCacheType minimumCacheType, SelectionOrder inheritedSelectionOrder) {
        ValueSelector<Solution_> valueSelector = ValueSelectorFactory
                .<Solution_> create(Objects.requireNonNull(config.getValueSelectorConfig()))
                .buildValueSelector(configPolicy, entityDescriptor, minimumCacheType, inheritedSelectionOrder,
                        // Do not override reinitializeVariableFilterEnabled.
                        configPolicy.isReinitializeVariableFilterEnabled(),
                        /*
                         * Filter assigned values (but only if this filtering type is allowed by the configPolicy).
                         *
                         * The destination selector requires the child value selector to only select assigned values.
                         * To guarantee this during CH, where not all values are assigned, the UnassignedValueSelector filter
                         * must be applied.
                         *
                         * In the LS phase, not only is the filter redundant because there are no unassigned values,
                         * but it would also crash if the base value selector inherits random selection order,
                         * because the filter cannot work on a never-ending child value selector.
                         * Therefore, it must not be applied even though it is requested here. This is accomplished by
                         * the configPolicy that only allows this filtering type in the CH phase.
                         */
                        ValueSelectorFactory.ListValueFilteringType.ACCEPT_ASSIGNED);
        if (!(valueSelector instanceof EntityIndependentValueSelector)) {
            throw new IllegalArgumentException("The destinationSelector (" + config
                    + ") for a list variable needs to be based on an "
                    + EntityIndependentValueSelector.class.getSimpleName() + " (" + valueSelector + ")."
                    + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");

        }
        return (EntityIndependentValueSelector<Solution_>) valueSelector;
    }

    private DestinationSelector<Solution_> applyNearbySelection(
            HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType,
            SelectionOrder resolvedSelectionOrder,
            ElementDestinationSelector<Solution_> destinationSelector) {
        NearbySelectionConfig nearbySelectionConfig = config.getNearbySelectionConfig();
        if (nearbySelectionConfig == null) {
            return destinationSelector;
        }

        nearbySelectionConfig.validateNearby(minimumCacheType, resolvedSelectionOrder);

        boolean randomSelection = resolvedSelectionOrder.toRandomSelectionBoolean();

        NearbyDistanceMeter<?, ?> nearbyDistanceMeter =
                configPolicy.getClassInstanceCache().newInstance(nearbySelectionConfig,
                        "nearbyDistanceMeterClass", nearbySelectionConfig.getNearbyDistanceMeterClass());
        // TODO Check nearbyDistanceMeterClass.getGenericInterfaces() to confirm generic type S is an entityClass
        NearbyRandom nearbyRandom = NearbyRandomFactory.create(nearbySelectionConfig).buildNearbyRandom(randomSelection);

        if (nearbySelectionConfig.getOriginValueSelectorConfig() != null) {
            ValueSelector<Solution_> originValueSelector = ValueSelectorFactory
                    .<Solution_> create(nearbySelectionConfig.getOriginValueSelectorConfig())
                    .buildValueSelector(configPolicy, destinationSelector.getEntityDescriptor(), minimumCacheType,
                            resolvedSelectionOrder);
            return new NearValueNearbyDestinationSelector<>(
                    destinationSelector,
                    ((EntityIndependentValueSelector<Solution_>) originValueSelector),
                    nearbyDistanceMeter,
                    nearbyRandom,
                    randomSelection);
        } else if (nearbySelectionConfig.getOriginSubListSelectorConfig() != null) {
            SubListSelector<Solution_> subListSelector = SubListSelectorFactory
                    .<Solution_> create(nearbySelectionConfig.getOriginSubListSelectorConfig())
                    // Entity selector not needed for replaying selector.
                    .buildSubListSelector(configPolicy, null, minimumCacheType, resolvedSelectionOrder);
            return new NearSubListNearbyDestinationSelector<>(
                    destinationSelector,
                    subListSelector,
                    nearbyDistanceMeter,
                    nearbyRandom,
                    randomSelection);
        } else {
            throw new IllegalArgumentException("The destinationSelector (" + config
                    + ")'s nearbySelectionConfig (" + nearbySelectionConfig
                    + ") requires an originSubListSelector or an originValueSelector.");
        }
    }
}

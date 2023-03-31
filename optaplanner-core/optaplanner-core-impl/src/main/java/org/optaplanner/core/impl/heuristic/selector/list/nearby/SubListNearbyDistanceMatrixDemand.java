package org.optaplanner.core.impl.heuristic.selector.list.nearby;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMatrix;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.list.ElementDestinationSelector;
import org.optaplanner.core.impl.heuristic.selector.list.mimic.MimicReplayingSubListSelector;
import org.optaplanner.core.impl.solver.ClassInstanceCache;
import org.optaplanner.core.impl.util.MemoizingSupply;

/**
 * Demands a distance matrix where the origins are planning values and nearby destinations are both planning entities and
 * values.
 * <p>
 * Calculating {@link NearbyDistanceMatrix} is very expensive,
 * therefore we want to reuse it as much as possible.
 * <p>
 * In cases where the demand represents the same nearby selector (as defined by
 * {@link SubListNearbyDistanceMatrixDemand#equals(Object)})
 * the {@link SupplyManager} ensures that the same supply instance is returned
 * with the pre-computed {@link NearbyDistanceMatrix}.
 *
 * @param <Solution_>
 * @param <Origin_> planning values
 * @param <Destination_> mix of planning entities and planning values
 */
public final class SubListNearbyDistanceMatrixDemand<Solution_, Origin_, Destination_>
        implements Demand<MemoizingSupply<NearbyDistanceMatrix<Origin_, Destination_>>> {

    private final NearbyDistanceMeter<Origin_, Destination_> meter;
    private final ElementDestinationSelector<Solution_> childDestinationSelector;
    private final MimicReplayingSubListSelector<Solution_> replayingOriginSubListSelector;
    private final ToIntFunction<Origin_> destinationSizeFunction;

    public SubListNearbyDistanceMatrixDemand(
            NearbyDistanceMeter<Origin_, Destination_> meter,
            ElementDestinationSelector<Solution_> childDestinationSelector,
            MimicReplayingSubListSelector<Solution_> replayingOriginSubListSelector,
            ToIntFunction<Origin_> destinationSizeFunction) {
        this.meter = meter;
        this.childDestinationSelector = childDestinationSelector;
        this.replayingOriginSubListSelector = replayingOriginSubListSelector;
        this.destinationSizeFunction = destinationSizeFunction;
    }

    @Override
    public MemoizingSupply<NearbyDistanceMatrix<Origin_, Destination_>> createExternalizedSupply(SupplyManager supplyManager) {
        Supplier<NearbyDistanceMatrix<Origin_, Destination_>> supplier = () -> {
            final long childSize = childDestinationSelector.getSize();
            if (childSize > Integer.MAX_VALUE) {
                throw new IllegalStateException("The childSize (" + childSize + ") is higher than Integer.MAX_VALUE.");
            }

            long originSize = replayingOriginSubListSelector.getValueCount();
            if (originSize > Integer.MAX_VALUE) {
                throw new IllegalStateException("The originSubListSelector (" + replayingOriginSubListSelector
                        + ") has a subListSize (" + originSize
                        + ") which is higher than Integer.MAX_VALUE.");
            }
            // Destinations: mix of planning entities and values extracted from a destination selector.
            // Distance "matrix" elements must be user classes (entities and values) because they are exposed
            // to the user-implemented NearbyDistanceMeter. Therefore, we cannot insert ElementRefs in the matrix.
            // For this reason, destination selector's endingIterator() returns entities and values produced by
            // its child selectors.
            Function<Origin_, Iterator<Destination_>> destinationIteratorProvider =
                    origin -> (Iterator<Destination_>) childDestinationSelector.endingIterator();
            NearbyDistanceMatrix<Origin_, Destination_> nearbyDistanceMatrix =
                    new NearbyDistanceMatrix<>(meter, (int) originSize, destinationIteratorProvider, destinationSizeFunction);
            // Origins: values extracted from a subList selector.
            replayingOriginSubListSelector.endingValueIterator()
                    .forEachRemaining(origin -> nearbyDistanceMatrix.addAllDestinations((Origin_) origin));
            return nearbyDistanceMatrix;
        };
        return new MemoizingSupply<>(supplier);
    }

    /**
     * Two instances of this class are considered equal if and only if:
     *
     * <ul>
     * <li>Their meter instances are equal.</li>
     * <li>Their child destination selectors are equal.</li>
     * <li>Their replaying origin subList selectors are equal.</li>
     * </ul>
     *
     * Otherwise as defined by {@link Object#equals(Object)}.
     *
     * @see ClassInstanceCache for how we ensure equality for meter instances in particular and selectors in general.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubListNearbyDistanceMatrixDemand<?, ?, ?> that = (SubListNearbyDistanceMatrixDemand<?, ?, ?>) o;
        return Objects.equals(meter, that.meter)
                && Objects.equals(childDestinationSelector, that.childDestinationSelector)
                && Objects.equals(replayingOriginSubListSelector, that.replayingOriginSubListSelector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meter, childDestinationSelector, replayingOriginSubListSelector);
    }
}

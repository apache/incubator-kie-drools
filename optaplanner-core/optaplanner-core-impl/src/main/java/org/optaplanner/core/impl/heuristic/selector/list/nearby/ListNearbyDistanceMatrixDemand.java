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
import org.optaplanner.core.impl.heuristic.selector.value.mimic.MimicReplayingValueSelector;
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
 * {@link ListNearbyDistanceMatrixDemand#equals(Object)})
 * the {@link SupplyManager} ensures that the same supply instance is returned
 * with the pre-computed {@link NearbyDistanceMatrix}.
 *
 * @param <Solution_>
 * @param <Origin_> planning values
 * @param <Destination_> mix of planning entities and planning values
 */
public final class ListNearbyDistanceMatrixDemand<Solution_, Origin_, Destination_>
        implements Demand<MemoizingSupply<NearbyDistanceMatrix<Origin_, Destination_>>> {

    private final NearbyDistanceMeter<Origin_, Destination_> meter;
    private final ElementDestinationSelector<Solution_> childDestinationSelector;
    private final MimicReplayingValueSelector<Solution_> replayingOriginValueSelector;
    private final ToIntFunction<Origin_> destinationSizeFunction;

    public ListNearbyDistanceMatrixDemand(
            NearbyDistanceMeter<Origin_, Destination_> meter,
            ElementDestinationSelector<Solution_> childDestinationSelector,
            MimicReplayingValueSelector<Solution_> replayingOriginValueSelector,
            ToIntFunction<Origin_> destinationSizeFunction) {
        this.meter = meter;
        this.childDestinationSelector = childDestinationSelector;
        this.replayingOriginValueSelector = replayingOriginValueSelector;
        this.destinationSizeFunction = destinationSizeFunction;
    }

    @Override
    public MemoizingSupply<NearbyDistanceMatrix<Origin_, Destination_>> createExternalizedSupply(SupplyManager supplyManager) {
        Supplier<NearbyDistanceMatrix<Origin_, Destination_>> supplier = () -> {
            final long childSize = childDestinationSelector.getSize();
            if (childSize > Integer.MAX_VALUE) {
                throw new IllegalStateException("The childSize (" + childSize + ") is higher than Integer.MAX_VALUE.");
            }

            long originSize = replayingOriginValueSelector.getSize();
            if (originSize > Integer.MAX_VALUE) {
                throw new IllegalStateException("The originValueSelector (" + replayingOriginValueSelector
                        + ") has a valueSize (" + originSize
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
            // Origins: values extracted from a value selector.
            // Replaying selector's ending iterator uses the recording selector's ending iterator. Since list variables
            // use entity independent value selectors, we can pass null here.
            replayingOriginValueSelector.endingIterator(null)
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
     * <li>Their replaying origin value selectors are equal.</li>
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
        ListNearbyDistanceMatrixDemand<?, ?, ?> that = (ListNearbyDistanceMatrixDemand<?, ?, ?>) o;
        return Objects.equals(meter, that.meter)
                && Objects.equals(childDestinationSelector, that.childDestinationSelector)
                && Objects.equals(replayingOriginValueSelector, that.replayingOriginValueSelector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meter, childDestinationSelector, replayingOriginValueSelector);
    }
}

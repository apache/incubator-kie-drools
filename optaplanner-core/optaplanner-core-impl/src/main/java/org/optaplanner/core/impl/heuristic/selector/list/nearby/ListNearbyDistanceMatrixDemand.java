package org.optaplanner.core.impl.heuristic.selector.list.nearby;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.AbstractNearbyDistanceMatrixDemand;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMatrix;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.list.ElementDestinationSelector;
import org.optaplanner.core.impl.heuristic.selector.value.mimic.MimicReplayingValueSelector;

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
final class ListNearbyDistanceMatrixDemand<Solution_, Origin_, Destination_>
        extends
        AbstractNearbyDistanceMatrixDemand<Origin_, Destination_, ElementDestinationSelector<Solution_>, MimicReplayingValueSelector<Solution_>> {

    private final ToIntFunction<Origin_> destinationSizeFunction;

    public ListNearbyDistanceMatrixDemand(NearbyDistanceMeter<Origin_, Destination_> meter,
            NearbyRandom random,
            ElementDestinationSelector<Solution_> childDestinationSelector,
            MimicReplayingValueSelector<Solution_> replayingOriginValueSelector,
            ToIntFunction<Origin_> destinationSizeFunction) {
        super(meter, random, childDestinationSelector, replayingOriginValueSelector);
        this.destinationSizeFunction = destinationSizeFunction;
    }

    @Override
    protected NearbyDistanceMatrix<Origin_, Destination_> supplyNearbyDistanceMatrix() {
        final long childSize = childSelector.getSize();
        if (childSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The childSize (" + childSize + ") is higher than Integer.MAX_VALUE.");
        }

        long originSize = replayingSelector.getSize();
        if (originSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The originValueSelector (" + replayingSelector
                    + ") has a valueSize (" + originSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        // Destinations: mix of planning entities and values extracted from a destination selector.
        // Distance "matrix" elements must be user classes (entities and values) because they are exposed
        // to the user-implemented NearbyDistanceMeter. Therefore, we cannot insert ElementRefs in the matrix.
        // For this reason, destination selector's endingIterator() returns entities and values produced by
        // its child selectors.
        Function<Origin_, Iterator<Destination_>> destinationIteratorProvider =
                origin -> (Iterator<Destination_>) childSelector.endingIterator();
        NearbyDistanceMatrix<Origin_, Destination_> nearbyDistanceMatrix =
                new NearbyDistanceMatrix<>(meter, (int) originSize, destinationIteratorProvider, destinationSizeFunction);
        // Origins: values extracted from a value selector.
        // Replaying selector's ending iterator uses the recording selector's ending iterator. Since list variables
        // use entity independent value selectors, we can pass null here.
        replayingSelector.endingIterator(null)
                .forEachRemaining(origin -> nearbyDistanceMatrix.addAllDestinations((Origin_) origin));
        return nearbyDistanceMatrix;
    }

}

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
import org.optaplanner.core.impl.heuristic.selector.list.RandomSubListSelector;
import org.optaplanner.core.impl.heuristic.selector.list.mimic.MimicReplayingSubListSelector;
import org.optaplanner.core.impl.solver.ClassInstanceCache;
import org.optaplanner.core.impl.util.MemoizingSupply;

/**
 * Demands a distance matrix where both the origins and nearby destinations are planning values.
 * <p>
 * Calculating {@link NearbyDistanceMatrix} is very expensive,
 * therefore we want to reuse it as much as possible.
 * <p>
 * In cases where the demand represents the same nearby selector (as defined by
 * {@link SubListNearbySubListMatrixDemand#equals(Object)})
 * the {@link SupplyManager} ensures that the same supply instance is returned
 * with the pre-computed {@link NearbyDistanceMatrix}.
 *
 * @param <Solution_>
 * @param <Origin_> planning values
 * @param <Destination_> planning values
 */
public final class SubListNearbySubListMatrixDemand<Solution_, Origin_, Destination_>
        implements Demand<MemoizingSupply<NearbyDistanceMatrix<Origin_, Destination_>>> {

    private final NearbyDistanceMeter<Origin_, Destination_> meter;
    private final RandomSubListSelector<Solution_> childSubListSelector;
    private final MimicReplayingSubListSelector<Solution_> replayingOriginSubListSelector;
    private final ToIntFunction<Origin_> destinationSizeFunction;

    public SubListNearbySubListMatrixDemand(
            NearbyDistanceMeter<Origin_, Destination_> meter,
            RandomSubListSelector<Solution_> childSubListSelector,
            MimicReplayingSubListSelector<Solution_> replayingOriginSubListSelector,
            ToIntFunction<Origin_> destinationSizeFunction) {
        this.meter = meter;
        this.childSubListSelector = childSubListSelector;
        this.replayingOriginSubListSelector = replayingOriginSubListSelector;
        this.destinationSizeFunction = destinationSizeFunction;
    }

    @Override
    public MemoizingSupply<NearbyDistanceMatrix<Origin_, Destination_>> createExternalizedSupply(SupplyManager supplyManager) {
        Supplier<NearbyDistanceMatrix<Origin_, Destination_>> supplier = () -> {
            final long childSize = childSubListSelector.getValueCount();
            if (childSize > Integer.MAX_VALUE) {
                throw new IllegalStateException("The childSize (" + childSize + ") is higher than Integer.MAX_VALUE.");
            }

            long originSize = replayingOriginSubListSelector.getValueCount();
            if (originSize > Integer.MAX_VALUE) {
                throw new IllegalStateException("The originSubListSelector (" + replayingOriginSubListSelector
                        + ") has a subListSize (" + originSize
                        + ") which is higher than Integer.MAX_VALUE.");
            }
            // Destinations: values extracted from a subList selector.
            // Distance "matrix" cannot contain subLists because:
            // 1. Its elements are exposed to the user-implemented NearbyDistanceMeter. SubList is an internal class.
            // 2. The matrix is static; it's computed once when solving starts and then never changes.
            //    The subLists available in the solution change with every step.
            Function<Origin_, Iterator<Destination_>> destinationIteratorProvider =
                    origin -> (Iterator<Destination_>) childSubListSelector.endingValueIterator();
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
     * <li>Their child subList selectors are equal.</li>
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
        SubListNearbySubListMatrixDemand<?, ?, ?> that = (SubListNearbySubListMatrixDemand<?, ?, ?>) o;
        return Objects.equals(meter, that.meter)
                && Objects.equals(childSubListSelector, that.childSubListSelector)
                && Objects.equals(replayingOriginSubListSelector, that.replayingOriginSubListSelector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meter, childSubListSelector, replayingOriginSubListSelector);
    }
}

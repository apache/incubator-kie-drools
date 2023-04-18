package org.optaplanner.core.impl.heuristic.selector.list.nearby;

import java.util.Iterator;

import org.optaplanner.core.impl.heuristic.selector.common.nearby.AbstractNearbyDistanceMatrixDemand;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.list.DestinationSelector;
import org.optaplanner.core.impl.heuristic.selector.list.ElementDestinationSelector;
import org.optaplanner.core.impl.heuristic.selector.list.ElementRef;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.mimic.MimicReplayingValueSelector;

public final class NearValueNearbyDestinationSelector<Solution_>
        extends AbstractNearbyDestinationSelector<Solution_, MimicReplayingValueSelector<Solution_>>
        implements DestinationSelector<Solution_> {

    public NearValueNearbyDestinationSelector(ElementDestinationSelector<Solution_> childDestinationSelector,
            EntityIndependentValueSelector<Solution_> originValueSelector, NearbyDistanceMeter<?, ?> nearbyDistanceMeter,
            NearbyRandom nearbyRandom, boolean randomSelection) {
        super(childDestinationSelector, originValueSelector, nearbyDistanceMeter, nearbyRandom, randomSelection);
    }

    @Override
    protected MimicReplayingValueSelector<Solution_> castReplayingSelector(Object uncastReplayingSelector) {
        if (!(uncastReplayingSelector instanceof MimicReplayingValueSelector)) {
            // In order to select a nearby destination, we must first have something to be near by.
            throw new IllegalStateException("Impossible state: Nearby destination selector (" + this +
                    ") did not receive a replaying value selector (" + uncastReplayingSelector + ").");
        }
        return (MimicReplayingValueSelector<Solution_>) uncastReplayingSelector;
    }

    @Override
    protected AbstractNearbyDistanceMatrixDemand<?, ?, ?, ?> createDemand() {
        return new ListNearbyDistanceMatrixDemand<>(nearbyDistanceMeter, nearbyRandom, childSelector, replayingSelector,
                origin -> computeDestinationSize());
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public Iterator<ElementRef> iterator() {
        Iterator<Object> replayingOriginValueIterator = replayingSelector.iterator();
        if (!randomSelection) {
            return new OriginalNearbyDestinationIterator(nearbyDistanceMatrix, replayingOriginValueIterator, this::elementRef,
                    childSelector.getSize());
        } else {
            return new RandomNearbyDestinationIterator(nearbyDistanceMatrix, nearbyRandom, workingRandom,
                    replayingOriginValueIterator, this::elementRef, childSelector.getSize());
        }
    }

}

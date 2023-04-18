package org.optaplanner.core.impl.heuristic.selector.entity.nearby;

import java.util.Iterator;

import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMatrix;

final class OriginalNearbyEntityIterator extends SelectionIterator<Object> {

    private final Iterator<Object> replayingOriginEntityIterator;
    private final long childSize;
    private final NearbyDistanceMatrix<Object, Object> nearbyDistanceMatrix;
    private int nextNearbyIndex;

    public OriginalNearbyEntityIterator(NearbyDistanceMatrix<Object, Object> nearbyDistanceMatrix,
            Iterator<Object> replayingOriginEntityIterator, long childSize, boolean discardNearbyIndexZero) {
        this.nearbyDistanceMatrix = nearbyDistanceMatrix;
        this.replayingOriginEntityIterator = replayingOriginEntityIterator;
        this.childSize = childSize;
        this.nextNearbyIndex = discardNearbyIndexZero ? 1 : 0;
    }

    @Override
    public boolean hasNext() {
        return replayingOriginEntityIterator.hasNext() && nextNearbyIndex < childSize;
    }

    @Override
    public Object next() {
        /*
         * The origin iterator is guaranteed to be a replaying iterator.
         * Therefore next() will point to whatever that the related recording iterator was pointing to at the time
         * when its next() was called.
         * As a result, origin here will be constant unless next() on the original recording iterator is called
         * first.
         */
        Object origin = replayingOriginEntityIterator.next();
        Object next = nearbyDistanceMatrix.getDestination(origin, nextNearbyIndex);
        nextNearbyIndex++;
        return next;
    }

}

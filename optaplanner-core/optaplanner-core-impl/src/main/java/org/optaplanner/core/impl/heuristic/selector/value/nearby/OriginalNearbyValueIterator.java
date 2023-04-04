package org.optaplanner.core.impl.heuristic.selector.value.nearby;

import java.util.Iterator;

import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMatrix;
import org.optaplanner.core.impl.util.MemoizingSupply;

final class OriginalNearbyValueIterator extends SelectionIterator<Object> {

    private final MemoizingSupply<NearbyDistanceMatrix<Object, Object>> nearbyDistanceMatrixSupply;
    private final Iterator<Object> replayingIterator;
    private final long childSize;

    private boolean originSelected = false;
    private boolean originIsNotEmpty;
    private Object origin;

    private int nextNearbyIndex;

    public OriginalNearbyValueIterator(MemoizingSupply<NearbyDistanceMatrix<Object, Object>> nearbyDistanceMatrixSupply,
            Iterator<Object> replayingIterator, long childSize, boolean discardNearbyIndexZero) {
        this.nearbyDistanceMatrixSupply = nearbyDistanceMatrixSupply;
        this.replayingIterator = replayingIterator;
        this.childSize = childSize;
        this.nextNearbyIndex = discardNearbyIndexZero ? 1 : 0;
    }

    private void selectOrigin() {
        if (originSelected) {
            return;
        }
        /*
         * The origin iterator is guaranteed to be a replaying iterator.
         * Therefore next() will point to whatever that the related recording iterator was pointing to at the time
         * when its next() was called.
         * As a result, origin here will be constant unless next() on the original recording iterator is called
         * first.
         */
        originIsNotEmpty = replayingIterator.hasNext();
        origin = replayingIterator.next();
        originSelected = true;
    }

    @Override
    public boolean hasNext() {
        selectOrigin();
        return originIsNotEmpty && nextNearbyIndex < childSize;
    }

    @Override
    public Object next() {
        selectOrigin();
        Object next = nearbyDistanceMatrixSupply.read().getDestination(origin, nextNearbyIndex);
        nextNearbyIndex++;
        return next;
    }

}

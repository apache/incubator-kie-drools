package org.optaplanner.core.impl.heuristic.selector.entity.nearby;

import java.util.Iterator;
import java.util.ListIterator;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMatrix;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;

public final class NearEntityNearbyEntitySelector<Solution_> extends AbstractEntitySelector<Solution_> {

    protected final EntitySelector<Solution_> childEntitySelector;
    protected final EntitySelector<Solution_> replayingOriginEntitySelector;
    protected final NearbyDistanceMeter<?, ?> nearbyDistanceMeter;
    protected final NearbyRandom nearbyRandom;
    protected final boolean randomSelection;
    protected final boolean discardNearbyIndexZero = true; // TODO deactivate me when appropriate

    protected NearbyDistanceMatrix nearbyDistanceMatrix = null;

    public NearEntityNearbyEntitySelector(EntitySelector<Solution_> childEntitySelector,
            EntitySelector<Solution_> originEntitySelector, NearbyDistanceMeter<?, ?> nearbyDistanceMeter,
            NearbyRandom nearbyRandom, boolean randomSelection) {
        this.childEntitySelector = childEntitySelector;
        if (!(originEntitySelector instanceof MimicReplayingEntitySelector)) {
            // In order to select a nearby entity, we must first have something to be near by.
            throw new IllegalStateException("Impossible state: Nearby entity selector (" + this +
                    ") did not receive a replaying entity selector (" + originEntitySelector + ").");
        }
        this.replayingOriginEntitySelector = originEntitySelector;
        this.nearbyDistanceMeter = nearbyDistanceMeter;
        this.nearbyRandom = nearbyRandom;
        this.randomSelection = randomSelection;
        if (randomSelection && nearbyRandom == null) {
            throw new IllegalArgumentException("The entitySelector (" + this
                    + ") with randomSelection (" + randomSelection + ") has no nearbyRandom (" + nearbyRandom + ").");
        }
        // TODO Remove this limitation
        if (!childEntitySelector.getEntityDescriptor().getEntityClass().isAssignableFrom(
                originEntitySelector.getEntityDescriptor().getEntityClass())) {
            throw new IllegalArgumentException("The entitySelector (" + this
                    + ") has an entityClass ("
                    + childEntitySelector.getEntityDescriptor().getEntityClass()
                    + ") which is not a superclass of the originEntitySelector's entityClass ("
                    + originEntitySelector.getEntityDescriptor().getEntityClass() + ").");
        }
        phaseLifecycleSupport.addEventListener(childEntitySelector);
        phaseLifecycleSupport.addEventListener(originEntitySelector);
    }

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        // Cannot be done during solverStarted because
        super.phaseStarted(phaseScope);
        long originSize = replayingOriginEntitySelector.getSize();
        if (originSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The originEntitySelector (" + replayingOriginEntitySelector
                    + ") has an entitySize (" + originSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        final long childSize = childEntitySelector.getSize();
        if (childSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The childEntitySelector (" + childEntitySelector
                    + ") has an entitySize (" + childSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }

        nearbyDistanceMatrix = new NearbyDistanceMatrix(nearbyDistanceMeter, (int) originSize,
                origin -> childEntitySelector.endingIterator(), origin -> computeDestinationSize(childSize));
        replayingOriginEntitySelector.endingIterator()
                .forEachRemaining(origin -> nearbyDistanceMatrix.addAllDestinations(origin));
    }

    private int computeDestinationSize(long childSize) {
        int destinationSize = (int) childSize;
        if (randomSelection) {
            // Reduce RAM memory usage by reducing destinationSize if nearbyRandom will never select a higher value
            int overallSizeMaximum = nearbyRandom.getOverallSizeMaximum();
            if (discardNearbyIndexZero) {
                if (overallSizeMaximum != Integer.MAX_VALUE) {
                    overallSizeMaximum++;
                }
            }
            if (destinationSize > overallSizeMaximum) {
                destinationSize = overallSizeMaximum;
            }
        }
        return destinationSize;
    }

    @Override
    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        nearbyDistanceMatrix = null;
    }

    @Override
    public EntityDescriptor<Solution_> getEntityDescriptor() {
        return childEntitySelector.getEntityDescriptor();
    }

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public boolean isNeverEnding() {
        return randomSelection;
    }

    @Override
    public long getSize() {
        return childEntitySelector.getSize() - (discardNearbyIndexZero ? 1 : 0);
    }

    @Override
    public Iterator<Object> iterator() {
        Iterator<Object> replayingOriginEntityIterator = replayingOriginEntitySelector.iterator();
        if (!randomSelection) {
            return new OriginalNearbyEntityIterator(replayingOriginEntityIterator, childEntitySelector.getSize());
        } else {
            return new RandomNearbyEntityIterator(replayingOriginEntityIterator, childEntitySelector.getSize());
        }
    }

    @Override
    public ListIterator<Object> listIterator() {
        // TODO Not yet implemented
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        // TODO Not yet implemented
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Object> endingIterator() {
        // TODO It should probably use nearby order
        // It must include the origin entity too
        return childEntitySelector.endingIterator();
    }

    private final class OriginalNearbyEntityIterator extends SelectionIterator<Object> {

        private final Iterator<Object> replayingOriginEntityIterator;
        private final long childSize;
        private int nextNearbyIndex;

        public OriginalNearbyEntityIterator(Iterator<Object> replayingOriginEntityIterator, long childSize) {
            this.replayingOriginEntityIterator = replayingOriginEntityIterator;
            this.childSize = childSize;
            nextNearbyIndex = discardNearbyIndexZero ? 1 : 0;
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

    private final class RandomNearbyEntityIterator extends SelectionIterator<Object> {

        private final Iterator<Object> replayingOriginEntityIterator;
        private final int nearbySize;

        public RandomNearbyEntityIterator(Iterator<Object> replayingOriginEntityIterator, long childSize) {
            this.replayingOriginEntityIterator = replayingOriginEntityIterator;
            if (childSize > Integer.MAX_VALUE) {
                throw new IllegalStateException("The valueSelector (" + this
                        + ") has an entitySize (" + childSize
                        + ") which is higher than Integer.MAX_VALUE.");
            }
            nearbySize = (int) childSize - (discardNearbyIndexZero ? 1 : 0);
        }

        @Override
        public boolean hasNext() {

            return replayingOriginEntityIterator.hasNext() && nearbySize > 0;
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
            int nearbyIndex = nearbyRandom.nextInt(workingRandom, nearbySize);
            if (discardNearbyIndexZero) {
                nearbyIndex++;
            }
            return nearbyDistanceMatrix.getDestination(origin, nearbyIndex);
        }

    }

}

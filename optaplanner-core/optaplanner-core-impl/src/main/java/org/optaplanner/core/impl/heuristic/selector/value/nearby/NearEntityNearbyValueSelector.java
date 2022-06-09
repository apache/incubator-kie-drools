package org.optaplanner.core.impl.heuristic.selector.value.nearby;

import java.util.Iterator;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMatrix;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.AbstractValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;

public final class NearEntityNearbyValueSelector<Solution_> extends AbstractValueSelector<Solution_> {

    protected final ValueSelector<Solution_> childValueSelector;
    protected final EntitySelector<Solution_> replayingOriginEntitySelector;
    protected final NearbyDistanceMeter<?, ?> nearbyDistanceMeter;
    protected final NearbyRandom nearbyRandom;
    protected final boolean randomSelection;
    protected final boolean discardNearbyIndexZero;

    protected NearbyDistanceMatrix nearbyDistanceMatrix = null;

    public NearEntityNearbyValueSelector(ValueSelector<Solution_> childValueSelector,
            EntitySelector<Solution_> originEntitySelector, NearbyDistanceMeter<?, ?> nearbyDistanceMeter,
            NearbyRandom nearbyRandom, boolean randomSelection) {
        this.childValueSelector = childValueSelector;
        if (!(originEntitySelector instanceof MimicReplayingEntitySelector)) {
            // In order to select a nearby value, we must first have something to be near by.
            throw new IllegalStateException("Impossible state: Nearby value selector (" + this +
                    ") did not receive a replaying entity selector (" + originEntitySelector + ").");
        }
        this.replayingOriginEntitySelector = originEntitySelector;
        this.nearbyDistanceMeter = nearbyDistanceMeter;
        this.nearbyRandom = nearbyRandom;
        this.randomSelection = randomSelection;
        if (randomSelection && nearbyRandom == null) {
            throw new IllegalArgumentException("The valueSelector (" + this
                    + ") with randomSelection (" + randomSelection + ") has no nearbyRandom (" + nearbyRandom + ").");
        }
        discardNearbyIndexZero = childValueSelector.getVariableDescriptor().getVariablePropertyType().isAssignableFrom(
                originEntitySelector.getEntityDescriptor().getEntityClass());
        phaseLifecycleSupport.addEventListener(childValueSelector);
        phaseLifecycleSupport.addEventListener(originEntitySelector);
    }

    @Override
    public GenuineVariableDescriptor<Solution_> getVariableDescriptor() {
        return childValueSelector.getVariableDescriptor();
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
        nearbyDistanceMatrix = new NearbyDistanceMatrix(nearbyDistanceMeter, (int) originSize,
                childValueSelector::endingIterator, this::computeDestinationSize);
        replayingOriginEntitySelector.endingIterator()
                .forEachRemaining(origin -> nearbyDistanceMatrix.addAllDestinations(origin));
    }

    private int computeDestinationSize(Object origin) {
        long childSize = childValueSelector.getSize(origin);
        if (childSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The childEntitySelector (" + childValueSelector
                    + ") has an entitySize (" + childSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        int destinationSize = (int) childSize;
        if (randomSelection) {
            // Reduce RAM memory usage by reducing destinationSize if nearbyRandom will never select a higher value
            int overallSizeMaximum = nearbyRandom.getOverallSizeMaximum();
            if (discardNearbyIndexZero && overallSizeMaximum < Integer.MAX_VALUE) {
                overallSizeMaximum++;
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

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isCountable() {
        return childValueSelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return randomSelection || !isCountable();
    }

    @Override
    public long getSize(Object entity) {
        return childValueSelector.getSize(entity) - (discardNearbyIndexZero ? 1 : 0);
    }

    @Override
    public Iterator<Object> iterator(Object entity) {
        Iterator<Object> replayingOriginEntityIterator = replayingOriginEntitySelector.iterator();
        if (!randomSelection) {
            return new OriginalEntityNearbyValueIterator(replayingOriginEntityIterator, childValueSelector.getSize(entity));
        } else {
            return new RandomEntityNearbyValueIterator(replayingOriginEntityIterator, childValueSelector.getSize(entity));
        }
    }

    @Override
    public Iterator<Object> endingIterator(Object entity) {
        // TODO It should probably use nearby order
        // It must include the origin entity too
        return childValueSelector.endingIterator(entity);
    }

    private final class OriginalEntityNearbyValueIterator extends SelectionIterator<Object> {

        private final Iterator<Object> replayingOriginEntityIterator;
        private final long childSize;

        private boolean originSelected = false;
        private boolean originIsNotEmpty;
        private Object origin;

        private int nextNearbyIndex;

        public OriginalEntityNearbyValueIterator(Iterator<Object> replayingOriginEntityIterator, long childSize) {
            this.replayingOriginEntityIterator = replayingOriginEntityIterator;
            this.childSize = childSize;
            nextNearbyIndex = discardNearbyIndexZero ? 1 : 0;
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
            originIsNotEmpty = replayingOriginEntityIterator.hasNext();
            origin = replayingOriginEntityIterator.next();
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
            Object next = nearbyDistanceMatrix.getDestination(origin, nextNearbyIndex);
            nextNearbyIndex++;
            return next;
        }

    }

    private final class RandomEntityNearbyValueIterator extends SelectionIterator<Object> {

        private final Iterator<Object> replayingOriginEntityIterator;
        private final int nearbySize;

        public RandomEntityNearbyValueIterator(Iterator<Object> replayingOriginEntityIterator, long childSize) {
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + replayingOriginEntitySelector + ", " + childValueSelector + ")";
    }

}

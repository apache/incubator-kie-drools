package org.optaplanner.core.impl.heuristic.selector.value.nearby;

import java.util.Iterator;
import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMatrix;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.value.AbstractValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.mimic.MimicReplayingValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.util.MemoizingSupply;

public final class NearValueNearbyValueSelector<Solution_> extends AbstractValueSelector<Solution_>
        implements EntityIndependentValueSelector<Solution_> {

    private final EntityIndependentValueSelector<Solution_> childValueSelector;
    private final MimicReplayingValueSelector<Solution_> replayingOriginValueSelector;
    private final NearbyDistanceMeter<?, ?> nearbyDistanceMeter;
    private final NearbyRandom nearbyRandom;
    private final boolean randomSelection;
    private final ListValueNearbyDistanceMatrixDemand<Solution_, ?, ?> nearbyDistanceMatrixDemand;

    private MemoizingSupply<NearbyDistanceMatrix<Object, Object>> nearbyDistanceMatrixSupply = null;

    public NearValueNearbyValueSelector(
            EntityIndependentValueSelector<Solution_> childValueSelector,
            EntityIndependentValueSelector<Solution_> originValueSelector,
            NearbyDistanceMeter<?, ?> nearbyDistanceMeter,
            NearbyRandom nearbyRandom, boolean randomSelection) {
        this.childValueSelector = childValueSelector;
        if (!(originValueSelector instanceof MimicReplayingValueSelector)) {
            // In order to select a nearby value, we must first have something to be near by.
            throw new IllegalStateException("Impossible state: Nearby value selector (" + this +
                    ") did not receive a replaying value selector (" + originValueSelector + ").");
        }
        this.replayingOriginValueSelector = (MimicReplayingValueSelector<Solution_>) originValueSelector;
        this.nearbyDistanceMeter = nearbyDistanceMeter;
        this.nearbyRandom = nearbyRandom;
        this.randomSelection = randomSelection;
        if (randomSelection && nearbyRandom == null) {
            throw new IllegalArgumentException("The entitySelector (" + this
                    + ") with randomSelection (" + randomSelection + ") has no nearbyRandom (" + nearbyRandom + ").");
        }
        this.nearbyDistanceMatrixDemand = new ListValueNearbyDistanceMatrixDemand<>(
                nearbyDistanceMeter,
                childValueSelector,
                replayingOriginValueSelector,
                this::computeDestinationSize);

        phaseLifecycleSupport.addEventListener(childValueSelector);
        phaseLifecycleSupport.addEventListener(originValueSelector);
    }

    @Override
    public GenuineVariableDescriptor<Solution_> getVariableDescriptor() {
        return childValueSelector.getVariableDescriptor();
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        super.solvingStarted(solverScope);
        /*
         * Supply will ask questions of the child selector.
         * However, child selector will only be initialized during phase start.
         * Yet we still want the very expensive nearby distance matrix to be reused across phases.
         * Therefore we request the supply here, but actually lazily initialize it during phase start.
         */
        nearbyDistanceMatrixSupply = (MemoizingSupply) solverScope.getScoreDirector().getSupplyManager()
                .demand(nearbyDistanceMatrixDemand);
    }

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        // Lazily initialize the supply, so that steps can then have uniform performance.
        nearbyDistanceMatrixSupply.read();
    }

    private int computeDestinationSize(Object origin) {
        long childSize = childValueSelector.getSize();
        if (childSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The childValueSelector (" + childValueSelector
                    + ") has a valueSize (" + childSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }

        int destinationSize = (int) childSize;
        if (randomSelection) {
            // Reduce RAM memory usage by reducing destinationSize if nearbyRandom will never select a higher value
            int overallSizeMaximum = nearbyRandom.getOverallSizeMaximum();
            if (destinationSize > overallSizeMaximum) {
                destinationSize = overallSizeMaximum;
            }
        }
        return destinationSize;
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        super.solvingEnded(solverScope);
        solverScope.getScoreDirector().getSupplyManager().cancel(nearbyDistanceMatrixDemand);
        nearbyDistanceMatrixSupply = null;
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
        return getSize();
    }

    @Override
    public long getSize() {
        return childValueSelector.getSize();
    }

    @Override
    public Iterator<Object> iterator(Object entity) {
        return iterator();
    }

    @Override
    public Iterator<Object> iterator() {
        Iterator<Object> replayingOriginValueIterator = replayingOriginValueSelector.iterator();
        if (!randomSelection) {
            return new OriginalNearbyValueIterator(replayingOriginValueIterator, childValueSelector.getSize());
        } else {
            return new RandomNearbyValueIterator(replayingOriginValueIterator, childValueSelector.getSize());
        }
    }

    @Override
    public Iterator<Object> endingIterator(Object entity) {
        // TODO It should probably use nearby order
        // It must include the origin entity too
        return childValueSelector.endingIterator(entity);
    }

    private final class OriginalNearbyValueIterator extends SelectionIterator<Object> {

        private final Iterator<Object> replayingOriginValueIterator;
        private final long childSize;

        private boolean originSelected = false;
        private boolean originIsNotEmpty;
        private Object origin;

        private int nextNearbyIndex;

        public OriginalNearbyValueIterator(Iterator<Object> replayingOriginValueIterator, long childSize) {
            this.replayingOriginValueIterator = replayingOriginValueIterator;
            this.childSize = childSize;
            nextNearbyIndex = 0;
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
            originIsNotEmpty = replayingOriginValueIterator.hasNext();
            origin = replayingOriginValueIterator.next();
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

    private final class RandomNearbyValueIterator extends SelectionIterator<Object> {

        private final Iterator<Object> replayingOriginValueIterator;
        private final int nearbySize;

        public RandomNearbyValueIterator(Iterator<Object> replayingOriginValueIterator, long childSize) {
            this.replayingOriginValueIterator = replayingOriginValueIterator;
            if (childSize > Integer.MAX_VALUE) {
                throw new IllegalStateException("The destinationSelector (" + this
                        + ") has a destinationSize (" + childSize
                        + ") which is higher than Integer.MAX_VALUE.");
            }
            nearbySize = (int) childSize;
        }

        @Override
        public boolean hasNext() {
            return replayingOriginValueIterator.hasNext() && nearbySize > 0;
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
            Object origin = replayingOriginValueIterator.next();
            int nearbyIndex = nearbyRandom.nextInt(workingRandom, nearbySize);
            return nearbyDistanceMatrixSupply.read().getDestination(origin, nearbyIndex);
        }

    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        NearValueNearbyValueSelector<?> that = (NearValueNearbyValueSelector<?>) other;
        return randomSelection == that.randomSelection
                && Objects.equals(childValueSelector, that.childValueSelector)
                && Objects.equals(replayingOriginValueSelector, that.replayingOriginValueSelector)
                && Objects.equals(nearbyDistanceMeter, that.nearbyDistanceMeter)
                && Objects.equals(nearbyRandom, that.nearbyRandom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(childValueSelector, replayingOriginValueSelector, nearbyDistanceMeter, nearbyRandom,
                randomSelection);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + replayingOriginValueSelector + ", " + childValueSelector + ")";
    }
}

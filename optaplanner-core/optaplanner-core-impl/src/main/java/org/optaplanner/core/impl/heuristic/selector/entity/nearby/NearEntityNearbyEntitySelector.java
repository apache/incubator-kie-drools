package org.optaplanner.core.impl.heuristic.selector.entity.nearby;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Objects;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMatrix;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMatrixDemand;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.util.MemoizingSupply;

public final class NearEntityNearbyEntitySelector<Solution_> extends AbstractEntitySelector<Solution_> {

    private final EntitySelector<Solution_> childEntitySelector;
    private final EntitySelector<Solution_> replayingOriginEntitySelector;
    private final NearbyDistanceMeter<?, ?> nearbyDistanceMeter;
    private final NearbyRandom nearbyRandom;
    private final boolean randomSelection;
    // TODO deactivate me when appropriate; consider if this field needs to be included in selector equality
    private final boolean discardNearbyIndexZero = true;
    private final NearbyDistanceMatrixDemand<Solution_, ?, ?> nearbyDistanceMatrixDemand;

    private MemoizingSupply<NearbyDistanceMatrix<Object, Object>> nearbyDistanceMatrixSupply = null;

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
        this.nearbyDistanceMatrixDemand = new NearbyDistanceMatrixDemand<>(
                nearbyDistanceMeter,
                childEntitySelector,
                replayingOriginEntitySelector,
                origin -> computeDestinationSize(childEntitySelector.getSize()));

        phaseLifecycleSupport.addEventListener(childEntitySelector);
        phaseLifecycleSupport.addEventListener(originEntitySelector);
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
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        super.solvingEnded(solverScope);
        solverScope.getScoreDirector().getSupplyManager().cancel(nearbyDistanceMatrixDemand);
        nearbyDistanceMatrixSupply = null;
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
            Object next = nearbyDistanceMatrixSupply.read().getDestination(origin, nextNearbyIndex);
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
            return nearbyDistanceMatrixSupply.read().getDestination(origin, nearbyIndex);
        }

    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        NearEntityNearbyEntitySelector<?> that = (NearEntityNearbyEntitySelector<?>) other;
        return randomSelection == that.randomSelection
                && Objects.equals(childEntitySelector, that.childEntitySelector)
                && Objects.equals(replayingOriginEntitySelector, that.replayingOriginEntitySelector)
                && Objects.equals(nearbyDistanceMeter, that.nearbyDistanceMeter)
                && Objects.equals(nearbyRandom, that.nearbyRandom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(childEntitySelector, replayingOriginEntitySelector, nearbyDistanceMeter, nearbyRandom,
                randomSelection);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + replayingOriginEntitySelector + ", " + childEntitySelector + ")";
    }
}

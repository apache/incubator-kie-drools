package org.optaplanner.core.impl.heuristic.selector.list.nearby;

import java.util.Iterator;
import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableDemand;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonListInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.selector.AbstractSelector;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMatrix;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.list.DestinationSelector;
import org.optaplanner.core.impl.heuristic.selector.list.ElementDestinationSelector;
import org.optaplanner.core.impl.heuristic.selector.list.ElementRef;
import org.optaplanner.core.impl.heuristic.selector.list.SubList;
import org.optaplanner.core.impl.heuristic.selector.list.SubListSelector;
import org.optaplanner.core.impl.heuristic.selector.list.mimic.MimicReplayingSubListSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.util.MemoizingSupply;

public final class NearSubListNearbyDestinationSelector<Solution_> extends AbstractSelector<Solution_>
        implements DestinationSelector<Solution_> {

    private final ElementDestinationSelector<Solution_> childDestinationSelector;
    private final MimicReplayingSubListSelector<Solution_> replayingOriginSubListSelector;
    private final NearbyDistanceMeter<?, ?> nearbyDistanceMeter;
    private final NearbyRandom nearbyRandom;
    private final boolean randomSelection;
    private final SubListNearbyDistanceMatrixDemand<Solution_, ?, ?> nearbyDistanceMatrixDemand;

    private MemoizingSupply<NearbyDistanceMatrix<Object, Object>> nearbyDistanceMatrixSupply = null;
    private SingletonInverseVariableSupply inverseVariableSupply;
    private IndexVariableSupply indexVariableSupply;

    public NearSubListNearbyDestinationSelector(
            ElementDestinationSelector<Solution_> childDestinationSelector,
            SubListSelector<Solution_> originSubListSelector,
            NearbyDistanceMeter<?, ?> nearbyDistanceMeter,
            NearbyRandom nearbyRandom, boolean randomSelection) {
        this.childDestinationSelector = childDestinationSelector;
        if (!(originSubListSelector instanceof MimicReplayingSubListSelector)) {
            // In order to select a nearby destination, we must first have something to be near by.
            throw new IllegalStateException("Impossible state: Nearby destination selector (" + this +
                    ") did not receive a replaying subList selector (" + originSubListSelector + ").");
        }
        this.replayingOriginSubListSelector = (MimicReplayingSubListSelector<Solution_>) originSubListSelector;
        this.nearbyDistanceMeter = nearbyDistanceMeter;
        this.nearbyRandom = nearbyRandom;
        this.randomSelection = randomSelection;
        if (randomSelection && nearbyRandom == null) {
            throw new IllegalArgumentException("The destinationSelector (" + this
                    + ") with randomSelection (" + randomSelection + ") has no nearbyRandom (" + nearbyRandom + ").");
        }
        this.nearbyDistanceMatrixDemand = new SubListNearbyDistanceMatrixDemand<>(
                nearbyDistanceMeter,
                childDestinationSelector,
                replayingOriginSubListSelector,
                this::computeDestinationSize);

        phaseLifecycleSupport.addEventListener(childDestinationSelector);
        phaseLifecycleSupport.addEventListener(originSubListSelector);
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        super.solvingStarted(solverScope);
        SupplyManager supplyManager = solverScope.getScoreDirector().getSupplyManager();
        ListVariableDescriptor<Solution_> listVariableDescriptor = childDestinationSelector.getVariableDescriptor();
        /*
         * Supply will ask questions of the child selector.
         * However, child selector will only be initialized during phase start.
         * Yet we still want the very expensive nearby distance matrix to be reused across phases.
         * Therefore we request the supply here, but actually lazily initialize it during phase start.
         */
        nearbyDistanceMatrixSupply = (MemoizingSupply) supplyManager.demand(nearbyDistanceMatrixDemand);
        inverseVariableSupply = supplyManager.demand(new SingletonListInverseVariableDemand<>(listVariableDescriptor));
        indexVariableSupply = supplyManager.demand(new IndexVariableDemand<>(listVariableDescriptor));
    }

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        // Lazily initialize the supply, so that steps can then have uniform performance.
        nearbyDistanceMatrixSupply.read();
    }

    private int computeDestinationSize(Object origin) {
        long childSize = childDestinationSelector.getSize();
        if (childSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The childDestinationSelector (" + childDestinationSelector
                    + ") has a destinationSize (" + childSize
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
        inverseVariableSupply = null;
        indexVariableSupply = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isCountable() {
        return childDestinationSelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return randomSelection || !isCountable();
    }

    @Override
    public long getSize() {
        return childDestinationSelector.getSize();
    }

    @Override
    public Iterator<ElementRef> iterator() {
        Iterator<SubList> replayingOriginSubListIterator = replayingOriginSubListSelector.iterator();
        if (!randomSelection) {
            return new OriginalSubListNearbyDestinationIterator(replayingOriginSubListIterator,
                    childDestinationSelector.getSize());
        } else {
            return new RandomSubListNearbyDestinationIterator(replayingOriginSubListIterator,
                    childDestinationSelector.getSize());
        }
    }

    private final class OriginalSubListNearbyDestinationIterator extends SelectionIterator<ElementRef> {

        private final Iterator<SubList> replayingOriginSubListIterator;
        private final long childSize;

        private boolean originSelected = false;
        private boolean originIsNotEmpty;
        private Object origin;

        private int nextNearbyIndex;

        public OriginalSubListNearbyDestinationIterator(Iterator<SubList> replayingOriginSubListIterator, long childSize) {
            this.replayingOriginSubListIterator = replayingOriginSubListIterator;
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
            originIsNotEmpty = replayingOriginSubListIterator.hasNext();
            SubList subList = replayingOriginSubListIterator.next();
            // Origin is the subList's first element.
            origin = firstElement(subList);
            originSelected = true;
        }

        @Override
        public boolean hasNext() {
            selectOrigin();
            return originIsNotEmpty && nextNearbyIndex < childSize;
        }

        @Override
        public ElementRef next() {
            selectOrigin();
            Object next = nearbyDistanceMatrixSupply.read().getDestination(origin, nextNearbyIndex);
            nextNearbyIndex++;
            return elementRef(next);
        }

    }

    private final class RandomSubListNearbyDestinationIterator extends SelectionIterator<ElementRef> {

        private final Iterator<SubList> replayingOriginSubListIterator;
        private final int nearbySize;

        public RandomSubListNearbyDestinationIterator(Iterator<SubList> replayingOriginSubListIterator, long childSize) {
            this.replayingOriginSubListIterator = replayingOriginSubListIterator;
            if (childSize > Integer.MAX_VALUE) {
                throw new IllegalStateException("The destinationSelector (" + this
                        + ") has a destinationSize (" + childSize
                        + ") which is higher than Integer.MAX_VALUE.");
            }
            nearbySize = (int) childSize;
        }

        @Override
        public boolean hasNext() {
            return replayingOriginSubListIterator.hasNext() && nearbySize > 0;
        }

        @Override
        public ElementRef next() {
            /*
             * The subList iterator is guaranteed to be a replaying iterator.
             * Therefore next() will point to whatever that the related recording iterator was pointing to at the time
             * when its next() was called.
             * As a result, subList here will be constant unless next() on the original recording iterator is called
             * first.
             */
            SubList subList = replayingOriginSubListIterator.next();
            // Origin is the subList's first element.
            Object origin = firstElement(subList);
            int nearbyIndex = nearbyRandom.nextInt(workingRandom, nearbySize);
            Object next = nearbyDistanceMatrixSupply.read().getDestination(origin, nearbyIndex);
            return elementRef(next);
        }

    }

    private Object firstElement(SubList subList) {
        return replayingOriginSubListSelector.getVariableDescriptor().getElement(subList.getEntity(), subList.getFromIndex());
    }

    private ElementRef elementRef(Object next) {
        if (childDestinationSelector.getEntityDescriptor().matchesEntity(next)) {
            return ElementRef.of(next, 0);
        }
        return ElementRef.of(
                inverseVariableSupply.getInverseSingleton(next),
                indexVariableSupply.getIndex(next) + 1);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        NearSubListNearbyDestinationSelector<?> that = (NearSubListNearbyDestinationSelector<?>) other;
        return randomSelection == that.randomSelection
                && Objects.equals(childDestinationSelector, that.childDestinationSelector)
                && Objects.equals(replayingOriginSubListSelector, that.replayingOriginSubListSelector)
                && Objects.equals(nearbyDistanceMeter, that.nearbyDistanceMeter)
                && Objects.equals(nearbyRandom, that.nearbyRandom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(childDestinationSelector, replayingOriginSubListSelector, nearbyDistanceMeter, nearbyRandom,
                randomSelection);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + replayingOriginSubListSelector + ", " + childDestinationSelector + ")";
    }
}

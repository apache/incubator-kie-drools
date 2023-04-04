package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import java.util.Objects;

import org.optaplanner.core.impl.heuristic.selector.AbstractDemandEnabledSelector;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.util.MemoizingSupply;

public abstract class AbstractNearbySelector<Solution_, ChildSelector_ extends PhaseLifecycleListener<Solution_>, ReplayingSelector_ extends PhaseLifecycleListener<Solution_>>
        extends AbstractDemandEnabledSelector<Solution_> {

    protected final ChildSelector_ childSelector;
    protected final ReplayingSelector_ replayingSelector;
    protected final NearbyDistanceMeter<?, ?> nearbyDistanceMeter;
    protected final NearbyRandom nearbyRandom;
    protected final boolean randomSelection;
    private final AbstractNearbyDistanceMatrixDemand<?, ?, ?, ?> nearbyDistanceMatrixDemand;

    protected MemoizingSupply<NearbyDistanceMatrix<Object, Object>> nearbyDistanceMatrixSupply = null;

    protected AbstractNearbySelector(ChildSelector_ childSelector, Object replayingSelector,
            NearbyDistanceMeter<?, ?> nearbyDistanceMeter, NearbyRandom nearbyRandom, boolean randomSelection) {
        this.childSelector = childSelector;
        this.replayingSelector = castReplayingSelector(replayingSelector);
        this.nearbyDistanceMeter = nearbyDistanceMeter;
        if (randomSelection && nearbyRandom == null) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") with randomSelection (" + randomSelection + ") has no nearbyRandom (" + nearbyRandom + ").");
        }
        this.nearbyRandom = nearbyRandom;
        this.randomSelection = randomSelection;
        this.nearbyDistanceMatrixDemand = createDemand();
        this.phaseLifecycleSupport.addEventListener(childSelector);
        this.phaseLifecycleSupport.addEventListener(this.replayingSelector);
    }

    protected abstract ReplayingSelector_ castReplayingSelector(Object uncastReplayingSelector);

    protected abstract AbstractNearbyDistanceMatrixDemand<?, ?, ?, ?> createDemand();

    public final AbstractNearbyDistanceMatrixDemand<?, ?, ?, ?> getNearbyDistanceMatrixDemand() {
        return nearbyDistanceMatrixDemand;
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
    public final void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        // Lazily initialize the supply, so that steps can then have uniform performance.
        nearbyDistanceMatrixSupply.read();
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        super.solvingEnded(solverScope);
        solverScope.getScoreDirector().getSupplyManager().cancel(nearbyDistanceMatrixDemand);
        nearbyDistanceMatrixSupply = null;
    }

    @Override
    public final boolean isNeverEnding() {
        return randomSelection || !isCountable();
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public final boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        AbstractNearbySelector<?, ?, ?> that = (AbstractNearbySelector<?, ?, ?>) other;
        return randomSelection == that.randomSelection
                && Objects.equals(childSelector, that.childSelector)
                && Objects.equals(replayingSelector, that.replayingSelector)
                && Objects.equals(nearbyDistanceMeter, that.nearbyDistanceMeter)
                && Objects.equals(nearbyRandom, that.nearbyRandom);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(randomSelection, childSelector, replayingSelector, nearbyDistanceMeter, nearbyRandom);
    }

    @Override
    public final String toString() {
        return getClass().getSimpleName() + "(" + replayingSelector + ", " + childSelector + ")";
    }
}

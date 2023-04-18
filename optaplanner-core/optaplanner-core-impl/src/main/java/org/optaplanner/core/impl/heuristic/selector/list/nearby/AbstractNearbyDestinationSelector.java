package org.optaplanner.core.impl.heuristic.selector.list.nearby;

import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableDemand;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonListInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.AbstractNearbySelector;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.list.DestinationSelector;
import org.optaplanner.core.impl.heuristic.selector.list.ElementDestinationSelector;
import org.optaplanner.core.impl.heuristic.selector.list.ElementRef;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
import org.optaplanner.core.impl.solver.scope.SolverScope;

abstract class AbstractNearbyDestinationSelector<Solution_, ReplayingSelector_ extends PhaseLifecycleListener<Solution_>>
        extends AbstractNearbySelector<Solution_, ElementDestinationSelector<Solution_>, ReplayingSelector_>
        implements DestinationSelector<Solution_> {

    protected SingletonInverseVariableSupply inverseVariableSupply;
    protected IndexVariableSupply indexVariableSupply;

    public AbstractNearbyDestinationSelector(ElementDestinationSelector<Solution_> childDestinationSelector,
            Object originSubListSelector, NearbyDistanceMeter<?, ?> nearbyDistanceMeter, NearbyRandom nearbyRandom,
            boolean randomSelection) {
        super(childDestinationSelector, originSubListSelector, nearbyDistanceMeter, nearbyRandom, randomSelection);
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        super.solvingStarted(solverScope);
        SupplyManager supplyManager = solverScope.getScoreDirector().getSupplyManager();
        ListVariableDescriptor<Solution_> listVariableDescriptor = childSelector.getVariableDescriptor();
        inverseVariableSupply = supplyManager.demand(new SingletonListInverseVariableDemand<>(listVariableDescriptor));
        indexVariableSupply = supplyManager.demand(new IndexVariableDemand<>(listVariableDescriptor));
    }

    protected int computeDestinationSize() {
        long childSize = childSelector.getSize();
        if (childSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The childDestinationSelector (" + childSelector
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
        inverseVariableSupply = null;
        indexVariableSupply = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isCountable() {
        return childSelector.isCountable();
    }

    @Override
    public long getSize() {
        return childSelector.getSize();
    }

    protected ElementRef elementRef(Object next) {
        if (childSelector.getEntityDescriptor().matchesEntity(next)) {
            return ElementRef.of(next, 0);
        }
        return ElementRef.of(
                inverseVariableSupply.getInverseSingleton(next),
                indexVariableSupply.getIndex(next) + 1);
    }

}

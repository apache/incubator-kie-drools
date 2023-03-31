package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Iterator;

import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableDemand;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonListInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.list.DestinationSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.GenericMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public class ListChangeMoveSelector<Solution_> extends GenericMoveSelector<Solution_> {

    private final EntityIndependentValueSelector<Solution_> sourceValueSelector;
    private final DestinationSelector<Solution_> destinationSelector;
    private final boolean randomSelection;

    private SingletonInverseVariableSupply inverseVariableSupply;
    private IndexVariableSupply indexVariableSupply;

    public ListChangeMoveSelector(
            EntityIndependentValueSelector<Solution_> sourceValueSelector,
            DestinationSelector<Solution_> destinationSelector,
            boolean randomSelection) {
        this.sourceValueSelector = sourceValueSelector;
        this.destinationSelector = destinationSelector;
        this.randomSelection = randomSelection;

        phaseLifecycleSupport.addEventListener(sourceValueSelector);
        phaseLifecycleSupport.addEventListener(destinationSelector);
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        super.solvingStarted(solverScope);
        ListVariableDescriptor<Solution_> listVariableDescriptor =
                (ListVariableDescriptor<Solution_>) sourceValueSelector.getVariableDescriptor();
        SupplyManager supplyManager = solverScope.getScoreDirector().getSupplyManager();
        inverseVariableSupply = supplyManager.demand(new SingletonListInverseVariableDemand<>(listVariableDescriptor));
        indexVariableSupply = supplyManager.demand(new IndexVariableDemand<>(listVariableDescriptor));
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        super.solvingEnded(solverScope);
        inverseVariableSupply = null;
        indexVariableSupply = null;
    }

    @Override
    public long getSize() {
        return sourceValueSelector.getSize() * destinationSelector.getSize();
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        if (randomSelection) {
            return new RandomListChangeIterator<>(
                    inverseVariableSupply,
                    indexVariableSupply,
                    sourceValueSelector,
                    destinationSelector);
        } else {
            return new OriginalListChangeIterator<>(
                    inverseVariableSupply,
                    indexVariableSupply,
                    sourceValueSelector,
                    destinationSelector);
        }
    }

    @Override
    public boolean isCountable() {
        return sourceValueSelector.isCountable() && destinationSelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return randomSelection || sourceValueSelector.isNeverEnding() || destinationSelector.isNeverEnding();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sourceValueSelector + ", " + destinationSelector + ")";
    }
}

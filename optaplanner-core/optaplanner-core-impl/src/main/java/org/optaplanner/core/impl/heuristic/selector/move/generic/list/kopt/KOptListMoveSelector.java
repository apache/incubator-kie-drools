package org.optaplanner.core.impl.heuristic.selector.move.generic.list.kopt;

import java.util.Iterator;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableDemand;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonListInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.GenericMoveSelector;
import org.optaplanner.core.impl.solver.scope.SolverScope;

final class KOptListMoveSelector<Solution_> extends GenericMoveSelector<Solution_> {

    private final ListVariableDescriptor<Solution_> listVariableDescriptor;
    private final EntitySelector<Solution_> entitySelector;
    private final int minK;
    private final int maxK;
    private SingletonInverseVariableSupply inverseVariableSupply;
    private IndexVariableSupply indexVariableSupply;

    public KOptListMoveSelector(
            ListVariableDescriptor<Solution_> listVariableDescriptor,
            EntitySelector<Solution_> entitySelector,
            int minK,
            int maxK) {
        this.listVariableDescriptor = listVariableDescriptor;
        this.entitySelector = entitySelector;
        this.minK = minK;
        this.maxK = maxK;
        phaseLifecycleSupport.addEventListener(entitySelector);
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        super.solvingStarted(solverScope);
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
        Iterator<Object> entityIterator = entitySelector.endingIterator();
        long total = 0;
        while (entityIterator.hasNext()) {
            Object entity = entityIterator.next();
            int valueSelectorSize = listVariableDescriptor.getListSize(entity);
            for (int i = minK; i < Math.min(valueSelectorSize, maxK); i++) {
                if (valueSelectorSize > i) { // need more than k nodes in order to perform a k-opt
                    long kOptMoveTypes = KOptUtils.getPureKOptMoveTypes(i);

                    // A tour with n nodes have n - 1 edges
                    // And we chose k of them to remove in a k-opt
                    long edgeChoices = CombinatoricsUtils.binomialCoefficient(valueSelectorSize - 1, i);
                    total += kOptMoveTypes * edgeChoices;
                }
            }
        }
        return total;
    }

    private long factorialUpToCount(long toMultiply, int count) {
        long total = toMultiply;
        for (int i = 1; i < count; i++) {
            total *= (toMultiply - i);
        }
        return total;
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        return new KOptListMoveIterator<>(workingRandom, listVariableDescriptor, inverseVariableSupply, indexVariableSupply,
                entitySelector, minK, maxK);
    }

    @Override
    public boolean isCountable() {
        return false;
    }

    @Override
    public boolean isNeverEnding() {
        return true;
    }
}

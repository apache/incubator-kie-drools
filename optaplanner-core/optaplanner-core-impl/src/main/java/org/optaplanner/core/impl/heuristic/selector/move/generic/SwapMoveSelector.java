package org.optaplanner.core.impl.heuristic.selector.move.generic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractOriginalSwapIterator;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractRandomSwapIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.ChainedSwapMove;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public class SwapMoveSelector<Solution_> extends GenericMoveSelector<Solution_> {

    protected final EntitySelector<Solution_> leftEntitySelector;
    protected final EntitySelector<Solution_> rightEntitySelector;
    protected final List<GenuineVariableDescriptor<Solution_>> variableDescriptorList;
    protected final boolean randomSelection;

    protected final boolean anyChained;
    protected List<SingletonInverseVariableSupply> inverseVariableSupplyList = null;

    public SwapMoveSelector(EntitySelector<Solution_> leftEntitySelector, EntitySelector<Solution_> rightEntitySelector,
            List<GenuineVariableDescriptor<Solution_>> variableDescriptorList, boolean randomSelection) {
        this.leftEntitySelector = leftEntitySelector;
        this.rightEntitySelector = rightEntitySelector;
        this.variableDescriptorList = variableDescriptorList;
        this.randomSelection = randomSelection;
        EntityDescriptor<Solution_> leftEntityDescriptor = leftEntitySelector.getEntityDescriptor();
        EntityDescriptor<Solution_> rightEntityDescriptor = rightEntitySelector.getEntityDescriptor();
        if (!leftEntityDescriptor.getEntityClass().equals(rightEntityDescriptor.getEntityClass())) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a leftEntitySelector's entityClass (" + leftEntityDescriptor.getEntityClass()
                    + ") which is not equal to the rightEntitySelector's entityClass ("
                    + rightEntityDescriptor.getEntityClass() + ").");
        }
        boolean anyChained = false;
        if (variableDescriptorList.isEmpty()) {
            throw new IllegalStateException("The selector (" + this
                    + ")'s variableDescriptors (" + variableDescriptorList + ") is empty.");
        }
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : variableDescriptorList) {
            if (!variableDescriptor.getEntityDescriptor().getEntityClass().isAssignableFrom(
                    leftEntityDescriptor.getEntityClass())) {
                throw new IllegalStateException("The selector (" + this
                        + ") has a variableDescriptor with a entityClass ("
                        + variableDescriptor.getEntityDescriptor().getEntityClass()
                        + ") which is not equal or a superclass to the leftEntitySelector's entityClass ("
                        + leftEntityDescriptor.getEntityClass() + ").");
            }
            if (variableDescriptor.isChained()) {
                anyChained = true;
            }
        }
        this.anyChained = anyChained;
        phaseLifecycleSupport.addEventListener(leftEntitySelector);
        if (leftEntitySelector != rightEntitySelector) {
            phaseLifecycleSupport.addEventListener(rightEntitySelector);
        }
    }

    @Override
    public boolean supportsPhaseAndSolverCaching() {
        return !anyChained;
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        super.solvingStarted(solverScope);
        if (anyChained) {
            inverseVariableSupplyList = new ArrayList<>(variableDescriptorList.size());
            SupplyManager supplyManager = solverScope.getScoreDirector().getSupplyManager();
            for (GenuineVariableDescriptor<Solution_> variableDescriptor : variableDescriptorList) {
                SingletonInverseVariableSupply inverseVariableSupply;
                if (variableDescriptor.isChained()) {
                    inverseVariableSupply = supplyManager.demand(new SingletonInverseVariableDemand<>(variableDescriptor));
                } else {
                    inverseVariableSupply = null;
                }
                inverseVariableSupplyList.add(inverseVariableSupply);
            }
        }
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        super.solvingEnded(solverScope);
        if (anyChained) {
            inverseVariableSupplyList = null;
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isCountable() {
        return leftEntitySelector.isCountable() && rightEntitySelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return randomSelection || leftEntitySelector.isNeverEnding() || rightEntitySelector.isNeverEnding();
    }

    @Override
    public long getSize() {
        return AbstractOriginalSwapIterator.getSize(leftEntitySelector, rightEntitySelector);
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        if (!randomSelection) {
            return new AbstractOriginalSwapIterator<>(leftEntitySelector, rightEntitySelector) {
                @Override
                protected Move<Solution_> newSwapSelection(Object leftSubSelection, Object rightSubSelection) {
                    return anyChained
                            ? new ChainedSwapMove<>(variableDescriptorList, inverseVariableSupplyList, leftSubSelection,
                                    rightSubSelection)
                            : new SwapMove<>(variableDescriptorList, leftSubSelection, rightSubSelection);
                }
            };
        } else {
            return new AbstractRandomSwapIterator<>(leftEntitySelector, rightEntitySelector) {
                @Override
                protected Move<Solution_> newSwapSelection(Object leftSubSelection, Object rightSubSelection) {
                    return anyChained
                            ? new ChainedSwapMove<>(variableDescriptorList, inverseVariableSupplyList, leftSubSelection,
                                    rightSubSelection)
                            : new SwapMove<>(variableDescriptorList, leftSubSelection, rightSubSelection);
                }
            };
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + leftEntitySelector + ", " + rightEntitySelector + ")";
    }

}

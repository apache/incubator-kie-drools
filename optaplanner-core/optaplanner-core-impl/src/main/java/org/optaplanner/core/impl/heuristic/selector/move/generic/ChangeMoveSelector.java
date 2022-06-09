package org.optaplanner.core.impl.heuristic.selector.move.generic;

import java.util.Iterator;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.IterableSelector;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractOriginalChangeIterator;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractRandomChangeIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.ChainedChangeMove;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public class ChangeMoveSelector<Solution_> extends GenericMoveSelector<Solution_> {

    protected final EntitySelector<Solution_> entitySelector;
    protected final ValueSelector<Solution_> valueSelector;
    protected final boolean randomSelection;

    protected final boolean chained;
    protected SingletonInverseVariableSupply inverseVariableSupply = null;

    public ChangeMoveSelector(EntitySelector<Solution_> entitySelector, ValueSelector<Solution_> valueSelector,
            boolean randomSelection) {
        this.entitySelector = entitySelector;
        this.valueSelector = valueSelector;
        this.randomSelection = randomSelection;
        GenuineVariableDescriptor<Solution_> variableDescriptor = valueSelector.getVariableDescriptor();
        chained = variableDescriptor.isChained();
        phaseLifecycleSupport.addEventListener(entitySelector);
        phaseLifecycleSupport.addEventListener(valueSelector);
    }

    @Override
    public boolean supportsPhaseAndSolverCaching() {
        return !chained;
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        super.solvingStarted(solverScope);
        if (chained) {
            SupplyManager supplyManager = solverScope.getScoreDirector().getSupplyManager();
            inverseVariableSupply =
                    supplyManager.demand(new SingletonInverseVariableDemand<>(valueSelector.getVariableDescriptor()));
        }
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        super.solvingEnded(solverScope);
        if (chained) {
            inverseVariableSupply = null;
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isCountable() {
        return entitySelector.isCountable() && valueSelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return randomSelection || entitySelector.isNeverEnding() || valueSelector.isNeverEnding();
    }

    @Override
    public long getSize() {
        if (valueSelector instanceof IterableSelector) {
            return entitySelector.getSize() * ((IterableSelector<Solution_, ?>) valueSelector).getSize();
        } else {
            long size = 0;
            for (Iterator<?> it = entitySelector.endingIterator(); it.hasNext();) {
                Object entity = it.next();
                size += valueSelector.getSize(entity);
            }
            return size;
        }
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        final GenuineVariableDescriptor<Solution_> variableDescriptor = valueSelector.getVariableDescriptor();
        if (!randomSelection) {
            if (chained) {
                return new AbstractOriginalChangeIterator<>(entitySelector, valueSelector) {
                    @Override
                    protected Move<Solution_> newChangeSelection(Object entity, Object toValue) {
                        return new ChainedChangeMove<>(variableDescriptor, entity, toValue, inverseVariableSupply);
                    }
                };
            } else {
                return new AbstractOriginalChangeIterator<>(entitySelector, valueSelector) {
                    @Override
                    protected Move<Solution_> newChangeSelection(Object entity, Object toValue) {
                        return new ChangeMove<>(variableDescriptor, entity, toValue);
                    }
                };
            }
        } else {
            if (chained) {
                return new AbstractRandomChangeIterator<>(entitySelector, valueSelector) {
                    @Override
                    protected Move<Solution_> newChangeSelection(Object entity, Object toValue) {
                        return new ChainedChangeMove<>(variableDescriptor, entity, toValue, inverseVariableSupply);
                    }
                };
            } else {
                return new AbstractRandomChangeIterator<>(entitySelector, valueSelector) {
                    @Override
                    protected Move<Solution_> newChangeSelection(Object entity, Object toValue) {
                        return new ChangeMove<>(variableDescriptor, entity, toValue);
                    }
                };
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelector + ", " + valueSelector + ")";
    }

}

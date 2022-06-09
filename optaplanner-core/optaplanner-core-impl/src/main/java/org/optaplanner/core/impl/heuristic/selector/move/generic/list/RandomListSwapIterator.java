package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Iterator;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class RandomListSwapIterator<Solution_> extends UpcomingSelectionIterator<Move<Solution_>> {

    private final ListVariableDescriptor<Solution_> listVariableDescriptor;
    private final SingletonInverseVariableSupply inverseVariableSupply;
    private final IndexVariableSupply indexVariableSupply;
    private final Iterator<Object> leftValueIterator;
    private final Iterator<Object> rightValueIterator;

    public RandomListSwapIterator(
            ListVariableDescriptor<Solution_> listVariableDescriptor,
            SingletonInverseVariableSupply inverseVariableSupply,
            IndexVariableSupply indexVariableSupply,
            EntityIndependentValueSelector<Solution_> leftValueSelector,
            EntityIndependentValueSelector<Solution_> rightValueSelector) {
        this.listVariableDescriptor = listVariableDescriptor;
        this.inverseVariableSupply = inverseVariableSupply;
        this.indexVariableSupply = indexVariableSupply;
        this.leftValueIterator = leftValueSelector.iterator();
        this.rightValueIterator = rightValueSelector.iterator();
    }

    @Override
    protected Move<Solution_> createUpcomingSelection() {
        if (!leftValueIterator.hasNext() || !rightValueIterator.hasNext()) {
            return noUpcomingSelection();
        }

        Object upcomingLeftValue = leftValueIterator.next();
        Object upcomingRightValue = rightValueIterator.next();

        return new ListSwapMove<>(
                listVariableDescriptor,
                inverseVariableSupply.getInverseSingleton(upcomingLeftValue),
                indexVariableSupply.getIndex(upcomingLeftValue),
                inverseVariableSupply.getInverseSingleton(upcomingRightValue),
                indexVariableSupply.getIndex(upcomingRightValue));
    }
}

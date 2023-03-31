package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Iterator;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.list.DestinationSelector;
import org.optaplanner.core.impl.heuristic.selector.list.ElementRef;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class RandomListChangeIterator<Solution_> extends UpcomingSelectionIterator<Move<Solution_>> {

    private final SingletonInverseVariableSupply inverseVariableSupply;
    private final IndexVariableSupply indexVariableSupply;
    private final ListVariableDescriptor<Solution_> listVariableDescriptor;
    private final Iterator<Object> valueIterator;
    private final Iterator<ElementRef> destinationIterator;

    public RandomListChangeIterator(
            SingletonInverseVariableSupply inverseVariableSupply,
            IndexVariableSupply indexVariableSupply,
            EntityIndependentValueSelector<Solution_> valueSelector,
            DestinationSelector<Solution_> destinationSelector) {
        this.inverseVariableSupply = inverseVariableSupply;
        this.indexVariableSupply = indexVariableSupply;
        this.listVariableDescriptor = (ListVariableDescriptor<Solution_>) valueSelector.getVariableDescriptor();
        this.valueIterator = valueSelector.iterator();
        this.destinationIterator = destinationSelector.iterator();
    }

    @Override
    protected Move<Solution_> createUpcomingSelection() {
        if (!valueIterator.hasNext() || !destinationIterator.hasNext()) {
            return noUpcomingSelection();
        }

        Object upcomingValue = valueIterator.next();
        ElementRef destination = destinationIterator.next();

        return new ListChangeMove<>(
                listVariableDescriptor,
                inverseVariableSupply.getInverseSingleton(upcomingValue),
                indexVariableSupply.getIndex(upcomingValue),
                destination.getEntity(),
                destination.getIndex());
    }
}

package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Collections;
import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;

/**
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class OriginalListChangeIterator<Solution_> extends UpcomingSelectionIterator<Move<Solution_>> {

    private final ListVariableDescriptor<Solution_> listVariableDescriptor;
    private final SingletonInverseVariableSupply inverseVariableSupply;
    private final IndexVariableSupply indexVariableSupply;
    private final Iterator<Object> valueIterator;
    private final EntitySelector<Solution_> entitySelector;
    private Iterator<Object> destinationEntityIterator;
    private PrimitiveIterator.OfInt destinationIndexIterator;

    private Object upcomingSourceEntity;
    private Integer upcomingSourceIndex;
    private Object upcomingDestinationEntity;
    private Object upcomingValue;

    public OriginalListChangeIterator(
            ListVariableDescriptor<Solution_> listVariableDescriptor,
            SingletonInverseVariableSupply inverseVariableSupply,
            IndexVariableSupply indexVariableSupply,
            EntityIndependentValueSelector<Solution_> valueSelector,
            EntitySelector<Solution_> entitySelector) {
        this.listVariableDescriptor = listVariableDescriptor;
        this.inverseVariableSupply = inverseVariableSupply;
        this.indexVariableSupply = indexVariableSupply;
        this.valueIterator = valueSelector.iterator();
        this.entitySelector = entitySelector;
        this.destinationEntityIterator = Collections.emptyIterator();
        this.destinationIndexIterator = IntStream.empty().iterator();
    }

    @Override
    protected Move<Solution_> createUpcomingSelection() {
        if (!destinationIndexIterator.hasNext()) {
            while (!destinationEntityIterator.hasNext()) {
                if (!valueIterator.hasNext()) {
                    return noUpcomingSelection();
                }
                upcomingValue = valueIterator.next();
                upcomingSourceEntity = inverseVariableSupply.getInverseSingleton(upcomingValue);
                upcomingSourceIndex = indexVariableSupply.getIndex(upcomingValue);

                destinationEntityIterator = entitySelector.iterator();
            }
            upcomingDestinationEntity = destinationEntityIterator.next();
            destinationIndexIterator = listIndexIterator(upcomingDestinationEntity);
        }

        if (upcomingSourceEntity == null && upcomingSourceIndex == null) {
            return new ListAssignMove<>(
                    listVariableDescriptor,
                    upcomingValue,
                    upcomingDestinationEntity,
                    destinationIndexIterator.nextInt());
        }

        // No need to generate ListUnassignMove because they are only used as undo moves.

        return new ListChangeMove<>(
                listVariableDescriptor,
                upcomingSourceEntity,
                upcomingSourceIndex,
                upcomingDestinationEntity,
                destinationIndexIterator.nextInt());
    }

    private PrimitiveIterator.OfInt listIndexIterator(Object entity) {
        return IntStream.rangeClosed(0, listVariableDescriptor.getListSize(entity)).iterator();
    }
}

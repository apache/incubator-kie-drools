package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.util.Pair;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class RandomListChangeIterator<Solution_> extends UpcomingSelectionIterator<Move<Solution_>> {

    private final ListVariableDescriptor<Solution_> listVariableDescriptor;
    private final SingletonInverseVariableSupply inverseVariableSupply;
    private final IndexVariableSupply indexVariableSupply;
    private final Iterator<Object> valueIterator;
    private final Random workingRandom;
    private final NavigableMap<Integer, Object> indexToDestinationEntityMap;
    private final int destinationIndexRange;

    public RandomListChangeIterator(
            ListVariableDescriptor<Solution_> listVariableDescriptor,
            SingletonInverseVariableSupply inverseVariableSupply,
            IndexVariableSupply indexVariableSupply,
            EntityIndependentValueSelector<Solution_> valueSelector,
            EntitySelector<Solution_> entitySelector,
            Random workingRandom) {
        this.listVariableDescriptor = listVariableDescriptor;
        this.inverseVariableSupply = inverseVariableSupply;
        this.indexVariableSupply = indexVariableSupply;
        this.valueIterator = valueSelector.iterator();
        this.workingRandom = workingRandom;

        // TODO optimize this (don't rebuild the whole map at the beginning of each step).
        //  https://issues.redhat.com/browse/PLANNER-2507
        indexToDestinationEntityMap = new TreeMap<>();
        int cumulativeDestinationListSize = 0;
        for (Object entity : ((Iterable<Object>) entitySelector::endingIterator)) {
            indexToDestinationEntityMap.put(cumulativeDestinationListSize, entity);
            cumulativeDestinationListSize += (listVariableDescriptor.getListSize(entity) + 1);
        }
        this.destinationIndexRange = cumulativeDestinationListSize;
    }

    @Override
    protected Move<Solution_> createUpcomingSelection() {
        if (!valueIterator.hasNext() || destinationIndexRange == 0) {
            return noUpcomingSelection();
        }

        Object upcomingValue = valueIterator.next();
        Pair<Object, Integer> destination = entityAndIndexFromGlobalIndex(workingRandom.nextInt(destinationIndexRange));

        return new ListChangeMove<>(
                listVariableDescriptor,
                inverseVariableSupply.getInverseSingleton(upcomingValue),
                indexVariableSupply.getIndex(upcomingValue),
                destination.getKey(),
                destination.getValue());
    }

    Pair<Object, Integer> entityAndIndexFromGlobalIndex(int index) {
        Map.Entry<Integer, Object> entry = indexToDestinationEntityMap.floorEntry(index);
        return Pair.of(entry.getValue(), index - entry.getKey());
    }
}

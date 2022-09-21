package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.util.Pair;

class RandomSubListChangeMoveIterator<Solution_> extends UpcomingSelectionIterator<Move<Solution_>> {

    private final ListVariableDescriptor<Solution_> listVariableDescriptor;
    private final Iterator<SubList> subListIterator;
    private final Random workingRandom;
    private final boolean selectReversingMoveToo;
    private final NavigableMap<Integer, Object> indexToDestinationEntityMap;
    private final int destinationIndexRange;

    RandomSubListChangeMoveIterator(
            ListVariableDescriptor<Solution_> listVariableDescriptor,
            RandomSubListSelector<Solution_> subListSelector,
            EntitySelector<Solution_> entitySelector,
            Random workingRandom,
            boolean selectReversingMoveToo) {
        this.listVariableDescriptor = listVariableDescriptor;
        subListIterator = subListSelector.iterator();
        this.workingRandom = workingRandom;
        this.selectReversingMoveToo = selectReversingMoveToo;

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
        if (!subListIterator.hasNext() || destinationIndexRange == 0) {
            return noUpcomingSelection();
        }
        SubList subList = subListIterator.next();
        // TODO maybe destinationSelector
        Pair<Object, Integer> destination = entityAndIndexFromGlobalIndex(workingRandom.nextInt(destinationIndexRange));
        boolean reversing = selectReversingMoveToo && workingRandom.nextBoolean();
        return new SubListChangeMove<>(listVariableDescriptor, subList, destination.getKey(), destination.getValue(),
                reversing);
    }

    Pair<Object, Integer> entityAndIndexFromGlobalIndex(int index) {
        Map.Entry<Integer, Object> entry = indexToDestinationEntityMap.floorEntry(index);
        return Pair.of(entry.getValue(), index - entry.getKey());
    }
}

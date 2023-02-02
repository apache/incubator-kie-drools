package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Iterator;
import java.util.Random;

import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;

class RandomSubListChangeMoveIterator<Solution_> extends UpcomingSelectionIterator<Move<Solution_>> {

    private final ListVariableDescriptor<Solution_> listVariableDescriptor;
    private final Iterator<SubList> subListIterator;
    private final Iterator<ElementRef> destinationIterator;
    private final Random workingRandom;
    private final boolean selectReversingMoveToo;

    RandomSubListChangeMoveIterator(
            ListVariableDescriptor<Solution_> listVariableDescriptor,
            RandomSubListSelector<Solution_> subListSelector,
            ElementDestinationSelector<Solution_> destinationSelector,
            Random workingRandom,
            boolean selectReversingMoveToo) {
        this.listVariableDescriptor = listVariableDescriptor;
        this.subListIterator = subListSelector.iterator();
        this.destinationIterator = destinationSelector.iterator();
        this.workingRandom = workingRandom;
        this.selectReversingMoveToo = selectReversingMoveToo;
    }

    @Override
    protected Move<Solution_> createUpcomingSelection() {
        if (!subListIterator.hasNext() || !destinationIterator.hasNext()) {
            return noUpcomingSelection();
        }

        SubList subList = subListIterator.next();
        ElementRef destination = destinationIterator.next();
        boolean reversing = selectReversingMoveToo && workingRandom.nextBoolean();

        return new SubListChangeMove<>(
                listVariableDescriptor,
                subList,
                destination.getEntity(),
                destination.getIndex(),
                reversing);
    }
}

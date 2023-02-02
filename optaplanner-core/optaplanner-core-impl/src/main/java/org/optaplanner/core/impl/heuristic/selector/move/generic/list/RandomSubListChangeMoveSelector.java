package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Iterator;

import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.generic.GenericMoveSelector;

public class RandomSubListChangeMoveSelector<Solution_> extends GenericMoveSelector<Solution_> {

    private final ListVariableDescriptor<Solution_> listVariableDescriptor;
    private final RandomSubListSelector<Solution_> subListSelector;
    private final ElementDestinationSelector<Solution_> destinationSelector;
    private final boolean selectReversingMoveToo;

    public RandomSubListChangeMoveSelector(
            ListVariableDescriptor<Solution_> listVariableDescriptor,
            RandomSubListSelector<Solution_> subListSelector,
            ElementDestinationSelector<Solution_> destinationSelector,
            boolean selectReversingMoveToo) {
        this.listVariableDescriptor = listVariableDescriptor;
        this.subListSelector = subListSelector;
        this.destinationSelector = destinationSelector;
        this.selectReversingMoveToo = selectReversingMoveToo;

        phaseLifecycleSupport.addEventListener(subListSelector);
        phaseLifecycleSupport.addEventListener(destinationSelector);
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        return new RandomSubListChangeMoveIterator<>(
                listVariableDescriptor,
                subListSelector,
                destinationSelector,
                workingRandom,
                selectReversingMoveToo);
    }

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public boolean isNeverEnding() {
        return true;
    }

    @Override
    public long getSize() {
        long destinationCount = destinationSelector.getSize();
        long subListCount = subListSelector.getSize();
        return subListCount * destinationCount * (selectReversingMoveToo ? 2 : 1);
    }

    boolean isSelectReversingMoveToo() {
        return selectReversingMoveToo;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + subListSelector + ", " + destinationSelector + ")";
    }
}

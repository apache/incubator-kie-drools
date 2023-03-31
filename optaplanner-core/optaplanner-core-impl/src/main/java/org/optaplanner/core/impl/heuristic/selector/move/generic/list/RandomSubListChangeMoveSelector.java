package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Iterator;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.list.DestinationSelector;
import org.optaplanner.core.impl.heuristic.selector.list.SubListSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.GenericMoveSelector;

public class RandomSubListChangeMoveSelector<Solution_> extends GenericMoveSelector<Solution_> {

    private final SubListSelector<Solution_> subListSelector;
    private final DestinationSelector<Solution_> destinationSelector;
    private final boolean selectReversingMoveToo;

    public RandomSubListChangeMoveSelector(
            SubListSelector<Solution_> subListSelector,
            DestinationSelector<Solution_> destinationSelector,
            boolean selectReversingMoveToo) {
        this.subListSelector = subListSelector;
        this.destinationSelector = destinationSelector;
        this.selectReversingMoveToo = selectReversingMoveToo;

        phaseLifecycleSupport.addEventListener(subListSelector);
        phaseLifecycleSupport.addEventListener(destinationSelector);
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        return new RandomSubListChangeMoveIterator<>(
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
        long subListCount = subListSelector.getSize();
        long destinationCount = destinationSelector.getSize();
        return subListCount * destinationCount * (selectReversingMoveToo ? 2 : 1);
    }

    boolean isSelectReversingMoveToo() {
        return selectReversingMoveToo;
    }

    SubListSelector<Solution_> getSubListSelector() {
        return subListSelector;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + subListSelector + ", " + destinationSelector + ")";
    }
}

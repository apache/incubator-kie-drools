package org.optaplanner.core.impl.heuristic.selector.move.composite;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

final class UniformRandomUnionMoveIterator<Solution_> extends SelectionIterator<Move<Solution_>> {

    private final List<Iterator<Move<Solution_>>> moveIteratorList;
    private final Random workingRandom;

    public UniformRandomUnionMoveIterator(List<MoveSelector<Solution_>> childMoveSelectorList, Random workingRandom) {
        this.moveIteratorList = childMoveSelectorList.stream()
                .map(Iterable::iterator)
                .filter(Iterator::hasNext)
                .collect(Collectors.toList());
        this.workingRandom = workingRandom;
    }

    @Override
    public boolean hasNext() {
        return !moveIteratorList.isEmpty();
    }

    @Override
    public Move<Solution_> next() {
        int index = workingRandom.nextInt(moveIteratorList.size());
        Iterator<Move<Solution_>> moveIterator = moveIteratorList.get(index);
        Move<Solution_> next = moveIterator.next();
        if (!moveIterator.hasNext()) {
            moveIteratorList.remove(index);
        }
        return next;
    }

}

package org.optaplanner.core.impl.heuristic.selector.move.composite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.optaplanner.core.impl.heuristic.move.CompositeMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.move.NoChangeMove;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

/**
 * A {@link CompositeMoveSelector} that Cartesian products 2 or more {@link MoveSelector}s.
 * <p>
 * For example: a Cartesian product of {A, B, C} and {X, Y} will result in {AX, AY, BX, BY, CX, CY}.
 * <p>
 * Warning: there is no duplicated {@link Move} check, so union of {A, B} and {B} will result in {AB, BB}.
 *
 * @see CompositeMoveSelector
 */
public class CartesianProductMoveSelector<Solution_> extends CompositeMoveSelector<Solution_> {

    private static final Move<?> EMPTY_MARK = new NoChangeMove<>();

    private final boolean ignoreEmptyChildIterators;

    public CartesianProductMoveSelector(List<MoveSelector<Solution_>> childMoveSelectorList,
            boolean ignoreEmptyChildIterators, boolean randomSelection) {
        super(childMoveSelectorList, randomSelection);
        this.ignoreEmptyChildIterators = ignoreEmptyChildIterators;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isNeverEnding() {
        if (randomSelection) {
            return true;
        } else {
            // Only the last childMoveSelector can be neverEnding
            return !childMoveSelectorList.isEmpty()
                    && childMoveSelectorList.get(childMoveSelectorList.size() - 1).isNeverEnding();
        }
    }

    @Override
    public long getSize() {
        long size = 0L;
        for (MoveSelector<Solution_> moveSelector : childMoveSelectorList) {
            long childSize = moveSelector.getSize();
            if (childSize == 0L) {
                if (!ignoreEmptyChildIterators) {
                    return 0L;
                }
                // else ignore that child
            } else {
                if (size == 0L) {
                    // There must be at least 1 non-empty child to change the size from 0
                    size = childSize;
                } else {
                    size *= childSize;
                }
            }
        }
        return size;
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        if (!randomSelection) {
            return new OriginalCartesianProductMoveIterator();
        } else {
            return new RandomCartesianProductMoveIterator();
        }
    }

    public class OriginalCartesianProductMoveIterator extends UpcomingSelectionIterator<Move<Solution_>> {

        private List<Iterator<Move<Solution_>>> moveIteratorList;

        private Move<Solution_>[] subSelections;

        public OriginalCartesianProductMoveIterator() {
            moveIteratorList = new ArrayList<>(childMoveSelectorList.size());
            for (int i = 0; i < childMoveSelectorList.size(); i++) {
                moveIteratorList.add(null);
            }
            subSelections = null;
        }

        @Override
        protected Move<Solution_> createUpcomingSelection() {
            int childSize = moveIteratorList.size();
            int startingIndex;
            Move<Solution_>[] moveList = new Move[childSize];
            if (subSelections == null) {
                startingIndex = -1;
            } else {
                startingIndex = childSize - 1;
                while (startingIndex >= 0) {
                    Iterator<Move<Solution_>> moveIterator = moveIteratorList.get(startingIndex);
                    if (moveIterator.hasNext()) {
                        break;
                    }
                    startingIndex--;
                }
                if (startingIndex < 0) {
                    return noUpcomingSelection();
                }
                // Clone to avoid CompositeMove corruption
                System.arraycopy(subSelections, 0, moveList, 0, startingIndex);
                moveList[startingIndex] = moveIteratorList.get(startingIndex).next(); // Increment the 4 in 004999
            }
            for (int i = startingIndex + 1; i < childSize; i++) { // Increment the 9s in 004999
                Iterator<Move<Solution_>> moveIterator = childMoveSelectorList.get(i).iterator();
                moveIteratorList.set(i, moveIterator);
                Move<Solution_> next;
                if (!moveIterator.hasNext()) { // in case a moveIterator is empty
                    if (ignoreEmptyChildIterators) {
                        next = (Move<Solution_>) EMPTY_MARK;
                    } else {
                        return noUpcomingSelection();
                    }
                } else {
                    next = moveIterator.next();
                }
                moveList[i] = next;
            }
            // No need to clone to avoid CompositeMove corruption because subSelections's elements never change
            subSelections = moveList;
            if (ignoreEmptyChildIterators) {
                // Clone because EMPTY_MARK should survive in subSelections
                Move<Solution_>[] newMoveList = new Move[childSize];
                int newSize = 0;
                for (int i = 0; i < childSize; i++) {
                    if (moveList[i] != EMPTY_MARK) {
                        newMoveList[newSize] = moveList[i];
                        newSize++;
                    }
                }
                if (newSize == 0) {
                    return noUpcomingSelection();
                } else if (newSize == 1) {
                    return newMoveList[0];
                }
                moveList = Arrays.copyOfRange(newMoveList, 0, newSize);
            }
            return new CompositeMove<>(moveList);
        }

    }

    public class RandomCartesianProductMoveIterator extends SelectionIterator<Move<Solution_>> {

        private List<Iterator<Move<Solution_>>> moveIteratorList;
        private Boolean empty;

        public RandomCartesianProductMoveIterator() {
            moveIteratorList = new ArrayList<>(childMoveSelectorList.size());
            empty = null;
            for (MoveSelector<Solution_> moveSelector : childMoveSelectorList) {
                moveIteratorList.add(moveSelector.iterator());
            }
        }

        @Override
        public boolean hasNext() {
            if (empty == null) { // Only done in the first call
                int emptyCount = 0;
                for (Iterator<Move<Solution_>> moveIterator : moveIteratorList) {
                    if (!moveIterator.hasNext()) {
                        emptyCount++;
                        if (!ignoreEmptyChildIterators) {
                            break;
                        }
                    }
                }
                empty = ignoreEmptyChildIterators ? emptyCount == moveIteratorList.size() : emptyCount > 0;
            }
            return !empty;
        }

        @Override
        public Move<Solution_> next() {
            List<Move<Solution_>> moveList = new ArrayList<>(moveIteratorList.size());
            for (int i = 0; i < moveIteratorList.size(); i++) {
                Iterator<Move<Solution_>> moveIterator = moveIteratorList.get(i);
                boolean skip = false;
                if (!moveIterator.hasNext()) {
                    MoveSelector<Solution_> moveSelector = childMoveSelectorList.get(i);
                    moveIterator = moveSelector.iterator();
                    moveIteratorList.set(i, moveIterator);
                    if (!moveIterator.hasNext()) {
                        if (ignoreEmptyChildIterators) {
                            skip = true;
                        } else {
                            throw new NoSuchElementException("The iterator of childMoveSelector (" + moveSelector
                                    + ") is empty.");
                        }
                    }
                }
                if (!skip) {
                    moveList.add(moveIterator.next());
                }
            }
            if (ignoreEmptyChildIterators) {
                if (moveList.isEmpty()) {
                    throw new NoSuchElementException("All iterators of childMoveSelectorList (" + childMoveSelectorList
                            + ") are empty.");
                } else if (moveList.size() == 1) {
                    return moveList.get(0);
                }
            }
            return new CompositeMove<>(moveList.toArray(new Move[0]));
        }

    }

    @Override
    public String toString() {
        return "CartesianProduct(" + childMoveSelectorList + ")";
    }

}

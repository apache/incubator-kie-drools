/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.move.composite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.collections.IteratorUtils;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.move.CompositeMove;
import org.optaplanner.core.impl.move.Move;

/**
 * A {@link CompositeMoveSelector} that cartesian products 2 or more {@link MoveSelector}s.
 * <p/>
 * For example: a cartesian product of {A, B, C} and {X, Y} will result in {AX, AY, BX, BY, CX, CY}.
 * <p/>
 * Warning: there is no duplicated {@link Move} check, so union of {A, B} and {B} will result in {AB, BB}.
 * @see CompositeMoveSelector
 */
public class CartesianProductMoveSelector extends CompositeMoveSelector {

    private final boolean ignoreEmptyChildIterators;

    public CartesianProductMoveSelector(List<MoveSelector> childMoveSelectorList, boolean ignoreEmptyChildIterators,
            boolean randomSelection) {
        super(childMoveSelectorList, randomSelection);
        this.ignoreEmptyChildIterators = ignoreEmptyChildIterators;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isNeverEnding() {
        if (randomSelection) {
            return true;
        } else {
            // Only the last childMoveSelector can be neverEnding
            return !childMoveSelectorList.isEmpty()
                    && childMoveSelectorList.get(childMoveSelectorList.size() - 1).isNeverEnding();
        }
    }

    public long getSize() {
        long size = 1L;
        for (MoveSelector moveSelector : childMoveSelectorList) {
            long childSize = moveSelector.getSize();
            if (ignoreEmptyChildIterators && childSize == 0L) {
                childSize = 1L;
            }
            size *= childSize;
        }
        return size;
    }

    public Iterator<Move> iterator() {
        if (!randomSelection) {
            return new OriginalCartesianProductMoveIterator();
        } else {
            return new RandomCartesianProductMoveIterator();
        }
    }

    public class OriginalCartesianProductMoveIterator extends UpcomingSelectionIterator<Move> {

        private List<Iterator<Move>> moveIteratorList;

        private List<Move> subSelections;

        public OriginalCartesianProductMoveIterator() {
            moveIteratorList = new ArrayList<Iterator<Move>>(childMoveSelectorList.size());
            subSelections = new ArrayList<Move>(childMoveSelectorList.size());
            for (int i = 0; i < childMoveSelectorList.size(); i++) {
                if (i == 0) {
                    moveIteratorList.add(childMoveSelectorList.get(i).iterator());
                } else {
                    moveIteratorList.add(IteratorUtils.emptyListIterator());
                }
                subSelections.add(null);
            }
        }

        @Override
        protected Move createUpcomingSelection() {
            int startingIndex = moveIteratorList.size() - 1;
            while (startingIndex >= 0) {
                Iterator<Move> moveIterator =  moveIteratorList.get(startingIndex);
                if (moveIterator.hasNext()) {
                    break;
                }
                startingIndex--;
            }
            if (startingIndex < 0) {
                return noUpcomingSelection();
            }
            // Clone to avoid CompositeMove corruption
            List<Move> moveList = new ArrayList<Move>(subSelections.subList(0, startingIndex));
            moveList.add(moveIteratorList.get(startingIndex).next());
            for (int i = startingIndex + 1; i < moveIteratorList.size(); i++) {
                Iterator<Move>  moveIterator = childMoveSelectorList.get(i).iterator();
                moveIteratorList.set(i, moveIterator);
                Move next;
                if (!moveIterator.hasNext()) { // in case a moveIterator is empty
                    if (ignoreEmptyChildIterators) {
                        next = null; // OK because a Move is never null (unlike a planning value)
                    } else {
                        return noUpcomingSelection();
                    }
                } else {
                    next = moveIterator.next();
                }
                moveList.add(next);
            }
            // No need to clone to avoid CompositeMove corruption because subSelections's elements never change
            subSelections = moveList;
            if (ignoreEmptyChildIterators) {
                moveList = new ArrayList<Move>(moveList); // Clone because the null should survive in subSelections
                for (Iterator<Move> it = moveList.iterator(); it.hasNext(); ) {
                    Move move = it.next();
                    if (move == null) {
                        it.remove();
                    }
                }
                if (moveList.isEmpty()) {
                    return noUpcomingSelection();
                } else if (moveList.size() == 1) {
                    return moveList.get(0);
                }
            }
            return new CompositeMove(moveList);
        }

    }

    public class RandomCartesianProductMoveIterator extends SelectionIterator<Move> {

        private List<Iterator<Move>> moveIteratorList;
        private Boolean empty;

        public RandomCartesianProductMoveIterator() {
            moveIteratorList = new ArrayList<Iterator<Move>>(childMoveSelectorList.size());
            empty = null;
            for (MoveSelector moveSelector : childMoveSelectorList) {
                moveIteratorList.add(moveSelector.iterator());
            }
        }

        public boolean hasNext() {
            if (empty == null) { // Only done in the first call
                int emptyCount = 0;
                for (Iterator<Move> moveIterator : moveIteratorList) {
                    if (!moveIterator.hasNext()) {
                        emptyCount++;
                        if (!ignoreEmptyChildIterators) {
                            break;
                        }
                    }
                }
                empty = ignoreEmptyChildIterators ? emptyCount == moveIteratorList.size(): emptyCount > 0;
            }
            return !empty;
        }

        public Move next() {
            List<Move> moveList = new ArrayList<Move>(moveIteratorList.size());
            for (int i = 0; i < moveIteratorList.size(); i++) {
                Iterator<Move> moveIterator = moveIteratorList.get(i);
                boolean skip = false;
                if (!moveIterator.hasNext()) {
                    MoveSelector moveSelector = childMoveSelectorList.get(i);
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
            return new CompositeMove(moveList);
        }

    }

    @Override
    public String toString() {
        return "CartesianProduct(" + childMoveSelectorList + ")";
    }

}

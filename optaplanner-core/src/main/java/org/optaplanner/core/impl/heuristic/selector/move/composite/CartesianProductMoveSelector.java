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

    public CartesianProductMoveSelector(List<MoveSelector> childMoveSelectorList, boolean randomSelection) {
        super(childMoveSelectorList, randomSelection);
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
            size *= moveSelector.getSize();
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
            createUpcomingSelection();
        }

        @Override
        protected void createUpcomingSelection() {
            List<Move> moveList = new ArrayList<Move>(subSelections); // Clone to avoid CompositeMove corruption
            for (int i = moveIteratorList.size() - 1; i >= 0; i--) {
                Iterator<Move> moveIterator =  moveIteratorList.get(i);
                if (moveIterator.hasNext()) {
                    moveList.set(i, moveIterator.next());
                    break;
                }
                if (i == 0) {
                    upcomingSelection = null;
                    return;
                }
                moveIterator = childMoveSelectorList.get(i).iterator();
                moveIteratorList.set(i, moveIterator);
                if (!moveIterator.hasNext()) { // in case a moveIterator is empty
                    upcomingSelection = null;
                    return;
                }
                moveList.set(i, moveIterator.next());
            }
            // No need to clone to avoid CompositeMove corruption because subSelections's elements never change
            subSelections = moveList;
            upcomingSelection = new CompositeMove(moveList);
        }

    }

    public class RandomCartesianProductMoveIterator implements Iterator<Move> {

        private List<Iterator<Move>> moveIteratorList;
        private boolean empty;

        public RandomCartesianProductMoveIterator() {
            moveIteratorList = new ArrayList<Iterator<Move>>(childMoveSelectorList.size());
            empty = false;
            for (MoveSelector moveSelector : childMoveSelectorList) {
                Iterator<Move> moveIterator = moveSelector.iterator();
                if (!moveIterator.hasNext()) {
                    empty = true;
                }
                moveIteratorList.add(moveIterator);
            }
        }

        public boolean hasNext() {
            return !empty;
        }

        public Move next() {
            List<Move> moveList = new ArrayList<Move>(moveIteratorList.size());
            for (int i = 0; i < moveIteratorList.size(); i++) {
                Iterator<Move> moveIterator = moveIteratorList.get(i);
                if (!moveIterator.hasNext()) {
                    MoveSelector moveSelector = childMoveSelectorList.get(i);
                    moveIterator = moveSelector.iterator();
                    moveIteratorList.set(i, moveIterator);
                    if (!moveIterator.hasNext()) {
                        throw new NoSuchElementException("The moveSelector (" + moveSelector + ") is empty.");
                    }
                }
                moveList.add(moveIterator.next());
            }
            return new CompositeMove(moveList);
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported.");
        }

    }

    @Override
    public String toString() {
        return "CartesianProduct(" + childMoveSelectorList + ")";
    }

}

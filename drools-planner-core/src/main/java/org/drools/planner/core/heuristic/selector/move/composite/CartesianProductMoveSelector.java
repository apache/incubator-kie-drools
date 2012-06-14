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

package org.drools.planner.core.heuristic.selector.move.composite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import org.apache.commons.collections.iterators.IteratorChain;
import org.drools.planner.core.heuristic.selector.cached.SelectionProbabilityWeightFactory;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.move.CompositeMove;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.util.RandomUtils;

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

    public Iterator<Move> iterator() {
        if (!randomSelection) {
            throw new UnsupportedOperationException("Not yet implemented"); // TODO
        } else {
            return new RandomCartesianProductMoveIterator();
        }
    }

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

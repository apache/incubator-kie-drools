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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.collections.iterators.IteratorChain;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.util.RandomUtils;

/**
 * A {@link CompositeMoveSelector} that unions 2 or more {@link MoveSelector}s.
 * <p/>
 * For example: a union of {A, B, C} and {X, Y} will result in {A, B, C, X, Y}.
 * <p/>
 * Warning: there is no duplicated {@link Move} check, so union of {A, B, C} and {B, D} will result in {A, B, C, B, D}.
 * @see CompositeMoveSelector
 */
public class UnionMoveSelector extends CompositeMoveSelector {

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public Iterator<Move> iterator() {
        if (!randomSelection) {
            List<Iterator<Move>> iteratorList = new ArrayList<Iterator<Move>>(childMoveSelectorList.size());
            for (MoveSelector moveSelector : childMoveSelectorList) {
                iteratorList.add(moveSelector.iterator());
            }
            return new IteratorChain(iteratorList);
        } else {
            return new RandomUnionMoveIterator();
        }
    }

    public boolean isNeverEnding() {
        if (randomSelection) {
            for (MoveSelector moveSelector : childMoveSelectorList) {
                if (moveSelector.isNeverEnding()) {
                    return true;
                }
            }
            // The UnionMoveSelector is special: it can be randomSelection true and still neverEnding false
            return false;
        } else {
            // Only the last childMoveSelector can be neverEnding
            if (!childMoveSelectorList.isEmpty()
                    && childMoveSelectorList.get(childMoveSelectorList.size() - 1).isNeverEnding()) {
                return true;
            }
            return false;
        }
    }

    public long getSize() {
        long size = 0L;
        for (MoveSelector moveSelector : childMoveSelectorList) {
            size +=  moveSelector.getSize();
        }
        return size;
    }

    public class RandomUnionMoveIterator implements Iterator<Move> {

        protected final NavigableMap<Long, Iterator<Move>> moveIteratorMap;
        protected final Map<Iterator<Move>, MoveSelector> moveSelectorMap;
        protected long randomProbabilityWeightTotal;

        public RandomUnionMoveIterator() {
            moveIteratorMap = new TreeMap<Long, Iterator<Move>>();
            moveSelectorMap = new LinkedHashMap<Iterator<Move>, MoveSelector>(childMoveSelectorList.size());
            for (MoveSelector moveSelector : childMoveSelectorList) {
                Iterator<Move> moveIterator = moveSelector.iterator();
                if (moveIterator.hasNext()) {
                    moveSelectorMap.put(moveIterator, moveSelector);
                }
            }
            refreshMoveIteratorMap();
        }

        public boolean hasNext() {
            return !moveIteratorMap.isEmpty();
        }

        public Move next() {
            long randomProbability = RandomUtils.nextLong(workingRandom, randomProbabilityWeightTotal);
            Map.Entry<Long, Iterator<Move>> entry = moveIteratorMap.floorEntry(randomProbability);
            // entry is never null because randomProbability < randomProbabilityWeightTotal
            Iterator<Move> moveIterator = entry.getValue();
            Move next = moveIterator.next();
            if (!moveIterator.hasNext()) {
                moveSelectorMap.remove(moveIterator);
                refreshMoveIteratorMap();
            }
            return next;
        }

        private void refreshMoveIteratorMap() {
            moveIteratorMap.clear();
            long randomProbabilityWeightOffset = 0L;
            for (Map.Entry<Iterator<Move>, MoveSelector> moveSelectorEntry : moveSelectorMap.entrySet()) {
                Iterator<Move> moveIterator = moveSelectorEntry.getKey();
                MoveSelector moveSelector = moveSelectorEntry.getValue();
                moveIteratorMap.put(randomProbabilityWeightOffset, moveIterator);
                randomProbabilityWeightOffset += moveSelector.getRandomProbabilityWeight();
            }
            randomProbabilityWeightTotal = randomProbabilityWeightOffset;
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported.");
        }

    }

}

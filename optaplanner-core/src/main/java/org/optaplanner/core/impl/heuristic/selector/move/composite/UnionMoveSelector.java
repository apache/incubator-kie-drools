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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.collections.iterators.IteratorChain;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.util.RandomUtils;

/**
 * A {@link CompositeMoveSelector} that unions 2 or more {@link MoveSelector}s.
 * <p/>
 * For example: a union of {A, B, C} and {X, Y} will result in {A, B, C, X, Y}.
 * <p/>
 * Warning: there is no duplicated {@link Move} check, so union of {A, B, C} and {B, D} will result in {A, B, C, B, D}.
 * @see CompositeMoveSelector
 */
public class UnionMoveSelector extends CompositeMoveSelector {

    protected final SelectionProbabilityWeightFactory selectorProbabilityWeightFactory;

    protected ScoreDirector scoreDirector;

    public UnionMoveSelector(List<MoveSelector> childMoveSelectorList, boolean randomSelection) {
        this(childMoveSelectorList, randomSelection, null);
    }

    public UnionMoveSelector(List<MoveSelector> childMoveSelectorList, boolean randomSelection,
            SelectionProbabilityWeightFactory selectorProbabilityWeightFactory) {
        super(childMoveSelectorList, randomSelection);
        this.selectorProbabilityWeightFactory = selectorProbabilityWeightFactory;
        if (!randomSelection) {
            if (selectorProbabilityWeightFactory != null) {
                throw new IllegalArgumentException("The selector (" + this
                        + ") with randomSelection (" + randomSelection
                        + ") cannot have a selectorProbabilityWeightFactory (" + selectorProbabilityWeightFactory
                        + ").");
            }
        } else {
            if (selectorProbabilityWeightFactory == null) {
                throw new IllegalArgumentException("The selector (" + this
                        + ") with randomSelection (" + randomSelection
                        + ") requires a selectorProbabilityWeightFactory ("  + selectorProbabilityWeightFactory
                        + ").");
            }
        }
    }

    @Override
    public void stepStarted(AbstractStepScope stepScope) {
        scoreDirector = stepScope.getScoreDirector();
        super.stepStarted(stepScope);
    }

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        super.stepEnded(stepScope);
        scoreDirector = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

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
            size += moveSelector.getSize();
        }
        return size;
    }

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

    public class RandomUnionMoveIterator implements Iterator<Move> {

        protected final NavigableMap<Double, Iterator<Move>> moveIteratorMap;
        protected final Map<Iterator<Move>, ProbabilityItem> probabilityItemMap;
        protected double probabilityWeightTotal;

        public RandomUnionMoveIterator() {
            moveIteratorMap = new TreeMap<Double, Iterator<Move>>();
            probabilityItemMap = new LinkedHashMap<Iterator<Move>, ProbabilityItem>(childMoveSelectorList.size());
            for (MoveSelector moveSelector : childMoveSelectorList) {
                Iterator<Move> moveIterator = moveSelector.iterator();
                if (moveIterator.hasNext()) {
                    ProbabilityItem probabilityItem = new ProbabilityItem();
                    probabilityItem.moveSelector = moveSelector;
                    probabilityItem.moveIterator = moveIterator;
                    probabilityItem.probabilityWeight= selectorProbabilityWeightFactory
                            .createProbabilityWeight(scoreDirector, moveSelector);
                    probabilityItemMap.put(moveIterator, probabilityItem);
                }
            }
            refreshMoveIteratorMap();
        }

        public boolean hasNext() {
            return !moveIteratorMap.isEmpty();
        }

        public Move next() {
            double randomOffset = RandomUtils.nextDouble(workingRandom, probabilityWeightTotal);
            Map.Entry<Double, Iterator<Move>> entry = moveIteratorMap.floorEntry(randomOffset);
            // entry is never null because randomOffset < probabilityWeightTotal
            Iterator<Move> moveIterator = entry.getValue();
            Move next = moveIterator.next();
            if (!moveIterator.hasNext()) {
                probabilityItemMap.remove(moveIterator);
                refreshMoveIteratorMap();
            }
            return next;
        }

        private void refreshMoveIteratorMap() {
            moveIteratorMap.clear();
            double probabilityWeightOffset = 0.0;
            for (ProbabilityItem probabilityItem : probabilityItemMap.values()) {
                moveIteratorMap.put(probabilityWeightOffset, probabilityItem.moveIterator);
                probabilityWeightOffset += probabilityItem.probabilityWeight;
            }
            probabilityWeightTotal = probabilityWeightOffset;
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported.");
        }

    }

    private static class ProbabilityItem {

        protected MoveSelector moveSelector;
        protected Iterator<Move> moveIterator;
        protected double probabilityWeight;

    }

    @Override
    public String toString() {
        return "Union(" + childMoveSelectorList + ")";
    }

}

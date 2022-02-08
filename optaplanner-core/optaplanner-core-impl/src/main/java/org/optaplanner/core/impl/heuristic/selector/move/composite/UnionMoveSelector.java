/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.random.RandomUtils;

/**
 * A {@link CompositeMoveSelector} that unions 2 or more {@link MoveSelector}s.
 * <p>
 * For example: a union of {A, B, C} and {X, Y} will result in {A, B, C, X, Y}.
 * <p>
 * Warning: there is no duplicated {@link Move} check, so union of {A, B, C} and {B, D} will result in {A, B, C, B, D}.
 *
 * @see CompositeMoveSelector
 */
public class UnionMoveSelector<Solution_> extends CompositeMoveSelector<Solution_> {

    protected final SelectionProbabilityWeightFactory<Solution_, MoveSelector<Solution_>> selectorProbabilityWeightFactory;

    protected ScoreDirector<Solution_> scoreDirector;

    public UnionMoveSelector(List<MoveSelector<Solution_>> childMoveSelectorList, boolean randomSelection) {
        this(childMoveSelectorList, randomSelection, null);
    }

    public UnionMoveSelector(List<MoveSelector<Solution_>> childMoveSelectorList, boolean randomSelection,
            SelectionProbabilityWeightFactory<Solution_, MoveSelector<Solution_>> selectorProbabilityWeightFactory) {
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
                        + ") requires a selectorProbabilityWeightFactory (" + selectorProbabilityWeightFactory
                        + ").");
            }
        }
    }

    @Override
    public void stepStarted(AbstractStepScope<Solution_> stepScope) {
        scoreDirector = stepScope.getScoreDirector();
        super.stepStarted(stepScope);
    }

    @Override
    public void stepEnded(AbstractStepScope<Solution_> stepScope) {
        super.stepEnded(stepScope);
        scoreDirector = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isNeverEnding() {
        if (randomSelection) {
            for (MoveSelector<Solution_> moveSelector : childMoveSelectorList) {
                if (moveSelector.isNeverEnding()) {
                    return true;
                }
            }
            // The UnionMoveSelector is special: it can be randomSelection true and still neverEnding false
            return false;
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
            size += moveSelector.getSize();
        }
        return size;
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        if (!randomSelection) {
            Stream<Move<Solution_>> stream = Stream.empty();
            for (MoveSelector<Solution_> moveSelector : childMoveSelectorList) {
                stream = Stream.concat(stream, toStream(moveSelector));
            }
            return stream.iterator();
        } else {
            return new RandomUnionMoveIterator();
        }
    }

    private static <Solution_> Stream<Move<Solution_>> toStream(MoveSelector<Solution_> moveSelector) {
        return StreamSupport.stream(moveSelector.spliterator(), false);
    }

    public class RandomUnionMoveIterator extends SelectionIterator<Move<Solution_>> {

        protected final Map<Iterator<Move<Solution_>>, ProbabilityItem<Solution_>> probabilityItemMap;

        protected final NavigableMap<Double, Iterator<Move<Solution_>>> moveIteratorMap;
        protected double probabilityWeightTotal;
        protected boolean stale;

        public RandomUnionMoveIterator() {
            probabilityItemMap = new LinkedHashMap<>(childMoveSelectorList.size());
            for (MoveSelector<Solution_> moveSelector : childMoveSelectorList) {
                Iterator<Move<Solution_>> moveIterator = moveSelector.iterator();
                ProbabilityItem<Solution_> probabilityItem = new ProbabilityItem<>();
                probabilityItem.moveSelector = moveSelector;
                probabilityItem.moveIterator = moveIterator;
                probabilityItem.probabilityWeight = selectorProbabilityWeightFactory
                        .createProbabilityWeight(scoreDirector, moveSelector);
                if (probabilityItem.probabilityWeight < 0.0) {
                    throw new IllegalStateException(
                            "The selectorProbabilityWeightFactory (" + selectorProbabilityWeightFactory
                                    + ") returned a negative probabilityWeight (" + probabilityItem.probabilityWeight + ").");
                }
                probabilityItemMap.put(moveIterator, probabilityItem);
            }
            moveIteratorMap = new TreeMap<>();
            stale = true;
        }

        @Override
        public boolean hasNext() {
            if (stale) {
                refreshMoveIteratorMap();
            }
            return !moveIteratorMap.isEmpty();
        }

        @Override
        public Move<Solution_> next() {
            if (stale) {
                refreshMoveIteratorMap();
            }
            double randomOffset = RandomUtils.nextDouble(workingRandom, probabilityWeightTotal);
            Map.Entry<Double, Iterator<Move<Solution_>>> entry = moveIteratorMap.floorEntry(randomOffset);
            // entry is never null because randomOffset < probabilityWeightTotal
            Iterator<Move<Solution_>> moveIterator = entry.getValue();
            Move<Solution_> next = moveIterator.next();
            if (!moveIterator.hasNext()) {
                stale = true;
            }
            return next;
        }

        private void refreshMoveIteratorMap() {
            moveIteratorMap.clear();
            double probabilityWeightOffset = 0.0;
            for (ProbabilityItem<Solution_> probabilityItem : probabilityItemMap.values()) {
                if (probabilityItem.probabilityWeight != 0.0
                        && probabilityItem.moveIterator.hasNext()) {
                    moveIteratorMap.put(probabilityWeightOffset, probabilityItem.moveIterator);
                    probabilityWeightOffset += probabilityItem.probabilityWeight;
                }
            }
            probabilityWeightTotal = probabilityWeightOffset;
        }

    }

    private static class ProbabilityItem<Solution_> {

        protected MoveSelector<Solution_> moveSelector;
        protected Iterator<Move<Solution_>> moveIterator;
        protected double probabilityWeight;

    }

    @Override
    public String toString() {
        return "Union(" + childMoveSelectorList + ")";
    }

}

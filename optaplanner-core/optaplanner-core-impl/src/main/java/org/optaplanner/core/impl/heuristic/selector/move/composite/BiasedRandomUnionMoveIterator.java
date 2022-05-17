/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import java.util.Random;
import java.util.TreeMap;
import java.util.function.ToDoubleFunction;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.solver.random.RandomUtils;

final class BiasedRandomUnionMoveIterator<Solution_> extends SelectionIterator<Move<Solution_>> {

    private final Map<Iterator<Move<Solution_>>, ProbabilityItem<Solution_>> probabilityItemMap;
    private final NavigableMap<Double, Iterator<Move<Solution_>>> moveIteratorMap;
    private final Random workingRandom;
    private double probabilityWeightTotal;
    private boolean stale;

    public BiasedRandomUnionMoveIterator(List<MoveSelector<Solution_>> childMoveSelectorList,
            ToDoubleFunction<MoveSelector<Solution_>> probabilityWeightFunction,
            Random workingRandom) {
        this.probabilityItemMap = new LinkedHashMap<>(childMoveSelectorList.size());
        for (MoveSelector<Solution_> moveSelector : childMoveSelectorList) {
            Iterator<Move<Solution_>> moveIterator = moveSelector.iterator();
            ProbabilityItem<Solution_> probabilityItem = new ProbabilityItem<>();
            probabilityItem.moveSelector = moveSelector;
            probabilityItem.moveIterator = moveIterator;
            probabilityItem.probabilityWeight = probabilityWeightFunction.applyAsDouble(moveSelector);
            probabilityItemMap.put(moveIterator, probabilityItem);
        }
        this.moveIteratorMap = new TreeMap<>();
        this.stale = true;
        this.workingRandom = workingRandom;
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
        // The entry is never null because randomOffset < probabilityWeightTotal
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

    private static final class ProbabilityItem<Solution_> {

        MoveSelector<Solution_> moveSelector;
        Iterator<Move<Solution_>> moveIterator;
        double probabilityWeight;

    }

}

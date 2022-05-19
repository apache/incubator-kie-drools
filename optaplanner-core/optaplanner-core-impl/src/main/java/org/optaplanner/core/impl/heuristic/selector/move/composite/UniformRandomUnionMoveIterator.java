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

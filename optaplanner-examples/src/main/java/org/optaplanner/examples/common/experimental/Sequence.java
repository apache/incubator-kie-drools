/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.experimental;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Sequence<ValueType_> {
    private final NavigableSet<ValueType_> consecutiveItemsSet;
    private final Map<ValueType_, Integer> count;
    private final ConsecutiveSetTree<ValueType_, ?, ?> sourceTree;

    protected Sequence(ConsecutiveSetTree<ValueType_, ?, ?> sourceTree) {
        this(sourceTree, new TreeSet<>(sourceTree.getComparator()), new IdentityHashMap<>());
    }

    protected Sequence(ConsecutiveSetTree<ValueType_, ?, ?> sourceTree, NavigableSet<ValueType_> consecutiveItemsSet,
            Map<ValueType_, Integer> count) {
        this.sourceTree = sourceTree;
        this.consecutiveItemsSet = consecutiveItemsSet;
        this.count = count;
    }

    public NavigableSet<ValueType_> getItems() {
        return consecutiveItemsSet;
    }

    public int getLength() {
        return consecutiveItemsSet.size();
    }

    protected boolean isEmpty() {
        return consecutiveItemsSet.isEmpty();
    }

    protected int getCountIncludingDuplicates() {
        return count.values().stream().reduce(Integer::sum).orElse(0);
    }

    protected Stream<ValueType_> getDuplicatedStream() {
        return consecutiveItemsSet.stream()
                .flatMap(item -> IntStream.range(0, count.get(item)).mapToObj(index -> item));
    }

    protected void add(ValueType_ item) {
        if (!count.containsKey(item)) {
            consecutiveItemsSet.add(item);
        }
        count.merge(item, 1, Integer::sum);
    }

    protected Sequence<ValueType_> split(ValueType_ fromElement) {
        TreeSet<ValueType_> splitConsecutiveItemsSet = new TreeSet<>(consecutiveItemsSet.tailSet(fromElement));
        Map<ValueType_, Integer> newCountMap = new IdentityHashMap<>();
        splitConsecutiveItemsSet.forEach(item -> {
            newCountMap.put(item, count.remove(item));
            consecutiveItemsSet.remove(item);
        });
        return new Sequence<>(sourceTree, splitConsecutiveItemsSet, newCountMap);
    }

    protected boolean remove(ValueType_ item) {
        if (!count.containsKey(item)) {
            return true;
        }
        Integer newCount = count.merge(item, -1, (a, b) -> {
            int out = a + b;
            if (out == 0) {
                return null;
            }
            return out;
        });
        if (newCount == null) {
            consecutiveItemsSet.remove(item);
            return true;
        }
        return false;
    }

    protected void putAll(Sequence<ValueType_> other) {
        other.getItems().forEach(item -> {
            consecutiveItemsSet.add(item);
            count.merge(item, other.count.get(item), Integer::sum);
        });
    }

    @Override
    public String toString() {
        return consecutiveItemsSet.stream().map(Objects::toString).collect(Collectors.joining(", ", "Sequence [", "]"));
    }
}

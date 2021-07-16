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

package org.optaplanner.examples.common.experimental.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.optaplanner.examples.common.experimental.api.Break;
import org.optaplanner.examples.common.experimental.api.ConsecutiveInfo;
import org.optaplanner.examples.common.experimental.api.Sequence;

public class ConsecutiveSetTreeTest {

    private ConsecutiveSetTree<Integer, Integer, Integer> getIntegerConsecutiveSetTree() {
        return new ConsecutiveSetTree<>(i -> i, (a, b) -> b - a, Integer::sum, 1, 0);
    }

    private <ValueType_, DifferenceType_ extends Comparable<DifferenceType_>> Break<ValueType_, DifferenceType_> getBreak(
            ConsecutiveInfo<ValueType_, DifferenceType_> consecutiveData, ValueType_ start, ValueType_ end,
            DifferenceType_ length) {
        Sequence<ValueType_, DifferenceType_> previousSequence = null;
        Sequence<ValueType_, DifferenceType_> nextSequence = null;
        for (Sequence<ValueType_, DifferenceType_> sequence : consecutiveData.getConsecutiveSequences()) {
            if (sequence.getLastItem().equals(start)) {
                previousSequence = sequence;
            }
            if (sequence.getFirstItem().equals(end)) {
                nextSequence = sequence;
            }
        }

        if (previousSequence == null || nextSequence == null) {
            throw new IllegalStateException("Unable to find sequence with provided start/end points in ("
                    + consecutiveData.getConsecutiveSequences() + ")");
        }
        return new BreakImpl<>(previousSequence, nextSequence, length);
    }

    @Test
    public void testNonconsecutiveNumbers() {
        ConsecutiveSetTree<Integer, Integer, Integer> tree = getIntegerConsecutiveSetTree();
        tree.add(1);
        tree.add(3);
        tree.add(7);

        ConsecutiveInfo<Integer, Integer> consecutiveData = tree.getConsecutiveData();

        IterableList<Sequence<Integer, Integer>> sequenceList = new IterableList<>(consecutiveData.getConsecutiveSequences());
        assertThat(sequenceList).hasSize(3);
        IterableList<Break<Integer, Integer>> breakList = new IterableList<>(consecutiveData.getBreaks());
        assertThat(breakList).hasSize(2);

        assertThat(consecutiveData.getConsecutiveSequences()).allMatch(seq -> seq.getCount() == 1);
        assertThat(breakList.get(0)).usingRecursiveComparison().isEqualTo(getBreak(consecutiveData, 1, 3, 2));
        assertThat(breakList.get(1)).usingRecursiveComparison().isEqualTo(getBreak(consecutiveData, 3, 7, 4));
    }

    @Test
    public void testConsecutiveNumbers() {
        ConsecutiveSetTree<Integer, Integer, Integer> tree = getIntegerConsecutiveSetTree();
        tree.add(1);
        tree.add(2);
        tree.add(3);

        tree.add(5);
        tree.add(6);
        tree.add(7);
        tree.add(8);

        ConsecutiveInfo<Integer, Integer> consecutiveData = tree.getConsecutiveData();
        IterableList<Sequence<Integer, Integer>> sequenceList = new IterableList<>(consecutiveData.getConsecutiveSequences());
        assertThat(sequenceList).hasSize(2);
        IterableList<Break<Integer, Integer>> breakList = new IterableList<>(consecutiveData.getBreaks());
        assertThat(breakList).hasSize(1);

        assertThat(sequenceList.get(0).getCount()).isEqualTo(3);
        assertThat(sequenceList.get(1).getCount()).isEqualTo(4);
        assertThat(breakList.get(0)).usingRecursiveComparison().isEqualTo(getBreak(consecutiveData, 3, 5, 2));
    }

    @Test
    public void testDuplicateNumbers() {
        ConsecutiveSetTree<Integer, Integer, Integer> tree = getIntegerConsecutiveSetTree();
        tree.add(1);
        tree.add(2);
        tree.add(3);
        tree.add(3);
        tree.add(3);

        ConsecutiveInfo<Integer, Integer> consecutiveData = tree.getConsecutiveData();
        IterableList<Sequence<Integer, Integer>> sequenceList = new IterableList<>(consecutiveData.getConsecutiveSequences());
        assertThat(sequenceList).hasSize(1);
        IterableList<Break<Integer, Integer>> breakList = new IterableList<>(consecutiveData.getBreaks());
        assertThat(breakList).hasSize(0);

        assertThat(sequenceList.get(0).getCount()).isEqualTo(3);
        assertThat(consecutiveData.getBreaks()).hasSize(0);

        tree.remove(3);
        assertThat(sequenceList).hasSize(1);
        assertThat(sequenceList.get(0).getCount()).isEqualTo(3);
        assertThat(breakList).hasSize(0);

        tree.remove(3);
        assertThat(sequenceList).hasSize(1);
        assertThat(sequenceList.get(0).getCount()).isEqualTo(3);
        assertThat(breakList).hasSize(0);

        tree.remove(3);
        assertThat(sequenceList).hasSize(1);
        assertThat(sequenceList.get(0).getCount()).isEqualTo(2);
        assertThat(consecutiveData.getBreaks()).hasSize(0);
    }

    @Test
    public void testConsecutiveReverseNumbers() {
        ConsecutiveSetTree<Integer, Integer, Integer> tree = getIntegerConsecutiveSetTree();
        tree.add(3);
        tree.add(2);
        tree.add(1);

        tree.add(8);
        tree.add(7);
        tree.add(6);
        tree.add(5);

        ConsecutiveInfo<Integer, Integer> consecutiveData = tree.getConsecutiveData();
        IterableList<Sequence<Integer, Integer>> sequenceList = new IterableList<>(consecutiveData.getConsecutiveSequences());
        assertThat(sequenceList).hasSize(2);
        IterableList<Break<Integer, Integer>> breakList = new IterableList<>(consecutiveData.getBreaks());
        assertThat(breakList).hasSize(1);

        assertThat(sequenceList.get(0).getCount()).isEqualTo(3);
        assertThat(sequenceList.get(1).getCount()).isEqualTo(4);
        assertThat(breakList.get(0)).usingRecursiveComparison().isEqualTo(getBreak(consecutiveData, 3, 5, 2));
    }

    @Test
    public void testJoinOfTwoChains() {
        ConsecutiveSetTree<Integer, Integer, Integer> tree = getIntegerConsecutiveSetTree();
        tree.add(1);
        tree.add(2);
        tree.add(3);

        tree.add(5);
        tree.add(6);
        tree.add(7);
        tree.add(8);

        tree.add(4);

        ConsecutiveInfo<Integer, Integer> consecutiveData = tree.getConsecutiveData();
        IterableList<Sequence<Integer, Integer>> sequenceList = new IterableList<>(consecutiveData.getConsecutiveSequences());

        assertThat(sequenceList).hasSize(1);
        assertThat(sequenceList.get(0).getCount()).isEqualTo(8);
        assertThat(consecutiveData.getBreaks()).hasSize(0);
    }

    @Test
    public void testBreakOfChain() {
        ConsecutiveSetTree<Integer, Integer, Integer> tree = getIntegerConsecutiveSetTree();
        tree.add(1);
        tree.add(2);
        tree.add(3);
        tree.add(4);
        tree.add(5);
        tree.add(6);
        tree.add(7);

        tree.remove(4);

        ConsecutiveInfo<Integer, Integer> consecutiveData = tree.getConsecutiveData();
        IterableList<Sequence<Integer, Integer>> sequenceList = new IterableList<>(consecutiveData.getConsecutiveSequences());
        assertThat(sequenceList).hasSize(2);
        IterableList<Break<Integer, Integer>> breakList = new IterableList<>(consecutiveData.getBreaks());
        assertThat(breakList).hasSize(1);

        assertThat(sequenceList).hasSize(2);
        assertThat(sequenceList.get(0).getCount()).isEqualTo(3);
        assertThat(sequenceList.get(1).getCount()).isEqualTo(3);
        assertThat(breakList).hasSize(1);
        assertThat(breakList.get(0)).usingRecursiveComparison().isEqualTo(getBreak(consecutiveData, 3, 5, 2));
    }

    @Test
    public void testChainRemoval() {
        ConsecutiveSetTree<Integer, Integer, Integer> tree = getIntegerConsecutiveSetTree();
        tree.add(1);
        tree.add(2);
        tree.add(3);

        tree.add(5);
        tree.add(6);
        tree.add(7);

        tree.remove(2);
        tree.remove(1);
        tree.remove(3);

        ConsecutiveInfo<Integer, Integer> consecutiveData = tree.getConsecutiveData();
        IterableList<Sequence<Integer, Integer>> sequenceList = new IterableList<>(consecutiveData.getConsecutiveSequences());
        assertThat(sequenceList).hasSize(1);

        assertThat(sequenceList.get(0).getCount()).isEqualTo(3);
        assertThat(consecutiveData.getBreaks()).hasSize(0);
    }

    @Test
    public void testShorteningOfChain() {
        ConsecutiveSetTree<Integer, Integer, Integer> tree = getIntegerConsecutiveSetTree();
        tree.add(1);
        tree.add(2);
        tree.add(3);
        tree.add(4);
        tree.add(5);
        tree.add(6);
        tree.add(7);

        tree.remove(7);

        ConsecutiveInfo<Integer, Integer> consecutiveData = tree.getConsecutiveData();
        IterableList<Sequence<Integer, Integer>> sequenceList = new IterableList<>(consecutiveData.getConsecutiveSequences());

        assertThat(sequenceList).hasSize(1);
        assertThat(sequenceList.get(0).getCount()).isEqualTo(6);
        assertThat(consecutiveData.getBreaks()).hasSize(0);

        tree.remove(1);
        assertThat(sequenceList).hasSize(1);
        assertThat(sequenceList.get(0).getCount()).isEqualTo(5);
        assertThat(consecutiveData.getBreaks()).hasSize(0);
    }

    @Test
    public void testRandomSequences() {
        Random random = new Random(1);
        TreeMap<Integer, Integer> valueToCountMap = new TreeMap<>();

        // Tree we are testing is at most difference 2
        ConsecutiveSetTree<Integer, Integer, Integer> tree =
                new ConsecutiveSetTree<>(i -> i, (a, b) -> b - a, Integer::sum, 2, 0);

        for (int i = 0; i < 1000; i++) {
            int value = random.nextInt(64);
            String op;
            if (valueToCountMap.containsKey(value) && random.nextDouble() < 0.75) {
                op = valueToCountMap.keySet().stream().map(Object::toString)
                        .collect(Collectors.joining(", ", "Removing " + value + " from [", "]"));
                valueToCountMap.computeIfPresent(value, (key, count) -> (count == 1) ? null : count - 1);
                tree.remove(value);
            } else {
                op = valueToCountMap.keySet().stream().map(Object::toString)
                        .collect(Collectors.joining(", ", "Adding " + value + " to [", "]"));
                valueToCountMap.merge(value, 1, Integer::sum);
                tree.add(value);
            }

            ConsecutiveSetTree<Integer, Integer, Integer> freshTree =
                    new ConsecutiveSetTree<>(val -> val, (a, b) -> b - a, Integer::sum, 2, 0);
            for (Map.Entry<Integer, Integer> entry : valueToCountMap.entrySet()) {
                IntStream.range(0, entry.getValue()).map(index -> entry.getKey()).forEach(freshTree::add);
            }

            assertThat(tree.getConsecutiveSequences()).as("Mismatched Sequence: " + op)
                    .usingRecursiveComparison()
                    .ignoringFields("sourceTree")
                    .isEqualTo(freshTree.getConsecutiveSequences());
            assertThat(tree.getBreaks()).as("Mismatched Break: " + op)
                    .usingRecursiveComparison()
                    .isEqualTo(freshTree.getBreaks());
        }
    }

    @Test
    public void testRandomSequencesWithDuplicates() {
        Random random = new Random(1);
        TreeMap<Integer, Integer> valueToCountMap =
                new TreeMap<>(Comparator.<Integer, Integer> comparing(Math::abs).thenComparing(System::identityHashCode));

        // Tree we are absolute value consecutive
        ConsecutiveSetTree<Integer, Integer, Integer> tree =
                new ConsecutiveSetTree<>(Math::abs, (a, b) -> b - a, Integer::sum, 2, 0);

        for (int i = 0; i < 1000; i++) {
            int value = random.nextInt(64) - 32;
            String op;
            if (valueToCountMap.containsKey(value) && random.nextDouble() < 0.75) {
                op = valueToCountMap.keySet().stream().map(Object::toString)
                        .collect(Collectors.joining(", ", "Removing " + value + " from [", "]"));
                valueToCountMap.computeIfPresent(value, (key, count) -> (count == 1) ? null : count - 1);
                tree.remove(value);
            } else {
                op = valueToCountMap.keySet().stream().map(Object::toString)
                        .collect(Collectors.joining(", ", "Adding " + value + " to [", "]"));
                valueToCountMap.merge(value, 1, Integer::sum);
                tree.add(value);
            }

            ConsecutiveSetTree<Integer, Integer, Integer> freshTree =
                    new ConsecutiveSetTree<>(Math::abs, (a, b) -> b - a, Integer::sum, 2, 0);
            for (Map.Entry<Integer, Integer> entry : valueToCountMap.entrySet()) {
                IntStream.range(0, entry.getValue()).map(index -> entry.getKey()).forEach(freshTree::add);
            }

            assertThat(tree.getConsecutiveSequences()).as("Mismatched Sequence: " + op)
                    .usingRecursiveComparison()
                    .ignoringFields("sourceTree")
                    .isEqualTo(freshTree.getConsecutiveSequences());
            assertThat(tree.getBreaks()).as("Mismatched Break: " + op)
                    .usingRecursiveComparison()
                    .isEqualTo(freshTree.getBreaks());
        }
    }

    private static class Timeslot {
        OffsetDateTime from;
        OffsetDateTime to;

        public Timeslot(int fromIndex, int toIndex) {
            from = OffsetDateTime.of(2000, 1, fromIndex + 1, 0, 0, 0, 0, ZoneOffset.UTC);
            to = OffsetDateTime.of(2000, 1, toIndex + 1, 0, 0, 0, 0, ZoneOffset.UTC);
        }
    }

    @Test
    public void testTimeslotConsecutive() {
        ConsecutiveSetTree<Timeslot, OffsetDateTime, Duration> tree = new ConsecutiveSetTree<>(
                ts -> ts.from, Duration::between, Duration::plus, Duration.ofDays(1), Duration.ZERO);

        Timeslot t1 = new Timeslot(0, 1);
        Timeslot t2 = new Timeslot(1, 2);

        Timeslot t3 = new Timeslot(3, 4);
        Timeslot t4 = new Timeslot(4, 5);
        Timeslot t5 = new Timeslot(5, 6);

        tree.add(t4);
        tree.add(t2);
        tree.add(t4);
        tree.add(t3);
        tree.add(t1);
        tree.add(t5);

        ConsecutiveInfo<Timeslot, Duration> consecutiveData = tree.getConsecutiveData();
        Iterable<Sequence<Timeslot, Duration>> sequenceList = consecutiveData.getConsecutiveSequences();
        assertThat(sequenceList).hasSize(2);
        Iterator<Sequence<Timeslot, Duration>> sequenceIterator = sequenceList.iterator();
        Iterable<Break<Timeslot, Duration>> breakList = consecutiveData.getBreaks();
        Iterator<Break<Timeslot, Duration>> breakIterator = breakList.iterator();
        assertThat(breakList).hasSize(1);

        assertThat(sequenceList).hasSize(2);
        assertThat(sequenceIterator.next().getItems()).containsExactly(t1, t2);
        assertThat(sequenceIterator.next().getItems()).containsExactly(t3, t4, t5);

        assertThat(breakList).hasSize(1);
        assertThat(breakIterator.next()).usingRecursiveComparison()
                .isEqualTo(getBreak(consecutiveData, t2, t3, Duration.ofDays(2)));
    }
}

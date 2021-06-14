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

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.optaplanner.examples.common.experimental.api.Break;
import org.optaplanner.examples.common.experimental.api.ConsecutiveInfo;
import org.optaplanner.examples.common.experimental.api.Sequence;

class ConsecutiveDataImpl<ValueType_, DifferenceType_ extends Comparable<DifferenceType_>> implements
        ConsecutiveInfo<ValueType_, DifferenceType_> {
    private final ConsecutiveSetTree<ValueType_, ?, DifferenceType_> sourceTree;

    protected ConsecutiveDataImpl(ConsecutiveSetTree<ValueType_, ?, DifferenceType_> sourceTree) {
        this.sourceTree = sourceTree;
    }

    @Override
    public Iterable<Sequence<ValueType_, DifferenceType_>> getConsecutiveSequences() {
        return (Iterable) sourceTree.getConsecutiveSequences();
    }

    @Override
    public Iterable<Break<ValueType_, DifferenceType_>> getBreaks() {
        return (Iterable) sourceTree.getBreaks();
    }

    public String toString() {
        Stream.Builder<Sequence<ValueType_, DifferenceType_>> streamBuilder = Stream.builder();
        for (Sequence<ValueType_, DifferenceType_> sequence : getConsecutiveSequences()) {
            streamBuilder.add(sequence);
        }

        return streamBuilder.build().map(Sequence::toString)
                .collect(Collectors.joining("; ", "ConsecutiveData [", "]"));
    }
}

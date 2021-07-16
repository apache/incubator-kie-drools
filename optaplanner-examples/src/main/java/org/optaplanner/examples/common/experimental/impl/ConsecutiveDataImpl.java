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

class ConsecutiveDataImpl<Value_, Difference_ extends Comparable<Difference_>> implements
        ConsecutiveInfo<Value_, Difference_> {
    private final ConsecutiveSetTree<Value_, ?, Difference_> sourceTree;

    protected ConsecutiveDataImpl(ConsecutiveSetTree<Value_, ?, Difference_> sourceTree) {
        this.sourceTree = sourceTree;
    }

    @Override
    public Iterable<Sequence<Value_, Difference_>> getConsecutiveSequences() {
        return (Iterable) sourceTree.getConsecutiveSequences();
    }

    @Override
    public Iterable<Break<Value_, Difference_>> getBreaks() {
        return (Iterable) sourceTree.getBreaks();
    }

    public String toString() {
        Stream.Builder<Sequence<Value_, Difference_>> streamBuilder = Stream.builder();
        for (Sequence<Value_, Difference_> sequence : getConsecutiveSequences()) {
            streamBuilder.add(sequence);
        }

        return streamBuilder.build().map(Sequence::toString)
                .collect(Collectors.joining("; ", "ConsecutiveData [", "]"));
    }
}

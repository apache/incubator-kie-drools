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

import java.util.NavigableSet;
import java.util.Objects;
import java.util.stream.Collectors;

import org.optaplanner.examples.common.experimental.api.Sequence;

class SequenceImpl<ValueType_, DifferenceType_ extends Comparable<DifferenceType_>>
        implements Sequence<ValueType_, DifferenceType_> {

    private final ConsecutiveSetTree<ValueType_, ?, DifferenceType_> sourceTree;
    private ValueType_ firstItem;
    private ValueType_ lastItem;

    // Memorized calculations
    private DifferenceType_ length;
    private NavigableSet<ValueType_> items;

    protected SequenceImpl(ConsecutiveSetTree<ValueType_, ?, DifferenceType_> sourceTree, ValueType_ item) {
        this(sourceTree, item, item);
    }

    protected SequenceImpl(ConsecutiveSetTree<ValueType_, ?, DifferenceType_> sourceTree,
            ValueType_ firstItem, ValueType_ lastItem) {
        this.sourceTree = sourceTree;
        this.firstItem = firstItem;
        this.lastItem = lastItem;
        length = null;
        items = null;
    }

    @Override
    public ValueType_ getFirstItem() {
        return firstItem;
    }

    @Override
    public ValueType_ getLastItem() {
        return lastItem;
    }

    @Override
    public NavigableSet<ValueType_> getItems() {
        if (items == null) {
            return items = sourceTree.getItemSet()
                    .subSet(firstItem, true, lastItem, true);
        }
        return items;
    }

    @Override
    public int getCount() {
        return getItems().size();
    }

    @Override
    public DifferenceType_ getLength() {
        if (length == null) {
            // memoize length for later calls
            // (assignment returns the right hand side)
            return length = sourceTree.getSequenceLength(this);
        }
        return length;
    }

    protected void setStart(ValueType_ item) {
        firstItem = item;
        invalidate();
    }

    protected void setEnd(ValueType_ item) {
        lastItem = item;
        invalidate();
    }

    // Called when start or end are removed; length
    // need to be invalidated
    protected void invalidate() {
        length = null;
        items = null;
    }

    protected SequenceImpl<ValueType_, DifferenceType_> split(ValueType_ fromElement) {
        ValueType_ newSequenceStart = sourceTree.getItemSet().higher(fromElement);
        ValueType_ newSequenceEnd = lastItem;

        lastItem = sourceTree.getItemSet().lower(fromElement);
        invalidate();
        return new SequenceImpl<>(sourceTree, newSequenceStart, newSequenceEnd);
    }

    // This Sequence is ALWAYS before other Sequence
    protected void merge(SequenceImpl<ValueType_, DifferenceType_> other) {
        lastItem = other.lastItem;
        invalidate();
    }

    @Override
    public String toString() {
        return getItems().stream().map(Objects::toString).collect(Collectors.joining(", ", "Sequence [", "]"));
    }
}

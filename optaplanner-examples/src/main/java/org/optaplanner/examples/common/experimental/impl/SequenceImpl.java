/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.common.experimental.impl;

import java.util.NavigableSet;
import java.util.Objects;
import java.util.stream.Collectors;

import org.optaplanner.examples.common.experimental.api.Break;
import org.optaplanner.examples.common.experimental.api.Sequence;

final class SequenceImpl<Value_, Difference_ extends Comparable<Difference_>> implements Sequence<Value_, Difference_> {

    private final ConsecutiveSetTree<Value_, ?, Difference_> sourceTree;
    private Value_ firstItem;
    private Value_ lastItem;

    // Memorized calculations
    private Difference_ length;
    private NavigableSet<Value_> items;

    SequenceImpl(ConsecutiveSetTree<Value_, ?, Difference_> sourceTree, Value_ item) {
        this(sourceTree, item, item);
    }

    SequenceImpl(ConsecutiveSetTree<Value_, ?, Difference_> sourceTree, Value_ firstItem, Value_ lastItem) {
        this.sourceTree = sourceTree;
        this.firstItem = firstItem;
        this.lastItem = lastItem;
        length = null;
        items = null;
    }

    @Override
    public Value_ getFirstItem() {
        return firstItem;
    }

    @Override
    public Value_ getLastItem() {
        return lastItem;
    }

    @Override
    public Break<Value_, Difference_> getPreviousBreak() {
        return sourceTree.getBreakBefore(firstItem);
    }

    @Override
    public Break<Value_, Difference_> getNextBreak() {
        return sourceTree.getBreakAfter(lastItem);
    }

    @Override
    public boolean isFirst() {
        return firstItem == sourceTree.getItemSet().first();
    }

    @Override
    public boolean isLast() {
        return lastItem == sourceTree.getItemSet().last();
    }

    @Override
    public NavigableSet<Value_> getItems() {
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
    public Difference_ getLength() {
        if (length == null) {
            // memoize length for later calls
            // (assignment returns the right hand side)
            return length = sourceTree.getSequenceLength(this);
        }
        return length;
    }

    void setStart(Value_ item) {
        firstItem = item;
        invalidate();
    }

    void setEnd(Value_ item) {
        lastItem = item;
        invalidate();
    }

    // Called when start or end are removed; length
    // need to be invalidated
    void invalidate() {
        length = null;
        items = null;
    }

    SequenceImpl<Value_, Difference_> split(Value_ fromElement) {
        NavigableSet<Value_> itemSet = getItems();
        Value_ newSequenceStart = itemSet.higher(fromElement);
        Value_ newSequenceEnd = lastItem;
        setEnd(itemSet.lower(fromElement));
        return new SequenceImpl<>(sourceTree, newSequenceStart, newSequenceEnd);
    }

    // This Sequence is ALWAYS before other Sequence
    void merge(SequenceImpl<Value_, Difference_> other) {
        lastItem = other.lastItem;
        invalidate();
    }

    @Override
    public String toString() {
        return getItems().stream().map(Objects::toString).collect(Collectors.joining(", ", "Sequence [", "]"));
    }
}

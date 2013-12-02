/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.api.domain.value.composite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections.IteratorUtils;
import org.optaplanner.core.api.domain.value.AbstractValueRange;
import org.optaplanner.core.api.domain.value.ValueRange;
import org.optaplanner.core.api.domain.value.iterator.ValueRangeIterator;
import org.optaplanner.core.impl.util.RandomUtils;

public class CompositeValueRange<T> extends AbstractValueRange<T> {

    private final List<? extends ValueRange<T>> childValueRangeList;
    private final long size;

    public CompositeValueRange(List<? extends ValueRange<T>> childValueRangeList) {
        this.childValueRangeList = childValueRangeList;
        long size = 0L;
        for (ValueRange<T> childValueRange : childValueRangeList) {
            size +=  childValueRange.getSize();
        }
        this.size = size;
    }

    public List<? extends ValueRange<T>> getChildValueRangeList() {
        return childValueRangeList;
    }

    @Override
    public boolean isCountable() {
        for (ValueRange<T> childValueRange : childValueRangeList) {
            if (!childValueRange.isCountable()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public T get(long index) {
        long remainingIndex = index;
        for (ValueRange<T> childValueRange : childValueRangeList) {
            long childSize = childValueRange.getSize();
            if (remainingIndex < childSize) {
                return childValueRange.get(remainingIndex);
            }
            remainingIndex -= childSize;
        }
        throw new IndexOutOfBoundsException("The index (" + index + ") must be less than the size (" + size + ").");
    }

    @Override
    public Iterator<T> createOriginalIterator() {
        List<Iterator<T>> iteratorList = new ArrayList<Iterator<T>>(childValueRangeList.size());
        for (ValueRange<T> childValueRange : childValueRangeList) {
            iteratorList.add(childValueRange.createOriginalIterator());
        }
        return IteratorUtils.chainedIterator(iteratorList);
    }

    @Override
    public Iterator<T> createRandomIterator(Random workingRandom) {
        return new RandomCompositeValueRangeIterator(workingRandom);
    }

    private class RandomCompositeValueRangeIterator extends ValueRangeIterator<T> {

        private final Random workingRandom;

        public RandomCompositeValueRangeIterator(Random workingRandom) {
            this.workingRandom = workingRandom;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public T next() {
            long index = RandomUtils.nextLong(workingRandom, size);
            long remainingIndex = index;
            for (ValueRange<T> childValueRange : childValueRangeList) {
                long childSize = childValueRange.getSize();
                if (remainingIndex < childSize) {
                    return childValueRange.get(remainingIndex);
                }
                remainingIndex -= childSize;
            }
            throw new IllegalStateException("Impossible state because index (" + index
                    + ") is always less than the size (" + size + ").");
        }

    }

}

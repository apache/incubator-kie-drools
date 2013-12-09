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

package org.optaplanner.core.impl.domain.valuerange.buildin.composite;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import org.apache.commons.collections.IteratorUtils;
import org.optaplanner.core.impl.domain.valuerange.AbstractValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.impl.domain.valuerange.util.ValueRangeIterator;
import org.optaplanner.core.impl.util.RandomUtils;

public class NullableValueRange<T> extends AbstractValueRange<T> {

    private final ValueRange<T> childValueRange;
    private final long size;

    public NullableValueRange(ValueRange<T> childValueRange) {
        this.childValueRange = childValueRange;
        size = childValueRange.getSize() + 1L;
    }

    public ValueRange<T> getChildValueRange() {
        return childValueRange;
    }

    @Override
    public boolean isCountable() {
        return childValueRange.isCountable();
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public T get(long index) {
        if (index == size - 1L) {
            return null;
        } else {
            return childValueRange.get(index);
        }
    }

    @Override
    public Iterator<T> createOriginalIterator() {
        return IteratorUtils.chainedIterator(childValueRange.createOriginalIterator(),
                new NullValueRangeIterator());
    }

    private class NullValueRangeIterator extends ValueRangeIterator<T> {

        private boolean hasNext = true;

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public T next() {
            if (!hasNext) {
                throw new NoSuchElementException();
            }
            hasNext = false;
            return null;
        }
    }

    @Override
    public Iterator<T> createRandomIterator(Random workingRandom) {
        return new RandomNullValueRangeIterator(workingRandom);
    }

    private class RandomNullValueRangeIterator extends ValueRangeIterator<T> {

        private final Random workingRandom;

        public RandomNullValueRangeIterator(Random workingRandom) {
            this.workingRandom = workingRandom;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public T next() {
            long index = RandomUtils.nextLong(workingRandom, size);
            return get(index);
        }

    }

}

/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.impl.domain.valuerange.AbstractCountableValueRange;
import org.optaplanner.core.impl.domain.valuerange.util.ValueRangeIterator;

public class EmptyValueRange<T> extends AbstractCountableValueRange<T> {

    public EmptyValueRange() {
    }

    @Override
    public long getSize() {
        return 0L;
    }

    @Override
    public T get(long index) {
        throw new IndexOutOfBoundsException("The index (" + index + ") must be >= 0 and < size ("
                + getSize() + ").");
    }

    @Override
    public boolean contains(T value) {
        return false;
    }

    @Override
    public Iterator<T> createOriginalIterator() {
        return new EmptyValueRangeIterator();
    }

    @Override
    public Iterator<T> createRandomIterator(Random workingRandom) {
        return new EmptyValueRangeIterator();
    }

    private class EmptyValueRangeIterator extends ValueRangeIterator<T> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public T next() {
            throw new NoSuchElementException();
        }

    }

    @Override
    public String toString() {
        return "[]"; // Formatting: interval (mathematics) ISO 31-11
    }

}

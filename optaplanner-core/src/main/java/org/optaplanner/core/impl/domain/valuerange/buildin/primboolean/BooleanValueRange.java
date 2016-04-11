/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.valuerange.buildin.primboolean;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import org.optaplanner.core.impl.domain.valuerange.AbstractCountableValueRange;
import org.optaplanner.core.impl.domain.valuerange.util.ValueRangeIterator;

public class BooleanValueRange extends AbstractCountableValueRange<Boolean> {

    public BooleanValueRange() {}

    @Override
    public long getSize() {
        return 2L;
    }

    @Override
    public boolean contains(Boolean value) {
        if (value == null) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean get(long index) {
        if (index < 0L || index >= 2L) {
            throw new IndexOutOfBoundsException("The index (" + index + ") must be >= 0 and < 2.");
        }
        return index == 0L ? Boolean.FALSE : Boolean.TRUE;
    }

    @Override
    public Iterator<Boolean> createOriginalIterator() {
        return new OriginalBooleanValueRangeIterator();
    }

    private class OriginalBooleanValueRangeIterator extends ValueRangeIterator<Boolean> {

        private boolean hasNext = true;
        private Boolean upcoming = Boolean.FALSE;

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public Boolean next() {
            if (!hasNext) {
                throw new NoSuchElementException();
            }
            Boolean next = upcoming;
            if (upcoming) {
                hasNext = false;
            } else {
                upcoming = Boolean.TRUE;
            }
            return next;
        }

    }

    @Override
    public Iterator<Boolean> createRandomIterator(Random workingRandom) {
        return new RandomBooleanValueRangeIterator(workingRandom);
    }

    private class RandomBooleanValueRangeIterator extends ValueRangeIterator<Boolean> {

        private final Random workingRandom;

        public RandomBooleanValueRangeIterator(Random workingRandom) {
            this.workingRandom = workingRandom;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Boolean next() {
            return Boolean.valueOf(workingRandom.nextBoolean());
        }

    }

    @Override
    public String toString() {
        return "[false, true]"; // Formatting: interval (mathematics) ISO 31-11
    }

}

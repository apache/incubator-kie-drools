package org.optaplanner.core.impl.domain.valuerange.buildin.primboolean;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import org.optaplanner.core.impl.domain.valuerange.AbstractCountableValueRange;
import org.optaplanner.core.impl.domain.valuerange.util.ValueRangeIterator;

public class BooleanValueRange extends AbstractCountableValueRange<Boolean> {

    public BooleanValueRange() {
    }

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

    private static final class OriginalBooleanValueRangeIterator extends ValueRangeIterator<Boolean> {

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

    private static final class RandomBooleanValueRangeIterator extends ValueRangeIterator<Boolean> {

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

package org.optaplanner.core.api.domain.value.composite;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import org.optaplanner.core.api.domain.value.AbstractValueRange;
import org.optaplanner.core.api.domain.value.iterator.ValueRangeIterator;
import org.optaplanner.core.impl.util.RandomUtils;

public class EmptyValueRange<T> extends AbstractValueRange<T> {

    public EmptyValueRange() {
    }

    @Override
    public boolean isCountable() {
        return true;
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

}

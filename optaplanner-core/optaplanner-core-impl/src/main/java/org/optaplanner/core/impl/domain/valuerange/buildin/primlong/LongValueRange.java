package org.optaplanner.core.impl.domain.valuerange.buildin.primlong;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import org.optaplanner.core.impl.domain.valuerange.AbstractCountableValueRange;
import org.optaplanner.core.impl.domain.valuerange.util.ValueRangeIterator;
import org.optaplanner.core.impl.solver.random.RandomUtils;

public class LongValueRange extends AbstractCountableValueRange<Long> {

    private final long from;
    private final long to;
    private final long incrementUnit;

    /**
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     */
    public LongValueRange(long from, long to) {
        this(from, to, 1);
    }

    /**
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     * @param incrementUnit {@code > 0}
     */
    public LongValueRange(long from, long to, long incrementUnit) {
        this.from = from;
        this.to = to;
        this.incrementUnit = incrementUnit;
        if (to < from) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " cannot have a from (" + from + ") which is strictly higher than its to (" + to + ").");
        }
        if (incrementUnit <= 0L) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " must have strictly positive incrementUnit (" + incrementUnit + ").");
        }
        if ((to - from) < 0L) { // Overflow way to detect if ((to - from) > Long.MAX_VALUE)
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " cannot have a from (" + from + ") and to (" + to
                    + ") with a gap greater than Long.MAX_VALUE (" + Long.MAX_VALUE + ").");
        }
        if ((to - from) % incrementUnit != 0L) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + "'s incrementUnit (" + incrementUnit
                    + ") must fit an integer number of times between from (" + from + ") and to (" + to + ").");
        }
    }

    @Override
    public long getSize() {
        return (to - from) / incrementUnit;
    }

    @Override
    public boolean contains(Long value) {
        if (value == null || value < from || value >= to) {
            return false;
        }
        if (incrementUnit == 1L) {
            return true;
        }
        return (value - from) % incrementUnit == 0L;
    }

    @Override
    public Long get(long index) {
        if (index < 0L || index >= getSize()) {
            throw new IndexOutOfBoundsException("The index (" + index + ") must be >= 0 and < size ("
                    + getSize() + ").");
        }
        return index * incrementUnit + from;
    }

    @Override
    public Iterator<Long> createOriginalIterator() {
        return new OriginalLongValueRangeIterator();
    }

    private class OriginalLongValueRangeIterator extends ValueRangeIterator<Long> {

        private long upcoming = from;

        @Override
        public boolean hasNext() {
            return upcoming < to;
        }

        @Override
        public Long next() {
            if (upcoming >= to) {
                throw new NoSuchElementException();
            }
            long next = upcoming;
            upcoming += incrementUnit;
            return next;
        }

    }

    @Override
    public Iterator<Long> createRandomIterator(Random workingRandom) {
        return new RandomLongValueRangeIterator(workingRandom);
    }

    private class RandomLongValueRangeIterator extends ValueRangeIterator<Long> {

        private final Random workingRandom;
        private final long size = getSize();

        public RandomLongValueRangeIterator(Random workingRandom) {
            this.workingRandom = workingRandom;
        }

        @Override
        public boolean hasNext() {
            return size > 0L;
        }

        @Override
        public Long next() {
            if (size <= 0L) {
                throw new NoSuchElementException();
            }
            long index = RandomUtils.nextLong(workingRandom, size);
            return index * incrementUnit + from;
        }

    }

    @Override
    public String toString() {
        return "[" + from + "-" + to + ")"; // Formatting: interval (mathematics) ISO 31-11
    }

}

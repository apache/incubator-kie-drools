package org.optaplanner.core.impl.domain.valuerange.buildin.primint;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import org.optaplanner.core.impl.domain.valuerange.AbstractCountableValueRange;
import org.optaplanner.core.impl.domain.valuerange.util.ValueRangeIterator;
import org.optaplanner.core.impl.solver.random.RandomUtils;

public class IntValueRange extends AbstractCountableValueRange<Integer> {

    private final int from;
    private final int to;
    private final int incrementUnit;

    /**
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     */
    public IntValueRange(int from, int to) {
        this(from, to, 1);
    }

    /**
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     * @param incrementUnit {@code > 0}
     */
    public IntValueRange(int from, int to, int incrementUnit) {
        this.from = from;
        this.to = to;
        this.incrementUnit = incrementUnit;
        if (to < from) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " cannot have a from (" + from + ") which is strictly higher than its to (" + to + ").");
        }
        if (incrementUnit <= 0) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " must have strictly positive incrementUnit (" + incrementUnit + ").");
        }
        if ((to - (long) from) % incrementUnit != 0L) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + "'s incrementUnit (" + incrementUnit
                    + ") must fit an integer number of times between from (" + from + ") and to (" + to + ").");
        }
    }

    @Override
    public long getSize() {
        return (to - (long) from) / incrementUnit;
    }

    @Override
    public boolean contains(Integer value) {
        if (value == null || value < from || value >= to) {
            return false;
        }
        if (incrementUnit == 1) {
            return true;
        }
        return ((long) value - from) % incrementUnit == 0;
    }

    @Override
    public Integer get(long index) {
        if (index < 0L || index >= getSize()) {
            throw new IndexOutOfBoundsException("The index (" + index + ") must be >= 0 and < size ("
                    + getSize() + ").");
        }
        return (int) (index * incrementUnit + from);
    }

    @Override
    public Iterator<Integer> createOriginalIterator() {
        return new OriginalIntValueRangeIterator();
    }

    private class OriginalIntValueRangeIterator extends ValueRangeIterator<Integer> {

        private int upcoming = from;

        @Override
        public boolean hasNext() {
            return upcoming < to;
        }

        @Override
        public Integer next() {
            if (upcoming >= to) {
                throw new NoSuchElementException();
            }
            int next = upcoming;
            upcoming += incrementUnit;
            return next;
        }

    }

    @Override
    public Iterator<Integer> createRandomIterator(Random workingRandom) {
        return new RandomIntValueRangeIterator(workingRandom);
    }

    private class RandomIntValueRangeIterator extends ValueRangeIterator<Integer> {

        private final Random workingRandom;
        private final long size = getSize();

        public RandomIntValueRangeIterator(Random workingRandom) {
            this.workingRandom = workingRandom;
        }

        @Override
        public boolean hasNext() {
            return size > 0L;
        }

        @Override
        public Integer next() {
            if (size <= 0L) {
                throw new NoSuchElementException();
            }
            long index = RandomUtils.nextLong(workingRandom, size);
            return (int) (index * incrementUnit + from);
        }

    }

    @Override
    public String toString() {
        return "[" + from + "-" + to + ")"; // Formatting: interval (mathematics) ISO 31-11
    }

}

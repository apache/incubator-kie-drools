package org.optaplanner.core.api.domain.value.buildin.primdouble;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import org.optaplanner.core.api.domain.value.AbstractValueRange;
import org.optaplanner.core.api.domain.value.iterator.ValueRangeIterator;
import org.optaplanner.core.impl.util.RandomUtils;

public class DoubleValueRange extends AbstractValueRange<Double> {

    private final double from;
    private final double to;

    /**
     * @param from inclusive minimum
     * @param to exclusive maximum, >= {@code from}
     */
    public DoubleValueRange(double from, double to) {
        this.from = from;
        this.to = to;
        if (to < from) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " cannot have a from (" + from + ") which is strictly higher than its to (" + to + ").");
        }
    }

    @Override
    public boolean isCountable() {
        return false;
    }

    @Override
    public long getSize() {
        throw new IllegalStateException("The " + getClass().getSimpleName() + " is not countable.");
    }

    @Override
    public Double get(long index) {
        throw new IllegalStateException("The " + getClass().getSimpleName() + " is not countable.");
    }

    @Override
    public Iterator<Double> createOriginalIterator() {
        // In theory, we can implement this by using Math.nextAfter(). But in practice, no one could use it.
        throw new IllegalStateException("The " + getClass().getSimpleName() + " is not countable.");
    }

    @Override
    public Iterator<Double> createRandomIterator(Random workingRandom) {
        return new RandomDoubleValueRangeIterator(workingRandom);
    }

    private class RandomDoubleValueRangeIterator extends ValueRangeIterator<Double> {

        private final Random workingRandom;

        public RandomDoubleValueRangeIterator(Random workingRandom) {
            this.workingRandom = workingRandom;
        }

        @Override
        public boolean hasNext() {
            return to != from;
        }

        @Override
        public Double next() {
            double diff = to - from;
            double next = from + diff * workingRandom.nextDouble();
            if (next >= to) {
                // Rounding error occurred
                next = Math.nextAfter(next, Double.NEGATIVE_INFINITY);
            }
            return next;
        }

    }

}

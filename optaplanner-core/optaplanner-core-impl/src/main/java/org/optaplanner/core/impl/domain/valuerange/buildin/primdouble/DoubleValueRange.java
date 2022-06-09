package org.optaplanner.core.impl.domain.valuerange.buildin.primdouble;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import org.optaplanner.core.impl.domain.valuerange.AbstractUncountableValueRange;
import org.optaplanner.core.impl.domain.valuerange.util.ValueRangeIterator;

public class DoubleValueRange extends AbstractUncountableValueRange<Double> {

    private final double from;
    private final double to;

    /**
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
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
    public boolean isEmpty() {
        return from == to;
    }

    @Override
    public boolean contains(Double value) {
        if (value == null) {
            return false;
        }
        return value >= from && value < to;
    }

    // In theory, we can implement createOriginalIterator() by using Math.nextAfter().
    // But in practice, no one could use it.

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
            if (to == from) {
                throw new NoSuchElementException();
            }
            double diff = to - from;
            double next = from + diff * workingRandom.nextDouble();
            if (next >= to) {
                // Rounding error occurred
                next = Math.nextAfter(next, Double.NEGATIVE_INFINITY);
            }
            return next;
        }

    }

    @Override
    public String toString() {
        return "[" + from + "-" + to + ")"; // Formatting: interval (mathematics) ISO 31-11
    }

}

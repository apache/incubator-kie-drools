package org.optaplanner.core.impl.domain.valuerange.buildin.biginteger;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import org.optaplanner.core.impl.domain.valuerange.AbstractCountableValueRange;
import org.optaplanner.core.impl.domain.valuerange.util.ValueRangeIterator;
import org.optaplanner.core.impl.solver.random.RandomUtils;

public class BigIntegerValueRange extends AbstractCountableValueRange<BigInteger> {

    private final BigInteger from;
    private final BigInteger to;
    private final BigInteger incrementUnit;

    /**
     * @param from never null, inclusive minimum
     * @param to never null, exclusive maximum, {@code >= from}
     */
    public BigIntegerValueRange(BigInteger from, BigInteger to) {
        this(from, to, BigInteger.valueOf(1L));
    }

    /**
     * @param from never null, inclusive minimum
     * @param to never null, exclusive maximum, {@code >= from}
     * @param incrementUnit never null, {@code > 0}
     */
    public BigIntegerValueRange(BigInteger from, BigInteger to, BigInteger incrementUnit) {
        this.from = from;
        this.to = to;
        this.incrementUnit = incrementUnit;
        if (to.compareTo(from) < 0) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " cannot have a from (" + from + ") which is strictly higher than its to (" + to + ").");
        }
        if (incrementUnit.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " must have strictly positive incrementUnit (" + incrementUnit + ").");
        }

        if (!to.subtract(from).remainder(incrementUnit).equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + "'s incrementUnit (" + incrementUnit
                    + ") must fit an integer number of times between from (" + from + ") and to (" + to + ").");
        }
    }

    @Override
    public long getSize() {
        return to.subtract(from).divide(incrementUnit).longValue();
    }

    @Override
    public BigInteger get(long index) {
        if (index < 0L || index >= getSize()) {
            throw new IndexOutOfBoundsException("The index (" + index + ") must be >= 0 and < size ("
                    + getSize() + ").");
        }
        return incrementUnit.multiply(BigInteger.valueOf(index)).add(from);
    }

    @Override
    public boolean contains(BigInteger value) {
        if (value == null || value.compareTo(from) < 0 || value.compareTo(to) >= 0) {
            return false;
        }
        return value.subtract(from).remainder(incrementUnit).compareTo(BigInteger.ZERO) == 0;
    }

    @Override
    public Iterator<BigInteger> createOriginalIterator() {
        return new OriginalBigIntegerValueRangeIterator();
    }

    private class OriginalBigIntegerValueRangeIterator extends ValueRangeIterator<BigInteger> {

        private BigInteger upcoming = from;

        @Override
        public boolean hasNext() {
            return upcoming.compareTo(to) < 0;
        }

        @Override
        public BigInteger next() {
            if (upcoming.compareTo(to) >= 0) {
                throw new NoSuchElementException();
            }
            BigInteger next = upcoming;
            upcoming = upcoming.add(incrementUnit);
            return next;
        }

    }

    @Override
    public Iterator<BigInteger> createRandomIterator(Random workingRandom) {
        return new RandomBigIntegerValueRangeIterator(workingRandom);
    }

    private class RandomBigIntegerValueRangeIterator extends ValueRangeIterator<BigInteger> {

        private final Random workingRandom;
        private final long size = getSize();

        public RandomBigIntegerValueRangeIterator(Random workingRandom) {
            this.workingRandom = workingRandom;
        }

        @Override
        public boolean hasNext() {
            return size > 0L;
        }

        @Override
        public BigInteger next() {
            if (size <= 0L) {
                throw new NoSuchElementException();
            }
            long index = RandomUtils.nextLong(workingRandom, size);
            return incrementUnit.multiply(BigInteger.valueOf(index)).add(from);
        }

    }

    @Override
    public String toString() {
        return "[" + from + "-" + to + ")"; // Formatting: interval (mathematics) ISO 31-11
    }

}

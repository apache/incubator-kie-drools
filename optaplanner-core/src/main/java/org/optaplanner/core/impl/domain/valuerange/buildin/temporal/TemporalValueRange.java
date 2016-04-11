package org.optaplanner.core.impl.domain.valuerange.buildin.temporal;

import org.optaplanner.core.impl.domain.valuerange.AbstractCountableValueRange;
import org.optaplanner.core.impl.domain.valuerange.util.ValueRangeIterator;
import org.optaplanner.core.impl.solver.random.RandomUtils;

import java.math.BigInteger;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class TemporalValueRange extends AbstractCountableValueRange<Temporal> {

    private final Temporal from;
    private final Temporal to;
    /** We could not use a {@link TemporalAmount} as {@code incrementUnit} due to lack of calculus functions. */
    private final long incrementUnitAmount;
    private final TemporalUnit incrementUnitType;

    /**
     * @param from never null, inclusive minimum
     * @param to never null, exclusive maximum, {@code >= from}
     * @param incrementUnitAmount {@code > 0}
     * @param incrementUnitType never null, must be {@link Temporal#isSupported(TemporalUnit) supported} by {@code from} and {@code to}
     */
    public TemporalValueRange(Temporal from, Temporal to, long incrementUnitAmount, TemporalUnit incrementUnitType) {
        this.from = from;
        this.to = to;
        this.incrementUnitAmount = incrementUnitAmount;
        this.incrementUnitType = incrementUnitType;

        if (from == null || to == null || incrementUnitType == null) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " must have a from (" + from + "),  to (" + to + ") and incrementUnitType (" + incrementUnitType
                    + ") that are not null.");
        }
        if (incrementUnitAmount <= 0) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " must have strictly positive incrementUnitAmount (" + incrementUnitAmount + ").");
        }
        if (!from.isSupported(incrementUnitType) || !to.isSupported(incrementUnitType)) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " must have a incrementUnitType (" + incrementUnitType
                    + ") that is supported by its from (" + from + ") and to (" + to + ").");
        }
        long space = from.until(to, incrementUnitType);
        if (space < 0) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " cannot have a from (" + from + ") which is strictly higher than its to (" + to + ").");
        }

        // Fail fast if there's a remainder on amount (to be consistent with other value ranges)
        // Do not fail fast if there's a remainder on type: what is the remainder in months between 31-JAN and 1-MAR?
        if (space % incrementUnitAmount > 0) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " 's incrementUnitAmount (" + incrementUnitAmount
                    + ") must fit an integer number of times in the space (" + space
                    + ") between from (" + from + ") and to (" + to + ").");
        }
    }

    @Override
    public long getSize() {
        return (from.until(to, incrementUnitType) / incrementUnitAmount);
    }

    @Override
    public Temporal get(long index) {
        if (index >= getSize() || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        return from.plus(index * incrementUnitAmount, incrementUnitType);
    }

    @Override
    public boolean contains(Temporal value) {
        if (value == null || !value.isSupported(incrementUnitType)) {
            return false;
        }

        // long delta = ...
        // return from.until(value, incrementUnit) >= 0 && to.until(value, incrementUnit) < 0 && delta == 0;

        for (long i = 0; i < getSize(); i++) {
            Temporal temporal = get(i);
            if (value.equals(temporal)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Temporal> createOriginalIterator() {
        return new OriginalTemporalValueRangeIterator();
    }

    private class OriginalTemporalValueRangeIterator extends ValueRangeIterator<Temporal> {

        private Temporal upcoming = from;

        @Override
        public boolean hasNext() {
            return upcoming.until(to, incrementUnitType) >= incrementUnitAmount;
        }

        @Override
        public Temporal next() {
            if (upcoming.until(to, incrementUnitType) < 0) {
                throw new NoSuchElementException();
            }

            Temporal next = upcoming;
            upcoming = upcoming.plus(incrementUnitAmount, incrementUnitType);
            return next;
        }

    }

    @Override
    public Iterator<Temporal> createRandomIterator(Random workingRandom) {
        return new RandomTemporalValueRangeIterator(workingRandom);
    }

    private class RandomTemporalValueRangeIterator extends ValueRangeIterator<Temporal> {

        private final Random workingRandom;
        private final long size = getSize();

        public RandomTemporalValueRangeIterator(Random workingRandom) {
            this.workingRandom = workingRandom;
        }

        @Override
        public boolean hasNext() {
            return size > 0L;
        }

        @Override
        public Temporal next() {
            long index = RandomUtils.nextLong(workingRandom, size);
            return get(index);
        }

    }

}

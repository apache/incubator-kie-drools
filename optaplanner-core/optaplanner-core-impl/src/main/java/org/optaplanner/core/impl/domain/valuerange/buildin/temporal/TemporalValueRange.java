package org.optaplanner.core.impl.domain.valuerange.buildin.temporal;

import java.time.DateTimeException;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import org.optaplanner.core.impl.domain.valuerange.AbstractCountableValueRange;
import org.optaplanner.core.impl.domain.valuerange.util.ValueRangeIterator;
import org.optaplanner.core.impl.solver.random.RandomUtils;

public class TemporalValueRange<Temporal_ extends Temporal & Comparable<? super Temporal_>>
        extends AbstractCountableValueRange<Temporal_> {

    private final Temporal_ from;
    private final Temporal_ to;
    /** We could not use a {@link TemporalAmount} as {@code incrementUnit} due to lack of calculus functions. */
    private final long incrementUnitAmount;
    private final TemporalUnit incrementUnitType;

    private final long size;

    /**
     * @param from never null, inclusive minimum
     * @param to never null, exclusive maximum, {@code >= from}
     * @param incrementUnitAmount {@code > 0}
     * @param incrementUnitType never null, must be {@link Temporal#isSupported(TemporalUnit) supported} by {@code from}
     *        and {@code to}
     */
    public TemporalValueRange(Temporal_ from, Temporal_ to, long incrementUnitAmount, TemporalUnit incrementUnitType) {
        this.from = from;
        this.to = to;
        this.incrementUnitAmount = incrementUnitAmount;
        this.incrementUnitType = incrementUnitType;

        if (from == null || to == null || incrementUnitType == null) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " must have a from (" + from + "), to (" + to + ") and incrementUnitType (" + incrementUnitType
                    + ") that are not null.");
        }
        if (incrementUnitAmount <= 0) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " must have strictly positive incrementUnitAmount (" + incrementUnitAmount + ").");
        }
        if (!from.isSupported(incrementUnitType) || !to.isSupported(incrementUnitType)) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " must have an incrementUnitType (" + incrementUnitType
                    + ") that is supported by its from (" + from + ") class (" + from.getClass().getSimpleName()
                    + ") and to (" + to + ") class (" + to.getClass().getSimpleName() + ").");
        }
        // We cannot use Temporal.until() to check bounds due to rounding errors
        if (from.compareTo(to) > 0) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " cannot have a from (" + from + ") which is strictly higher than its to (" + to + ").");
        }
        long space = from.until(to, incrementUnitType);
        Temporal expectedTo = from.plus(space, incrementUnitType);
        if (!to.equals(expectedTo)) {
            // Temporal.until() rounds down, but it needs to round up, to be consistent with Temporal.plus()
            space++;
            Temporal roundedExpectedTo;
            try {
                roundedExpectedTo = from.plus(space, incrementUnitType);
            } catch (DateTimeException e) {
                throw new IllegalArgumentException("The " + getClass().getSimpleName()
                        + "'s incrementUnitType (" + incrementUnitType
                        + ") must fit an integer number of times in the space (" + space
                        + ") between from (" + from + ") and to (" + to + ").\n"
                        + "The to (" + to + ") is not the expectedTo (" + expectedTo + ").", e);
            }
            // Fail fast if there's a remainder on type (to be consistent with other value ranges)
            if (!to.equals(roundedExpectedTo)) {
                throw new IllegalArgumentException("The " + getClass().getSimpleName()
                        + "'s incrementUnitType (" + incrementUnitType
                        + ") must fit an integer number of times in the space (" + space
                        + ") between from (" + from + ") and to (" + to + ").\n"
                        + "The to (" + to + ") is not the expectedTo (" + expectedTo
                        + ") nor the roundedExpectedTo (" + roundedExpectedTo + ").");
            }
        }

        // Fail fast if there's a remainder on amount (to be consistent with other value ranges)
        if (space % incrementUnitAmount > 0) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + "'s incrementUnitAmount (" + incrementUnitAmount
                    + ") must fit an integer number of times in the space (" + space
                    + ") between from (" + from + ") and to (" + to + ").");
        }
        size = space / incrementUnitAmount;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public Temporal_ get(long index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        return (Temporal_) from.plus(index * incrementUnitAmount, incrementUnitType);
    }

    @Override
    public boolean contains(Temporal_ value) {
        if (value == null || !value.isSupported(incrementUnitType)) {
            return false;
        }
        // We cannot use Temporal.until() to check bounds due to rounding errors
        if (value.compareTo(from) < 0 || value.compareTo(to) >= 0) {
            return false;
        }
        long fromSpace = from.until(value, incrementUnitType);
        if (value.equals(from.plus(fromSpace + 1, incrementUnitType))) {
            // Temporal.until() rounds down, but it needs to round up, to be consistent with Temporal.plus()
            fromSpace++;
        }

        // Only checking the modulus is not enough: 1-MAR + 1 month doesn't include 7-MAR but the modulus is 0 anyway
        return fromSpace % incrementUnitAmount == 0
                && value.equals(from.plus(fromSpace, incrementUnitType));
    }

    @Override
    public Iterator<Temporal_> createOriginalIterator() {
        return new OriginalTemporalValueRangeIterator();
    }

    private class OriginalTemporalValueRangeIterator extends ValueRangeIterator<Temporal_> {

        private long index = 0L;

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public Temporal_ next() {
            if (index >= size) {
                throw new NoSuchElementException();
            }

            // Do not use upcoming += incrementUnitAmount because 31-JAN + 1 month + 1 month returns 28-MAR
            Temporal_ next = get(index);
            index++;
            return next;
        }

    }

    @Override
    public Iterator<Temporal_> createRandomIterator(Random workingRandom) {
        return new RandomTemporalValueRangeIterator(workingRandom);
    }

    private class RandomTemporalValueRangeIterator extends ValueRangeIterator<Temporal_> {

        private final Random workingRandom;

        public RandomTemporalValueRangeIterator(Random workingRandom) {
            this.workingRandom = workingRandom;
        }

        @Override
        public boolean hasNext() {
            return size > 0L;
        }

        @Override
        public Temporal_ next() {
            long index = RandomUtils.nextLong(workingRandom, size);
            return get(index);
        }

    }

    @Override
    public String toString() {
        return "[" + from + "-" + to + ")"; // Formatting: interval (mathematics) ISO 31-11
    }

}

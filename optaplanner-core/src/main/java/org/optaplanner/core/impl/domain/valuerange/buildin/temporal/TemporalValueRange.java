package org.optaplanner.core.impl.domain.valuerange.buildin.temporal;

import org.optaplanner.core.impl.domain.valuerange.AbstractCountableValueRange;
import org.optaplanner.core.impl.domain.valuerange.util.ValueRangeIterator;
import org.optaplanner.core.impl.solver.random.RandomUtils;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Created by kevin on 08.04.2016.
 */
public class TemporalValueRange extends AbstractCountableValueRange<Temporal> {

    private final Temporal from;
    private final Temporal to;
    private final long incrementAmount;
    private final TemporalUnit incrementUnit;

    /**
     * @param from inclusive minimum
     * @param to exclusive maximum, {@code >= from}
     * @param incrementAmount {@code > 0}
     * @param incrementUnit depends on the supported units of {from} and {to}
     */
    public TemporalValueRange(Temporal from, Temporal to, long incrementAmount, TemporalUnit incrementUnit) {
        this.from = from;
        this.to = to;
        this.incrementAmount = incrementAmount;
        this.incrementUnit = incrementUnit;

        if (from == null || to == null || incrementUnit == null) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " must have from (" + from + "),  to (" + to + "), incrementUnit (" + incrementUnit + ") which are not null.");
        }

        if (incrementAmount <= 0) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " must have strictly positive incrementAmount (" + incrementAmount + ").");
        }

        if (!from.isSupported(incrementUnit) || !to.isSupported(incrementUnit)) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " must have a incrementUnit which is supported by from/to (" + incrementUnit + ").");
        }

        if (from.until(to, incrementUnit) < 0) {
            throw new IllegalArgumentException("The " + getClass().getSimpleName()
                    + " cannot have a from (" + from + ") which is strictly higher than its to (" + to + ").");
        }


    }

    private long getDuration() {
        return from.until(to, incrementUnit);
    }

    @Override
    public long getSize() {
        return (getDuration() / incrementAmount);
    }

    @Override
    public Temporal get(long index) {
        if (index >= getSize() || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        return from.plus(index * incrementAmount, incrementUnit);
    }

    @Override
    public Iterator<Temporal> createOriginalIterator() {
        return new OriginalTemporalValueRangeIterator();
    }

    private class OriginalTemporalValueRangeIterator extends ValueRangeIterator<Temporal> {

        private Temporal upcoming = from;

        @Override
        public boolean hasNext() {
            return upcoming.until(to, incrementUnit) >= incrementAmount;
        }

        @Override
        public Temporal next() {
            if (upcoming.until(to, incrementUnit) < 0) {
                throw new NoSuchElementException();
            }

            Temporal next = upcoming;
            upcoming = upcoming.plus(incrementAmount, incrementUnit);
            return next;
        }
    }

    @Override
    public boolean contains(Temporal value) {
        if (value == null || !value.isSupported(incrementUnit)) {return false;}

        // long delta = ...
        // return from.until(value, incrementUnit) >= 0 && to.until(value, incrementUnit) < 0 && delta == 0;

        for (long i = 0; i < getSize(); i++) {
            Temporal temp = get(i);
            if (value.equals(temp)) {
                return true;
            }
        }

        return false;
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

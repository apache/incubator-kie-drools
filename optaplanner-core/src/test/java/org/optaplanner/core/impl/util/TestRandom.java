package org.optaplanner.core.impl.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Assertions;

/**
 * On the later JDKs, it is no longer possible to mock {@link Random} to return custom sequences.
 * Therefore we introduce this class to allow for that use case.
 *
 * It allows to provide a sequence of pre-defined "random" values.
 * It throws an exception of that sequence has been exhausted.
 *
 * Due to some internals of OptaPlanner where randoms are read from {@link org.optaplanner.core.impl.solver.scope.SolverScope}
 * and never updated in later phases and steps,
 * we need to be able to reset the same random to start running a new sequence of numbers.
 * That is what {@link #reset(int...)} et al. are for.
 */
public final class TestRandom extends Random {

    private BigDecimal[] toReturn;
    private int returnCount = 0;
    private Integer lastRequestedIntBound = null;

    public TestRandom(int... toReturn) {
        super(0);
        reset(toReturn);
    }

    public TestRandom(long... toReturn) {
        super(0);
        reset(toReturn);
    }

    public TestRandom(double... toReturn) {
        super(0);
        reset(toReturn);
    }

    public TestRandom(boolean... toReturn) {
        super(0);
        reset(toReturn);
    }

    @Override
    public int nextInt(int bound) {
        lastRequestedIntBound = bound;
        return getNextValue().intValue();
    }

    private BigDecimal getNextValue() {
        returnCount++;
        if (returnCount > toReturn.length) {
            throw new IllegalStateException("Requested a random value past the specified collection (" +
                    Arrays.toString(toReturn) + ").\n" +
                    "The code being tested is requesting more random values than expected.");
        }
        return toReturn[returnCount - 1];
    }

    @Override
    protected int next(int bits) {
        throw new UnsupportedOperationException(getClass().getCanonicalName() + " does not support this method.");
    }

    @Override
    public void nextBytes(byte[] bytes) {
        throw new UnsupportedOperationException(getClass().getCanonicalName() + " does not support this method.");
    }

    @Override
    public int nextInt() {
        return nextInt(0);
    }

    @Override
    public long nextLong() {
        return getNextValue().longValue();
    }

    @Override
    public boolean nextBoolean() {
        return nextInt() > 0;
    }

    @Override
    public float nextFloat() {
        return getNextValue().floatValue();
    }

    @Override
    public double nextDouble() {
        return getNextValue().doubleValue();
    }

    @Override
    public synchronized double nextGaussian() {
        throw new UnsupportedOperationException(getClass().getCanonicalName() + " does not support this method.");
    }

    @Override
    public IntStream ints(long streamSize) {
        throw new UnsupportedOperationException(getClass().getCanonicalName() + " does not support this method.");
    }

    @Override
    public IntStream ints() {
        throw new UnsupportedOperationException(getClass().getCanonicalName() + " does not support this method.");
    }

    @Override
    public IntStream ints(long streamSize, int randomNumberOrigin, int randomNumberBound) {
        throw new UnsupportedOperationException(getClass().getCanonicalName() + " does not support this method.");
    }

    @Override
    public IntStream ints(int randomNumberOrigin, int randomNumberBound) {
        throw new UnsupportedOperationException(getClass().getCanonicalName() + " does not support this method.");
    }

    @Override
    public LongStream longs(long streamSize) {
        throw new UnsupportedOperationException(getClass().getCanonicalName() + " does not support this method.");
    }

    @Override
    public LongStream longs() {
        throw new UnsupportedOperationException(getClass().getCanonicalName() + " does not support this method.");
    }

    @Override
    public LongStream longs(long streamSize, long randomNumberOrigin, long randomNumberBound) {
        throw new UnsupportedOperationException(getClass().getCanonicalName() + " does not support this method.");
    }

    @Override
    public LongStream longs(long randomNumberOrigin, long randomNumberBound) {
        throw new UnsupportedOperationException(getClass().getCanonicalName() + " does not support this method.");
    }

    @Override
    public DoubleStream doubles(long streamSize) {
        throw new UnsupportedOperationException(getClass().getCanonicalName() + " does not support this method.");
    }

    @Override
    public DoubleStream doubles() {
        throw new UnsupportedOperationException(getClass().getCanonicalName() + " does not support this method.");
    }

    @Override
    public DoubleStream doubles(long streamSize, double randomNumberOrigin, double randomNumberBound) {
        throw new UnsupportedOperationException(getClass().getCanonicalName() + " does not support this method.");
    }

    @Override
    public DoubleStream doubles(double randomNumberOrigin, double randomNumberBound) {
        throw new UnsupportedOperationException(getClass().getCanonicalName() + " does not support this method.");
    }

    public void reset(int... toReturn) {
        this.toReturn = Arrays.stream(toReturn)
                .mapToObj(BigDecimal::valueOf)
                .toArray(BigDecimal[]::new);
        this.returnCount = 0;
    }

    public void reset(long... toReturn) {
        this.toReturn = Arrays.stream(toReturn)
                .mapToObj(BigDecimal::valueOf)
                .toArray(BigDecimal[]::new);
        this.returnCount = 0;
    }

    public void reset(double... toReturn) {
        this.toReturn = Arrays.stream(toReturn)
                .mapToObj(BigDecimal::valueOf)
                .toArray(BigDecimal[]::new);
        this.returnCount = 0;
    }

    public void reset(boolean... toReturn) {
        this.toReturn = new BigDecimal[toReturn.length];
        for (int i = 0; i < toReturn.length; i++) {
            this.toReturn[i] = toReturn[i] ? BigDecimal.ONE : BigDecimal.ZERO;
        }
        this.returnCount = 0;
    }

    /**
     * Check whether the last time that {@link #nextInt(int)} was last called with the given bound value.
     * If not, throws; otherwise resets the last known bound.
     *
     * @param bound
     * @throws org.opentest4j.AssertionFailedError when bound not matching
     */
    public void assertIntBoundJustRequested(int bound) {
        if (Objects.equals(bound, lastRequestedIntBound)) {
            lastRequestedIntBound = null;
            return;
        }
        Assertions.fail("Expected bound (" + bound + ") to have just been requested, " +
                "but was (" + lastRequestedIntBound + ").");
    }

}

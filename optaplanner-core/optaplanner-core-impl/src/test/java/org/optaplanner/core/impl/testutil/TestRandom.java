/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.testutil;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * On the later JDKs, it is no longer possible to mock {@link Random} to return custom sequences.
 * Therefore we introduce this class to allow for that use case.
 *
 * It allows to provide a sequence of pre-defined "random" values.
 * It throws an exception if that sequence has been exhausted.
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
        return nextInt(Integer.MAX_VALUE);
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
        return ints(streamSize, 0, Integer.MAX_VALUE);
    }

    @Override
    public IntStream ints() {
        return ints(toReturn.length);
    }

    @Override
    public IntStream ints(long streamSize, int randomNumberOrigin, int randomNumberBound) {
        return Stream.generate(this::nextInt)
                .mapToInt(i -> i)
                .filter(i -> i >= randomNumberOrigin && i < randomNumberBound)
                .limit(streamSize);
    }

    @Override
    public IntStream ints(int randomNumberOrigin, int randomNumberBound) {
        return ints(toReturn.length, randomNumberOrigin, randomNumberBound);
    }

    @Override
    public LongStream longs(long streamSize) {
        return longs(streamSize, 0, Long.MAX_VALUE);
    }

    @Override
    public LongStream longs() {
        return longs(toReturn.length);
    }

    @Override
    public LongStream longs(long streamSize, long randomNumberOrigin, long randomNumberBound) {
        return Stream.generate(this::nextLong)
                .mapToLong(l -> l)
                .filter(l -> l >= randomNumberOrigin && l < randomNumberBound)
                .limit(streamSize);
    }

    @Override
    public LongStream longs(long randomNumberOrigin, long randomNumberBound) {
        return longs(toReturn.length, randomNumberOrigin, randomNumberBound);
    }

    @Override
    public DoubleStream doubles(long streamSize) {
        return doubles(streamSize, 0, Double.MAX_VALUE);
    }

    @Override
    public DoubleStream doubles() {
        return doubles(toReturn.length);
    }

    @Override
    public DoubleStream doubles(long streamSize, double randomNumberOrigin, double randomNumberBound) {
        return Stream.generate(this::nextDouble)
                .mapToDouble(d -> d)
                .filter(d -> d >= randomNumberOrigin && d < randomNumberBound)
                .limit(streamSize);
    }

    @Override
    public DoubleStream doubles(double randomNumberOrigin, double randomNumberBound) {
        return doubles(toReturn.length, randomNumberOrigin, randomNumberBound);
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
        assertThat(lastRequestedIntBound)
                .as("Expected bound (%s) to have just been requested, but was (%s).", bound, lastRequestedIntBound)
                .isEqualTo(bound);
        lastRequestedIntBound = null;
    }

}

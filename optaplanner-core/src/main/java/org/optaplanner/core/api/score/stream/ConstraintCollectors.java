/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.api.score.stream;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Period;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.bi.DefaultBiConstraintCollector;
import org.optaplanner.core.impl.score.stream.uni.DefaultUniConstraintCollector;

/**
 * Creates an {@link UniConstraintCollector}, {@link BiConstraintCollector}, ... instance
 * for use in {@link UniConstraintStream#groupBy(Function, UniConstraintCollector)}, ...
 */
public final class ConstraintCollectors {

    // ************************************************************************
    // count
    // ************************************************************************

    public static <A> UniConstraintCollector<A, ?, Integer> count() {
        return new DefaultUniConstraintCollector<>(
                () -> new int[1],
                (resultContainer, a) -> {
                    resultContainer[0]++;
                    return (() -> resultContainer[0]--);
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A> UniConstraintCollector<A, ?, Long> countLong() {
        return new DefaultUniConstraintCollector<>(
                () -> new long[1],
                (resultContainer, a) -> {
                    resultContainer[0]++;
                    return (() -> resultContainer[0]--);
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B> BiConstraintCollector<A, B, ?, Integer> countBi() {
        return new DefaultBiConstraintCollector<>(
                () -> new int[1],
                (resultContainer, a, b) -> {
                    resultContainer[0]++;
                    return (() -> resultContainer[0]--);
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B> BiConstraintCollector<A, B, ?, Long> countLongBi() {
        return new DefaultBiConstraintCollector<>(
                () -> new long[1],
                (resultContainer, a, b) -> {
                    resultContainer[0]++;
                    return (() -> resultContainer[0]--);
                },
                resultContainer -> resultContainer[0]);
    }

    // ************************************************************************
    // countDistinct
    // ************************************************************************

    public static <A> UniConstraintCollector<A, ?, Integer> countDistinct(Function<A, ?> groupValueMapping) {
        class CountDistinctResultContainer {
            int count = 0;
            Map<Object, int[]> objectCountMap = new HashMap<>();
        }
        return new DefaultUniConstraintCollector<>(
                CountDistinctResultContainer::new,
                (resultContainer, a) -> {
                    Object value = groupValueMapping.apply(a);
                    int[] objectCount = resultContainer.objectCountMap.computeIfAbsent(value, k -> new int[1]);
                    if (objectCount[0] == 0) {
                        resultContainer.count++;
                    }
                    objectCount[0]++;
                    return (() -> {
                        int[] objectCount2 = resultContainer.objectCountMap.get(value);
                        if (objectCount2 == null) {
                            throw new IllegalStateException("Impossible state: the value (" + value
                                    + ") of A (" + a + ") is removed more times than it was added.");
                        }
                        objectCount2[0]--;
                        if (objectCount2[0] == 0) {
                            resultContainer.objectCountMap.remove(value);
                            resultContainer.count--;
                        }
                    });
                },
                resultContainer -> resultContainer.count);
    }

    public static <A> UniConstraintCollector<A, ?, Long> countDistinctLong(Function<A, ?> groupValueMapping) {
        class CountDistinctResultContainer {
            long count = 0L;
            Map<Object, long[]> objectCountMap = new HashMap<>();
        }
        return new DefaultUniConstraintCollector<>(
                CountDistinctResultContainer::new,
                (resultContainer, a) -> {
                    Object value = groupValueMapping.apply(a);
                    long[] objectCount = resultContainer.objectCountMap.computeIfAbsent(value, k -> new long[1]);
                    if (objectCount[0] == 0L) {
                        resultContainer.count++;
                    }
                    objectCount[0]++;
                    return (() -> {
                        long[] objectCount2 = resultContainer.objectCountMap.get(value);
                        if (objectCount2 == null) {
                            throw new IllegalStateException("Impossible state: the value (" + value
                                    + ") of A (" + a + ") is removed more times than it was added.");
                        }
                        objectCount2[0]--;
                        if (objectCount2[0] == 0L) {
                            resultContainer.objectCountMap.remove(value);
                            resultContainer.count--;
                        }
                    });
                },
                resultContainer -> resultContainer.count);
    }

    // ************************************************************************
    // sum
    // ************************************************************************

    public static <A> UniConstraintCollector<A, ?, Integer> sum(ToIntFunction<? super A> groupValueMapping) {
        return new DefaultUniConstraintCollector<>(
                () -> new int[1],
                (resultContainer, a) -> {
                    int value = groupValueMapping.applyAsInt(a);
                    resultContainer[0] += value;
                    return (() -> resultContainer[0] -= value);
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A> UniConstraintCollector<A, ?, Long> sumLong(ToLongFunction<? super A> groupValueMapping) {
        return new DefaultUniConstraintCollector<>(
                () -> new long[1],
                (resultContainer, a) -> {
                    long value = groupValueMapping.applyAsLong(a);
                    resultContainer[0] += value;
                    return (() -> resultContainer[0] -= value);
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A> UniConstraintCollector<A, ?, BigDecimal> sumBigDecimal(
            Function<? super A, BigDecimal> groupValueMapping) {
        return new DefaultUniConstraintCollector<>(
                () -> new BigDecimal[] { BigDecimal.ZERO },
                (resultContainer, a) -> {
                    BigDecimal value = groupValueMapping.apply(a);
                    resultContainer[0] = resultContainer[0].add(value);
                    return (() -> resultContainer[0] = resultContainer[0].subtract(value));
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A> UniConstraintCollector<A, ?, BigInteger> sumBigInteger(
            Function<? super A, BigInteger> groupValueMapping) {
        return new DefaultUniConstraintCollector<>(
                () -> new BigInteger[] { BigInteger.ZERO },
                (resultContainer, a) -> {
                    BigInteger value = groupValueMapping.apply(a);
                    resultContainer[0] = resultContainer[0].add(value);
                    return (() -> resultContainer[0] = resultContainer[0].subtract(value));
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A> UniConstraintCollector<A, ?, Duration> sumDuration(Function<? super A, Duration> groupValueMapping) {
        return new DefaultUniConstraintCollector<>(
                () -> new Duration[] { Duration.ZERO },
                (resultContainer, a) -> {
                    Duration value = groupValueMapping.apply(a);
                    resultContainer[0] = resultContainer[0].plus(value);
                    return (() -> resultContainer[0] = resultContainer[0].minus(value));
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A> UniConstraintCollector<A, ?, Period> sumPeriod(Function<? super A, Period> groupValueMapping) {
        return new DefaultUniConstraintCollector<>(
                () -> new Period[] { Period.ZERO },
                (resultContainer, a) -> {
                    Period value = groupValueMapping.apply(a);
                    resultContainer[0] = resultContainer[0].plus(value);
                    return (() -> resultContainer[0] = resultContainer[0].minus(value));
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B> BiConstraintCollector<A, B, ?, Integer> sum(
            ToIntBiFunction<? super A, ? super B> groupValueMapping) {
        return new DefaultBiConstraintCollector<>(
                () -> new int[1],
                (resultContainer, a, b) -> {
                    int value = groupValueMapping.applyAsInt(a, b);
                    resultContainer[0] += value;
                    return (() -> resultContainer[0] -= value);
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B> BiConstraintCollector<A, B, ?, Long> sumLong(
            ToLongBiFunction<? super A, ? super B> groupValueMapping) {
        return new DefaultBiConstraintCollector<>(
                () -> new long[1],
                (resultContainer, a, b) -> {
                    long value = groupValueMapping.applyAsLong(a, b);
                    resultContainer[0] += value;
                    return (() -> resultContainer[0] -= value);
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B> BiConstraintCollector<A, B, ?, BigDecimal> sumBigDecimal(
            BiFunction<? super A, ? super B, BigDecimal> groupValueMapping) {
        return new DefaultBiConstraintCollector<>(
                () -> new BigDecimal[] { BigDecimal.ZERO },
                (resultContainer, a, b) -> {
                    BigDecimal value = groupValueMapping.apply(a, b);
                    resultContainer[0] = resultContainer[0].add(value);
                    return (() -> resultContainer[0] = resultContainer[0].subtract(value));
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B> BiConstraintCollector<A, B, ?, BigInteger> sumBigInteger(
            BiFunction<? super A, ? super B, BigInteger> groupValueMapping) {
        return new DefaultBiConstraintCollector<>(
                () -> new BigInteger[] { BigInteger.ZERO },
                (resultContainer, a, b) -> {
                    BigInteger value = groupValueMapping.apply(a, b);
                    resultContainer[0] = resultContainer[0].add(value);
                    return (() -> resultContainer[0] = resultContainer[0].subtract(value));
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B> BiConstraintCollector<A, B, ?, Duration> sumDuration(
            BiFunction<? super A, ? super B, Duration> groupValueMapping) {
        return new DefaultBiConstraintCollector<>(
                () -> new Duration[] { Duration.ZERO },
                (resultContainer, a, b) -> {
                    Duration value = groupValueMapping.apply(a, b);
                    resultContainer[0] = resultContainer[0].plus(value);
                    return (() -> resultContainer[0] = resultContainer[0].minus(value));
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B> BiConstraintCollector<A, B, ?, Period> sumPeriod(
            BiFunction<? super A, ? super B, Period> groupValueMapping) {
        return new DefaultBiConstraintCollector<>(
                () -> new Period[] { Period.ZERO },
                (resultContainer, a, b) -> {
                    Period value = groupValueMapping.apply(a, b);
                    resultContainer[0] = resultContainer[0].plus(value);
                    return (() -> resultContainer[0] = resultContainer[0].minus(value));
                },
                resultContainer -> resultContainer[0]);
    }

    // ************************************************************************
    // min
    // ************************************************************************

    public static <A> UniConstraintCollector<A, ?, A> min(Comparator<A> comparator) {
        return minOrMax(comparator, true);
    }

    public static <A extends Comparable<A>> UniConstraintCollector<A, ?, A> min() {
        return min(Comparable::compareTo);
    }

    // ************************************************************************
    // max
    // ************************************************************************

    public static <A> UniConstraintCollector<A, ?, A> max(Comparator<A> comparator) {
        return minOrMax(comparator, false);
    }

    public static <A extends Comparable<A>> UniConstraintCollector<A, ?, A> max() {
        return max(Comparable::compareTo);
    }

    private static <A> UniConstraintCollector<A, SortedMap<A, Long>, A> minOrMax(Comparator<A> comparator,
            boolean min) {
        Function<SortedMap<A, Long>, A> keySupplier = min ? SortedMap::firstKey : SortedMap::lastKey;
        return new DefaultUniConstraintCollector<>(
                () -> new TreeMap<>(comparator),
                (resultContainer, a) -> {
                    resultContainer.compute(a, (key, value) -> value == null ? 1 : value + 1);
                    return (() -> resultContainer.compute(a, (key, value) -> value == 1 ? null : value - 1));
                },
                (resultContainer) -> resultContainer.size() == 0 ? null : keySupplier.apply(resultContainer));
    }

    private ConstraintCollectors() {
    }

}

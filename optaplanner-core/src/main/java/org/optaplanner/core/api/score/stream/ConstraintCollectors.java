/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.lang.reflect.Array;
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
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.ToIntQuadFunction;
import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongQuadFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.bi.DefaultBiConstraintCollector;
import org.optaplanner.core.impl.score.stream.quad.DefaultQuadConstraintCollector;
import org.optaplanner.core.impl.score.stream.tri.DefaultTriConstraintCollector;
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
                    return () -> resultContainer[0]--;
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A> UniConstraintCollector<A, ?, Long> countLong() {
        return new DefaultUniConstraintCollector<>(
                () -> new long[1],
                (resultContainer, a) -> {
                    resultContainer[0]++;
                    return () -> resultContainer[0]--;
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
                    return () -> resultContainer[0]--;
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B, C> TriConstraintCollector<A, B, C, ?, Integer> countTri() {
        return new DefaultTriConstraintCollector<>(
                () -> new int[1],
                (resultContainer, a, b, c) -> {
                    resultContainer[0]++;
                    return (() -> resultContainer[0]--);
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B, C> TriConstraintCollector<A, B, C, ?, Long> countLongTri() {
        return new DefaultTriConstraintCollector<>(
                () -> new long[1],
                (resultContainer, a, b, c) -> {
                    resultContainer[0]++;
                    return () -> resultContainer[0]--;
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B, C, D> QuadConstraintCollector<A, B, C, D, ?, Integer> countQuad() {
        return new DefaultQuadConstraintCollector<>(
                () -> new int[1],
                (resultContainer, a, b, c, d) -> {
                    resultContainer[0]++;
                    return () -> resultContainer[0]--;
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B, C, D> QuadConstraintCollector<A, B, C, D, ?, Long> countLongQuad() {
        return new DefaultQuadConstraintCollector<>(
                () -> new long[1],
                (resultContainer, a, b, c, d) -> {
                    resultContainer[0]++;
                    return () -> resultContainer[0]--;
                },
                resultContainer -> resultContainer[0]);
    }

    // ************************************************************************
    // countDistinct
    // ************************************************************************

    public static <A> UniConstraintCollector<A, ?, Integer> countDistinct(Function<A, ?> groupValueMapping) {
        return new DefaultUniConstraintCollector<>(
                CountDistinctResultContainer::new,
                (resultContainer, a) -> {
                    Object value = groupValueMapping.apply(a);
                    return innerCountDistinct(resultContainer, value);
                },
                resultContainer -> resultContainer.count);
    }

    public static <A> UniConstraintCollector<A, ?, Long> countDistinctLong(Function<A, ?> groupValueMapping) {
        return new DefaultUniConstraintCollector<>(
                CountDistinctLongResultContainer::new,
                (resultContainer, a) -> {
                    Object value = groupValueMapping.apply(a);
                    return innerCountDistinctLong(resultContainer, value);
                },
                resultContainer -> resultContainer.count);
    }

    public static <A, B> BiConstraintCollector<A, B, ?, Integer> countDistinct(
            BiFunction<A, B, ?> groupValueMapping) {
        return new DefaultBiConstraintCollector<>(
                CountDistinctResultContainer::new,
                (resultContainer, a, b) -> {
                    Object value = groupValueMapping.apply(a, b);
                    return innerCountDistinct(resultContainer, value);
                },
                resultContainer -> resultContainer.count);
    }

    public static <A, B> BiConstraintCollector<A, B, ?, Long> countDistinctLong(
            BiFunction<A, B, ?> groupValueMapping) {
        return new DefaultBiConstraintCollector<>(
                CountDistinctLongResultContainer::new,
                (resultContainer, a, b) -> {
                    Object value = groupValueMapping.apply(a, b);
                    return innerCountDistinctLong(resultContainer, value);
                },
                resultContainer -> resultContainer.count);
    }

    public static <A, B, C> TriConstraintCollector<A, B, C, ?, Integer> countDistinct(
            TriFunction<A, B, C, ?> groupValueMapping) {
        return new DefaultTriConstraintCollector<>(
                CountDistinctResultContainer::new,
                (resultContainer, a, b, c) -> {
                    Object value = groupValueMapping.apply(a, b, c);
                    return innerCountDistinct(resultContainer, value);
                },
                resultContainer -> resultContainer.count);
    }

    public static <A, B, C> TriConstraintCollector<A, B, C, ?, Long> countDistinctLong(
            TriFunction<A, B, C, ?> groupValueMapping) {
        return new DefaultTriConstraintCollector<>(
                CountDistinctLongResultContainer::new,
                (resultContainer, a, b, c) -> {
                    Object value = groupValueMapping.apply(a, b, c);
                    return innerCountDistinctLong(resultContainer, value);
                },
                resultContainer -> resultContainer.count);
    }

    public static <A, B, C, D> QuadConstraintCollector<A, B, C, D, ?, Integer> countDistinct(
            QuadFunction<A, B, C, D, ?> groupValueMapping) {
        return new DefaultQuadConstraintCollector<>(
                CountDistinctResultContainer::new,
                (resultContainer, a, b, c, d) -> {
                    Object value = groupValueMapping.apply(a, b, c, d);
                    return innerCountDistinct(resultContainer, value);
                },
                resultContainer -> resultContainer.count);
    }


    public static <A, B, C, D> QuadConstraintCollector<A, B, C, D, ?, Long> countDistinctLong(
            QuadFunction<A, B, C, D, ?> groupValueMapping) {
        return new DefaultQuadConstraintCollector<>(
                CountDistinctLongResultContainer::new,
                (resultContainer, a, b, c, d) -> {
                    Object value = groupValueMapping.apply(a, b, c, d);
                    return innerCountDistinctLong(resultContainer, value);
                },
                resultContainer -> resultContainer.count);
    }

    private static class CountDistinctResultContainer {
        int count = 0;
        Map<Object, int[]> objectCountMap = new HashMap<>();
    }

    private static Runnable innerCountDistinct(CountDistinctResultContainer resultContainer, Object value) {
        int[] objectCount = resultContainer.objectCountMap.computeIfAbsent(value, k -> new int[1]);
        if (objectCount[0] == 0L) {
            resultContainer.count++;
        }
        objectCount[0]++;
        return () -> {
            int[] objectCount2 = resultContainer.objectCountMap.get(value);
            if (objectCount2 == null) {
                throw new IllegalStateException("Impossible state: the value (" + value +
                        ") is removed more times than it was added.");
            }
            objectCount2[0]--;
            if (objectCount2[0] == 0L) {
                resultContainer.objectCountMap.remove(value);
                resultContainer.count--;
            }
        };
    }

    private static class CountDistinctLongResultContainer {
        long count = 0L;
        Map<Object, long[]> objectCountMap = new HashMap<>();
    }

    private static Runnable innerCountDistinctLong(CountDistinctLongResultContainer resultContainer, Object value) {
        long[] objectCount = resultContainer.objectCountMap.computeIfAbsent(value, k -> new long[1]);
        if (objectCount[0] == 0L) {
            resultContainer.count++;
        }
        objectCount[0]++;
        return () -> {
            long[] objectCount2 = resultContainer.objectCountMap.get(value);
            if (objectCount2 == null) {
                throw new IllegalStateException("Impossible state: the value (" + value +
                        ") is removed more times than it was added.");
            }
            objectCount2[0]--;
            if (objectCount2[0] == 0L) {
                resultContainer.objectCountMap.remove(value);
                resultContainer.count--;
            }
        };
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
                    return () -> resultContainer[0] -= value;
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A> UniConstraintCollector<A, ?, Long> sumLong(ToLongFunction<? super A> groupValueMapping) {
        return new DefaultUniConstraintCollector<>(
                () -> new long[1],
                (resultContainer, a) -> {
                    long value = groupValueMapping.applyAsLong(a);
                    resultContainer[0] += value;
                    return () -> resultContainer[0] -= value;
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, Result> UniConstraintCollector<A, ?, Result> sum(Function<? super A, Result> groupValueMapping,
            Result zero, BinaryOperator<Result> adder, BinaryOperator<Result> subtractor) {
        return new DefaultUniConstraintCollector<>(
                () -> createContainer(zero),
                (resultContainer, a) -> {
                    Result value = groupValueMapping.apply(a);
                    resultContainer[0] = adder.apply(resultContainer[0], value);
                    return () -> resultContainer[0] = subtractor.apply(resultContainer[0], value);
                },
                resultContainer -> resultContainer[0]);
    }

    private static <Result> Result[] createContainer(Result initialValue) {
        Result[] container = (Result[]) Array.newInstance(initialValue.getClass(), 1);
        container[0] = initialValue;
        return container;
    }

    public static <A> UniConstraintCollector<A, ?, BigDecimal> sumBigDecimal(
            Function<? super A, BigDecimal> groupValueMapping) {
        return sum(groupValueMapping, BigDecimal.ZERO, BigDecimal::add, BigDecimal::subtract);
    }

    public static <A> UniConstraintCollector<A, ?, BigInteger> sumBigInteger(
            Function<? super A, BigInteger> groupValueMapping) {
        return sum(groupValueMapping, BigInteger.ZERO, BigInteger::add, BigInteger::subtract);
    }

    public static <A> UniConstraintCollector<A, ?, Duration> sumDuration(
            Function<? super A, Duration> groupValueMapping) {
        return sum(groupValueMapping, Duration.ZERO, Duration::plus, Duration::minus);
    }

    public static <A> UniConstraintCollector<A, ?, Period> sumPeriod(Function<? super A, Period> groupValueMapping) {
        return sum(groupValueMapping, Period.ZERO, Period::plus, Period::minus);
    }

    public static <A, B> BiConstraintCollector<A, B, ?, Integer> sum(
            ToIntBiFunction<? super A, ? super B> groupValueMapping) {
        return new DefaultBiConstraintCollector<>(
                () -> new int[1],
                (resultContainer, a, b) -> {
                    int value = groupValueMapping.applyAsInt(a, b);
                    resultContainer[0] += value;
                    return () -> resultContainer[0] -= value;
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
                    return () -> resultContainer[0] -= value;
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B, Result> BiConstraintCollector<A, B, ?, Result> sum(
            BiFunction<? super A, ? super B, Result> groupValueMapping, Result zero, BinaryOperator<Result> adder,
            BinaryOperator<Result> subtractor) {
        return new DefaultBiConstraintCollector<>(
                () -> createContainer(zero),
                (resultContainer, a, b) -> {
                    Result value = groupValueMapping.apply(a, b);
                    resultContainer[0] = adder.apply(resultContainer[0], value);
                    return () -> resultContainer[0] = subtractor.apply(resultContainer[0], value);
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B> BiConstraintCollector<A, B, ?, BigDecimal> sumBigDecimal(
            BiFunction<? super A, ? super B, BigDecimal> groupValueMapping) {
        return sum(groupValueMapping, BigDecimal.ZERO, BigDecimal::add, BigDecimal::subtract);
    }

    public static <A, B> BiConstraintCollector<A, B, ?, BigInteger> sumBigInteger(
            BiFunction<? super A, ? super B, BigInteger> groupValueMapping) {
        return sum(groupValueMapping, BigInteger.ZERO, BigInteger::add, BigInteger::subtract);
    }

    public static <A, B> BiConstraintCollector<A, B, ?, Duration> sumDuration(
            BiFunction<? super A, ? super B, Duration> groupValueMapping) {
        return sum(groupValueMapping, Duration.ZERO, Duration::plus, Duration::minus);
    }

    public static <A, B> BiConstraintCollector<A, B, ?, Period> sumPeriod(
            BiFunction<? super A, ? super B, Period> groupValueMapping) {
        return sum(groupValueMapping, Period.ZERO, Period::plus, Period::minus);
    }

    public static <A, B, C> TriConstraintCollector<A, B, C, ?, Integer> sum(
            ToIntTriFunction<? super A, ? super B, ? super C> groupValueMapping) {
        return new DefaultTriConstraintCollector<>(
                () -> new int[1],
                (resultContainer, a, b, c) -> {
                    int value = groupValueMapping.applyAsInt(a, b, c);
                    resultContainer[0] += value;
                    return () -> resultContainer[0] -= value;
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B, C> TriConstraintCollector<A, B, C, ?, Long> sumLong(
            ToLongTriFunction<? super A, ? super B, ? super C> groupValueMapping) {
        return new DefaultTriConstraintCollector<>(
                () -> new long[1],
                (resultContainer, a, b, c) -> {
                    long value = groupValueMapping.applyAsLong(a, b, c);
                    resultContainer[0] += value;
                    return () -> resultContainer[0] -= value;
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B, C, Result> TriConstraintCollector<A, B, C, ?, Result> sum(
            TriFunction<? super A, ? super B, ? super C, Result> groupValueMapping, Result zero,
            BinaryOperator<Result> adder, BinaryOperator<Result> subtractor) {
        return new DefaultTriConstraintCollector<>(
                () -> createContainer(zero),
                (resultContainer, a, b, c) -> {
                    Result value = groupValueMapping.apply(a, b, c);
                    resultContainer[0] = adder.apply(resultContainer[0], value);
                    return () -> resultContainer[0] = subtractor.apply(resultContainer[0], value);
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B, C> TriConstraintCollector<A, B, C, ?, BigDecimal> sumBigDecimal(
            TriFunction<? super A, ? super B, ? super C, BigDecimal> groupValueMapping) {
        return sum(groupValueMapping, BigDecimal.ZERO, BigDecimal::add, BigDecimal::subtract);
    }

    public static <A, B, C> TriConstraintCollector<A, B, C, ?, BigInteger> sumBigInteger(
            TriFunction<? super A, ? super B, ? super C, BigInteger> groupValueMapping) {
        return sum(groupValueMapping, BigInteger.ZERO, BigInteger::add, BigInteger::subtract);
    }

    public static <A, B, C> TriConstraintCollector<A, B, C, ?, Duration> sumDuration(
            TriFunction<? super A, ? super B, ? super C, Duration> groupValueMapping) {
        return sum(groupValueMapping, Duration.ZERO, Duration::plus, Duration::minus);
    }

    public static <A, B, C> TriConstraintCollector<A, B, C, ?, Period> sumPeriod(
            TriFunction<? super A, ? super B, ? super C, Period> groupValueMapping) {
        return sum(groupValueMapping, Period.ZERO, Period::plus, Period::minus);
    }

    public static <A, B, C, D> QuadConstraintCollector<A, B, C, D, ?, Integer> sum(
            ToIntQuadFunction<? super A, ? super B, ? super C, ? super D> groupValueMapping) {
        return new DefaultQuadConstraintCollector<>(
                () -> new int[1],
                (resultContainer, a, b, c, d) -> {
                    int value = groupValueMapping.applyAsInt(a, b, c, d);
                    resultContainer[0] += value;
                    return () -> resultContainer[0] -= value;
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B, C, D> QuadConstraintCollector<A, B, C, D, ?, Long> sumLong(
            ToLongQuadFunction<? super A, ? super B, ? super C, ? super D> groupValueMapping) {
        return new DefaultQuadConstraintCollector<>(
                () -> new long[1],
                (resultContainer, a, b, c, d) -> {
                    long value = groupValueMapping.applyAsLong(a, b, c, d);
                    resultContainer[0] += value;
                    return () -> resultContainer[0] -= value;
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B, C, D, Result> QuadConstraintCollector<A, B, C, D, ?, Result> sum(
            QuadFunction<? super A, ? super B, ? super C, ? super D, Result> groupValueMapping, Result zero,
            BinaryOperator<Result> adder, BinaryOperator<Result> subtractor) {
        return new DefaultQuadConstraintCollector<>(
                () -> createContainer(zero),
                (resultContainer, a, b, c, d) -> {
                    Result value = groupValueMapping.apply(a, b, c, d);
                    resultContainer[0] = adder.apply(resultContainer[0], value);
                    return () -> resultContainer[0] = subtractor.apply(resultContainer[0], value);
                },
                resultContainer -> resultContainer[0]);
    }

    public static <A, B, C, D> QuadConstraintCollector<A, B, C, D, ?, BigDecimal> sumBigDecimal(
            QuadFunction<? super A, ? super B, ? super C, ? super D, BigDecimal> groupValueMapping) {
        return sum(groupValueMapping, BigDecimal.ZERO, BigDecimal::add, BigDecimal::subtract);
    }

    public static <A, B, C, D> QuadConstraintCollector<A, B, C, D, ?, BigInteger> sumBigInteger(
            QuadFunction<? super A, ? super B, ? super C, ? super D, BigInteger> groupValueMapping) {
        return sum(groupValueMapping, BigInteger.ZERO, BigInteger::add, BigInteger::subtract);
    }

    public static <A, B, C, D> QuadConstraintCollector<A, B, C, D, ?, Duration> sumDuration(
            QuadFunction<? super A, ? super B, ? super C, ? super D, Duration> groupValueMapping) {
        return sum(groupValueMapping, Duration.ZERO, Duration::plus, Duration::minus);
    }

    public static <A, B, C, D> QuadConstraintCollector<A, B, C, D, ?, Period> sumPeriod(
            QuadFunction<? super A, ? super B, ? super C, ? super D, Period> groupValueMapping) {
        return sum(groupValueMapping, Period.ZERO, Period::plus, Period::minus);
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
                    return () -> resultContainer.compute(a, (key, value) -> value == 1 ? null : value - 1);
                },
                (resultContainer) -> resultContainer.isEmpty() ? null : keySupplier.apply(resultContainer));
    }

    private ConstraintCollectors() {
    }

}

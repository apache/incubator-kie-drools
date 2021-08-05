/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.function.PentaFunction;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.ToIntQuadFunction;
import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongQuadFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
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

    private static final Runnable NOOP = () -> {
        // No operation.
    };

    // ************************************************************************
    // count
    // ************************************************************************

    /**
     * Returns a collector that counts the number of elements that are being grouped.
     * <p>
     * For example, {@code [Ann(age = 20), Beth(age = 25), Cathy(age = 30), David(age = 30), Eric(age = 20)]} with
     * {@code .groupBy(count())} returns {@code 5}.
     * <p>
     * The default result of the collector (e.g. when never called) is {@code 0}.
     *
     * @param <A> type of the matched fact
     * @return never null
     */
    public static <A> UniConstraintCollector<A, ?, Integer> count() {
        return new DefaultUniConstraintCollector<>(
                () -> new int[1],
                (resultContainer, a) -> {
                    resultContainer[0]++;
                    return () -> resultContainer[0]--;
                },
                resultContainer -> resultContainer[0]);
    }

    /**
     * As defined by {@link #count()}.
     */
    public static <A> UniConstraintCollector<A, ?, Long> countLong() {
        return new DefaultUniConstraintCollector<>(
                () -> new long[1],
                (resultContainer, a) -> {
                    resultContainer[0]++;
                    return () -> resultContainer[0]--;
                },
                resultContainer -> resultContainer[0]);
    }

    /**
     * As defined by {@link #count()}.
     */
    public static <A, B> BiConstraintCollector<A, B, ?, Integer> countBi() {
        return new DefaultBiConstraintCollector<>(
                () -> new int[1],
                (resultContainer, a, b) -> {
                    resultContainer[0]++;
                    return (() -> resultContainer[0]--);
                },
                resultContainer -> resultContainer[0]);
    }

    /**
     * As defined by {@link #count()}.
     */
    public static <A, B> BiConstraintCollector<A, B, ?, Long> countLongBi() {
        return new DefaultBiConstraintCollector<>(
                () -> new long[1],
                (resultContainer, a, b) -> {
                    resultContainer[0]++;
                    return () -> resultContainer[0]--;
                },
                resultContainer -> resultContainer[0]);
    }

    /**
     * As defined by {@link #count()}.
     */
    public static <A, B, C> TriConstraintCollector<A, B, C, ?, Integer> countTri() {
        return new DefaultTriConstraintCollector<>(
                () -> new int[1],
                (resultContainer, a, b, c) -> {
                    resultContainer[0]++;
                    return (() -> resultContainer[0]--);
                },
                resultContainer -> resultContainer[0]);
    }

    /**
     * As defined by {@link #count()}.
     */
    public static <A, B, C> TriConstraintCollector<A, B, C, ?, Long> countLongTri() {
        return new DefaultTriConstraintCollector<>(
                () -> new long[1],
                (resultContainer, a, b, c) -> {
                    resultContainer[0]++;
                    return () -> resultContainer[0]--;
                },
                resultContainer -> resultContainer[0]);
    }

    /**
     * As defined by {@link #count()}.
     */
    public static <A, B, C, D> QuadConstraintCollector<A, B, C, D, ?, Integer> countQuad() {
        return new DefaultQuadConstraintCollector<>(
                () -> new int[1],
                (resultContainer, a, b, c, d) -> {
                    resultContainer[0]++;
                    return () -> resultContainer[0]--;
                },
                resultContainer -> resultContainer[0]);
    }

    /**
     * As defined by {@link #count()}.
     */
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

    /**
     * As defined by {@link #countDistinct(Function)}, with {@link Function#identity()} as the argument.
     */
    public static <A> UniConstraintCollector<A, ?, Integer> countDistinct() {
        return countDistinct(Function.identity());
    }

    /**
     * Returns a collector that counts the number of unique elements that are being grouped.
     * Uniqueness is determined by {@link #equals(Object) equality}.
     * <p>
     * For example, {@code [Ann(age = 20), Beth(age = 25), Cathy(age = 30), David(age = 30), Eric(age = 20)]} with
     * {@code .groupBy(countDistinct(Person::getAge))} returns {@code 3}, one for age 20, 25 and 30 each.
     * <p>
     * The default result of the collector (e.g. when never called) is {@code 0}.
     *
     * @param <A> type of the matched fact
     * @return never null
     */
    public static <A> UniConstraintCollector<A, ?, Integer> countDistinct(Function<A, ?> groupValueMapping) {
        return new DefaultUniConstraintCollector<>(
                CountDistinctResultContainer::new,
                (resultContainer, a) -> {
                    Object value = groupValueMapping.apply(a);
                    return innerCountDistinct(resultContainer, value);
                },
                resultContainer -> resultContainer.count);
    }

    /**
     * As defined by {@link #countDistinct(Function)}.
     */
    public static <A> UniConstraintCollector<A, ?, Long> countDistinctLong(Function<A, ?> groupValueMapping) {
        return new DefaultUniConstraintCollector<>(
                CountDistinctLongResultContainer::new,
                (resultContainer, a) -> {
                    Object value = groupValueMapping.apply(a);
                    return innerCountDistinctLong(resultContainer, value);
                },
                resultContainer -> resultContainer.count);
    }

    /**
     * As defined by {@link #countDistinct(Function)}.
     */
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

    /**
     * As defined by {@link #countDistinct(Function)}.
     */
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

    /**
     * As defined by {@link #countDistinct(Function)}.
     */
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

    /**
     * As defined by {@link #countDistinct(Function)}.
     */
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

    /**
     * As defined by {@link #countDistinct(Function)}.
     */
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

    /**
     * As defined by {@link #countDistinct(Function)}.
     */
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

    private static class CountDistinctResultContainer {
        int count = 0;
        Map<Object, int[]> objectCountMap = new HashMap<>();
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

    private static class CountDistinctLongResultContainer {
        long count = 0L;
        Map<Object, long[]> objectCountMap = new HashMap<>();
    }

    // ************************************************************************
    // sum
    // ************************************************************************

    /**
     * Returns a collector that sums an {@code int} property of the elements that are being grouped.
     * <p>
     * For example, {@code [Ann(age = 20), Beth(age = 25), Cathy(age = 30), David(age = 30), Eric(age = 20)]} with
     * {@code .groupBy(sum(Person::getAge))} returns {@code 125}.
     * <p>
     * The default result of the collector (e.g. when never called) is {@code 0}.
     *
     * @param <A> type of the matched fact
     * @return never null
     */
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

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
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

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
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

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
    public static <A> UniConstraintCollector<A, ?, BigDecimal> sumBigDecimal(
            Function<? super A, BigDecimal> groupValueMapping) {
        return sum(groupValueMapping, BigDecimal.ZERO, BigDecimal::add, BigDecimal::subtract);
    }

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
    public static <A> UniConstraintCollector<A, ?, BigInteger> sumBigInteger(
            Function<? super A, BigInteger> groupValueMapping) {
        return sum(groupValueMapping, BigInteger.ZERO, BigInteger::add, BigInteger::subtract);
    }

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
    public static <A> UniConstraintCollector<A, ?, Duration> sumDuration(
            Function<? super A, Duration> groupValueMapping) {
        return sum(groupValueMapping, Duration.ZERO, Duration::plus, Duration::minus);
    }

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
    public static <A> UniConstraintCollector<A, ?, Period> sumPeriod(Function<? super A, Period> groupValueMapping) {
        return sum(groupValueMapping, Period.ZERO, Period::plus, Period::minus);
    }

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
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

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
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

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
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

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
    public static <A, B> BiConstraintCollector<A, B, ?, BigDecimal> sumBigDecimal(
            BiFunction<? super A, ? super B, BigDecimal> groupValueMapping) {
        return sum(groupValueMapping, BigDecimal.ZERO, BigDecimal::add, BigDecimal::subtract);
    }

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
    public static <A, B> BiConstraintCollector<A, B, ?, BigInteger> sumBigInteger(
            BiFunction<? super A, ? super B, BigInteger> groupValueMapping) {
        return sum(groupValueMapping, BigInteger.ZERO, BigInteger::add, BigInteger::subtract);
    }

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
    public static <A, B> BiConstraintCollector<A, B, ?, Duration> sumDuration(
            BiFunction<? super A, ? super B, Duration> groupValueMapping) {
        return sum(groupValueMapping, Duration.ZERO, Duration::plus, Duration::minus);
    }

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
    public static <A, B> BiConstraintCollector<A, B, ?, Period> sumPeriod(
            BiFunction<? super A, ? super B, Period> groupValueMapping) {
        return sum(groupValueMapping, Period.ZERO, Period::plus, Period::minus);
    }

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
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

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
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

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
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

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
    public static <A, B, C> TriConstraintCollector<A, B, C, ?, BigDecimal> sumBigDecimal(
            TriFunction<? super A, ? super B, ? super C, BigDecimal> groupValueMapping) {
        return sum(groupValueMapping, BigDecimal.ZERO, BigDecimal::add, BigDecimal::subtract);
    }

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
    public static <A, B, C> TriConstraintCollector<A, B, C, ?, BigInteger> sumBigInteger(
            TriFunction<? super A, ? super B, ? super C, BigInteger> groupValueMapping) {
        return sum(groupValueMapping, BigInteger.ZERO, BigInteger::add, BigInteger::subtract);
    }

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
    public static <A, B, C> TriConstraintCollector<A, B, C, ?, Duration> sumDuration(
            TriFunction<? super A, ? super B, ? super C, Duration> groupValueMapping) {
        return sum(groupValueMapping, Duration.ZERO, Duration::plus, Duration::minus);
    }

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
    public static <A, B, C> TriConstraintCollector<A, B, C, ?, Period> sumPeriod(
            TriFunction<? super A, ? super B, ? super C, Period> groupValueMapping) {
        return sum(groupValueMapping, Period.ZERO, Period::plus, Period::minus);
    }

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
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

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
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

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
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

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
    public static <A, B, C, D> QuadConstraintCollector<A, B, C, D, ?, BigDecimal> sumBigDecimal(
            QuadFunction<? super A, ? super B, ? super C, ? super D, BigDecimal> groupValueMapping) {
        return sum(groupValueMapping, BigDecimal.ZERO, BigDecimal::add, BigDecimal::subtract);
    }

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
    public static <A, B, C, D> QuadConstraintCollector<A, B, C, D, ?, BigInteger> sumBigInteger(
            QuadFunction<? super A, ? super B, ? super C, ? super D, BigInteger> groupValueMapping) {
        return sum(groupValueMapping, BigInteger.ZERO, BigInteger::add, BigInteger::subtract);
    }

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
    public static <A, B, C, D> QuadConstraintCollector<A, B, C, D, ?, Duration> sumDuration(
            QuadFunction<? super A, ? super B, ? super C, ? super D, Duration> groupValueMapping) {
        return sum(groupValueMapping, Duration.ZERO, Duration::plus, Duration::minus);
    }

    /**
     * As defined by {@link #sum(ToIntFunction)}.
     */
    public static <A, B, C, D> QuadConstraintCollector<A, B, C, D, ?, Period> sumPeriod(
            QuadFunction<? super A, ? super B, ? super C, ? super D, Period> groupValueMapping) {
        return sum(groupValueMapping, Period.ZERO, Period::plus, Period::minus);
    }

    // ************************************************************************
    // min
    // ************************************************************************

    /**
     * Returns a collector that finds a minimum value in a group of {@link Comparable} elements.
     * <p>
     * Important: The {@link Comparable}'s {@link Comparable#compareTo(Object)} must be <i>consistent with equals</i>,
     * such that <tt>e1.compareTo(e2) == 0</tt> has the same boolean value as <tt>e1.equals(e2)</tt>.
     * In other words, if two elements compare to zero, any of them can be returned by the collector.
     * It can even differ between 2 score calculations on the exact same {@link PlanningSolution} state, due to
     * incremental score calculation.
     * <p>
     * For example, {@code [Ann(age = 20), Beth(age = 25), Cathy(age = 30), David(age = 30), Eric(age = 20)]} with
     * {@code .groupBy(min())} returns either {@code Ann} or {@code Eric} arbitrarily, assuming the objects are
     * {@link Comparable} by the {@code age} field.
     * To avoid this, always end your {@link Comparator} by an identity comparison, such as
     * {@code Comparator.comparing(Person::getAge).comparing(Person::getId))}.
     * <p>
     * The default result of the collector (e.g. when never called) is {@code null}.
     *
     * @param <A> type of the matched fact
     * @return never null
     */
    public static <A extends Comparable<A>> UniConstraintCollector<A, ?, A> min() {
        return min(Comparator.<A> naturalOrder());
    }

    /**
     * Returns a collector that finds a minimum value in a group of {@link Comparable} elements.
     * <p>
     * Important: The {@link Comparable}'s {@link Comparable#compareTo(Object)} must be <i>consistent with equals</i>,
     * such that <tt>e1.compareTo(e2) == 0</tt> has the same boolean value as <tt>e1.equals(e2)</tt>.
     * In other words, if two elements compare to zero, any of them can be returned by the collector.
     * It can even differ between 2 score calculations on the exact same {@link PlanningSolution} state, due to
     * incremental score calculation.
     * <p>
     * For example, {@code [Ann(age = 20), Beth(age = 25), Cathy(age = 30), David(age = 30), Eric(age = 20)]} with
     * {@code .groupBy(min(Person::getAge))} returns {@code 20}.
     * <p>
     * The default result of the collector (e.g. when never called) is {@code null}.
     *
     * @param <A> type of the matched fact
     * @param <Mapped> type of the result
     * @param groupValueMapping never null, maps facts from the matched type to the result type
     * @return never null
     */
    public static <A, Mapped extends Comparable<Mapped>> UniConstraintCollector<A, ?, Mapped> min(
            Function<A, Mapped> groupValueMapping) {
        return min(groupValueMapping, Comparator.naturalOrder());
    }

    /**
     * As defined by {@link #min()}, only with a custom {@link Comparator}.
     */
    public static <A> UniConstraintCollector<A, ?, A> min(Comparator<A> comparator) {
        return min(Function.identity(), comparator);
    }

    /**
     * As defined by {@link #min(Function)}, only with a custom {@link Comparator}.
     */
    public static <A, Mapped> UniConstraintCollector<A, ?, Mapped> min(Function<A, Mapped> groupValueMapping,
            Comparator<Mapped> comparator) {
        return minOrMax(groupValueMapping, comparator, true);
    }

    /**
     * As defined by {@link #min(Function)}.
     */
    public static <A, B, Mapped extends Comparable<Mapped>> BiConstraintCollector<A, B, ?, Mapped> min(
            BiFunction<A, B, Mapped> groupValueMapping) {
        return min(groupValueMapping, Comparator.naturalOrder());
    }

    /**
     * As defined by {@link #min(Function)}, only with a custom {@link Comparator}.
     */
    public static <A, B, Mapped> BiConstraintCollector<A, B, ?, Mapped> min(BiFunction<A, B, Mapped> groupValueMapping,
            Comparator<Mapped> comparator) {
        return minOrMax(groupValueMapping, comparator, true);
    }

    /**
     * As defined by {@link #min(Function)}.
     */
    public static <A, B, C, Mapped extends Comparable<Mapped>> TriConstraintCollector<A, B, C, ?, Mapped> min(
            TriFunction<A, B, C, Mapped> groupValueMapping) {
        return min(groupValueMapping, Comparator.naturalOrder());
    }

    /**
     * As defined by {@link #min(Function)}, only with a custom {@link Comparator}.
     */
    public static <A, B, C, Mapped> TriConstraintCollector<A, B, C, ?, Mapped> min(
            TriFunction<A, B, C, Mapped> groupValueMapping, Comparator<Mapped> comparator) {
        return minOrMax(groupValueMapping, comparator, true);
    }

    /**
     * As defined by {@link #min(Function)}.
     */
    public static <A, B, C, D, Mapped extends Comparable<Mapped>> QuadConstraintCollector<A, B, C, D, ?, Mapped> min(
            QuadFunction<A, B, C, D, Mapped> groupValueMapping) {
        return min(groupValueMapping, Comparator.naturalOrder());
    }

    /**
     * As defined by {@link #min(Function)}, only with a custom {@link Comparator}.
     */
    public static <A, B, C, D, Mapped> QuadConstraintCollector<A, B, C, D, ?, Mapped> min(
            QuadFunction<A, B, C, D, Mapped> groupValueMapping, Comparator<Mapped> comparator) {
        return minOrMax(groupValueMapping, comparator, true);
    }

    // ************************************************************************
    // max
    // ************************************************************************

    /**
     * Returns a collector that finds a maximum value in a group of {@link Comparable} elements.
     * <p>
     * Important: The {@link Comparable}'s {@link Comparable#compareTo(Object)} must be <i>consistent with equals</i>,
     * such that <tt>e1.compareTo(e2) == 0</tt> has the same boolean value as <tt>e1.equals(e2)</tt>.
     * In other words, if two elements compare to zero, any of them can be returned by the collector.
     * It can even differ between 2 score calculations on the exact same {@link PlanningSolution} state, due to
     * incremental score calculation.
     * <p>
     * For example, {@code [Ann(age = 20), Beth(age = 25), Cathy(age = 30), David(age = 30), Eric(age = 20)]} with
     * {@code .groupBy(max())} returns either {@code Cathy} or {@code David} arbitrarily, assuming the objects are
     * {@link Comparable} by the {@code age} field.
     * To avoid this, always end your {@link Comparator} by an identity comparison, such as
     * {@code Comparator.comparing(Person::getAge).comparing(Person::getId))}.
     * <p>
     * The default result of the collector (e.g. when never called) is {@code null}.
     *
     * @param <A> type of the matched fact
     * @return never null
     */
    public static <A extends Comparable<A>> UniConstraintCollector<A, ?, A> max() {
        return max(Comparator.<A> naturalOrder());
    }

    /**
     * Returns a collector that finds a maximum value in a group of {@link Comparable} elements.
     * <p>
     * Important: The {@link Comparable}'s {@link Comparable#compareTo(Object)} must be <i>consistent with equals</i>,
     * such that <tt>e1.compareTo(e2) == 0</tt> has the same boolean value as <tt>e1.equals(e2)</tt>.
     * In other words, if two elements compare to zero, any of them can be returned by the collector.
     * It can even differ between 2 score calculations on the exact same {@link PlanningSolution} state, due to
     * incremental score calculation.
     * <p>
     * For example, {@code [Ann(age = 20), Beth(age = 25), Cathy(age = 30), David(age = 30), Eric(age = 20)]} with
     * {@code .groupBy(max(Person::getAge))} returns {@code 30}.
     * <p>
     * The default result of the collector (e.g. when never called) is {@code null}.
     *
     * @param <A> type of the matched fact
     * @param <Mapped> type of the result
     * @param groupValueMapping never null, maps facts from the matched type to the result type
     * @return never null
     */
    public static <A, Mapped extends Comparable<Mapped>> UniConstraintCollector<A, ?, Mapped> max(
            Function<A, Mapped> groupValueMapping) {
        return max(groupValueMapping, Comparator.naturalOrder());
    }

    /**
     * As defined by {@link #max()}, only with a custom {@link Comparator}.
     */
    public static <A> UniConstraintCollector<A, ?, A> max(Comparator<A> comparator) {
        return max(Function.identity(), comparator);
    }

    /**
     * As defined by {@link #max(Function)}, only with a custom {@link Comparator}.
     */
    public static <A, Mapped> UniConstraintCollector<A, ?, Mapped> max(Function<A, Mapped> groupValueMapping,
            Comparator<Mapped> comparator) {
        return minOrMax(groupValueMapping, comparator, false);
    }

    private static <A, Mapped> UniConstraintCollector<A, SortedMap<Mapped, Long>, Mapped> minOrMax(
            Function<A, Mapped> groupValueMapping, Comparator<Mapped> comparator, boolean min) {
        return new DefaultUniConstraintCollector<>(
                () -> new TreeMap<>(comparator),
                (resultContainer, a) -> {
                    Mapped mapped = groupValueMapping.apply(a);
                    return valueCountAccumulator(resultContainer, mapped);
                },
                getMinOrMaxFinisher(min));
    }

    private static <Value_> Runnable valueCountAccumulator(Map<Value_, Long> resultContainer, Value_ value) {
        resultContainer.compute(value, (key, count) -> count == null ? 1L : count + 1L);
        return () -> resultContainer.compute(value, (key, count) -> count == 1L ? null : count - 1L);
    }

    private static <Value_> Function<SortedMap<Value_, Long>, Value_> getMinOrMaxFinisher(boolean returnMinimum) {
        if (returnMinimum) {
            return resultContainer -> resultContainer.isEmpty() ? null : resultContainer.firstKey();
        } else {
            return resultContainer -> resultContainer.isEmpty() ? null : resultContainer.lastKey();
        }
    }

    /**
     * As defined by {@link #max(Function)}.
     */
    public static <A, B, Mapped extends Comparable<Mapped>> BiConstraintCollector<A, B, ?, Mapped> max(
            BiFunction<A, B, Mapped> groupValueMapping) {
        return max(groupValueMapping, Comparator.naturalOrder());
    }

    /**
     * As defined by {@link #max(Function)}, only with a custom {@link Comparator}.
     */
    public static <A, B, Mapped> BiConstraintCollector<A, B, ?, Mapped> max(BiFunction<A, B, Mapped> groupValueMapping,
            Comparator<Mapped> comparator) {
        return minOrMax(groupValueMapping, comparator, false);
    }

    private static <A, B, Mapped> BiConstraintCollector<A, B, SortedMap<Mapped, Long>, Mapped> minOrMax(
            BiFunction<A, B, Mapped> groupValueMapping, Comparator<Mapped> comparator, boolean min) {
        return new DefaultBiConstraintCollector<>(
                () -> new TreeMap<>(comparator),
                (resultContainer, a, b) -> {
                    Mapped mapped = groupValueMapping.apply(a, b);
                    return valueCountAccumulator(resultContainer, mapped);
                },
                getMinOrMaxFinisher(min));
    }

    /**
     * As defined by {@link #max(Function)}.
     */
    public static <A, B, C, Mapped extends Comparable<Mapped>> TriConstraintCollector<A, B, C, ?, Mapped> max(
            TriFunction<A, B, C, Mapped> groupValueMapping) {
        return max(groupValueMapping, Comparator.naturalOrder());
    }

    /**
     * As defined by {@link #max(Function)}, only with a custom {@link Comparator}.
     */
    public static <A, B, C, Mapped> TriConstraintCollector<A, B, C, ?, Mapped> max(
            TriFunction<A, B, C, Mapped> groupValueMapping, Comparator<Mapped> comparator) {
        return minOrMax(groupValueMapping, comparator, false);
    }

    private static <A, B, C, Mapped> TriConstraintCollector<A, B, C, SortedMap<Mapped, Long>, Mapped> minOrMax(
            TriFunction<A, B, C, Mapped> groupValueMapping, Comparator<Mapped> comparator, boolean min) {
        return new DefaultTriConstraintCollector<>(
                () -> new TreeMap<>(comparator),
                (resultContainer, a, b, c) -> {
                    Mapped mapped = groupValueMapping.apply(a, b, c);
                    return valueCountAccumulator(resultContainer, mapped);
                },
                getMinOrMaxFinisher(min));
    }

    /**
     * As defined by {@link #max(Function)}.
     */
    public static <A, B, C, D, Mapped extends Comparable<Mapped>> QuadConstraintCollector<A, B, C, D, ?, Mapped> max(
            QuadFunction<A, B, C, D, Mapped> groupValueMapping) {
        return max(groupValueMapping, Comparator.naturalOrder());
    }

    /**
     * As defined by {@link #max(Function)}, only with a custom {@link Comparator}.
     */
    public static <A, B, C, D, Mapped> QuadConstraintCollector<A, B, C, D, ?, Mapped> max(
            QuadFunction<A, B, C, D, Mapped> groupValueMapping, Comparator<Mapped> comparator) {
        return minOrMax(groupValueMapping, comparator, false);
    }

    private static <A, B, C, D, Mapped> QuadConstraintCollector<A, B, C, D, SortedMap<Mapped, Long>, Mapped> minOrMax(
            QuadFunction<A, B, C, D, Mapped> groupValueMapping, Comparator<Mapped> comparator, boolean min) {
        return new DefaultQuadConstraintCollector<>(
                () -> new TreeMap<>(comparator),
                (resultContainer, a, b, c, d) -> {
                    Mapped mapped = groupValueMapping.apply(a, b, c, d);
                    return valueCountAccumulator(resultContainer, mapped);
                },
                getMinOrMaxFinisher(min));
    }

    /**
     * @deprecated in favor of {@link #toList()}, {@link #toSet()} or {@link #toSortedSet()}
     */
    @Deprecated(/* forRemoval = true */)
    public static <A, Result extends Collection<A>> UniConstraintCollector<A, ?, Result> toCollection(
            IntFunction<Result> collectionFunction) {
        return toCollection(Function.identity(), collectionFunction);
    }

    // ************************************************************************
    // toCollection
    // ************************************************************************

    /**
     * Creates constraint collector that returns {@link Set} of the same element type as the {@link ConstraintStream}.
     * Makes no guarantees on iteration order.
     * For stable iteration order, use {@link #toSortedSet()}.
     * <p>
     * The default result of the collector (e.g. when never called) is an empty {@link Set}.
     *
     * @param <A> type of the matched fact
     * @return never null
     */
    public static <A> UniConstraintCollector<A, ?, Set<A>> toSet() {
        return toSet(Function.identity());
    }

    /**
     * Creates constraint collector that returns {@link SortedSet} of the same element type as the
     * {@link ConstraintStream}.
     * <p>
     * The default result of the collector (e.g. when never called) is an empty {@link SortedSet}.
     *
     * @param <A> type of the matched fact
     * @return never null
     */
    public static <A extends Comparable<A>> UniConstraintCollector<A, ?, SortedSet<A>> toSortedSet() {
        return toSortedSet(a -> a);
    }

    /**
     * As defined by {@link #toSortedSet()}, only with a custom {@link Comparator}.
     */
    public static <A> UniConstraintCollector<A, ?, SortedSet<A>> toSortedSet(Comparator<A> comparator) {
        return toSortedSet(a -> a, comparator);
    }

    /**
     * Creates constraint collector that returns {@link List} of the same element type as the {@link ConstraintStream}.
     * Makes no guarantees on iteration order.
     * For stable iteration order, use {@link #toSortedSet()}.
     * <p>
     * The default result of the collector (e.g. when never called) is an empty {@link List}.
     *
     * @param <A> type of the matched fact
     * @return never null
     */
    public static <A> UniConstraintCollector<A, ?, List<A>> toList() {
        return toList(Function.identity());
    }

    /**
     * @deprecated in favor of {@link #toList(Function)}, {@link #toSet(Function)} or {@link #toSortedSet(Function)}
     */
    @Deprecated(/* forRemoval = true */)
    public static <A, Mapped, Result extends Collection<Mapped>> UniConstraintCollector<A, ?, Result> toCollection(
            Function<A, Mapped> groupValueMapping, IntFunction<Result> collectionFunction) {
        return new DefaultUniConstraintCollector<>(
                (Supplier<List<Mapped>>) ArrayList::new,
                (resultContainer, a) -> {
                    Mapped mapped = groupValueMapping.apply(a);
                    resultContainer.add(mapped);
                    return () -> resultContainer.remove(mapped);
                },
                resultContainer -> toCollectionFinisher(collectionFunction, resultContainer));
    }

    private static <Mapped, Container extends List<Mapped>, Result extends Collection<Mapped>> Result toCollectionFinisher(
            IntFunction<Result> collectionFunction, Container resultContainer) {
        int size = resultContainer.size();
        Result collection = collectionFunction.apply(size);
        if (size > 0) { // Avoid exceptions in case collectionFunction gives Collections.emptyList().
            collection.addAll(resultContainer);
        }
        return collection;
    }

    /**
     * Creates constraint collector that returns {@link Set} of the same element type as the {@link ConstraintStream}.
     * Makes no guarantees on iteration order.
     * For stable iteration order, use {@link #toSortedSet()}.
     * <p>
     * The default result of the collector (e.g. when never called) is an empty {@link Set}.
     *
     * @param groupValueMapping never null, converts matched facts to elements of the resulting set
     * @param <A> type of the matched fact
     * @param <Mapped> type of elements in the resulting set
     * @return never null
     */
    public static <A, Mapped> UniConstraintCollector<A, ?, Set<Mapped>> toSet(Function<A, Mapped> groupValueMapping) {
        return new DefaultUniConstraintCollector<>(
                (Supplier<HashMap<Mapped, Long>>) HashMap::new,
                (resultContainer, a) -> {
                    Mapped mapped = groupValueMapping.apply(a);
                    return valueCountAccumulator(resultContainer, mapped);
                },
                HashMap::keySet);
    }

    /**
     * Creates constraint collector that returns {@link SortedSet} of the same element type as the
     * {@link ConstraintStream}.
     * <p>
     * The default result of the collector (e.g. when never called) is an empty {@link SortedSet}.
     *
     * @param groupValueMapping never null, converts matched facts to elements of the resulting set
     * @param <A> type of the matched fact
     * @param <Mapped> type of elements in the resulting set
     * @return never null
     */
    public static <A, Mapped extends Comparable<Mapped>> UniConstraintCollector<A, ?, SortedSet<Mapped>> toSortedSet(
            Function<A, Mapped> groupValueMapping) {
        return toSortedSet(groupValueMapping, Comparator.naturalOrder());
    }

    /**
     * As defined by {@link #toSortedSet(Function)}, only with a custom {@link Comparator}.
     */
    public static <A, Mapped> UniConstraintCollector<A, ?, SortedSet<Mapped>> toSortedSet(
            Function<A, Mapped> groupValueMapping, Comparator<Mapped> comparator) {
        return new DefaultUniConstraintCollector<A, TreeMap<Mapped, Long>, SortedSet<Mapped>>(
                () -> new TreeMap<>(comparator),
                (resultContainer, a) -> {
                    Mapped mapped = groupValueMapping.apply(a);
                    return valueCountAccumulator(resultContainer, mapped);
                },
                TreeMap::navigableKeySet);
    }

    /**
     * Creates constraint collector that returns {@link List} of the given element type.
     * Makes no guarantees on iteration order.
     * For stable iteration order, use {@link #toSortedSet(Function)}.
     * <p>
     * The default result of the collector (e.g. when never called) is an empty {@link List}.
     *
     * @param groupValueMapping never null, converts matched facts to elements of the resulting collection
     * @param <A> type of the matched fact
     * @param <Mapped> type of elements in the resulting collection
     * @return never null
     */
    public static <A, Mapped> UniConstraintCollector<A, ?, List<Mapped>> toList(Function<A, Mapped> groupValueMapping) {
        return new DefaultUniConstraintCollector<>(
                ArrayList::new,
                (resultContainer, a) -> {
                    Mapped mapped = groupValueMapping.apply(a);
                    resultContainer.add(mapped);
                    return () -> resultContainer.remove(mapped);
                },
                Function.identity());
    }

    /**
     * @deprecated in favor of {@link #toList(BiFunction)}, {@link #toSet(BiFunction)}
     *             or {@link #toSortedSet(BiFunction)}
     */
    @Deprecated(/* forRemoval = true */)
    public static <A, B, Mapped, Result extends Collection<Mapped>> BiConstraintCollector<A, B, ?, Result> toCollection(
            BiFunction<A, B, Mapped> groupValueMapping, IntFunction<Result> collectionFunction) {
        return new DefaultBiConstraintCollector<>(
                (Supplier<List<Mapped>>) ArrayList::new,
                (resultContainer, a, b) -> {
                    Mapped mapped = groupValueMapping.apply(a, b);
                    resultContainer.add(mapped);
                    return () -> resultContainer.remove(mapped);
                },
                resultContainer -> toCollectionFinisher(collectionFunction, resultContainer));
    }

    /**
     * As defined by {@link #toSet(Function)}.
     */
    public static <A, B, Mapped> BiConstraintCollector<A, B, ?, Set<Mapped>> toSet(
            BiFunction<A, B, Mapped> groupValueMapping) {
        return new DefaultBiConstraintCollector<>(
                (Supplier<HashMap<Mapped, Long>>) HashMap::new,
                (resultContainer, a, b) -> {
                    Mapped mapped = groupValueMapping.apply(a, b);
                    return valueCountAccumulator(resultContainer, mapped);
                },
                HashMap::keySet);
    }

    /**
     * As defined by {@link #toSortedSet(Function)}.
     */
    public static <A, B, Mapped extends Comparable<Mapped>> BiConstraintCollector<A, B, ?, SortedSet<Mapped>> toSortedSet(
            BiFunction<A, B, Mapped> groupValueMapping) {
        return toSortedSet(groupValueMapping, Comparator.naturalOrder());
    }

    /**
     * As defined by {@link #toSortedSet(Function, Comparator)}.
     */
    public static <A, B, Mapped> BiConstraintCollector<A, B, ?, SortedSet<Mapped>> toSortedSet(
            BiFunction<A, B, Mapped> groupValueMapping, Comparator<Mapped> comparator) {
        return new DefaultBiConstraintCollector<A, B, TreeMap<Mapped, Long>, SortedSet<Mapped>>(
                () -> new TreeMap<>(comparator),
                (resultContainer, a, b) -> {
                    Mapped mapped = groupValueMapping.apply(a, b);
                    return valueCountAccumulator(resultContainer, mapped);
                },
                TreeMap::navigableKeySet);
    }

    /**
     * As defined by {@link #toList(Function)}.
     */
    public static <A, B, Mapped> BiConstraintCollector<A, B, ?, List<Mapped>> toList(
            BiFunction<A, B, Mapped> groupValueMapping) {
        return new DefaultBiConstraintCollector<>(
                ArrayList::new,
                (resultContainer, a, b) -> {
                    Mapped mapped = groupValueMapping.apply(a, b);
                    resultContainer.add(mapped);
                    return () -> resultContainer.remove(mapped);
                },
                Function.identity());
    }

    /**
     * @deprecated in favor of {@link #toList(TriFunction)}, {@link #toSet(TriFunction)}
     *             or {@link #toSortedSet(TriFunction)}
     */
    @Deprecated(/* forRemoval = true */)
    public static <A, B, C, Mapped, Result extends Collection<Mapped>> TriConstraintCollector<A, B, C, ?, Result> toCollection(
            TriFunction<A, B, C, Mapped> groupValueMapping, IntFunction<Result> collectionFunction) {
        return new DefaultTriConstraintCollector<>(
                (Supplier<List<Mapped>>) ArrayList::new,
                (resultContainer, a, b, c) -> {
                    Mapped mapped = groupValueMapping.apply(a, b, c);
                    resultContainer.add(mapped);
                    return () -> resultContainer.remove(mapped);
                },
                resultContainer -> toCollectionFinisher(collectionFunction, resultContainer));
    }

    /**
     * As defined by {@link #toSet(Function)}.
     */
    public static <A, B, C, Mapped> TriConstraintCollector<A, B, C, ?, Set<Mapped>> toSet(
            TriFunction<A, B, C, Mapped> groupValueMapping) {
        return new DefaultTriConstraintCollector<>(
                (Supplier<HashMap<Mapped, Long>>) HashMap::new,
                (resultContainer, a, b, c) -> {
                    Mapped mapped = groupValueMapping.apply(a, b, c);
                    return valueCountAccumulator(resultContainer, mapped);
                },
                HashMap::keySet);
    }

    /**
     * As defined by {@link #toSortedSet(Function)}.
     */
    public static <A, B, C, Mapped extends Comparable<Mapped>> TriConstraintCollector<A, B, C, ?, SortedSet<Mapped>>
            toSortedSet(TriFunction<A, B, C, Mapped> groupValueMapping) {
        return toSortedSet(groupValueMapping, Comparator.naturalOrder());
    }

    /**
     * As defined by {@link #toSortedSet(Function, Comparator)}.
     */
    public static <A, B, C, Mapped> TriConstraintCollector<A, B, C, ?, SortedSet<Mapped>> toSortedSet(
            TriFunction<A, B, C, Mapped> groupValueMapping, Comparator<Mapped> comparator) {
        return new DefaultTriConstraintCollector<A, B, C, TreeMap<Mapped, Long>, SortedSet<Mapped>>(
                () -> new TreeMap<>(comparator),
                (resultContainer, a, b, c) -> {
                    Mapped mapped = groupValueMapping.apply(a, b, c);
                    return valueCountAccumulator(resultContainer, mapped);
                },
                TreeMap::navigableKeySet);
    }

    /**
     * As defined by {@link #toList(Function)}.
     */
    public static <A, B, C, Mapped> TriConstraintCollector<A, B, C, ?, List<Mapped>> toList(
            TriFunction<A, B, C, Mapped> groupValueMapping) {
        return new DefaultTriConstraintCollector<>(
                ArrayList::new,
                (resultContainer, a, b, c) -> {
                    Mapped mapped = groupValueMapping.apply(a, b, c);
                    resultContainer.add(mapped);
                    return () -> resultContainer.remove(mapped);
                },
                Function.identity());
    }

    /**
     * @deprecated in favor of {@link #toList(QuadFunction)}, {@link #toSet(QuadFunction)}
     *             or {@link #toSortedSet(QuadFunction)}
     */
    @Deprecated(/* forRemoval = true */)
    public static <A, B, C, D, Mapped, Result extends Collection<Mapped>> QuadConstraintCollector<A, B, C, D, ?, Result>
            toCollection(QuadFunction<A, B, C, D, Mapped> groupValueMapping, IntFunction<Result> collectionFunction) {
        return new DefaultQuadConstraintCollector<>(
                (Supplier<List<Mapped>>) ArrayList::new,
                (resultContainer, a, b, c, d) -> {
                    Mapped mapped = groupValueMapping.apply(a, b, c, d);
                    resultContainer.add(mapped);
                    return () -> resultContainer.remove(mapped);
                },
                resultContainer -> toCollectionFinisher(collectionFunction, resultContainer));
    }

    /**
     * As defined by {@link #toSet(Function)}.
     */
    public static <A, B, C, D, Mapped> QuadConstraintCollector<A, B, C, D, ?, Set<Mapped>> toSet(
            QuadFunction<A, B, C, D, Mapped> groupValueMapping) {
        return new DefaultQuadConstraintCollector<>(
                (Supplier<HashMap<Mapped, Long>>) HashMap::new,
                (resultContainer, a, b, c, d) -> {
                    Mapped mapped = groupValueMapping.apply(a, b, c, d);
                    return valueCountAccumulator(resultContainer, mapped);
                },
                HashMap::keySet);
    }

    /**
     * As defined by {@link #toSortedSet(Function)}.
     */
    public static <A, B, C, D, Mapped extends Comparable<Mapped>> QuadConstraintCollector<A, B, C, D, ?, SortedSet<Mapped>>
            toSortedSet(QuadFunction<A, B, C, D, Mapped> groupValueMapping) {
        return toSortedSet(groupValueMapping, Comparator.naturalOrder());
    }

    /**
     * As defined by {@link #toSortedSet(Function, Comparator)}.
     */
    public static <A, B, C, D, Mapped> QuadConstraintCollector<A, B, C, D, ?, SortedSet<Mapped>> toSortedSet(
            QuadFunction<A, B, C, D, Mapped> groupValueMapping, Comparator<Mapped> comparator) {
        return new DefaultQuadConstraintCollector<A, B, C, D, TreeMap<Mapped, Long>, SortedSet<Mapped>>(
                () -> new TreeMap<>(comparator),
                (resultContainer, a, b, c, d) -> {
                    Mapped mapped = groupValueMapping.apply(a, b, c, d);
                    return valueCountAccumulator(resultContainer, mapped);
                },
                TreeMap::navigableKeySet);
    }

    /**
     * As defined by {@link #toList(Function)}.
     */
    public static <A, B, C, D, Mapped> QuadConstraintCollector<A, B, C, D, ?, List<Mapped>> toList(
            QuadFunction<A, B, C, D, Mapped> groupValueMapping) {
        return new DefaultQuadConstraintCollector<>(
                ArrayList::new,
                (resultContainer, a, b, c, d) -> {
                    Mapped mapped = groupValueMapping.apply(a, b, c, d);
                    resultContainer.add(mapped);
                    return () -> resultContainer.remove(mapped);
                },
                Function.identity());
    }

    // ************************************************************************
    // toMap
    // ************************************************************************

    /**
     * Creates a constraint collector that returns a {@link Map} with given keys and values consisting of a
     * {@link Set} of mappings.
     * <p>
     * For example, {@code [Ann(age = 20), Beth(age = 25), Cathy(age = 30), David(age = 30), Eric(age = 20)]}
     * with {@code .groupBy(toMap(Person::getAge, Person::getName))} returns
     * {@code {20: [Ann, Eric], 25: [Beth], 30: [Cathy, David]}}.
     * <p>
     * Makes no guarantees on iteration order, neither for map entries, nor for the value sets.
     * For stable iteration order, use {@link #toSortedMap(Function, Function, IntFunction)}.
     * <p>
     * The default result of the collector (e.g. when never called) is an empty {@link Map}.
     *
     * @param keyMapper map matched fact to a map key
     * @param valueMapper map matched fact to a value
     * @param <A> type of the matched fact
     * @param <Key> type of map key
     * @param <Value> type of map value
     * @return never null
     */
    public static <A, Key, Value> UniConstraintCollector<A, ?, Map<Key, Set<Value>>> toMap(
            Function<? super A, ? extends Key> keyMapper, Function<? super A, ? extends Value> valueMapper) {
        return toMap(keyMapper, valueMapper, (IntFunction<Set<Value>>) LinkedHashSet::new);
    }

    /**
     * Creates a constraint collector that returns a {@link Map} with given keys and values consisting of a
     * {@link Set} of mappings.
     * <p>
     * For example, {@code [Ann(age = 20), Beth(age = 25), Cathy(age = 30), David(age = 30), Eric(age = 20)]}
     * with {@code .groupBy(toMap(Person::getAge, Person::getName))} returns
     * {@code {20: [Ann, Eric], 25: [Beth], 30: [Cathy, David]}}.
     * <p>
     * Iteration order of value collections depends on the {@link Set} provided.
     * Makes no guarantees on iteration order for map entries, use {@link #toSortedMap(Function, Function, IntFunction)}
     * for that.
     * <p>
     * The default result of the collector (e.g. when never called) is an empty {@link Map}.
     *
     * @param keyMapper map matched fact to a map key
     * @param valueMapper map matched fact to a value
     * @param valueSetFunction creates a set that will be used to store value mappings
     * @param <A> type of the matched fact
     * @param <Key> type of map key
     * @param <Value> type of map value
     * @param <ValueSet> type of the value set
     * @return never null
     */
    public static <A, Key, Value, ValueSet extends Set<Value>> UniConstraintCollector<A, ?, Map<Key, ValueSet>> toMap(
            Function<? super A, ? extends Key> keyMapper, Function<? super A, ? extends Value> valueMapper,
            IntFunction<ValueSet> valueSetFunction) {
        return new DefaultUniConstraintCollector<>(
                () -> new ToMultiMapResultContainer<>((IntFunction<HashMap<Key, ValueSet>>) HashMap::new, valueSetFunction),
                (resultContainer, a) -> toMapAccumulator(keyMapper, valueMapper, resultContainer, a),
                ToMultiMapResultContainer::getResult);
    }

    private static final class ToMapPerKeyCounter<Value> {

        private final Map<Value, Long> counts = new LinkedHashMap<>(0);

        public long add(Value value) {
            return counts.compute(value, (k, currentCount) -> {
                if (currentCount == null) {
                    return 1L;
                } else {
                    return currentCount + 1;
                }
            });
        }

        public long remove(Value value) {
            Long newCount = counts.compute(value, (k, currentCount) -> {
                if (currentCount > 1L) {
                    return currentCount - 1;
                } else {
                    return null;
                }
            });
            return newCount == null ? 0L : newCount;
        }

        public Value merge(BinaryOperator<Value> mergeFunction) {
            // Rebuilding the value from the collection is not incremental.
            // The impact is negligible, assuming there are not too many values for the same key.
            return counts.keySet()
                    .stream()
                    .reduce(mergeFunction)
                    .orElseThrow(() -> new IllegalStateException("Programming error: Should have had at least one value."));
        }

        public boolean isEmpty() {
            return counts.isEmpty();
        }

    }

    private interface ToMapResultContainer<Key, Value, ResultValue, Result_ extends Map<Key, ResultValue>> {

        void add(Key key, Value value);

        void remove(Key key, Value value);

        Result_ getResult();

    }

    private static final class ToSimpleMapResultContainer<Key, Value, Result_ extends Map<Key, Value>>
            implements ToMapResultContainer<Key, Value, Value, Result_> {

        private final BinaryOperator<Value> mergeFunction;
        private final Result_ result;
        private final Map<Key, ToMapPerKeyCounter<Value>> valueCounts = new HashMap<>(0);

        public ToSimpleMapResultContainer(Supplier<Result_> resultSupplier, BinaryOperator<Value> mergeFunction) {
            this.mergeFunction = Objects.requireNonNull(mergeFunction);
            this.result = Objects.requireNonNull(resultSupplier).get();
        }

        public ToSimpleMapResultContainer(IntFunction<Result_> resultSupplier, BinaryOperator<Value> mergeFunction) {
            this.mergeFunction = Objects.requireNonNull(mergeFunction);
            this.result = Objects.requireNonNull(resultSupplier).apply(0);
        }

        public void add(Key key, Value value) {
            ToMapPerKeyCounter<Value> counter = valueCounts.computeIfAbsent(key, k -> new ToMapPerKeyCounter<>());
            long newCount = counter.add(value);
            if (newCount == 1L) {
                result.put(key, value);
            } else {
                result.put(key, counter.merge(mergeFunction));
            }
        }

        public void remove(Key key, Value value) {
            ToMapPerKeyCounter<Value> counter = valueCounts.get(key);
            long newCount = counter.remove(value);
            if (newCount == 0L) {
                result.remove(key);
            } else {
                result.put(key, counter.merge(mergeFunction));
            }
            if (counter.isEmpty()) {
                valueCounts.remove(key);
            }
        }

        @Override
        public Result_ getResult() {
            return result;
        }

    }

    private static final class ToMultiMapResultContainer<Key, Value, Set_ extends Set<Value>, Result_ extends Map<Key, Set_>>
            implements ToMapResultContainer<Key, Value, Set_, Result_> {

        private final Supplier<Set_> setSupplier;
        private final Result_ result;
        private final Map<Key, ToMapPerKeyCounter<Value>> valueCounts = new HashMap<>(0);

        public ToMultiMapResultContainer(Supplier<Result_> resultSupplier, IntFunction<Set_> setFunction) {
            IntFunction<Set_> nonNullSetFunction = Objects.requireNonNull(setFunction);
            this.setSupplier = () -> nonNullSetFunction.apply(0);
            this.result = Objects.requireNonNull(resultSupplier).get();
        }

        public ToMultiMapResultContainer(IntFunction<Result_> resultFunction, IntFunction<Set_> setFunction) {
            IntFunction<Set_> nonNullSetFunction = Objects.requireNonNull(setFunction);
            this.setSupplier = () -> nonNullSetFunction.apply(0);
            this.result = Objects.requireNonNull(resultFunction).apply(0);
        }

        public void add(Key key, Value value) {
            ToMapPerKeyCounter<Value> counter = valueCounts.computeIfAbsent(key, k -> new ToMapPerKeyCounter<>());
            counter.add(value);
            result.computeIfAbsent(key, k -> setSupplier.get())
                    .add(value);
        }

        public void remove(Key key, Value value) {
            ToMapPerKeyCounter<Value> counter = valueCounts.get(key);
            long newCount = counter.remove(value);
            if (newCount == 0) {
                result.get(key).remove(value);
            }
            if (counter.isEmpty()) {
                valueCounts.remove(key);
                result.remove(key);
            }
        }

        @Override
        public Result_ getResult() {
            return result;
        }

    }

    private static <A, Key, Value> Runnable toMapAccumulator(Function<? super A, ? extends Key> keyMapper,
            Function<? super A, ? extends Value> valueMapper, ToMapResultContainer<Key, Value, ?, ?> resultContainer,
            A a) {
        Key key = keyMapper.apply(a);
        Value value = valueMapper.apply(a);
        return toMapInnerAccumulator(key, value, resultContainer);
    }

    private static <Key, Value> Runnable toMapInnerAccumulator(Key key, Value value,
            ToMapResultContainer<Key, Value, ?, ?> resultContainer) {
        resultContainer.add(key, value);
        return () -> resultContainer.remove(key, value);
    }

    /**
     * Creates a constraint collector that returns a {@link Map}.
     * <p>
     * For example, {@code [Ann(age = 20), Beth(age = 25), Cathy(age = 30), David(age = 30), Eric(age = 20)]}
     * with {@code .groupBy(toMap(Person::getAge, Person::getName, (name1, name2) -> name1 + " and " + name2)} returns
     * {@code {20: "Ann and Eric", 25: "Beth", 30: "Cathy and David"}}.
     * <p>
     * Makes no guarantees on iteration order for map entries.
     * For stable iteration order, use {@link #toSortedMap(Function, Function, BinaryOperator)}.
     * <p>
     * The default result of the collector (e.g. when never called) is an empty {@link Map}.
     *
     * @param keyMapper map matched fact to a map key
     * @param valueMapper map matched fact to a value
     * @param mergeFunction takes two values and merges them to one
     * @param <A> type of the matched fact
     * @param <Key> type of map key
     * @param <Value> type of map value
     * @return never null
     */
    public static <A, Key, Value> UniConstraintCollector<A, ?, Map<Key, Value>> toMap(
            Function<? super A, ? extends Key> keyMapper, Function<? super A, ? extends Value> valueMapper,
            BinaryOperator<Value> mergeFunction) {
        return new DefaultUniConstraintCollector<>(
                () -> new ToSimpleMapResultContainer<>((IntFunction<HashMap<Key, Value>>) HashMap::new, mergeFunction),
                (resultContainer, a) -> toMapAccumulator(keyMapper, valueMapper, resultContainer, a),
                ToMapResultContainer::getResult);
    }

    /**
     * Creates a constraint collector that returns a {@link SortedMap} with given keys and values consisting of a
     * {@link Set} of mappings.
     * <p>
     * For example, {@code [Ann(age = 20), Beth(age = 25), Cathy(age = 30), David(age = 30), Eric(age = 20)]}
     * with {@code .groupBy(toMap(Person::getAge, Person::getName))} returns
     * {@code {20: [Ann, Eric], 25: [Beth], 30: [Cathy, David]}}.
     * <p>
     * Makes no guarantees on iteration order for the value sets, use
     * {@link #toSortedMap(Function, Function, IntFunction)} for that.
     * <p>
     * The default result of the collector (e.g. when never called) is an empty {@link SortedMap}.
     *
     * @param keyMapper map matched fact to a map key
     * @param valueMapper map matched fact to a value
     * @param <A> type of the matched fact
     * @param <Key> type of map key
     * @param <Value> type of map value
     * @return never null
     */
    public static <A, Key extends Comparable<Key>, Value> UniConstraintCollector<A, ?, SortedMap<Key, Set<Value>>> toSortedMap(
            Function<? super A, ? extends Key> keyMapper, Function<? super A, ? extends Value> valueMapper) {
        return toSortedMap(keyMapper, valueMapper, (IntFunction<Set<Value>>) LinkedHashSet::new);
    }

    /**
     * Creates a constraint collector that returns a {@link SortedMap} with given keys and values consisting of a
     * {@link Set} of mappings.
     * <p>
     * For example, {@code [Ann(age = 20), Beth(age = 25), Cathy(age = 30), David(age = 30), Eric(age = 20)]}
     * with {@code .groupBy(toMap(Person::getAge, Person::getName))} returns
     * {@code {20: [Ann, Eric], 25: [Beth], 30: [Cathy, David]}}.
     * <p>
     * Iteration order of value collections depends on the {@link Set} provided.
     * <p>
     * The default result of the collector (e.g. when never called) is an empty {@link SortedMap}.
     *
     * @param keyMapper map matched fact to a map key
     * @param valueMapper map matched fact to a value
     * @param valueSetFunction creates a set that will be used to store value mappings
     * @param <A> type of the matched fact
     * @param <Key> type of map key
     * @param <Value> type of map value
     * @param <ValueSet> type of the value set
     * @return never null
     */
    public static <A, Key extends Comparable<Key>, Value, ValueSet extends Set<Value>>
            UniConstraintCollector<A, ?, SortedMap<Key, ValueSet>> toSortedMap(
                    Function<? super A, ? extends Key> keyMapper,
                    Function<? super A, ? extends Value> valueMapper, IntFunction<ValueSet> valueSetFunction) {
        return new DefaultUniConstraintCollector<>(
                () -> new ToMultiMapResultContainer<>((Supplier<TreeMap<Key, ValueSet>>) TreeMap::new, valueSetFunction),
                (resultContainer, a) -> toMapAccumulator(keyMapper, valueMapper, resultContainer, a),
                ToMultiMapResultContainer::getResult);
    }

    /**
     * Creates a constraint collector that returns a {@link SortedMap}.
     * <p>
     * For example, {@code [Ann(age = 20), Beth(age = 25), Cathy(age = 30), David(age = 30), Eric(age = 20)]}
     * with {@code .groupBy(toMap(Person::getAge, Person::getName, (name1, name2) -> name1 + " and " + name2)} returns
     * {@code {20: "Ann and Eric", 25: "Beth", 30: "Cathy and David"}}.
     * <p>
     * The default result of the collector (e.g. when never called) is an empty {@link SortedMap}.
     *
     * @param keyMapper map matched fact to a map key
     * @param valueMapper map matched fact to a value
     * @param mergeFunction takes two values and merges them to one
     * @param <A> type of the matched fact
     * @param <Key> type of map key
     * @param <Value> type of map value
     * @return never null
     */
    public static <A, Key extends Comparable<Key>, Value> UniConstraintCollector<A, ?, SortedMap<Key, Value>> toSortedMap(
            Function<? super A, ? extends Key> keyMapper, Function<? super A, ? extends Value> valueMapper,
            BinaryOperator<Value> mergeFunction) {
        return new DefaultUniConstraintCollector<>(
                () -> new ToSimpleMapResultContainer<>((Supplier<TreeMap<Key, Value>>) TreeMap::new, mergeFunction),
                (resultContainer, a) -> toMapAccumulator(keyMapper, valueMapper, resultContainer, a),
                ToSimpleMapResultContainer::getResult);
    }

    /**
     * As defined by {@link #toMap(Function, Function)}.
     */
    public static <A, B, Key, Value> BiConstraintCollector<A, B, ?, Map<Key, Set<Value>>> toMap(
            BiFunction<? super A, ? super B, ? extends Key> keyMapper,
            BiFunction<? super A, ? super B, ? extends Value> valueMapper) {
        return toMap(keyMapper, valueMapper, (IntFunction<Set<Value>>) LinkedHashSet::new);
    }

    /**
     * As defined by {@link #toMap(Function, Function, IntFunction)}.
     */
    public static <A, B, Key, Value, ValueSet extends Set<Value>> BiConstraintCollector<A, B, ?, Map<Key, ValueSet>> toMap(
            BiFunction<? super A, ? super B, ? extends Key> keyMapper,
            BiFunction<? super A, ? super B, ? extends Value> valueMapper, IntFunction<ValueSet> valueSetFunction) {
        return new DefaultBiConstraintCollector<>(
                () -> new ToMultiMapResultContainer<>((IntFunction<HashMap<Key, ValueSet>>) HashMap::new, valueSetFunction),
                (resultContainer, a, b) -> toMapAccumulator(keyMapper, valueMapper, resultContainer, a, b),
                ToMultiMapResultContainer::getResult);
    }

    private static <A, B, Key, Value> Runnable toMapAccumulator(
            BiFunction<? super A, ? super B, ? extends Key> keyMapper,
            BiFunction<? super A, ? super B, ? extends Value> valueMapper,
            ToMapResultContainer<Key, Value, ?, ?> resultContainer, A a, B b) {
        Key key = keyMapper.apply(a, b);
        Value value = valueMapper.apply(a, b);
        return toMapInnerAccumulator(key, value, resultContainer);
    }

    /**
     * As defined by {@link #toMap(Function, Function, BinaryOperator)}.
     */
    public static <A, B, Key, Value> BiConstraintCollector<A, B, ?, Map<Key, Value>> toMap(
            BiFunction<? super A, ? super B, ? extends Key> keyMapper,
            BiFunction<? super A, ? super B, ? extends Value> valueMapper, BinaryOperator<Value> mergeFunction) {
        return new DefaultBiConstraintCollector<>(
                () -> new ToSimpleMapResultContainer<>((IntFunction<HashMap<Key, Value>>) HashMap::new, mergeFunction),
                (resultContainer, a, b) -> toMapAccumulator(keyMapper, valueMapper, resultContainer, a, b),
                ToSimpleMapResultContainer::getResult);
    }

    /**
     * As defined by {@link #toSortedMap(Function, Function)}.
     */
    public static <A, B, Key extends Comparable<Key>, Value> BiConstraintCollector<A, B, ?, SortedMap<Key, Set<Value>>>
            toSortedMap(BiFunction<? super A, ? super B, ? extends Key> keyMapper,
                    BiFunction<? super A, ? super B, ? extends Value> valueMapper) {
        return toSortedMap(keyMapper, valueMapper, (IntFunction<Set<Value>>) LinkedHashSet::new);
    }

    /**
     * As defined by {@link #toSortedMap(Function, Function, IntFunction)}.
     */
    public static <A, B, Key extends Comparable<Key>, Value, ValueSet extends Set<Value>>
            BiConstraintCollector<A, B, ?, SortedMap<Key, ValueSet>> toSortedMap(
                    BiFunction<? super A, ? super B, ? extends Key> keyMapper,
                    BiFunction<? super A, ? super B, ? extends Value> valueMapper, IntFunction<ValueSet> valueSetFunction) {
        return new DefaultBiConstraintCollector<>(
                () -> new ToMultiMapResultContainer<>((Supplier<TreeMap<Key, ValueSet>>) TreeMap::new, valueSetFunction),
                (resultContainer, a, b) -> toMapAccumulator(keyMapper, valueMapper, resultContainer, a, b),
                ToMultiMapResultContainer::getResult);
    }

    /**
     * As defined by {@link #toSortedMap(Function, Function, BinaryOperator)}.
     */
    public static <A, B, Key extends Comparable<Key>, Value> BiConstraintCollector<A, B, ?, SortedMap<Key, Value>> toSortedMap(
            BiFunction<? super A, ? super B, ? extends Key> keyMapper,
            BiFunction<? super A, ? super B, ? extends Value> valueMapper, BinaryOperator<Value> mergeFunction) {
        return new DefaultBiConstraintCollector<>(
                () -> new ToSimpleMapResultContainer<>((Supplier<TreeMap<Key, Value>>) TreeMap::new, mergeFunction),
                (resultContainer, a, b) -> toMapAccumulator(keyMapper, valueMapper, resultContainer, a, b),
                ToSimpleMapResultContainer::getResult);
    }

    /**
     * As defined by {@link #toMap(Function, Function)}.
     */
    public static <A, B, C, Key, Value> TriConstraintCollector<A, B, C, ?, Map<Key, Set<Value>>> toMap(
            TriFunction<? super A, ? super B, ? super C, ? extends Key> keyMapper,
            TriFunction<? super A, ? super B, ? super C, ? extends Value> valueMapper) {
        return toMap(keyMapper, valueMapper, (IntFunction<Set<Value>>) LinkedHashSet::new);
    }

    /**
     * As defined by {@link #toMap(Function, Function, IntFunction)}.
     */
    public static <A, B, C, Key, Value, ValueSet extends Set<Value>> TriConstraintCollector<A, B, C, ?, Map<Key, ValueSet>>
            toMap(TriFunction<? super A, ? super B, ? super C, ? extends Key> keyMapper,
                    TriFunction<? super A, ? super B, ? super C, ? extends Value> valueMapper,
                    IntFunction<ValueSet> valueSetFunction) {
        return new DefaultTriConstraintCollector<>(
                () -> new ToMultiMapResultContainer<>((IntFunction<HashMap<Key, ValueSet>>) HashMap::new, valueSetFunction),
                (resultContainer, a, b, c) -> toMapAccumulator(keyMapper, valueMapper, resultContainer, a, b, c),
                ToMultiMapResultContainer::getResult);
    }

    private static <A, B, C, Key, Value> Runnable toMapAccumulator(
            TriFunction<? super A, ? super B, ? super C, ? extends Key> keyMapper,
            TriFunction<? super A, ? super B, ? super C, ? extends Value> valueMapper,
            ToMapResultContainer<Key, Value, ?, ?> resultContainer, A a, B b, C c) {
        Key key = keyMapper.apply(a, b, c);
        Value value = valueMapper.apply(a, b, c);
        return toMapInnerAccumulator(key, value, resultContainer);
    }

    /**
     * As defined by {@link #toMap(Function, Function, BinaryOperator)}.
     */
    public static <A, B, C, Key, Value> TriConstraintCollector<A, B, C, ?, Map<Key, Value>> toMap(
            TriFunction<? super A, ? super B, ? super C, ? extends Key> keyMapper,
            TriFunction<? super A, ? super B, ? super C, ? extends Value> valueMapper,
            BinaryOperator<Value> mergeFunction) {
        return new DefaultTriConstraintCollector<>(
                () -> new ToSimpleMapResultContainer<>((IntFunction<HashMap<Key, Value>>) HashMap::new, mergeFunction),
                (resultContainer, a, b, c) -> toMapAccumulator(keyMapper, valueMapper, resultContainer, a, b, c),
                ToSimpleMapResultContainer::getResult);
    }

    /**
     * As defined by {@link #toSortedMap(Function, Function)}.
     */
    public static <A, B, C, Key extends Comparable<Key>, Value> TriConstraintCollector<A, B, C, ?, SortedMap<Key, Set<Value>>>
            toSortedMap(TriFunction<? super A, ? super B, ? super C, ? extends Key> keyMapper,
                    TriFunction<? super A, ? super B, ? super C, ? extends Value> valueMapper) {
        return toSortedMap(keyMapper, valueMapper, (IntFunction<Set<Value>>) LinkedHashSet::new);
    }

    /**
     * As defined by {@link #toSortedMap(Function, Function, IntFunction)}.
     */
    public static <A, B, C, Key extends Comparable<Key>, Value, ValueSet extends Set<Value>>
            TriConstraintCollector<A, B, C, ?, SortedMap<Key, ValueSet>> toSortedMap(
                    TriFunction<? super A, ? super B, ? super C, ? extends Key> keyMapper,
                    TriFunction<? super A, ? super B, ? super C, ? extends Value> valueMapper,
                    IntFunction<ValueSet> valueSetFunction) {
        return new DefaultTriConstraintCollector<>(
                () -> new ToMultiMapResultContainer<>((Supplier<TreeMap<Key, ValueSet>>) TreeMap::new, valueSetFunction),
                (resultContainer, a, b, c) -> toMapAccumulator(keyMapper, valueMapper, resultContainer, a, b, c),
                ToMultiMapResultContainer::getResult);
    }

    /**
     * As defined by {@link #toSortedMap(Function, Function, BinaryOperator)}.
     */
    public static <A, B, C, Key extends Comparable<Key>, Value> TriConstraintCollector<A, B, C, ?, SortedMap<Key, Value>>
            toSortedMap(TriFunction<? super A, ? super B, ? super C, ? extends Key> keyMapper,
                    TriFunction<? super A, ? super B, ? super C, ? extends Value> valueMapper,
                    BinaryOperator<Value> mergeFunction) {
        return new DefaultTriConstraintCollector<>(
                () -> new ToSimpleMapResultContainer<>((Supplier<TreeMap<Key, Value>>) TreeMap::new, mergeFunction),
                (resultContainer, a, b, c) -> toMapAccumulator(keyMapper, valueMapper, resultContainer, a, b, c),
                ToSimpleMapResultContainer::getResult);
    }

    /**
     * As defined by {@link #toMap(Function, Function)}.
     */
    public static <A, B, C, D, Key, Value> QuadConstraintCollector<A, B, C, D, ?, Map<Key, Set<Value>>> toMap(
            QuadFunction<? super A, ? super B, ? super C, ? super D, ? extends Key> keyMapper,
            QuadFunction<? super A, ? super B, ? super C, ? super D, ? extends Value> valueMapper) {
        return toMap(keyMapper, valueMapper, (IntFunction<Set<Value>>) LinkedHashSet::new);
    }

    /**
     * As defined by {@link #toMap(Function, Function, IntFunction)}.
     */
    public static <A, B, C, D, Key, Value, ValueSet extends Set<Value>>
            QuadConstraintCollector<A, B, C, D, ?, Map<Key, ValueSet>> toMap(
                    QuadFunction<? super A, ? super B, ? super C, ? super D, ? extends Key> keyMapper,
                    QuadFunction<? super A, ? super B, ? super C, ? super D, ? extends Value> valueMapper,
                    IntFunction<ValueSet> valueSetFunction) {
        return new DefaultQuadConstraintCollector<>(
                () -> new ToMultiMapResultContainer<>((IntFunction<HashMap<Key, ValueSet>>) HashMap::new, valueSetFunction),
                (resultContainer, a, b, c, d) -> toMapAccumulator(keyMapper, valueMapper, resultContainer, a, b, c, d),
                ToMultiMapResultContainer::getResult);
    }

    private static <A, B, C, D, Key, Value> Runnable toMapAccumulator(
            QuadFunction<? super A, ? super B, ? super C, ? super D, ? extends Key> keyMapper,
            QuadFunction<? super A, ? super B, ? super C, ? super D, ? extends Value> valueMapper,
            ToMapResultContainer<Key, Value, ?, ?> resultContainer, A a, B b, C c, D d) {
        Key key = keyMapper.apply(a, b, c, d);
        Value value = valueMapper.apply(a, b, c, d);
        return toMapInnerAccumulator(key, value, resultContainer);
    }

    /**
     * As defined by {@link #toMap(Function, Function, BinaryOperator)}.
     */
    public static <A, B, C, D, Key, Value> QuadConstraintCollector<A, B, C, D, ?, Map<Key, Value>> toMap(
            QuadFunction<? super A, ? super B, ? super C, ? super D, ? extends Key> keyMapper,
            QuadFunction<? super A, ? super B, ? super C, ? super D, ? extends Value> valueMapper,
            BinaryOperator<Value> mergeFunction) {
        return new DefaultQuadConstraintCollector<>(
                () -> new ToSimpleMapResultContainer<>((IntFunction<HashMap<Key, Value>>) HashMap::new, mergeFunction),
                (resultContainer, a, b, c, d) -> toMapAccumulator(keyMapper, valueMapper, resultContainer, a, b, c, d),
                ToSimpleMapResultContainer::getResult);
    }

    /**
     * As defined by {@link #toSortedMap(Function, Function)}.
     */
    public static <A, B, C, D, Key extends Comparable<Key>, Value>
            QuadConstraintCollector<A, B, C, D, ?, SortedMap<Key, Set<Value>>> toSortedMap(
                    QuadFunction<? super A, ? super B, ? super C, ? super D, ? extends Key> keyMapper,
                    QuadFunction<? super A, ? super B, ? super C, ? super D, ? extends Value> valueMapper) {
        return toSortedMap(keyMapper, valueMapper, (IntFunction<Set<Value>>) LinkedHashSet::new);
    }

    /**
     * As defined by {@link #toSortedMap(Function, Function, IntFunction)}.
     */
    public static <A, B, C, D, Key extends Comparable<Key>, Value, ValueSet extends Set<Value>>
            QuadConstraintCollector<A, B, C, D, ?, SortedMap<Key, ValueSet>> toSortedMap(
                    QuadFunction<? super A, ? super B, ? super C, ? super D, ? extends Key> keyMapper,
                    QuadFunction<? super A, ? super B, ? super C, ? super D, ? extends Value> valueMapper,
                    IntFunction<ValueSet> valueSetFunction) {
        return new DefaultQuadConstraintCollector<>(
                () -> new ToMultiMapResultContainer<>((Supplier<TreeMap<Key, ValueSet>>) TreeMap::new, valueSetFunction),
                (resultContainer, a, b, c, d) -> toMapAccumulator(keyMapper, valueMapper, resultContainer, a, b, c, d),
                ToMultiMapResultContainer::getResult);
    }

    /**
     * As defined by {@link #toSortedMap(Function, Function, BinaryOperator)}.
     */
    public static <A, B, C, D, Key extends Comparable<Key>, Value> QuadConstraintCollector<A, B, C, D, ?, SortedMap<Key, Value>>
            toSortedMap(QuadFunction<? super A, ? super B, ? super C, ? super D, ? extends Key> keyMapper,
                    QuadFunction<? super A, ? super B, ? super C, ? super D, ? extends Value> valueMapper,
                    BinaryOperator<Value> mergeFunction) {
        return new DefaultQuadConstraintCollector<>(
                () -> new ToSimpleMapResultContainer<>((Supplier<TreeMap<Key, Value>>) TreeMap::new, mergeFunction),
                (resultContainer, a, b, c, d) -> toMapAccumulator(keyMapper, valueMapper, resultContainer, a, b, c, d),
                ToSimpleMapResultContainer::getResult);
    }

    // ************************************************************************
    // conditional collectors
    // ************************************************************************

    /**
     * Returns a collector that delegates to the underlying collector
     * if and only if the input tuple meets the given condition.
     *
     * <p>
     * The result of the collector is always the underlying collector's result.
     * Therefore the default result of the collector (e.g. when never called) is the default result of the underlying collector.
     *
     * @param condition never null, condition to meet in order to delegate to the underlying collector
     * @param delegate never null, the underlying collector to delegate to
     * @param <A> generic type of the tuple variable
     * @param <ResultContainer_> generic type of the result container
     * @param <Result_> generic type of the collector's return value
     * @return never null
     */
    public static <A, ResultContainer_, Result_> UniConstraintCollector<A, ResultContainer_, Result_> conditionally(
            Predicate<A> condition, UniConstraintCollector<A, ResultContainer_, Result_> delegate) {
        BiFunction<ResultContainer_, A, Runnable> accumulator = delegate.accumulator();
        return new DefaultUniConstraintCollector<>(
                delegate.supplier(),
                (resultContainer, a) -> {
                    if (condition.test(a)) {
                        return accumulator.apply(resultContainer, a);
                    } else {
                        return NOOP;
                    }
                },
                delegate.finisher());
    }

    /**
     * As defined by {@link #conditionally(Predicate, UniConstraintCollector)}.
     */
    public static <A, B, ResultContainer_, Result_> BiConstraintCollector<A, B, ResultContainer_, Result_>
            conditionally(BiPredicate<A, B> condition,
                    BiConstraintCollector<A, B, ResultContainer_, Result_> delegate) {
        TriFunction<ResultContainer_, A, B, Runnable> accumulator = delegate.accumulator();
        return new DefaultBiConstraintCollector<>(
                delegate.supplier(),
                (resultContainer, a, b) -> {
                    if (condition.test(a, b)) {
                        return accumulator.apply(resultContainer, a, b);
                    } else {
                        return NOOP;
                    }
                },
                delegate.finisher());
    }

    /**
     * As defined by {@link #conditionally(Predicate, UniConstraintCollector)}.
     */
    public static <A, B, C, ResultContainer_, Result_> TriConstraintCollector<A, B, C, ResultContainer_, Result_>
            conditionally(TriPredicate<A, B, C> condition,
                    TriConstraintCollector<A, B, C, ResultContainer_, Result_> delegate) {
        QuadFunction<ResultContainer_, A, B, C, Runnable> accumulator = delegate.accumulator();
        return new DefaultTriConstraintCollector<>(
                delegate.supplier(),
                (resultContainer, a, b, c) -> {
                    if (condition.test(a, b, c)) {
                        return accumulator.apply(resultContainer, a, b, c);
                    } else {
                        return NOOP;
                    }
                },
                delegate.finisher());
    }

    /**
     * As defined by {@link #conditionally(Predicate, UniConstraintCollector)}.
     */
    public static <A, B, C, D, ResultContainer_, Result_> QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_>
            conditionally(QuadPredicate<A, B, C, D> condition,
                    QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> delegate) {
        PentaFunction<ResultContainer_, A, B, C, D, Runnable> accumulator = delegate.accumulator();
        return new DefaultQuadConstraintCollector<>(
                delegate.supplier(),
                (resultContainer, a, b, c, d) -> {
                    if (condition.test(a, b, c, d)) {
                        return accumulator.apply(resultContainer, a, b, c, d);
                    } else {
                        return NOOP;
                    }
                },
                delegate.finisher());
    }

    private ConstraintCollectors() {
    }
}

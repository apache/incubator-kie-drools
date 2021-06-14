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

package org.optaplanner.examples.common.experimental;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.bi.DefaultBiConstraintCollector;
import org.optaplanner.core.impl.score.stream.quad.DefaultQuadConstraintCollector;
import org.optaplanner.core.impl.score.stream.tri.DefaultTriConstraintCollector;
import org.optaplanner.core.impl.score.stream.uni.DefaultUniConstraintCollector;
import org.optaplanner.examples.common.experimental.api.ConsecutiveInfo;
import org.optaplanner.examples.common.experimental.impl.ConsecutiveIntervalDataImpl;
import org.optaplanner.examples.common.experimental.impl.ConsecutiveSetTree;
import org.optaplanner.examples.common.experimental.impl.IntervalTree;

/**
 * A collection of experimental constraint collectors
 * subject to change in future versions.
 */
public class ExperimentalConstraintCollectors {
    /**
     * Creates a constraint collector that returns {@link ConsecutiveInfo} about the first
     * fact.
     *
     * For instance ${@code [Shift slot=1] [Shift slot=2] [Shift slot=4] [Shift slot=6]}
     * returns the following infomation
     *
     * ${@code
     * Consecutive Lengths: 2 1 1
     * Break Lengths: 1 2
     * Consecutive Items [[Shift slot=1] [Shift slot=2]], [[Shift slot=4]] [[Shift slot=6]]
     * }
     *
     * @param indexMap Maps the fact to its position in the sequence
     * @param <A> type of the first mapped fact
     * @return never null
     */
    public static <A> UniConstraintCollector<A, ConsecutiveSetTree<A, Integer, Integer>, ConsecutiveInfo<A, Integer>>
            consecutive(ToIntFunction<A> indexMap) {
        return new DefaultUniConstraintCollector<>(
                () -> new ConsecutiveSetTree<>(
                        indexMap::applyAsInt,
                        (Integer a, Integer b) -> b - a,
                        Integer::sum,
                        1, 0),
                (acc, a) -> {
                    acc.add(a);
                    return () -> acc.remove(a);
                },
                ConsecutiveSetTree::getConsecutiveData);
    }

    /**
     * As defined by {@link #consecutive(ToIntFunction)} (Function, Function, BinaryOperator)}.
     *
     * @param resultMap Maps both facts to an item in the sequence
     * @param indexMap Maps the item to its position in the sequence
     * @param <A> type of the first mapped fact
     * @param <B> type of the second mapped fact
     * @param <Result> type of item in the sequence
     * @return never null
     */
    public static <A, B, Result>
            BiConstraintCollector<A, B, ConsecutiveSetTree<Result, Integer, Integer>, ConsecutiveInfo<Result, Integer>>
            consecutive(BiFunction<A, B, Result> resultMap, ToIntFunction<Result> indexMap) {
        return new DefaultBiConstraintCollector<>(() -> new ConsecutiveSetTree<>(
                indexMap::applyAsInt,
                (Integer a, Integer b) -> b - a,
                Integer::sum, 1, 0),
                (acc, a, b) -> {
                    Result result = resultMap.apply(a, b);
                    acc.add(result);
                    return () -> acc.remove(result);
                },
                ConsecutiveSetTree::getConsecutiveData);
    }

    /**
     * As defined by {@link #consecutive(ToIntFunction)} (Function, Function, BinaryOperator)}.
     *
     * @param resultMap Maps the three facts to an item in the sequence
     * @param indexMap Maps the item to its position in the sequence
     * @param <A> type of the first mapped fact
     * @param <B> type of the second mapped fact
     * @param <C> type of the third mapped fact
     * @param <Result> type of item in the sequence
     * @return never null
     */
    public static <A, B, C, Result>
            TriConstraintCollector<A, B, C, ConsecutiveSetTree<Result, Integer, Integer>, ConsecutiveInfo<Result, Integer>>
            consecutive(TriFunction<A, B, C, Result> resultMap, ToIntFunction<Result> indexMap) {
        return new DefaultTriConstraintCollector<>(() -> new ConsecutiveSetTree<>(
                indexMap::applyAsInt,
                (Integer a, Integer b) -> b - a, Integer::sum, 1, 0),
                (acc, a, b, c) -> {
                    Result result = resultMap.apply(a, b, c);
                    acc.add(result);
                    return () -> acc.remove(result);
                },
                ConsecutiveSetTree::getConsecutiveData);
    }

    /**
     * As defined by {@link #consecutive(ToIntFunction)} (Function, Function, BinaryOperator)}.
     *
     * @param resultMap Maps the four facts to an item in the sequence
     * @param indexMap Maps the item to its position in the sequence
     * @param <A> type of the first mapped fact
     * @param <B> type of the second mapped fact
     * @param <C> type of the third mapped fact
     * @param <D> type of the fourth mapped fact
     * @param <Result> type of item in the sequence
     * @return never null
     */
    public static <A, B, C, D, Result>
            QuadConstraintCollector<A, B, C, D, ConsecutiveSetTree<Result, Integer, Integer>, ConsecutiveInfo<Result, Integer>>
            consecutive(QuadFunction<A, B, C, D, Result> resultMap, ToIntFunction<Result> indexMap) {
        return new DefaultQuadConstraintCollector<>(() -> new ConsecutiveSetTree<>(
                indexMap::applyAsInt,
                (Integer a, Integer b) -> b - a, Integer::sum, 1, 0),
                (acc, a, b, c, d) -> {
                    Result result = resultMap.apply(a, b, c, d);
                    acc.add(result);
                    return () -> acc.remove(result);
                },
                ConsecutiveSetTree::getConsecutiveData);
    }

    /**
     * Creates a constraint collector that returns {@link ConsecutiveIntervalDataImpl} about the first
     * fact.
     *
     * For instance ${@code [Shift from=2, to=4] [Shift from=3, to=5] [Shift from=6, to=7] [Shift from=7, to=8]}
     * returns the following infomation
     *
     * ${@code
     * IntervalClusters [[Shift from=2, to=4] [Shift from=3, to=5]], [[Shift from=6, to=7] [Shift from=7, to=8]]
     * }
     *
     * @param startMap Maps the fact to its start
     * @param endMap Maps the fact to its end
     * @param <A> type of the first mapped fact
     * @param <C> type of the fact endpoints
     * @return never null
     */
    public static <A, C extends Comparable<C>> UniConstraintCollector<A, IntervalTree<A, C>, ConsecutiveIntervalDataImpl<A, C>>
            consecutiveIntervals(Function<A, C> startMap, Function<A, C> endMap) {
        return new DefaultUniConstraintCollector<>(
                () -> new IntervalTree<>(
                        startMap,
                        endMap),
                (acc, a) -> {
                    acc.add(a);
                    return () -> acc.remove(a);
                },
                IntervalTree::getConsecutiveIntervalData);
    }

    /**
     * As defined by {@link #consecutiveIntervals(Function,Function)}.
     *
     * @param intervalMap Maps both facts to an item in the sequence
     * @param startMap Maps the item to its start
     * @param endMap Maps the item to its end
     * @param <A> type of the first mapped fact
     * @param <B> type of the second mapped fact
     * @param <C> type of the item endpoints
     * @return never null
     */
    public static <A, B, T, C extends Comparable<C>>
            BiConstraintCollector<A, B, IntervalTree<T, C>, ConsecutiveIntervalDataImpl<T, C>>
            consecutiveIntervals(BiFunction<A, B, T> intervalMap, Function<T, C> startMap, Function<T, C> endMap) {
        return new DefaultBiConstraintCollector<>(
                () -> new IntervalTree<>(
                        startMap,
                        endMap),
                (acc, a, b) -> {
                    T interval = intervalMap.apply(a, b);
                    acc.add(interval);
                    return () -> acc.remove(interval);
                },
                IntervalTree::getConsecutiveIntervalData);
    }

    /**
     * As defined by {@link #consecutiveIntervals(Function,Function)}.
     *
     * @param intervalMap Maps the three facts to an item in the sequence
     * @param startMap Maps the item to its start
     * @param endMap Maps the item to its end
     * @param <A> type of the first mapped fact
     * @param <B> type of the second mapped fact
     * @param <C> type of the third mapped fact
     * @param <I> type of the item endpoints
     * @return never null
     */
    public static <A, B, C, T, I extends Comparable<I>>
            TriConstraintCollector<A, B, C, IntervalTree<T, I>, ConsecutiveIntervalDataImpl<T, I>>
            consecutiveIntervals(TriFunction<A, B, C, T> intervalMap, Function<T, I> startMap, Function<T, I> endMap) {
        return new DefaultTriConstraintCollector<>(
                () -> new IntervalTree<>(
                        startMap,
                        endMap),
                (acc, a, b, c) -> {
                    T interval = intervalMap.apply(a, b, c);
                    acc.add(interval);
                    return () -> acc.remove(interval);
                },
                IntervalTree::getConsecutiveIntervalData);
    }

    /**
     * As defined by {@link #consecutiveIntervals(Function,Function)}.
     *
     * @param intervalMap Maps the four facts to an item in the sequence
     * @param startMap Maps the item to its start
     * @param endMap Maps the item to its end
     * @param <A> type of the first mapped fact
     * @param <B> type of the second mapped fact
     * @param <C> type of the third mapped fact
     * @param <D> type of the fourth mapped fact
     * @param <I> type of the item endpoints
     * @return never null
     */
    public static <A, B, C, D, T, I extends Comparable<I>>
            QuadConstraintCollector<A, B, C, D, IntervalTree<T, I>, ConsecutiveIntervalDataImpl<T, I>>
            consecutiveIntervals(QuadFunction<A, B, C, D, T> intervalMap, Function<T, I> startMap, Function<T, I> endMap) {
        return new DefaultQuadConstraintCollector<>(
                () -> new IntervalTree<>(
                        startMap,
                        endMap),
                (acc, a, b, c, d) -> {
                    T interval = intervalMap.apply(a, b, c, d);
                    acc.add(interval);
                    return () -> acc.remove(interval);
                },
                IntervalTree::getConsecutiveIntervalData);
    }

    // Hide constructor since this is a factory class
    private ExperimentalConstraintCollectors() {
    }
}

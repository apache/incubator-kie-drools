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

import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

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
import org.optaplanner.examples.common.experimental.api.ConsecutiveIntervalInfo;
import org.optaplanner.examples.common.experimental.impl.ConsecutiveSetTree;
import org.optaplanner.examples.common.experimental.impl.Interval;
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
     * Creates a constraint collector that returns {@link ConsecutiveIntervalInfo} about the first
     * fact.
     *
     * For instance ${@code [Shift from=2, to=4] [Shift from=3, to=5] [Shift from=6, to=7] [Shift from=7, to=8]}
     * returns the following information
     *
     * ${@code
     * IntervalClusters [[Shift from=2, to=4] [Shift from=3, to=5]], [[Shift from=6, to=7] [Shift from=7, to=8]]
     * Breaks [[Break from=5, to=6, length=1]]
     * }
     *
     * @param startMap Maps the fact to its start
     * @param endMap Maps the fact to its end
     * @param differenceFunction Computes the difference between two points. The second argument is always
     *        larger than the first (ex: {@link Duration#between}
     *        or (a,b) -> b - a).
     * @param <A> type of the first mapped fact
     * @param <PointType_> type of the fact endpoints
     * @param <DifferenceType_> type of difference between points
     * @return never null
     */
    public static <A, PointType_ extends Comparable<PointType_>, DifferenceType_ extends Comparable<DifferenceType_>>
            UniConstraintCollector<A, IntervalTree<A, PointType_, DifferenceType_>, ConsecutiveIntervalInfo<A, PointType_, DifferenceType_>>
            consecutiveIntervals(Function<A, PointType_> startMap, Function<A, PointType_> endMap,
                    BiFunction<PointType_, PointType_, DifferenceType_> differenceFunction) {
        return new DefaultUniConstraintCollector<>(
                () -> new IntervalTree<>(
                        startMap,
                        endMap, differenceFunction),
                (acc, a) -> {
                    Interval<A, PointType_> interval = acc.getInterval(a);
                    acc.add(interval);
                    return () -> acc.remove(interval);
                },
                IntervalTree::getConsecutiveIntervalData);
    }

    /**
     * Specialized version of {@link #consecutiveIntervals(Function,Function,BiFunction)} for
     * {@link Temporal} types.
     * 
     * @param <A> type of the first mapped fact
     * @param <PointType_> temporal type of the endpoints
     * @param startMap Maps the fact to its start
     * @param endMap Maps the fact to its end
     * @return never null
     */
    public static <A, PointType_ extends Temporal & Comparable<PointType_>>
            UniConstraintCollector<A, IntervalTree<A, PointType_, Duration>, ConsecutiveIntervalInfo<A, PointType_, Duration>>
            consecutiveTemporalIntervals(Function<A, PointType_> startMap, Function<A, PointType_> endMap) {
        return consecutiveIntervals(startMap, endMap, Duration::between);
    }

    /**
     * Specialized version of {@link #consecutiveIntervals(Function,Function,BiFunction)} for Long.
     *
     * @param startMap Maps the fact to its start
     * @param endMap Maps the fact to its end
     * @param <A> type of the first mapped fact
     * @return never null
     */
    public static <A> UniConstraintCollector<A, IntervalTree<A, Long, Long>, ConsecutiveIntervalInfo<A, Long, Long>>
            consecutiveIntervals(ToLongFunction<A> startMap, ToLongFunction<A> endMap) {
        return consecutiveIntervals(startMap::applyAsLong, endMap::applyAsLong, (a, b) -> b - a);
    }

    /**
     * As defined by {@link #consecutiveIntervals(Function,Function,BiFunction)}.
     *
     * @param intervalMap Maps both facts to an item in the cluster
     * @param startMap Maps the item to its start
     * @param endMap Maps the item to its end
     * @param differenceFunction Computes the difference between two points. The second argument is always
     *        larger than the first (ex: {@link Duration#between}
     *        or (a,b) -> b - a).
     * @param <A> type of the first mapped fact
     * @param <B> type of the second mapped fact
     * @param <IntervalType_> type of the item in the cluster
     * @param <PointType_> type of the item endpoints
     * @param <DifferenceType_> type of difference between points
     * @return never null
     */
    public static <A, B, IntervalType_, PointType_ extends Comparable<PointType_>, DifferenceType_ extends Comparable<DifferenceType_>>
            BiConstraintCollector<A, B, IntervalTree<IntervalType_, PointType_, DifferenceType_>, ConsecutiveIntervalInfo<IntervalType_, PointType_, DifferenceType_>>
            consecutiveIntervals(BiFunction<A, B, IntervalType_> intervalMap, Function<IntervalType_, PointType_> startMap,
                    Function<IntervalType_, PointType_> endMap,
                    BiFunction<PointType_, PointType_, DifferenceType_> differenceFunction) {
        return new DefaultBiConstraintCollector<>(
                () -> new IntervalTree<>(
                        startMap,
                        endMap, differenceFunction),
                (acc, a, b) -> {
                    IntervalType_ intervalObj = intervalMap.apply(a, b);
                    Interval<IntervalType_, PointType_> interval = acc.getInterval(intervalObj);
                    acc.add(interval);
                    return () -> acc.remove(interval);
                },
                IntervalTree::getConsecutiveIntervalData);
    }

    /**
     * As defined by {@link #consecutiveTemporalIntervals(Function,Function)}.
     *
     * @param intervalMap Maps the three facts to an item in the cluster
     * @param startMap Maps the fact to its start
     * @param endMap Maps the fact to its end
     * @param <A> type of the first mapped fact
     * @param <B> type of the second mapped fact
     * @param <IntervalType_> type of the item in the cluster
     * @param <PointType_> temporal type of the endpoints
     * @return never null
     */
    public static <A, B, IntervalType_, PointType_ extends Temporal & Comparable<PointType_>>
            BiConstraintCollector<A, B, IntervalTree<IntervalType_, PointType_, Duration>, ConsecutiveIntervalInfo<IntervalType_, PointType_, Duration>>
            consecutiveTemporalIntervals(BiFunction<A, B, IntervalType_> intervalMap,
                    Function<IntervalType_, PointType_> startMap, Function<IntervalType_, PointType_> endMap) {
        return consecutiveIntervals(intervalMap, startMap, endMap, Duration::between);
    }

    /**
     * As defined by {@link #consecutiveIntervals(ToLongFunction, ToLongFunction)}.
     *
     * @param startMap Maps the fact to its start
     * @param endMap Maps the fact to its end
     * @param <A> type of the first mapped fact
     * @param <B> type of the second mapped fact
     * @param <IntervalType_> type of the item in the cluster
     * @return never null
     */
    public static <A, B, IntervalType_>
            BiConstraintCollector<A, B, IntervalTree<IntervalType_, Long, Long>, ConsecutiveIntervalInfo<IntervalType_, Long, Long>>
            consecutiveIntervals(BiFunction<A, B, IntervalType_> intervalMap, ToLongFunction<IntervalType_> startMap,
                    ToLongFunction<IntervalType_> endMap) {
        return consecutiveIntervals(intervalMap, startMap::applyAsLong, endMap::applyAsLong, (a, b) -> b - a);
    }

    /**
     * As defined by {@link #consecutiveIntervals(Function,Function,BiFunction)}.
     *
     * @param intervalMap Maps the three facts to an item in the cluster
     * @param startMap Maps the item to its start
     * @param endMap Maps the item to its end
     * @param differenceFunction Computes the difference between two points. The second argument is always
     *        larger than the first (ex: {@link Duration#between)}
     *        or (a,b) -> b - a).
     * @param <A> type of the first mapped fact
     * @param <B> type of the second mapped fact
     * @param <C> type of the third mapped fact
     * @param <IntervalType_> type of the item in the cluster
     * @param <PointType_> type of the item endpoints
     * @param <DifferenceType_> type of difference between points
     * @return never null
     */
    public static <A, B, C, IntervalType_, PointType_ extends Comparable<PointType_>, DifferenceType_ extends Comparable<DifferenceType_>>
            TriConstraintCollector<A, B, C, IntervalTree<IntervalType_, PointType_, DifferenceType_>, ConsecutiveIntervalInfo<IntervalType_, PointType_, DifferenceType_>>
            consecutiveIntervals(TriFunction<A, B, C, IntervalType_> intervalMap, Function<IntervalType_, PointType_> startMap,
                    Function<IntervalType_, PointType_> endMap,
                    BiFunction<PointType_, PointType_, DifferenceType_> differenceFunction) {
        return new DefaultTriConstraintCollector<>(
                () -> new IntervalTree<>(
                        startMap,
                        endMap, differenceFunction),
                (acc, a, b, c) -> {
                    IntervalType_ intervalObj = intervalMap.apply(a, b, c);
                    Interval<IntervalType_, PointType_> interval = acc.getInterval(intervalObj);
                    acc.add(interval);
                    return () -> acc.remove(interval);
                },
                IntervalTree::getConsecutiveIntervalData);
    }

    /**
     * As defined by {@link #consecutiveTemporalIntervals(Function,Function)}.
     *
     * @param intervalMap Maps the three facts to an item in the cluster
     * @param startMap Maps the fact to its start
     * @param endMap Maps the fact to its end
     * @param <A> type of the first mapped fact
     * @param <B> type of the second mapped fact
     * @param <C> type of the third mapped fact
     * @param <IntervalType_> type of the item in the cluster
     * @param <PointType_> temporal type of the endpoints
     * @return never null
     */
    public static <A, B, C, IntervalType_, PointType_ extends Temporal & Comparable<PointType_>>
            TriConstraintCollector<A, B, C, IntervalTree<IntervalType_, PointType_, Duration>, ConsecutiveIntervalInfo<IntervalType_, PointType_, Duration>>
            consecutiveTemporalIntervals(TriFunction<A, B, C, IntervalType_> intervalMap,
                    Function<IntervalType_, PointType_> startMap, Function<IntervalType_, PointType_> endMap) {
        return consecutiveIntervals(intervalMap, startMap, endMap, Duration::between);
    }

    /**
     * As defined by {@link #consecutiveIntervals(ToLongFunction, ToLongFunction)}.
     *
     * @param startMap Maps the fact to its start
     * @param endMap Maps the fact to its end
     * @param <A> type of the first mapped fact
     * @param <B> type of the second mapped fact
     * @param <C> type of the third mapped fact
     * @param <IntervalType_> type of the item in the cluster
     * @return never null
     */
    public static <A, B, C, IntervalType_>
            TriConstraintCollector<A, B, C, IntervalTree<IntervalType_, Long, Long>, ConsecutiveIntervalInfo<IntervalType_, Long, Long>>
            consecutiveIntervals(TriFunction<A, B, C, IntervalType_> intervalMap, ToLongFunction<IntervalType_> startMap,
                    ToLongFunction<IntervalType_> endMap) {
        return consecutiveIntervals(intervalMap, startMap::applyAsLong, endMap::applyAsLong, (a, b) -> b - a);
    }

    /**
     * As defined by {@link #consecutiveIntervals(Function,Function,BiFunction)}.
     *
     * @param intervalMap Maps the four facts to an item in the cluster
     * @param startMap Maps the item to its start
     * @param endMap Maps the item to its end
     * @param differenceFunction Computes the difference between two points. The second argument is always
     *        larger than the first (ex: {@link Duration#between}
     *        or (a,b) -> b - a).
     * @param <A> type of the first mapped fact
     * @param <B> type of the second mapped fact
     * @param <C> type of the third mapped fact
     * @param <D> type of the fourth mapped fact
     * @param <IntervalType_> type of the item in the cluster
     * @param <PointType_> type of the item endpoints
     * @param <DifferenceType_> type of difference between points
     * @return never null
     */
    public static <A, B, C, D, IntervalType_, PointType_ extends Comparable<PointType_>, DifferenceType_ extends Comparable<DifferenceType_>>
            QuadConstraintCollector<A, B, C, D, IntervalTree<IntervalType_, PointType_, DifferenceType_>, ConsecutiveIntervalInfo<IntervalType_, PointType_, DifferenceType_>>
            consecutiveIntervals(QuadFunction<A, B, C, D, IntervalType_> intervalMap,
                    Function<IntervalType_, PointType_> startMap, Function<IntervalType_, PointType_> endMap,
                    BiFunction<PointType_, PointType_, DifferenceType_> differenceFunction) {
        return new DefaultQuadConstraintCollector<>(
                () -> new IntervalTree<>(
                        startMap,
                        endMap, differenceFunction),
                (acc, a, b, c, d) -> {
                    IntervalType_ intervalObj = intervalMap.apply(a, b, c, d);
                    Interval<IntervalType_, PointType_> interval = acc.getInterval(intervalObj);
                    acc.add(interval);
                    return () -> acc.remove(interval);
                },
                IntervalTree::getConsecutiveIntervalData);
    }

    /**
     * As defined by {@link #consecutiveTemporalIntervals(Function,Function)}.
     *
     * @param intervalMap Maps the three facts to an item in the cluster
     * @param startMap Maps the fact to its start
     * @param endMap Maps the fact to its end
     * @param <A> type of the first mapped fact
     * @param <B> type of the second mapped fact
     * @param <C> type of the third mapped fact
     * @param <D> type of the fourth mapped fact
     * @param <IntervalType_> type of the item in the cluster
     * @param <PointType_> temporal type of the endpoints
     * @return never null
     */
    public static <A, B, C, D, IntervalType_, PointType_ extends Temporal & Comparable<PointType_>>
            QuadConstraintCollector<A, B, C, D, IntervalTree<IntervalType_, PointType_, Duration>, ConsecutiveIntervalInfo<IntervalType_, PointType_, Duration>>
            consecutiveTemporalIntervals(QuadFunction<A, B, C, D, IntervalType_> intervalMap,
                    Function<IntervalType_, PointType_> startMap, Function<IntervalType_, PointType_> endMap) {
        return consecutiveIntervals(intervalMap, startMap, endMap, Duration::between);
    }

    /**
     * As defined by {@link #consecutiveIntervals(ToLongFunction, ToLongFunction)}.
     *
     * @param startMap Maps the fact to its start
     * @param endMap Maps the fact to its end
     * @param <A> type of the first mapped fact
     * @param <B> type of the second mapped fact
     * @param <C> type of the third mapped fact
     * @param <D> type of the fourth mapped fact
     * @param <IntervalType_> type of the item in the cluster
     * @return never null
     */
    public static <A, B, C, D, IntervalType_>
            QuadConstraintCollector<A, B, C, D, IntervalTree<IntervalType_, Long, Long>, ConsecutiveIntervalInfo<IntervalType_, Long, Long>>
            consecutiveIntervals(QuadFunction<A, B, C, D, IntervalType_> intervalMap, ToLongFunction<IntervalType_> startMap,
                    ToLongFunction<IntervalType_> endMap) {
        return consecutiveIntervals(intervalMap, startMap::applyAsLong, endMap::applyAsLong, (a, b) -> b - a);
    }

    // Hide constructor since this is a factory class
    private ExperimentalConstraintCollectors() {
    }
}

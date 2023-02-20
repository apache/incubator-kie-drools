package org.optaplanner.examples.common.experimental;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.core.api.function.PentaFunction;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.examples.common.experimental.api.ConsecutiveInfo;
import org.optaplanner.examples.common.experimental.api.ConsecutiveIntervalInfo;
import org.optaplanner.examples.common.experimental.impl.ConsecutiveSetTree;
import org.optaplanner.examples.common.experimental.impl.Interval;
import org.optaplanner.examples.common.experimental.impl.IntervalTree;

/**
 * A collection of experimental constraint collectors subject to change in future versions.
 */
public class ExperimentalConstraintCollectors {
    /**
     * Creates a constraint collector that returns {@link ConsecutiveInfo} about the first fact.
     *
     * For instance, {@code [Shift slot=1] [Shift slot=2] [Shift slot=4] [Shift slot=6]}
     * returns the following information:
     *
     * <pre>
     * {@code
     * Consecutive Lengths: 2, 1, 1
     * Break Lengths: 1, 2
     * Consecutive Items: [[Shift slot=1] [Shift slot=2]], [[Shift slot=4]], [[Shift slot=6]]
     * }
     * </pre>
     *
     * @param indexMap Maps the fact to its position in the sequence
     * @param <A> type of the first mapped fact
     * @return never null
     */
    public static <A> UniConstraintCollector<A, ConsecutiveSetTree<A, Integer, Integer>, ConsecutiveInfo<A, Integer>>
            consecutive(ToIntFunction<A> indexMap) {
        return new UniConstraintCollector<>() {

            @Override
            public Supplier<ConsecutiveSetTree<A, Integer, Integer>> supplier() {
                return () -> new ConsecutiveSetTree<>(
                        (Integer a, Integer b) -> b - a,
                        Integer::sum,
                        1, 0);
            }

            @Override
            public BiFunction<ConsecutiveSetTree<A, Integer, Integer>, A, Runnable> accumulator() {
                return (acc, a) -> {
                    Integer value = indexMap.applyAsInt(a);
                    acc.add(a, value);
                    return () -> acc.remove(a);
                };
            }

            @Override
            public Function<ConsecutiveSetTree<A, Integer, Integer>, ConsecutiveInfo<A, Integer>> finisher() {
                return tree -> tree;
            }
        };
    }

    /**
     * As defined by {@link #consecutive(ToIntFunction)}.
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
        return new BiConstraintCollector<>() {
            @Override
            public Supplier<ConsecutiveSetTree<Result, Integer, Integer>> supplier() {
                return () -> new ConsecutiveSetTree<>(
                        (Integer a, Integer b) -> b - a,
                        Integer::sum, 1, 0);
            }

            @Override
            public TriFunction<ConsecutiveSetTree<Result, Integer, Integer>, A, B, Runnable> accumulator() {
                return (acc, a, b) -> {
                    Result result = resultMap.apply(a, b);
                    Integer value = indexMap.applyAsInt(result);
                    acc.add(result, value);
                    return () -> acc.remove(result);
                };
            }

            @Override
            public Function<ConsecutiveSetTree<Result, Integer, Integer>, ConsecutiveInfo<Result, Integer>> finisher() {
                return tree -> tree;
            }
        };
    }

    /**
     * As defined by {@link #consecutive(ToIntFunction)}.
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
        return new TriConstraintCollector<>() {
            @Override
            public Supplier<ConsecutiveSetTree<Result, Integer, Integer>> supplier() {
                return () -> new ConsecutiveSetTree<>(
                        (Integer a, Integer b) -> b - a, Integer::sum, 1, 0);
            }

            @Override
            public QuadFunction<ConsecutiveSetTree<Result, Integer, Integer>, A, B, C, Runnable> accumulator() {
                return (acc, a, b, c) -> {
                    Result result = resultMap.apply(a, b, c);
                    Integer value = indexMap.applyAsInt(result);
                    acc.add(result, value);
                    return () -> acc.remove(result);
                };
            }

            @Override
            public Function<ConsecutiveSetTree<Result, Integer, Integer>, ConsecutiveInfo<Result, Integer>> finisher() {
                return tree -> tree;
            }
        };
    }

    /**
     * As defined by {@link #consecutive(ToIntFunction)}.
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
        return new QuadConstraintCollector<>() {
            @Override
            public Supplier<ConsecutiveSetTree<Result, Integer, Integer>> supplier() {
                return () -> new ConsecutiveSetTree<>(
                        (Integer a, Integer b) -> b - a, Integer::sum, 1, 0);
            }

            @Override
            public PentaFunction<ConsecutiveSetTree<Result, Integer, Integer>, A, B, C, D, Runnable> accumulator() {
                return (acc, a, b, c, d) -> {
                    Result result = resultMap.apply(a, b, c, d);
                    Integer value = indexMap.applyAsInt(result);
                    acc.add(result, value);
                    return () -> acc.remove(result);
                };
            }

            @Override
            public Function<ConsecutiveSetTree<Result, Integer, Integer>, ConsecutiveInfo<Result, Integer>> finisher() {
                return tree -> tree;
            }
        };
    }

    /**
     * Creates a constraint collector that returns {@link ConsecutiveIntervalInfo} about the first fact.
     *
     * For instance, {@code [Shift from=2, to=4] [Shift from=3, to=5] [Shift from=6, to=7] [Shift from=7, to=8]}
     * returns the following information:
     *
     * <pre>
     * {@code
     * IntervalClusters: [[Shift from=2, to=4] [Shift from=3, to=5]], [[Shift from=6, to=7] [Shift from=7, to=8]]
     * Breaks: [[Break from=5, to=6, length=1]]
     * }
     * </pre>
     *
     * @param startMap Maps the fact to its start
     * @param endMap Maps the fact to its end
     * @param differenceFunction Computes the difference between two points. The second argument is always
     *        larger than the first (ex: {@link Duration#between}
     *        or {@code (a,b) -> b - a}).
     * @param <A> type of the first mapped fact
     * @param <PointType_> type of the fact endpoints
     * @param <DifferenceType_> type of difference between points
     * @return never null
     */
    public static <A, PointType_ extends Comparable<PointType_>, DifferenceType_ extends Comparable<DifferenceType_>>
            UniConstraintCollector<A, IntervalTree<A, PointType_, DifferenceType_>, ConsecutiveIntervalInfo<A, PointType_, DifferenceType_>>
            consecutiveIntervals(Function<A, PointType_> startMap, Function<A, PointType_> endMap,
                    BiFunction<PointType_, PointType_, DifferenceType_> differenceFunction) {
        return new UniConstraintCollector<>() {
            @Override
            public Supplier<IntervalTree<A, PointType_, DifferenceType_>> supplier() {
                return () -> new IntervalTree<>(
                        startMap,
                        endMap, differenceFunction);
            }

            @Override
            public BiFunction<IntervalTree<A, PointType_, DifferenceType_>, A, Runnable> accumulator() {
                return (acc, a) -> {
                    Interval<A, PointType_> interval = acc.getInterval(a);
                    acc.add(interval);
                    return () -> acc.remove(interval);
                };
            }

            @Override
            public Function<IntervalTree<A, PointType_, DifferenceType_>, ConsecutiveIntervalInfo<A, PointType_, DifferenceType_>>
                    finisher() {
                return IntervalTree::getConsecutiveIntervalData;
            }
        };
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
     *        or {@code (a,b) -> b - a}).
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
        return new BiConstraintCollector<>() {
            @Override
            public Supplier<IntervalTree<IntervalType_, PointType_, DifferenceType_>> supplier() {
                return () -> new IntervalTree<>(
                        startMap,
                        endMap, differenceFunction);
            }

            @Override
            public TriFunction<IntervalTree<IntervalType_, PointType_, DifferenceType_>, A, B, Runnable> accumulator() {
                return (acc, a, b) -> {
                    IntervalType_ intervalObj = intervalMap.apply(a, b);
                    Interval<IntervalType_, PointType_> interval = acc.getInterval(intervalObj);
                    acc.add(interval);
                    return () -> acc.remove(interval);
                };
            }

            @Override
            public Function<IntervalTree<IntervalType_, PointType_, DifferenceType_>, ConsecutiveIntervalInfo<IntervalType_, PointType_, DifferenceType_>>
                    finisher() {
                return IntervalTree::getConsecutiveIntervalData;
            }
        };
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
     *        larger than the first (ex: {@link Duration#between}
     *        or {@code (a,b) -> b - a}).
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
        return new TriConstraintCollector<>() {
            @Override
            public Supplier<IntervalTree<IntervalType_, PointType_, DifferenceType_>> supplier() {
                return () -> new IntervalTree<>(
                        startMap,
                        endMap, differenceFunction);
            }

            @Override
            public QuadFunction<IntervalTree<IntervalType_, PointType_, DifferenceType_>, A, B, C, Runnable> accumulator() {
                return (acc, a, b, c) -> {
                    IntervalType_ intervalObj = intervalMap.apply(a, b, c);
                    Interval<IntervalType_, PointType_> interval = acc.getInterval(intervalObj);
                    acc.add(interval);
                    return () -> acc.remove(interval);
                };
            }

            @Override
            public Function<IntervalTree<IntervalType_, PointType_, DifferenceType_>, ConsecutiveIntervalInfo<IntervalType_, PointType_, DifferenceType_>>
                    finisher() {
                return IntervalTree::getConsecutiveIntervalData;
            }
        };
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
     *        or {@code (a,b) -> b - a}).
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
        return new QuadConstraintCollector<>() {
            @Override
            public Supplier<IntervalTree<IntervalType_, PointType_, DifferenceType_>> supplier() {
                return () -> new IntervalTree<>(
                        startMap,
                        endMap, differenceFunction);
            }

            @Override
            public PentaFunction<IntervalTree<IntervalType_, PointType_, DifferenceType_>, A, B, C, D, Runnable> accumulator() {
                return (acc, a, b, c, d) -> {
                    IntervalType_ intervalObj = intervalMap.apply(a, b, c, d);
                    Interval<IntervalType_, PointType_> interval = acc.getInterval(intervalObj);
                    acc.add(interval);
                    return () -> acc.remove(interval);
                };
            }

            @Override
            public Function<IntervalTree<IntervalType_, PointType_, DifferenceType_>, ConsecutiveIntervalInfo<IntervalType_, PointType_, DifferenceType_>>
                    finisher() {
                return IntervalTree::getConsecutiveIntervalData;
            }
        };
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

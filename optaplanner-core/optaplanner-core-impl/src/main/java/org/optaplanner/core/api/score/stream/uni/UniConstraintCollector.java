package org.optaplanner.core.api.score.stream.uni;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintStream;

/**
 * Usually created with {@link ConstraintCollectors}.
 * Used by {@link UniConstraintStream#groupBy(Function, UniConstraintCollector)}, ...
 * <p>
 * Loosely based on JDK's {@link Collector}, but it returns an undo operation for each accumulation
 * to enable incremental score calculation in {@link ConstraintStream constraint streams}.
 *
 * @param <A> the type of the one and only fact of the tuple in the source {@link UniConstraintStream}
 * @param <ResultContainer_> the mutable accumulation type (often hidden as an implementation detail)
 * @param <Result_> the type of the fact of the tuple in the destination {@link ConstraintStream}.
 *        It is recommended that this type be deeply immutable.
 *        Not following this recommendation may lead to hard-to-debug hashing issues down the stream,
 *        especially if this value is ever used as a group key.
 * @see ConstraintCollectors
 */
public interface UniConstraintCollector<A, ResultContainer_, Result_> {

    /**
     * A lambda that creates the result container, one for each group key combination.
     *
     * @return never null
     */
    Supplier<ResultContainer_> supplier();

    /**
     * A lambda that extracts data from the matched fact,
     * accumulates it in the result container
     * and returns an undo operation for that accumulation.
     *
     * @return never null, the undo operation. This lambda is called when the fact no longer matches.
     */
    BiFunction<ResultContainer_, A, Runnable> accumulator();

    /**
     * A lambda that converts the result container into the result.
     *
     * @return null when the result would be invalid, such as maximum value from an empty container.
     */
    Function<ResultContainer_, Result_> finisher();

}

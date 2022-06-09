package org.optaplanner.core.api.score.stream.quad;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.optaplanner.core.api.function.PentaFunction;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintStream;

/**
 * Usually created with {@link ConstraintCollectors}.
 * Used by {@link QuadConstraintStream#groupBy(QuadFunction, QuadConstraintCollector)}, ...
 * <p>
 * Loosely based on JDK's {@link Collector}, but it returns an undo operation for each accumulation
 * to enable incremental score calculation in {@link ConstraintStream constraint streams}.
 *
 * @param <A> the type of the first fact of the tuple in the source {@link QuadConstraintStream}
 * @param <B> the type of the second fact of the tuple in the source {@link QuadConstraintStream}
 * @param <C> the type of the third fact of the tuple in the source {@link QuadConstraintStream}
 * @param <D> the type of the fourth fact of the tuple in the source {@link QuadConstraintStream}
 * @param <ResultContainer_> the mutable accumulation type (often hidden as an implementation detail)
 * @param <Result_> the type of the fact of the tuple in the destination {@link ConstraintStream}
 * @see ConstraintCollectors
 */
public interface QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> {

    /**
     * A lambda that creates the result container, one for each group key combination.
     *
     * @return never null
     */
    Supplier<ResultContainer_> supplier();

    /**
     * A lambda that extracts data from the matched facts,
     * accumulates it in the result container
     * and returns an undo operation for that accumulation.
     *
     * @return never null, the undo operation. This lambda is called when the facts no longer matches.
     */
    PentaFunction<ResultContainer_, A, B, C, D, Runnable> accumulator();

    /**
     * A lambda that converts the result container into the result.
     *
     * @return never null
     */
    Function<ResultContainer_, Result_> finisher();

}

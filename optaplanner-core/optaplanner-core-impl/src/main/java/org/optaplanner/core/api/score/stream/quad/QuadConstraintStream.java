package org.optaplanner.core.api.score.stream.quad;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.ToIntQuadFunction;
import org.optaplanner.core.api.function.ToLongQuadFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintStream;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.penta.PentaJoiner;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;

/**
 * A {@link ConstraintStream} that matches four facts.
 *
 * @param <A> the type of the first matched fact (either a problem fact or a {@link PlanningEntity planning entity})
 * @param <B> the type of the second matched fact (either a problem fact or a {@link PlanningEntity planning entity})
 * @param <C> the type of the third matched fact (either a problem fact or a {@link PlanningEntity planning entity})
 * @param <D> the type of the fourth matched fact (either a problem fact or a {@link PlanningEntity planning entity})
 * @see ConstraintStream
 */
public interface QuadConstraintStream<A, B, C, D> extends ConstraintStream {

    // ************************************************************************
    // Filter
    // ************************************************************************

    /**
     * Exhaustively test each tuple of facts against the {@link QuadPredicate}
     * and match if {@link QuadPredicate#test(Object, Object, Object, Object)} returns true.
     * <p>
     * Important: This is slower and less scalable than
     * {@link TriConstraintStream#join(UniConstraintStream, QuadJoiner)} with a proper {@link QuadJoiner} predicate
     * (such as {@link Joiners#equal(TriFunction, Function)},
     * because the latter applies hashing and/or indexing, so it doesn't create every combination just to filter it out.
     *
     * @param predicate never null
     * @return never null
     */
    QuadConstraintStream<A, B, C, D> filter(QuadPredicate<A, B, C, D> predicate);

    // ************************************************************************
    // If (not) exists
    // ************************************************************************

    /**
     * Create a new {@link BiConstraintStream} for every tuple of A, B, C and D where E exists for which the
     * {@link PentaJoiner} is true (for the properties it extracts from the facts).
     * <p>
     * This method has overloaded methods with multiple {@link PentaJoiner} parameters.
     * <p>
     * Note that, if a legacy constraint stream uses {@link ConstraintFactory#from(Class)} as opposed to
     * {@link ConstraintFactory#forEach(Class)},
     * a different definition of exists applies.
     * (See {@link ConstraintFactory#from(Class)} Javadoc.)
     *
     * @param otherClass never null
     * @param joiner never null
     * @param <E> the type of the fifth matched fact
     * @return never null, a stream that matches every tuple of A, B, C and D where E exists for which the
     *         {@link PentaJoiner} is true
     */
    default <E> QuadConstraintStream<A, B, C, D> ifExists(Class<E> otherClass, PentaJoiner<A, B, C, D, E> joiner) {
        return ifExists(otherClass, new PentaJoiner[] { joiner });
    }

    /**
     * As defined by {@link #ifExists(Class, PentaJoiner)}.
     * For performance reasons, indexing joiners must be placed before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param <E> the type of the fifth matched fact
     * @return never null, a stream that matches every tuple of A, B, C and D where E exists for which the
     *         {@link PentaJoiner}s are true
     */
    default <E> QuadConstraintStream<A, B, C, D> ifExists(Class<E> otherClass, PentaJoiner<A, B, C, D, E> joiner1,
            PentaJoiner<A, B, C, D, E> joiner2) {
        return ifExists(otherClass, new PentaJoiner[] { joiner1, joiner2 });
    }

    /**
     * As defined by {@link #ifExists(Class, PentaJoiner)}.
     * For performance reasons, indexing joiners must be placed before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param <E> the type of the fifth matched fact
     * @return never null, a stream that matches every tuple of A, B, C and D where E exists for which the
     *         {@link PentaJoiner}s are true
     */
    default <E> QuadConstraintStream<A, B, C, D> ifExists(Class<E> otherClass, PentaJoiner<A, B, C, D, E> joiner1,
            PentaJoiner<A, B, C, D, E> joiner2, PentaJoiner<A, B, C, D, E> joiner3) {
        return ifExists(otherClass, new PentaJoiner[] { joiner1, joiner2, joiner3 });
    }

    /**
     * As defined by {@link #ifExists(Class, PentaJoiner)}.
     * For performance reasons, indexing joiners must be placed before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param joiner4 never null
     * @param <E> the type of the fifth matched fact
     * @return never null, a stream that matches every tuple of A, B, C and D where E exists for which the
     *         {@link PentaJoiner}s are true
     */
    default <E> QuadConstraintStream<A, B, C, D> ifExists(Class<E> otherClass, PentaJoiner<A, B, C, D, E> joiner1,
            PentaJoiner<A, B, C, D, E> joiner2, PentaJoiner<A, B, C, D, E> joiner3,
            PentaJoiner<A, B, C, D, E> joiner4) {
        return ifExists(otherClass, new PentaJoiner[] { joiner1, joiner2, joiner3, joiner4 });
    }

    /**
     * As defined by {@link #ifExists(Class, PentaJoiner)}.
     * For performance reasons, indexing joiners must be placed before filtering joiners.
     * <p>
     * This method causes <i>Unchecked generics array creation for varargs parameter</i> warnings,
     * but we can't fix it with a {@link SafeVarargs} annotation because it's an interface method.
     * Therefore, there are overloaded methods with up to 4 {@link PentaJoiner} parameters.
     *
     * @param otherClass never null
     * @param joiners never null
     * @param <E> the type of the fifth matched fact
     * @return never null, a stream that matches every tuple of A, B, C and D where E exists for which the
     *         {@link PentaJoiner}s are true
     */
    <E> QuadConstraintStream<A, B, C, D> ifExists(Class<E> otherClass, PentaJoiner<A, B, C, D, E>... joiners);

    /**
     * Create a new {@link BiConstraintStream} for every tuple of A, B, C and D where E exists for which the
     * {@link PentaJoiner} is true (for the properties it extracts from the facts).
     * For classes annotated with {@link org.optaplanner.core.api.domain.entity.PlanningEntity},
     * this method also includes instances with null variables.
     * <p>
     * This method has overloaded methods with multiple {@link PentaJoiner} parameters.
     *
     * @param otherClass never null
     * @param joiner never null
     * @param <E> the type of the fifth matched fact
     * @return never null, a stream that matches every tuple of A, B, C and D where E exists for which the
     *         {@link PentaJoiner} is true
     */
    default <E> QuadConstraintStream<A, B, C, D> ifExistsIncludingNullVars(Class<E> otherClass,
            PentaJoiner<A, B, C, D, E> joiner) {
        return ifExistsIncludingNullVars(otherClass, new PentaJoiner[] { joiner });
    }

    /**
     * As defined by {@link #ifExistsIncludingNullVars(Class, PentaJoiner)}.
     * For performance reasons, indexing joiners must be placed before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param <E> the type of the fifth matched fact
     * @return never null, a stream that matches every tuple of A, B, C and D where E exists for which the
     *         {@link PentaJoiner}s are true
     */
    default <E> QuadConstraintStream<A, B, C, D> ifExistsIncludingNullVars(Class<E> otherClass,
            PentaJoiner<A, B, C, D, E> joiner1, PentaJoiner<A, B, C, D, E> joiner2) {
        return ifExistsIncludingNullVars(otherClass, new PentaJoiner[] { joiner1, joiner2 });
    }

    /**
     * As defined by {@link #ifExistsIncludingNullVars(Class, PentaJoiner)}.
     * For performance reasons, indexing joiners must be placed before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param <E> the type of the fifth matched fact
     * @return never null, a stream that matches every tuple of A, B, C and D where E exists for which the
     *         {@link PentaJoiner}s are true
     */
    default <E> QuadConstraintStream<A, B, C, D> ifExistsIncludingNullVars(Class<E> otherClass,
            PentaJoiner<A, B, C, D, E> joiner1, PentaJoiner<A, B, C, D, E> joiner2, PentaJoiner<A, B, C, D, E> joiner3) {
        return ifExistsIncludingNullVars(otherClass, new PentaJoiner[] { joiner1, joiner2, joiner3 });
    }

    /**
     * As defined by {@link #ifExistsIncludingNullVars(Class, PentaJoiner)}.
     * For performance reasons, indexing joiners must be placed before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param joiner4 never null
     * @param <E> the type of the fifth matched fact
     * @return never null, a stream that matches every tuple of A, B, C and D where E exists for which the
     *         {@link PentaJoiner}s are true
     */
    default <E> QuadConstraintStream<A, B, C, D> ifExistsIncludingNullVars(Class<E> otherClass,
            PentaJoiner<A, B, C, D, E> joiner1, PentaJoiner<A, B, C, D, E> joiner2, PentaJoiner<A, B, C, D, E> joiner3,
            PentaJoiner<A, B, C, D, E> joiner4) {
        return ifExistsIncludingNullVars(otherClass, new PentaJoiner[] { joiner1, joiner2, joiner3, joiner4 });
    }

    /**
     * As defined by {@link #ifExistsIncludingNullVars(Class, PentaJoiner)}.
     * For performance reasons, indexing joiners must be placed before filtering joiners.
     * <p>
     * This method causes <i>Unchecked generics array creation for varargs parameter</i> warnings,
     * but we can't fix it with a {@link SafeVarargs} annotation because it's an interface method.
     * Therefore, there are overloaded methods with up to 4 {@link PentaJoiner} parameters.
     *
     * @param otherClass never null
     * @param joiners never null
     * @param <E> the type of the fifth matched fact
     * @return never null, a stream that matches every tuple of A, B, C and D where E exists for which the
     *         {@link PentaJoiner}s are true
     */
    <E> QuadConstraintStream<A, B, C, D> ifExistsIncludingNullVars(Class<E> otherClass,
            PentaJoiner<A, B, C, D, E>... joiners);

    /**
     * Create a new {@link BiConstraintStream} for every tuple of A, B, C and D where E does not exist for which the
     * {@link PentaJoiner} is true (for the properties it extracts from the facts).
     * <p>
     * This method has overloaded methods with multiple {@link PentaJoiner} parameters.
     * <p>
     * Note that, if a legacy constraint stream uses{@link ConstraintFactory#from(Class)} as opposed to
     * {@link ConstraintFactory#forEach(Class)},
     * a different definition of exists applies.
     * (See {@link ConstraintFactory#from(Class)} Javadoc.)
     *
     * @param otherClass never null
     * @param joiner never null
     * @param <E> the type of the fifth matched fact
     * @return never null, a stream that matches every tuple of A, B, C and D where E does not exist for which the
     *         {@link PentaJoiner} is true
     */
    default <E> QuadConstraintStream<A, B, C, D> ifNotExists(Class<E> otherClass, PentaJoiner<A, B, C, D, E> joiner) {
        return ifNotExists(otherClass, new PentaJoiner[] { joiner });
    }

    /**
     * As defined by {@link #ifNotExists(Class, PentaJoiner)}.
     * For performance reasons, indexing joiners must be placed before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param <E> the type of the fifth matched fact
     * @return never null, a stream that matches every tuple of A, B, C and D where E does not exist for which the
     *         {@link PentaJoiner}s are true
     */
    default <E> QuadConstraintStream<A, B, C, D> ifNotExists(Class<E> otherClass, PentaJoiner<A, B, C, D, E> joiner1,
            PentaJoiner<A, B, C, D, E> joiner2) {
        return ifNotExists(otherClass, new PentaJoiner[] { joiner1, joiner2 });
    }

    /**
     * As defined by {@link #ifNotExists(Class, PentaJoiner)}.
     * For performance reasons, indexing joiners must be placed before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param <E> the type of the fifth matched fact
     * @return never null, a stream that matches every tuple of A, B, C and D where E does not exist for which the
     *         {@link PentaJoiner}s are true
     */
    default <E> QuadConstraintStream<A, B, C, D> ifNotExists(Class<E> otherClass, PentaJoiner<A, B, C, D, E> joiner1,
            PentaJoiner<A, B, C, D, E> joiner2, PentaJoiner<A, B, C, D, E> joiner3) {
        return ifNotExists(otherClass, new PentaJoiner[] { joiner1, joiner2, joiner3 });
    }

    /**
     * As defined by {@link #ifNotExists(Class, PentaJoiner)}.
     * For performance reasons, indexing joiners must be placed before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param joiner4 never null
     * @param <E> the type of the fifth matched fact
     * @return never null, a stream that matches every tuple of A, B, C and D where E does not exist for which the
     *         {@link PentaJoiner}s are true
     */
    default <E> QuadConstraintStream<A, B, C, D> ifNotExists(Class<E> otherClass, PentaJoiner<A, B, C, D, E> joiner1,
            PentaJoiner<A, B, C, D, E> joiner2, PentaJoiner<A, B, C, D, E> joiner3, PentaJoiner<A, B, C, D, E> joiner4) {
        return ifNotExists(otherClass, new PentaJoiner[] { joiner1, joiner2, joiner3, joiner4 });
    }

    /**
     * As defined by {@link #ifNotExists(Class, PentaJoiner)}.
     * For performance reasons, indexing joiners must be placed before filtering joiners.
     * <p>
     * This method causes <i>Unchecked generics array creation for varargs parameter</i> warnings,
     * but we can't fix it with a {@link SafeVarargs} annotation because it's an interface method.
     * Therefore, there are overloaded methods with up to 4 {@link PentaJoiner} parameters.
     *
     * @param <E> the type of the fifth matched fact
     * @param otherClass never null
     * @param joiners never null
     * @return never null, a stream that matches every tuple of A, B, C and D where E does not exist for which the
     *         {@link PentaJoiner}s are true
     */
    <E> QuadConstraintStream<A, B, C, D> ifNotExists(Class<E> otherClass, PentaJoiner<A, B, C, D, E>... joiners);

    /**
     * Create a new {@link BiConstraintStream} for every tuple of A, B, C and D where E does not exist for which the
     * {@link PentaJoiner} is true (for the properties it extracts from the facts).
     * For classes annotated with {@link org.optaplanner.core.api.domain.entity.PlanningEntity},
     * this method also includes instances with null variables.
     * <p>
     * This method has overloaded methods with multiple {@link PentaJoiner} parameters.
     *
     * @param otherClass never null
     * @param joiner never null
     * @param <E> the type of the fifth matched fact
     * @return never null, a stream that matches every tuple of A, B, C and D where E does not exist for which the
     *         {@link PentaJoiner} is true
     */
    default <E> QuadConstraintStream<A, B, C, D> ifNotExistsIncludingNullVars(Class<E> otherClass,
            PentaJoiner<A, B, C, D, E> joiner) {
        return ifNotExistsIncludingNullVars(otherClass, new PentaJoiner[] { joiner });
    }

    /**
     * As defined by {@link #ifNotExistsIncludingNullVars(Class, PentaJoiner)}.
     * For performance reasons, indexing joiners must be placed before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param <E> the type of the fifth matched fact
     * @return never null, a stream that matches every tuple of A, B, C and D where E does not exist for which the
     *         {@link PentaJoiner}s are true
     */
    default <E> QuadConstraintStream<A, B, C, D> ifNotExistsIncludingNullVars(Class<E> otherClass,
            PentaJoiner<A, B, C, D, E> joiner1, PentaJoiner<A, B, C, D, E> joiner2) {
        return ifNotExistsIncludingNullVars(otherClass, new PentaJoiner[] { joiner1, joiner2 });
    }

    /**
     * As defined by {@link #ifNotExistsIncludingNullVars(Class, PentaJoiner)}.
     * For performance reasons, indexing joiners must be placed before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param <E> the type of the fifth matched fact
     * @return never null, a stream that matches every tuple of A, B, C and D where E does not exist for which the
     *         {@link PentaJoiner}s are true
     */
    default <E> QuadConstraintStream<A, B, C, D> ifNotExistsIncludingNullVars(Class<E> otherClass,
            PentaJoiner<A, B, C, D, E> joiner1, PentaJoiner<A, B, C, D, E> joiner2, PentaJoiner<A, B, C, D, E> joiner3) {
        return ifNotExistsIncludingNullVars(otherClass, new PentaJoiner[] { joiner1, joiner2, joiner3 });
    }

    /**
     * As defined by {@link #ifNotExistsIncludingNullVars(Class, PentaJoiner)}.
     * For performance reasons, indexing joiners must be placed before filtering joiners.
     *
     * @param otherClass never null
     * @param joiner1 never null
     * @param joiner2 never null
     * @param joiner3 never null
     * @param joiner4 never null
     * @param <E> the type of the fifth matched fact
     * @return never null, a stream that matches every tuple of A, B, C and D where E does not exist for which the
     *         {@link PentaJoiner}s are true
     */
    default <E> QuadConstraintStream<A, B, C, D> ifNotExistsIncludingNullVars(Class<E> otherClass,
            PentaJoiner<A, B, C, D, E> joiner1, PentaJoiner<A, B, C, D, E> joiner2, PentaJoiner<A, B, C, D, E> joiner3,
            PentaJoiner<A, B, C, D, E> joiner4) {
        return ifNotExistsIncludingNullVars(otherClass, new PentaJoiner[] { joiner1, joiner2, joiner3, joiner4 });
    }

    /**
     * As defined by {@link #ifNotExistsIncludingNullVars(Class, PentaJoiner)}.
     * For performance reasons, indexing joiners must be placed before filtering joiners.
     * <p>
     * This method causes <i>Unchecked generics array creation for varargs parameter</i> warnings,
     * but we can't fix it with a {@link SafeVarargs} annotation because it's an interface method.
     * Therefore, there are overloaded methods with up to 4 {@link PentaJoiner} parameters.
     *
     * @param <E> the type of the fifth matched fact
     * @param otherClass never null
     * @param joiners never null
     * @return never null, a stream that matches every tuple of A, B, C and D where E does not exist for which the
     *         {@link PentaJoiner}s are true
     */
    <E> QuadConstraintStream<A, B, C, D> ifNotExistsIncludingNullVars(Class<E> otherClass,
            PentaJoiner<A, B, C, D, E>... joiners);

    // ************************************************************************
    // Group by
    // ************************************************************************

    /**
     * Convert the {@link QuadConstraintStream} to a {@link UniConstraintStream}, containing only a single tuple, the
     * result of applying {@link QuadConstraintCollector}.
     * {@link UniConstraintStream} which only has a single tuple, the result of applying
     * {@link QuadConstraintCollector}.
     *
     * @param collector never null, the collector to perform the grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param <ResultContainer_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <Result_> the type of a fact in the destination {@link UniConstraintStream}'s tuple
     * @return never null
     */
    <ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(
            QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> collector);

    /**
     * Convert the {@link QuadConstraintStream} to a {@link BiConstraintStream}, containing only a single tuple,
     * the result of applying two {@link QuadConstraintCollector}s.
     *
     * @param collectorA never null, the collector to perform the first grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param collectorB never null, the collector to perform the second grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param <ResultContainerA_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultA_> the type of the first fact in the destination {@link BiConstraintStream}'s tuple
     * @param <ResultContainerB_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultB_> the type of the second fact in the destination {@link BiConstraintStream}'s tuple
     * @return never null
     */
    <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_> BiConstraintStream<ResultA_, ResultB_> groupBy(
            QuadConstraintCollector<A, B, C, D, ResultContainerA_, ResultA_> collectorA,
            QuadConstraintCollector<A, B, C, D, ResultContainerB_, ResultB_> collectorB);

    /**
     * Convert the {@link QuadConstraintStream} to a {@link TriConstraintStream}, containing only a single tuple,
     * the result of applying three {@link QuadConstraintCollector}s.
     *
     * @param collectorA never null, the collector to perform the first grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param collectorB never null, the collector to perform the second grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param collectorC never null, the collector to perform the third grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param <ResultContainerA_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultA_> the type of the first fact in the destination {@link TriConstraintStream}'s tuple
     * @param <ResultContainerB_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultB_> the type of the second fact in the destination {@link TriConstraintStream}'s tuple
     * @param <ResultContainerC_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultC_> the type of the third fact in the destination {@link TriConstraintStream}'s tuple
     * @return never null
     */
    <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<ResultA_, ResultB_, ResultC_> groupBy(
                    QuadConstraintCollector<A, B, C, D, ResultContainerA_, ResultA_> collectorA,
                    QuadConstraintCollector<A, B, C, D, ResultContainerB_, ResultB_> collectorB,
                    QuadConstraintCollector<A, B, C, D, ResultContainerC_, ResultC_> collectorC);

    /**
     * Convert the {@link QuadConstraintStream} to a {@link QuadConstraintStream}, containing only a single tuple,
     * the result of applying four {@link QuadConstraintCollector}s.
     *
     * @param collectorA never null, the collector to perform the first grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param collectorB never null, the collector to perform the second grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param collectorC never null, the collector to perform the third grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param collectorD never null, the collector to perform the fourth grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param <ResultContainerA_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultA_> the type of the first fact in the destination {@link QuadConstraintStream}'s tuple
     * @param <ResultContainerB_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultB_> the type of the second fact in the destination {@link QuadConstraintStream}'s tuple
     * @param <ResultContainerC_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultC_> the type of the third fact in the destination {@link QuadConstraintStream}'s tuple
     * @param <ResultContainerD_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultD_> the type of the fourth fact in the destination {@link QuadConstraintStream}'s tuple
     * @return never null
     */
    <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<ResultA_, ResultB_, ResultC_, ResultD_> groupBy(
                    QuadConstraintCollector<A, B, C, D, ResultContainerA_, ResultA_> collectorA,
                    QuadConstraintCollector<A, B, C, D, ResultContainerB_, ResultB_> collectorB,
                    QuadConstraintCollector<A, B, C, D, ResultContainerC_, ResultC_> collectorC,
                    QuadConstraintCollector<A, B, C, D, ResultContainerD_, ResultD_> collectorD);

    /**
     * Convert the {@link QuadConstraintStream} to a {@link UniConstraintStream}, containing the set of tuples resulting
     * from applying the group key mapping function on all tuples of the original stream.
     * Neither tuple of the new stream {@link Objects#equals(Object, Object)} any other.
     *
     * @param groupKeyMapping never null, mapping function to convert each element in the stream to a different element
     * @param <GroupKey_> the type of a fact in the destination {@link UniConstraintStream}'s tuple
     * @return never null
     */
    <GroupKey_> UniConstraintStream<GroupKey_> groupBy(QuadFunction<A, B, C, D, GroupKey_> groupKeyMapping);

    /**
     * Convert the {@link QuadConstraintStream} to a {@link BiConstraintStream}, consisting of unique tuples.
     * <p>
     * The first fact is the return value of the group key mapping function, applied on the incoming tuple.
     * The second fact is the return value of a given {@link QuadConstraintCollector} applied on all incoming tuples
     * with the same first fact.
     *
     * @param groupKeyMapping never null, function to convert the fact in the original tuple to a different fact
     * @param collector never null, the collector to perform the grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param <GroupKey_> the type of the first fact in the destination {@link BiConstraintStream}'s tuple
     * @param <ResultContainer_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <Result_> the type of the second fact in the destination {@link BiConstraintStream}'s tuple
     * @return never null
     */
    <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            QuadFunction<A, B, C, D, GroupKey_> groupKeyMapping,
            QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> collector);

    /**
     * Convert the {@link QuadConstraintStream} to a {@link TriConstraintStream}, consisting of unique tuples with three
     * facts.
     * <p>
     * The first fact is the return value of the group key mapping function, applied on the incoming tuple.
     * The remaining facts are the return value of the respective {@link QuadConstraintCollector} applied on all
     * incoming tuples with the same first fact.
     *
     * @param groupKeyMapping never null, function to convert the fact in the original tuple to a different fact
     * @param collectorB never null, the collector to perform the first grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param collectorC never null, the collector to perform the second grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param <GroupKey_> the type of the first fact in the destination {@link TriConstraintStream}'s tuple
     * @param <ResultContainerB_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultB_> the type of the second fact in the destination {@link TriConstraintStream}'s tuple
     * @param <ResultContainerC_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultC_> the type of the third fact in the destination {@link TriConstraintStream}'s tuple
     * @return never null
     */
    <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<GroupKey_, ResultB_, ResultC_> groupBy(
                    QuadFunction<A, B, C, D, GroupKey_> groupKeyMapping,
                    QuadConstraintCollector<A, B, C, D, ResultContainerB_, ResultB_> collectorB,
                    QuadConstraintCollector<A, B, C, D, ResultContainerC_, ResultC_> collectorC);

    /**
     * Convert the {@link QuadConstraintStream} to a {@link QuadConstraintStream}, consisting of unique tuples with four
     * facts.
     * <p>
     * The first fact is the return value of the group key mapping function, applied on the incoming tuple.
     * The remaining facts are the return value of the respective {@link QuadConstraintCollector} applied on all
     * incoming tuples with the same first fact.
     *
     * @param groupKeyMapping never null, function to convert the fact in the original tuple to a different fact
     * @param collectorB never null, the collector to perform the first grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param collectorC never null, the collector to perform the second grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param collectorD never null, the collector to perform the third grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param <GroupKey_> the type of the first fact in the destination {@link QuadConstraintStream}'s tuple
     * @param <ResultContainerB_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultB_> the type of the second fact in the destination {@link QuadConstraintStream}'s tuple
     * @param <ResultContainerC_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultC_> the type of the third fact in the destination {@link QuadConstraintStream}'s tuple
     * @param <ResultContainerD_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultD_> the type of the fourth fact in the destination {@link QuadConstraintStream}'s tuple
     * @return never null
     */
    <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKey_, ResultB_, ResultC_, ResultD_> groupBy(
                    QuadFunction<A, B, C, D, GroupKey_> groupKeyMapping,
                    QuadConstraintCollector<A, B, C, D, ResultContainerB_, ResultB_> collectorB,
                    QuadConstraintCollector<A, B, C, D, ResultContainerC_, ResultC_> collectorC,
                    QuadConstraintCollector<A, B, C, D, ResultContainerD_, ResultD_> collectorD);

    /**
     * Convert the {@link QuadConstraintStream} to a {@link BiConstraintStream}, consisting of unique tuples.
     * <p>
     * The first fact is the return value of the first group key mapping function, applied on the incoming tuple.
     * The second fact is the return value of the second group key mapping function, applied on all incoming tuples with
     * the same first fact.
     *
     * @param groupKeyAMapping never null, function to convert the facts in the original tuple to a new fact
     * @param groupKeyBMapping never null, function to convert the facts in the original tuple to another new fact
     * @param <GroupKeyA_> the type of the first fact in the destination {@link BiConstraintStream}'s tuple
     * @param <GroupKeyB_> the type of the second fact in the destination {@link BiConstraintStream}'s tuple
     * @return never null
     */
    <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
            QuadFunction<A, B, C, D, GroupKeyA_> groupKeyAMapping, QuadFunction<A, B, C, D, GroupKeyB_> groupKeyBMapping);

    /**
     * Combines the semantics of {@link #groupBy(QuadFunction, QuadFunction)} and
     * {@link #groupBy(QuadConstraintCollector)}.
     * That is, the first and second facts in the tuple follow the {@link #groupBy(QuadFunction, QuadFunction)}
     * semantics,
     * and the third fact is the result of applying {@link QuadConstraintCollector#finisher()} on all the tuples of the
     * original {@link UniConstraintStream} that belong to the group.
     *
     * @param groupKeyAMapping never null, function to convert the original tuple into a first fact
     * @param groupKeyBMapping never null, function to convert the original tuple into a second fact
     * @param collector never null, the collector to perform the grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param <GroupKeyA_> the type of the first fact in the destination {@link TriConstraintStream}'s tuple
     * @param <GroupKeyB_> the type of the second fact in the destination {@link TriConstraintStream}'s tuple
     * @param <ResultContainer_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <Result_> the type of the third fact in the destination {@link TriConstraintStream}'s tuple
     * @return never null
     */
    <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            QuadFunction<A, B, C, D, GroupKeyA_> groupKeyAMapping, QuadFunction<A, B, C, D, GroupKeyB_> groupKeyBMapping,
            QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> collector);

    /**
     * Combines the semantics of {@link #groupBy(QuadFunction, QuadFunction)} and
     * {@link #groupBy(QuadConstraintCollector)}.
     * That is, the first and second facts in the tuple follow the {@link #groupBy(QuadFunction, QuadFunction)}
     * semantics.
     * The third fact is the result of applying the first {@link QuadConstraintCollector#finisher()} on all the tuples
     * of the original {@link QuadConstraintStream} that belong to the group.
     * The fourth fact is the result of applying the second {@link QuadConstraintCollector#finisher()} on all the tuples
     * of the original {@link QuadConstraintStream} that belong to the group
     *
     * @param groupKeyAMapping never null, function to convert the original tuple into a first fact
     * @param groupKeyBMapping never null, function to convert the original tuple into a second fact
     * @param collectorC never null, the collector to perform the first grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param collectorD never null, the collector to perform the second grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param <GroupKeyA_> the type of the first fact in the destination {@link QuadConstraintStream}'s tuple
     * @param <GroupKeyB_> the type of the second fact in the destination {@link QuadConstraintStream}'s tuple
     * @param <ResultContainerC_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultC_> the type of the third fact in the destination {@link QuadConstraintStream}'s tuple
     * @param <ResultContainerD_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultD_> the type of the fourth fact in the destination {@link QuadConstraintStream}'s tuple
     * @return never null
     */
    <GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_> groupBy(
                    QuadFunction<A, B, C, D, GroupKeyA_> groupKeyAMapping,
                    QuadFunction<A, B, C, D, GroupKeyB_> groupKeyBMapping,
                    QuadConstraintCollector<A, B, C, D, ResultContainerC_, ResultC_> collectorC,
                    QuadConstraintCollector<A, B, C, D, ResultContainerD_, ResultD_> collectorD);

    /**
     * Convert the {@link QuadConstraintStream} to a {@link TriConstraintStream}, consisting of unique tuples with three
     * facts.
     * <p>
     * The first fact is the return value of the first group key mapping function, applied on the incoming tuple.
     * The second fact is the return value of the second group key mapping function, applied on all incoming tuples with
     * the same first fact.
     * The third fact is the return value of the third group key mapping function, applied on all incoming tuples with
     * the same first fact.
     *
     * @param groupKeyAMapping never null, function to convert the original tuple into a first fact
     * @param groupKeyBMapping never null, function to convert the original tuple into a second fact
     * @param groupKeyCMapping never null, function to convert the original tuple into a third fact
     * @param <GroupKeyA_> the type of the first fact in the destination {@link TriConstraintStream}'s tuple
     * @param <GroupKeyB_> the type of the second fact in the destination {@link TriConstraintStream}'s tuple
     * @param <GroupKeyC_> the type of the third fact in the destination {@link TriConstraintStream}'s tuple
     * @return never null
     */
    <GroupKeyA_, GroupKeyB_, GroupKeyC_> TriConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_> groupBy(
            QuadFunction<A, B, C, D, GroupKeyA_> groupKeyAMapping,
            QuadFunction<A, B, C, D, GroupKeyB_> groupKeyBMapping,
            QuadFunction<A, B, C, D, GroupKeyC_> groupKeyCMapping);

    /**
     * Combines the semantics of {@link #groupBy(QuadFunction, QuadFunction)} and {@link #groupBy(QuadConstraintCollector)}.
     * That is, the first three facts in the tuple follow the {@link #groupBy(QuadFunction, QuadFunction)} semantics.
     * The final fact is the result of applying the first {@link QuadConstraintCollector#finisher()} on all the tuples
     * of the original {@link QuadConstraintStream} that belong to the group.
     *
     * @param groupKeyAMapping never null, function to convert the original tuple into a first fact
     * @param groupKeyBMapping never null, function to convert the original tuple into a second fact
     * @param groupKeyCMapping never null, function to convert the original tuple into a third fact
     * @param collectorD never null, the collector to perform the grouping operation with
     *        See {@link ConstraintCollectors} for common operations, such as {@code count()}, {@code sum()} and others.
     * @param <GroupKeyA_> the type of the first fact in the destination {@link QuadConstraintStream}'s tuple
     * @param <GroupKeyB_> the type of the second fact in the destination {@link QuadConstraintStream}'s tuple
     * @param <GroupKeyC_> the type of the third fact in the destination {@link QuadConstraintStream}'s tuple
     * @param <ResultContainerD_> the mutable accumulation type (often hidden as an implementation detail)
     * @param <ResultD_> the type of the fourth fact in the destination {@link QuadConstraintStream}'s tuple
     * @return never null
     */
    <GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_> groupBy(
                    QuadFunction<A, B, C, D, GroupKeyA_> groupKeyAMapping,
                    QuadFunction<A, B, C, D, GroupKeyB_> groupKeyBMapping,
                    QuadFunction<A, B, C, D, GroupKeyC_> groupKeyCMapping,
                    QuadConstraintCollector<A, B, C, D, ResultContainerD_, ResultD_> collectorD);

    /**
     * Convert the {@link TriConstraintStream} to a {@link QuadConstraintStream}, consisting of unique tuples with four
     * facts.
     * <p>
     * The first fact is the return value of the first group key mapping function, applied on the incoming tuple.
     * The second fact is the return value of the second group key mapping function, applied on all incoming tuples with
     * the same first fact.
     * The third fact is the return value of the third group key mapping function, applied on all incoming tuples with
     * the same first fact.
     * The fourth fact is the return value of the fourth group key mapping function, applied on all incoming tuples with
     * the same first fact.
     *
     * @param groupKeyAMapping never null, function to convert the original tuple into a first fact
     * @param groupKeyBMapping never null, function to convert the original tuple into a second fact
     * @param groupKeyCMapping never null, function to convert the original tuple into a third fact
     * @param groupKeyDMapping never null, function to convert the original tuple into a fourth fact
     * @param <GroupKeyA_> the type of the first fact in the destination {@link QuadConstraintStream}'s tuple
     * @param <GroupKeyB_> the type of the second fact in the destination {@link QuadConstraintStream}'s tuple
     * @param <GroupKeyC_> the type of the third fact in the destination {@link QuadConstraintStream}'s tuple
     * @param <GroupKeyD_> the type of the fourth fact in the destination {@link QuadConstraintStream}'s tuple
     * @return never null
     */
    <GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_> groupBy(
                    QuadFunction<A, B, C, D, GroupKeyA_> groupKeyAMapping,
                    QuadFunction<A, B, C, D, GroupKeyB_> groupKeyBMapping,
                    QuadFunction<A, B, C, D, GroupKeyC_> groupKeyCMapping,
                    QuadFunction<A, B, C, D, GroupKeyD_> groupKeyDMapping);

    // ************************************************************************
    // Operations with duplicate tuple possibility
    // ************************************************************************

    /**
     * As defined by {@link UniConstraintStream#map(Function)}.
     *
     * @param mapping never null, function to convert the original tuple into the new tuple
     * @param <ResultA_> the type of the only fact in the resulting {@link UniConstraintStream}'s tuple
     * @return never null
     */
    <ResultA_> UniConstraintStream<ResultA_> map(QuadFunction<A, B, C, D, ResultA_> mapping);

    /**
     * As defined by {@link BiConstraintStream#flattenLast(Function)}.
     *
     * @param <ResultD_> the type of the last fact in the resulting tuples.
     *        It is recommended that this type be deeply immutable.
     *        Not following this recommendation may lead to hard-to-debug hashing issues down the stream,
     *        especially if this value is ever used as a group key.
     * @param mapping never null, function to convert the last fact in the original tuple into {@link Iterable}
     * @return never null
     */
    <ResultD_> QuadConstraintStream<A, B, C, ResultD_> flattenLast(Function<D, Iterable<ResultD_>> mapping);

    /**
     * Transforms the stream in such a way that all the tuples going through it are distinct.
     * (No two tuples will {@link Object#equals(Object) equal}.)
     *
     * <p>
     * By default, tuples going through a constraint stream are distinct.
     * However, operations such as {@link #map(QuadFunction)} may create a stream which breaks that promise.
     * By calling this method on such a stream,
     * duplicate copies of the same tuple will be omitted at a performance cost.
     *
     * @return never null
     */
    QuadConstraintStream<A, B, C, D> distinct();

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    /**
     * As defined by {@link #penalize(Score, ToIntQuadFunction)}, where the match weight is one (1).
     *
     * @return never null
     */
    default QuadConstraintBuilder<A, B, C, D> penalize(Score<?> constraintWeight) {
        return penalize(constraintWeight, (a, b, c, d) -> 1);
    }

    /**
     * Applies a negative {@link Score} impact,
     * subtracting the constraintWeight multiplied by the match weight,
     * and returns a builder to apply optional constraint properties.
     * <p>
     * For non-int {@link Score} types use {@link #penalizeLong(Score, ToLongQuadFunction)} or
     * {@link #penalizeBigDecimal(Score, QuadFunction)} instead.
     *
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    QuadConstraintBuilder<A, B, C, D> penalize(Score<?> constraintWeight, ToIntQuadFunction<A, B, C, D> matchWeigher);

    /**
     * As defined by {@link #penalize(Score, ToIntQuadFunction)}, with a penalty of type long.
     */
    QuadConstraintBuilder<A, B, C, D> penalizeLong(Score<?> constraintWeight, ToLongQuadFunction<A, B, C, D> matchWeigher);

    /**
     * As defined by {@link #penalize(Score, ToIntQuadFunction)}, with a penalty of type {@link BigDecimal}.
     */
    QuadConstraintBuilder<A, B, C, D> penalizeBigDecimal(Score<?> constraintWeight,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher);

    /**
     * Negatively impacts the {@link Score},
     * subtracting the {@link ConstraintWeight} for each match,
     * and returns a builder to apply optional constraint properties.
     * <p>
     * The constraintWeight comes from an {@link ConstraintWeight} annotated member on the {@link ConstraintConfiguration},
     * so end users can change the constraint weights dynamically.
     * This constraint may be deactivated if the {@link ConstraintWeight} is zero.
     * If there is no {@link ConstraintConfiguration}, use {@link #penalize(Score)} instead.
     *
     * @return never null
     */
    default QuadConstraintBuilder<A, B, C, D> penalizeConfigurable() {
        return penalizeConfigurable((a, b, c, d) -> 1);
    }

    /**
     * Negatively impacts the {@link Score},
     * subtracting the {@link ConstraintWeight} multiplied by match weight for each match,
     * and returns a builder to apply optional constraint properties.
     * <p>
     * The constraintWeight comes from an {@link ConstraintWeight} annotated member on the {@link ConstraintConfiguration},
     * so end users can change the constraint weights dynamically.
     * This constraint may be deactivated if the {@link ConstraintWeight} is zero.
     * If there is no {@link ConstraintConfiguration}, use {@link #penalize(Score, ToIntQuadFunction)} instead.
     *
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    QuadConstraintBuilder<A, B, C, D> penalizeConfigurable(ToIntQuadFunction<A, B, C, D> matchWeigher);

    /**
     * As defined by {@link #penalizeConfigurable(ToIntQuadFunction)}, with a penalty of type long.
     * <p>
     * If there is no {@link ConstraintConfiguration}, use {@link #penalizeLong(Score, ToLongQuadFunction)} instead.
     */
    QuadConstraintBuilder<A, B, C, D> penalizeConfigurableLong(ToLongQuadFunction<A, B, C, D> matchWeigher);

    /**
     * As defined by {@link #penalizeConfigurable(ToIntQuadFunction)}, with a penalty of type {@link BigDecimal}.
     * <p>
     * If there is no {@link ConstraintConfiguration}, use {@link #penalizeBigDecimal(Score, QuadFunction)} instead.
     */
    QuadConstraintBuilder<A, B, C, D> penalizeConfigurableBigDecimal(QuadFunction<A, B, C, D, BigDecimal> matchWeigher);

    /**
     * As defined by {@link #reward(Score, ToIntQuadFunction)}, where the match weight is one (1).
     *
     * @return never null
     */
    default QuadConstraintBuilder<A, B, C, D> reward(Score<?> constraintWeight) {
        return reward(constraintWeight, (a, b, c, d) -> 1);
    }

    /**
     * Applies a positive {@link Score} impact,
     * adding the constraintWeight multiplied by the match weight,
     * and returns a builder to apply optional constraint properties.
     * <p>
     * For non-int {@link Score} types use {@link #rewardLong(Score, ToLongQuadFunction)} or
     * {@link #rewardBigDecimal(Score, QuadFunction)} instead.
     *
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    QuadConstraintBuilder<A, B, C, D> reward(Score<?> constraintWeight, ToIntQuadFunction<A, B, C, D> matchWeigher);

    /**
     * As defined by {@link #reward(Score, ToIntQuadFunction)}, with a penalty of type long.
     */
    QuadConstraintBuilder<A, B, C, D> rewardLong(Score<?> constraintWeight, ToLongQuadFunction<A, B, C, D> matchWeigher);

    /**
     * As defined by {@link #reward(Score, ToIntQuadFunction)}, with a penalty of type {@link BigDecimal}.
     */
    QuadConstraintBuilder<A, B, C, D> rewardBigDecimal(Score<?> constraintWeight,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher);

    /**
     * Positively impacts the {@link Score},
     * adding the {@link ConstraintWeight} for each match,
     * and returns a builder to apply optional constraint properties.
     * <p>
     * The constraintWeight comes from an {@link ConstraintWeight} annotated member on the {@link ConstraintConfiguration},
     * so end users can change the constraint weights dynamically.
     * This constraint may be deactivated if the {@link ConstraintWeight} is zero.
     * If there is no {@link ConstraintConfiguration}, use {@link #reward(Score)} instead.
     *
     * @return never null
     */
    default QuadConstraintBuilder<A, B, C, D> rewardConfigurable() {
        return rewardConfigurable((a, b, c, d) -> 1);
    }

    /**
     * Positively impacts the {@link Score},
     * adding the {@link ConstraintWeight} multiplied by match weight for each match,
     * and returns a builder to apply optional constraint properties.
     * <p>
     * The constraintWeight comes from an {@link ConstraintWeight} annotated member on the {@link ConstraintConfiguration},
     * so end users can change the constraint weights dynamically.
     * This constraint may be deactivated if the {@link ConstraintWeight} is zero.
     * If there is no {@link ConstraintConfiguration}, use {@link #reward(Score, ToIntQuadFunction)} instead.
     *
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    QuadConstraintBuilder<A, B, C, D> rewardConfigurable(ToIntQuadFunction<A, B, C, D> matchWeigher);

    /**
     * As defined by {@link #rewardConfigurable(ToIntQuadFunction)}, with a penalty of type long.
     * <p>
     * If there is no {@link ConstraintConfiguration}, use {@link #rewardLong(Score, ToLongQuadFunction)} instead.
     */
    QuadConstraintBuilder<A, B, C, D> rewardConfigurableLong(ToLongQuadFunction<A, B, C, D> matchWeigher);

    /**
     * As defined by {@link #rewardConfigurable(ToIntQuadFunction)}, with a penalty of type {@link BigDecimal}.
     * <p>
     * If there is no {@link ConstraintConfiguration}, use {@link #rewardBigDecimal(Score, QuadFunction)} instead.
     */
    QuadConstraintBuilder<A, B, C, D> rewardConfigurableBigDecimal(QuadFunction<A, B, C, D, BigDecimal> matchWeigher);

    /**
     * Positively or negatively impacts the {@link Score} by the constraintWeight for each match
     * and returns a builder to apply optional constraint properties.
     * <p>
     * Use {@code penalize(...)} or {@code reward(...)} instead, unless this constraint can both have positive and
     * negative weights.
     *
     * @param constraintWeight never null
     * @return never null
     */
    default QuadConstraintBuilder<A, B, C, D> impact(Score<?> constraintWeight) {
        return impact(constraintWeight, (a, b, c, d) -> 1);
    }

    /**
     * Positively or negatively impacts the {@link Score} by constraintWeight multiplied by matchWeight for each match
     * and returns a builder to apply optional constraint properties.
     * <p>
     * Use {@code penalize(...)} or {@code reward(...)} instead, unless this constraint can both have positive and
     * negative weights.
     *
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    QuadConstraintBuilder<A, B, C, D> impact(Score<?> constraintWeight, ToIntQuadFunction<A, B, C, D> matchWeigher);

    /**
     * As defined by {@link #impact(Score, ToIntQuadFunction)}, with an impact of type long.
     */
    QuadConstraintBuilder<A, B, C, D> impactLong(Score<?> constraintWeight, ToLongQuadFunction<A, B, C, D> matchWeigher);

    /**
     * As defined by {@link #impact(Score, ToIntQuadFunction)}, with an impact of type {@link BigDecimal}.
     */
    QuadConstraintBuilder<A, B, C, D> impactBigDecimal(Score<?> constraintWeight,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher);

    /**
     * Positively impacts the {@link Score} by the {@link ConstraintWeight} for each match,
     * and returns a builder to apply optional constraint properties.
     * <p>
     * The constraintWeight comes from an {@link ConstraintWeight} annotated member on the {@link ConstraintConfiguration},
     * so end users can change the constraint weights dynamically.
     * This constraint may be deactivated if the {@link ConstraintWeight} is zero.
     * If there is no {@link ConstraintConfiguration}, use {@link #impact(Score)} instead.
     *
     * @return never null
     */
    default QuadConstraintBuilder<A, B, C, D> impactConfigurable() {
        return impactConfigurable((a, b, c, d) -> 1);
    }

    /**
     * Positively impacts the {@link Score} by the {@link ConstraintWeight} multiplied by match weight for each match,
     * and returns a builder to apply optional constraint properties.
     * <p>
     * The constraintWeight comes from an {@link ConstraintWeight} annotated member on the {@link ConstraintConfiguration},
     * so end users can change the constraint weights dynamically.
     * This constraint may be deactivated if the {@link ConstraintWeight} is zero.
     * If there is no {@link ConstraintConfiguration}, use {@link #impact(Score, ToIntQuadFunction)} instead.
     *
     * @return never null
     */
    QuadConstraintBuilder<A, B, C, D> impactConfigurable(ToIntQuadFunction<A, B, C, D> matchWeigher);

    /**
     * As defined by {@link #impactConfigurable(ToIntQuadFunction)}, with an impact of type long.
     * <p>
     * If there is no {@link ConstraintConfiguration}, use {@link #impactLong(Score, ToLongQuadFunction)} instead.
     */
    QuadConstraintBuilder<A, B, C, D> impactConfigurableLong(ToLongQuadFunction<A, B, C, D> matchWeigher);

    /**
     * As defined by {@link #impactConfigurable(ToIntQuadFunction)}, with an impact of type BigDecimal.
     * <p>
     * If there is no {@link ConstraintConfiguration}, use {@link #impactBigDecimal(Score, QuadFunction)} instead.
     */
    QuadConstraintBuilder<A, B, C, D> impactConfigurableBigDecimal(QuadFunction<A, B, C, D, BigDecimal> matchWeigher);

    /**
     * Negatively impact the {@link Score}: subtract the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #penalize(String, Score)}.
     * <p>
     * For non-int {@link Score} types use {@link #penalizeLong(String, Score, ToLongQuadFunction)} or
     * {@link #penalizeBigDecimal(String, Score, QuadFunction)} instead.
     *
     * @deprecated Prefer {@link #penalize(Score, ToIntQuadFunction)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint penalize(String constraintName, Score<?> constraintWeight, ToIntQuadFunction<A, B, C, D> matchWeigher) {
        return penalize(constraintWeight, matchWeigher)
                .asConstraint(constraintName);
    }

    /**
     * As defined by {@link #penalize(String, Score, ToIntQuadFunction)}.
     *
     * @deprecated Prefer {@link #penalize(Score, ToIntQuadFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint penalize(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntQuadFunction<A, B, C, D> matchWeigher) {
        return penalize(constraintWeight, matchWeigher)
                .asConstraint(constraintPackage, constraintName);
    }

    /**
     * Negatively impact the {@link Score}: subtract the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #penalize(String, Score)}.
     *
     * @deprecated Prefer {@link #penalizeLong(Score, ToLongQuadFunction)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint penalizeLong(String constraintName, Score<?> constraintWeight,
            ToLongQuadFunction<A, B, C, D> matchWeigher) {
        return penalizeLong(constraintWeight, matchWeigher)
                .asConstraint(constraintName);
    }

    /**
     * As defined by {@link #penalizeLong(String, Score, ToLongQuadFunction)}.
     *
     * @deprecated Prefer {@link #penalizeLong(Score, ToLongQuadFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint penalizeLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongQuadFunction<A, B, C, D> matchWeigher) {
        return penalizeLong(constraintWeight, matchWeigher)
                .asConstraint(constraintPackage, constraintName);
    }

    /**
     * Negatively impact the {@link Score}: subtract the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #penalize(String, Score)}.
     *
     * @deprecated Prefer {@link #penalizeBigDecimal(Score, QuadFunction)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint penalizeBigDecimal(String constraintName, Score<?> constraintWeight,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher) {
        return penalizeBigDecimal(constraintWeight, matchWeigher)
                .asConstraint(constraintName);
    }

    /**
     * As defined by {@link #penalizeBigDecimal(String, Score, QuadFunction)}.
     *
     * @deprecated Prefer {@link #penalizeBigDecimal(Score, QuadFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint penalizeBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher) {
        return penalizeBigDecimal(constraintWeight, matchWeigher)
                .asConstraint(constraintPackage, constraintName);
    }

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #penalizeConfigurable(String)}.
     * <p>
     * For non-int {@link Score} types use {@link #penalizeConfigurableLong(String, ToLongQuadFunction)} or
     * {@link #penalizeConfigurableBigDecimal(String, QuadFunction)} instead.
     *
     * @deprecated Prefer {@link #penalizeConfigurable(ToIntQuadFunction)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint penalizeConfigurable(String constraintName, ToIntQuadFunction<A, B, C, D> matchWeigher) {
        return penalizeConfigurable(matchWeigher)
                .asConstraint(constraintName);
    }

    /**
     * As defined by {@link #penalizeConfigurable(String, ToIntQuadFunction)}.
     *
     * @deprecated Prefer {@link #penalizeConfigurable(ToIntQuadFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint penalizeConfigurable(String constraintPackage, String constraintName,
            ToIntQuadFunction<A, B, C, D> matchWeigher) {
        return penalizeConfigurable(matchWeigher)
                .asConstraint(constraintPackage, constraintName);
    }

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #penalizeConfigurable(String)}.
     *
     * @deprecated Prefer {@link #penalizeConfigurableLong(ToLongQuadFunction)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint penalizeConfigurableLong(String constraintName, ToLongQuadFunction<A, B, C, D> matchWeigher) {
        return penalizeConfigurableLong(matchWeigher)
                .asConstraint(constraintName);
    }

    /**
     * As defined by {@link #penalizeConfigurableLong(String, ToLongQuadFunction)}.
     *
     * @deprecated Prefer {@link #penalizeConfigurableLong(ToLongQuadFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint penalizeConfigurableLong(String constraintPackage, String constraintName,
            ToLongQuadFunction<A, B, C, D> matchWeigher) {
        return penalizeConfigurableLong(matchWeigher)
                .asConstraint(constraintPackage, constraintName);
    }

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #penalizeConfigurable(String)}.
     *
     * @deprecated Prefer {@link #penalizeConfigurableBigDecimal(QuadFunction)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint penalizeConfigurableBigDecimal(String constraintName,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher) {
        return penalizeConfigurableBigDecimal(matchWeigher)
                .asConstraint(constraintName);
    }

    /**
     * As defined by {@link #penalizeConfigurableBigDecimal(String, QuadFunction)}.
     *
     * @deprecated Prefer {@link #penalizeConfigurableBigDecimal(QuadFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint penalizeConfigurableBigDecimal(String constraintPackage, String constraintName,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher) {
        return penalizeConfigurableBigDecimal(matchWeigher)
                .asConstraint(constraintPackage, constraintName);
    }

    /**
     * Positively impact the {@link Score}: add the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #reward(String, Score)}.
     * <p>
     * For non-int {@link Score} types use {@link #rewardLong(String, Score, ToLongQuadFunction)} or
     * {@link #rewardBigDecimal(String, Score, QuadFunction)} instead.
     *
     * @deprecated Prefer {@link #reward(Score, ToIntQuadFunction)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint reward(String constraintName, Score<?> constraintWeight,
            ToIntQuadFunction<A, B, C, D> matchWeigher) {
        return reward(constraintWeight, matchWeigher)
                .asConstraint(constraintName);
    }

    /**
     * As defined by {@link #reward(String, Score, ToIntQuadFunction)}.
     *
     * @deprecated Prefer {@link #reward(Score, ToIntQuadFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint reward(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntQuadFunction<A, B, C, D> matchWeigher) {
        return reward(constraintWeight, matchWeigher)
                .asConstraint(constraintPackage, constraintName);
    }

    /**
     * Positively impact the {@link Score}: add the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #reward(String, Score)}.
     *
     * @deprecated Prefer {@link #rewardLong(Score, ToLongQuadFunction)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint rewardLong(String constraintName, Score<?> constraintWeight,
            ToLongQuadFunction<A, B, C, D> matchWeigher) {
        return rewardLong(constraintWeight, matchWeigher)
                .asConstraint(constraintName);
    }

    /**
     * As defined by {@link #rewardLong(String, Score, ToLongQuadFunction)}.
     *
     * @deprecated Prefer {@link #rewardLong(Score, ToLongQuadFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint rewardLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongQuadFunction<A, B, C, D> matchWeigher) {
        return rewardLong(constraintWeight, matchWeigher)
                .asConstraint(constraintPackage, constraintName);
    }

    /**
     * Positively impact the {@link Score}: add the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #reward(String, Score)}.
     *
     * @deprecated Prefer {@link #rewardBigDecimal(Score, QuadFunction)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint rewardBigDecimal(String constraintName, Score<?> constraintWeight,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher) {
        return rewardBigDecimal(constraintWeight, matchWeigher)
                .asConstraint(constraintName);
    }

    /**
     * As defined by {@link #rewardBigDecimal(String, Score, QuadFunction)}.
     *
     * @deprecated Prefer {@link #rewardBigDecimal(Score, QuadFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint rewardBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher) {
        return rewardBigDecimal(constraintWeight, matchWeigher)
                .asConstraint(constraintPackage, constraintName);
    }

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #rewardConfigurable(String)}.
     * <p>
     * For non-int {@link Score} types use {@link #rewardConfigurableLong(String, ToLongQuadFunction)} or
     * {@link #rewardConfigurableBigDecimal(String, QuadFunction)} instead.
     *
     * @deprecated Prefer {@link #rewardConfigurable(ToIntQuadFunction)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint rewardConfigurable(String constraintName, ToIntQuadFunction<A, B, C, D> matchWeigher) {
        return rewardConfigurable(matchWeigher)
                .asConstraint(constraintName);
    }

    /**
     * As defined by {@link #rewardConfigurable(String, ToIntQuadFunction)}.
     *
     * @deprecated Prefer {@link #rewardConfigurable(ToIntQuadFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint rewardConfigurable(String constraintPackage, String constraintName,
            ToIntQuadFunction<A, B, C, D> matchWeigher) {
        return rewardConfigurable(matchWeigher)
                .asConstraint(constraintPackage, constraintName);
    }

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #rewardConfigurable(String)}.
     *
     * @deprecated Prefer {@link #rewardConfigurableLong(ToLongQuadFunction)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint rewardConfigurableLong(String constraintName, ToLongQuadFunction<A, B, C, D> matchWeigher) {
        return rewardConfigurableLong(matchWeigher)
                .asConstraint(constraintName);
    }

    /**
     * As defined by {@link #rewardConfigurableLong(String, ToLongQuadFunction)}.
     *
     * @deprecated Prefer {@link #rewardConfigurableLong(ToLongQuadFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint rewardConfigurableLong(String constraintPackage, String constraintName,
            ToLongQuadFunction<A, B, C, D> matchWeigher) {
        return rewardConfigurableLong(matchWeigher)
                .asConstraint(constraintPackage, constraintName);
    }

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
     * Otherwise as defined by {@link #rewardConfigurable(String)}.
     *
     * @deprecated Prefer {@link #rewardConfigurableBigDecimal(QuadFunction)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint rewardConfigurableBigDecimal(String constraintName,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher) {
        return rewardConfigurableBigDecimal(matchWeigher)
                .asConstraint(constraintName);
    }

    /**
     * As defined by {@link #rewardConfigurableBigDecimal(String, QuadFunction)}.
     *
     * @deprecated Prefer {@link #rewardConfigurableBigDecimal(QuadFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint rewardConfigurableBigDecimal(String constraintPackage, String constraintName,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher) {
        return rewardConfigurableBigDecimal(matchWeigher)
                .asConstraint(constraintPackage, constraintName);
    }

    /**
     * Positively or negatively impact the {@link Score} by the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #impact(String, Score)}.
     * <p>
     * Use {@code penalize(...)} or {@code reward(...)} instead, unless this constraint can both have positive and
     * negative weights.
     * <p>
     * For non-int {@link Score} types use {@link #impactLong(String, Score, ToLongQuadFunction)} or
     * {@link #impactBigDecimal(String, Score, QuadFunction)} instead.
     *
     * @deprecated Prefer {@link #impact(Score, ToIntQuadFunction)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint impact(String constraintName, Score<?> constraintWeight,
            ToIntQuadFunction<A, B, C, D> matchWeigher) {
        return impact(constraintWeight, matchWeigher)
                .asConstraint(constraintName);
    }

    /**
     * As defined by {@link #impact(String, Score, ToIntQuadFunction)}.
     *
     * @deprecated Prefer {@link #impact(Score, ToIntQuadFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint impact(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntQuadFunction<A, B, C, D> matchWeigher) {
        return impact(constraintWeight, matchWeigher)
                .asConstraint(constraintPackage, constraintName);
    }

    /**
     * Positively or negatively impact the {@link Score} by the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #impact(String, Score)}.
     * <p>
     * Use {@code penalizeLong(...)} or {@code rewardLong(...)} instead, unless this constraint can both have positive
     * and negative weights.
     *
     * @deprecated Prefer {@link #impactLong(Score, ToLongQuadFunction)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint impactLong(String constraintName, Score<?> constraintWeight,
            ToLongQuadFunction<A, B, C, D> matchWeigher) {
        return impactLong(constraintWeight, matchWeigher)
                .asConstraint(constraintName);
    }

    /**
     * As defined by {@link #impactLong(String, Score, ToLongQuadFunction)}.
     *
     * @deprecated Prefer {@link #impactLong(Score, ToLongQuadFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint impactLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongQuadFunction<A, B, C, D> matchWeigher) {
        return impactLong(constraintWeight, matchWeigher)
                .asConstraint(constraintPackage, constraintName);
    }

    /**
     * Positively or negatively impact the {@link Score} by the constraintWeight multiplied by the match weight.
     * Otherwise as defined by {@link #impact(String, Score)}.
     * <p>
     * Use {@code penalizeBigDecimal(...)} or {@code rewardBigDecimal(...)} instead, unless this constraint can both
     * have positive and negative weights.
     *
     * @deprecated Prefer {@link #impactBigDecimal(Score, QuadFunction)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint impactBigDecimal(String constraintName, Score<?> constraintWeight,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher) {
        return impactBigDecimal(constraintWeight, matchWeigher)
                .asConstraint(constraintName);
    }

    /**
     * As defined by {@link #impactBigDecimal(String, Score, QuadFunction)}.
     *
     * @deprecated Prefer {@link #impactBigDecimal(Score, QuadFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @param matchWeigher never null
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint impactBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher) {
        return impactBigDecimal(constraintWeight, matchWeigher)
                .asConstraint(constraintPackage, constraintName);
    }

    /**
     * Positively or negatively impact the {@link Score} by the {@link ConstraintWeight} for each match.
     * <p>
     * Use {@code penalizeConfigurable(...)} or {@code rewardConfigurable(...)} instead, unless this constraint can both
     * have positive and negative weights.
     * <p>
     * For non-int {@link Score} types use {@link #impactConfigurableLong(String, ToLongQuadFunction)} or
     * {@link #impactConfigurableBigDecimal(String, QuadFunction)} instead.
     * <p>
     * The constraintWeight comes from an {@link ConstraintWeight} annotated member on the
     * {@link ConstraintConfiguration}, so end users can change the constraint weights dynamically.
     * This constraint may be deactivated if the {@link ConstraintWeight} is zero.
     * If there is no {@link ConstraintConfiguration}, use {@link #impact(String, Score)} instead.
     * <p>
     * The {@link Constraint#getConstraintPackage()} defaults to {@link ConstraintConfiguration#constraintPackage()}.
     *
     * @deprecated Prefer {@link #impactConfigurable(ToIntQuadFunction)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint impactConfigurable(String constraintName, ToIntQuadFunction<A, B, C, D> matchWeigher) {
        return impactConfigurable(matchWeigher)
                .asConstraint(constraintName);
    }

    /**
     * As defined by {@link #impactConfigurable(String, ToIntQuadFunction)}.
     *
     * @deprecated Prefer {@link #impactConfigurable(ToIntQuadFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint impactConfigurable(String constraintPackage, String constraintName,
            ToIntQuadFunction<A, B, C, D> matchWeigher) {
        return impactConfigurable(matchWeigher)
                .asConstraint(constraintPackage, constraintName);
    }

    /**
     * Positively or negatively impact the {@link Score} by the {@link ConstraintWeight} for each match.
     * <p>
     * Use {@code penalizeConfigurableLong(...)} or {@code rewardConfigurableLong(...)} instead, unless this constraint
     * can both have positive and negative weights.
     * <p>
     * The constraintWeight comes from an {@link ConstraintWeight} annotated member on the
     * {@link ConstraintConfiguration}, so end users can change the constraint weights dynamically.
     * This constraint may be deactivated if the {@link ConstraintWeight} is zero.
     * If there is no {@link ConstraintConfiguration}, use {@link #impact(String, Score)} instead.
     * <p>
     * The {@link Constraint#getConstraintPackage()} defaults to {@link ConstraintConfiguration#constraintPackage()}.
     *
     * @deprecated Prefer {@link #impactConfigurableLong(ToLongQuadFunction)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint impactConfigurableLong(String constraintName, ToLongQuadFunction<A, B, C, D> matchWeigher) {
        return impactConfigurableLong(matchWeigher)
                .asConstraint(constraintName);
    }

    /**
     * As defined by {@link #impactConfigurableLong(String, ToLongQuadFunction)}.
     *
     * @deprecated Prefer {@link #impactConfigurableLong(ToLongQuadFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint impactConfigurableLong(String constraintPackage, String constraintName,
            ToLongQuadFunction<A, B, C, D> matchWeigher) {
        return impactConfigurableLong(matchWeigher)
                .asConstraint(constraintPackage, constraintName);
    }

    /**
     * Positively or negatively impact the {@link Score} by the {@link ConstraintWeight} for each match.
     * <p>
     * Use {@code penalizeConfigurableBigDecimal(...)} or {@code rewardConfigurableBigDecimal(...)} instead, unless this
     * constraint can both have positive and negative weights.
     * <p>
     * The constraintWeight comes from an {@link ConstraintWeight} annotated member on the
     * {@link ConstraintConfiguration}, so end users can change the constraint weights dynamically.
     * This constraint may be deactivated if the {@link ConstraintWeight} is zero.
     * If there is no {@link ConstraintConfiguration}, use {@link #impact(String, Score)} instead.
     * <p>
     * The {@link Constraint#getConstraintPackage()} defaults to {@link ConstraintConfiguration#constraintPackage()}.
     *
     * @deprecated Prefer {@link #impactConfigurableBigDecimal(QuadFunction)}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param matchWeigher never null, the result of this function (matchWeight) is multiplied by the constraintWeight
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint impactConfigurableBigDecimal(String constraintName,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher) {
        return impactConfigurableBigDecimal(matchWeigher)
                .asConstraint(constraintName);
    }

    /**
     * As defined by {@link #impactConfigurableBigDecimal(String, QuadFunction)}.
     *
     * @deprecated Prefer {@link #impactConfigurableBigDecimal(QuadFunction)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param matchWeigher never null
     * @return never null
     */
    @Deprecated(forRemoval = true)
    default Constraint impactConfigurableBigDecimal(String constraintPackage, String constraintName,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher) {
        return impactConfigurableBigDecimal(matchWeigher)
                .asConstraint(constraintPackage, constraintName);
    }

}

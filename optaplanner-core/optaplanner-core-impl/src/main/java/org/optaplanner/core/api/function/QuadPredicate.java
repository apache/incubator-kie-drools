package org.optaplanner.core.api.function;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Represents a predicate (boolean-valued function) of four arguments.
 * This is the four-arity specialization of {@link Predicate}.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #test(Object, Object, Object, Object)}.
 *
 * @param <A> the type of the first argument to the predicate
 * @param <B> the type of the second argument the predicate
 * @param <C> the type of the third argument the predicate
 * @param <D> the type of the fourth argument the predicate
 *
 * @see Predicate
 */
@FunctionalInterface
public interface QuadPredicate<A, B, C, D> {

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param a the first input argument
     * @param b the second input argument
     * @param c the third input argument
     * @param d the fourth input argument
     * @return {@code true} if the input arguments match the predicate,
     *         otherwise {@code false}
     */
    boolean test(A a, B b, C c, D d);

    /**
     * Returns a composed predicate that represents a short-circuiting logical
     * AND of this predicate and another. When evaluating the composed
     * predicate, if this predicate is {@code false}, then the {@code other}
     * predicate is not evaluated.
     *
     * <p>
     * Any exceptions thrown during evaluation of either predicate are relayed
     * to the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     *
     * @param other a predicate that will be logically-ANDed with this predicate
     * @return a composed predicate that represents the short-circuiting logical
     *         AND of this predicate and the {@code other} predicate
     * @throws NullPointerException if other is null
     */
    default QuadPredicate<A, B, C, D> and(QuadPredicate<? super A, ? super B, ? super C, ? super D> other) {
        Objects.requireNonNull(other);
        return (A a, B b, C c, D d) -> test(a, b, c, d) && other.test(a, b, c, d);
    }

    /**
     * Returns a predicate that represents the logical negation of this
     * predicate.
     *
     * @return a predicate that represents the logical negation of this
     *         predicate
     */
    default QuadPredicate<A, B, C, D> negate() {
        return (A a, B b, C c, D d) -> !test(a, b, c, d);
    }

    /**
     * Returns a composed predicate that represents a short-circuiting logical
     * OR of this predicate and another. When evaluating the composed
     * predicate, if this predicate is {@code true}, then the {@code other}
     * predicate is not evaluated.
     *
     * <p>
     * Any exceptions thrown during evaluation of either predicate are relayed
     * to the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     *
     * @param other a predicate that will be logically-ORed with this predicate
     * @return a composed predicate that represents the short-circuiting logical
     *         OR of this predicate and the {@code other} predicate
     * @throws NullPointerException if other is null
     */
    default QuadPredicate<A, B, C, D> or(QuadPredicate<? super A, ? super B, ? super C, ? super D> other) {
        Objects.requireNonNull(other);
        return (A a, B b, C c, D d) -> test(a, b, c, d) || other.test(a, b, c, d);
    }
}

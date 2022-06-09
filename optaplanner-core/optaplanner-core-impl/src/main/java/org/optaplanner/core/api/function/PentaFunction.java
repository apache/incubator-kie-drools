package org.optaplanner.core.api.function;

import java.util.function.Function;

/**
 * Represents a function that accepts five arguments and produces a result.
 * This is the five-arity specialization of {@link Function}.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object, Object, Object)}.
 *
 * @param <A> the type of the first argument to the function
 * @param <B> the type of the second argument to the function
 * @param <C> the type of the third argument to the function
 * @param <D> the type of the fourth argument to the function
 * @param <E> the type of the fifth argument to the function
 * @param <R> the type of the result of the function
 *
 * @see Function
 */
@FunctionalInterface
public interface PentaFunction<A, B, C, D, E, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param a the first function argument
     * @param b the second function argument
     * @param c the third function argument
     * @param d the fourth function argument
     * @param e the fifth function argument
     * @return the function result
     */
    R apply(A a, B b, C c, D d, E e);

}

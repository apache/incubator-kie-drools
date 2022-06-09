package org.optaplanner.core.api.function;

/**
 * Represents a function that accepts four arguments and produces an int-valued result.
 * This is the {@code int}-producing primitive specialization for {@link QuadFunction}.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #applyAsInt(Object, Object, Object, Object)}.
 *
 * @param <A> the type of the first argument to the function
 * @param <B> the type of the second argument to the function
 * @param <C> the type of the third argument to the function
 * @param <D> the type of the fourth argument to the function
 *
 * @see QuadFunction
 */
@FunctionalInterface
public interface ToIntQuadFunction<A, B, C, D> {

    /**
     * Applies this function to the given arguments.
     *
     * @param a the first function argument
     * @param b the second function argument
     * @param c the third function argument
     * @param d the fourth function argument
     * @return the function result
     */
    int applyAsInt(A a, B b, C c, D d);
}

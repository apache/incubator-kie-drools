package org.optaplanner.core.api.function;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a function that accepts three arguments and returns no result.
 * This is the three-arity specialization of {@link Consumer}.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(Object, Object, Object)}.
 *
 * @param <A> the type of the first argument to the function
 * @param <B> the type of the second argument to the function
 * @param <C> the type of the third argument to the function
 *
 * @see Function
 */
@FunctionalInterface
public interface TriConsumer<A, B, C> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param a the first function argument
     * @param b the second function argument
     * @param c the third function argument
     */
    void accept(A a, B b, C c);

    default TriConsumer<A, B, C> andThen(TriConsumer<? super A, ? super B, ? super C> after) {
        Objects.requireNonNull(after);
        return (a, b, c) -> {
            accept(a, b, c);
            after.accept(a, b, c);
        };
    }

}

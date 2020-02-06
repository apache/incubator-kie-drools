/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.api.interfaces;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

/**
 * Factory for <b>wrappers</b> around custom <b>exception-throwing</b> functional interfaces
 */
public class FunctionalWrapperFactory {

    /**
     * <code>Function</code> <code>wrapper</code> for exception-throwing <code>Function</code>
     *
     * @param throwingFunction
     * @param <T>
     * @param <R>
     * @param <E>
     * @return
     * @throws E
     */
    public static <T, R, E extends Exception> Function<T, R> throwingFunctionWrapper(ThrowingFunction<T, R, E> throwingFunction) throws E {
        return t -> {
            try {
                return throwingFunction.apply(t);
            } catch (Exception e) {
                throwActualException(e);
                return null;
            }
        };
    }

    /**
     * <code>Function</code> <code>wrapper</code> for exception-throwing <code>ToDoubleFunction</code>
     *
     * @param throwingToDoubleFunction
     * @param <T>
     * @param <E>
     * @return
     * @throws E
     */
    public static <T, E extends Exception> ToDoubleFunction<T> throwingToDoubleFunctionWrapper(ThrowingToDoubleFunction<T, E> throwingToDoubleFunction) throws E {
        return t -> {
            try {
                return throwingToDoubleFunction.applyAsDouble(t);
            } catch (Exception e) {
                throwActualException(e);
                return -1;
            }
        };
    }

    /**
     * <code>Consumer</code> <code>wrapper</code> for exception-throwing <code>Consumer</code>
     *
     * @param throwingConsumer
     * @param <T>
     * @param <E>
     * @return
     * @throws E
     */
    public static <T, E extends Exception> Consumer<T> throwingConsumerWrapper(ThrowingConsumer<T, E> throwingConsumer) throws E {
        return t -> {
            try {
                throwingConsumer.accept(t);
            } catch (Exception e) {
                throwActualException(e);
            }
        };
    }

    /**
     * <code>DoubleConsumer</code> <code>wrapper</code> for exception-throwing <code>DoubleConsumer</code>
     *
     * @param throwingConsumer
     * @param <E>
     * @return
     * @throws E
     */
    public static <E extends Exception> DoubleConsumer throwingDoubleConsumerWrapper(ThrowingDoubleConsumer<E> throwingConsumer) throws E {
        return t -> {
            try {
                throwingConsumer.accept(t);
            } catch (Exception e) {
                throwActualException(e);
            }
        };
    }

    /**
     * <code>BiConsumer</code> <code>wrapper</code> for exception-throwing <code>BiConsumer</code>
     *
     * @param throwingBiConsumer
     * @param <T>
     * @param <U>
     * @param <E>
     * @return
     * @throws E
     */
    public static <T, U, E extends Exception> BiConsumer<T, U> throwingBiConsumerWrapper(ThrowingBiConsumer<T, U, E> throwingBiConsumer) throws E {
        return (t, u) -> {
            try {
                throwingBiConsumer.accept(t, u);
            } catch (Exception e) {
                throwActualException(e);
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <E extends Exception> void throwActualException(Exception exception) throws E {
        throw (E) exception;
    }

}

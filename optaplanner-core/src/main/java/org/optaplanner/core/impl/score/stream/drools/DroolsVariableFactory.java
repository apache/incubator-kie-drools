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

package org.optaplanner.core.impl.score.stream.drools;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.drools.model.DSL;
import org.drools.model.Variable;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.TriFunction;

/**
 * Creates {@link Variable}s with unique names, by adding numeric suffixes to the user-provided names.
 *
 * Drools executable model unfortunately doesn't enforce unique variable names, yet requires them - therefore, by
 * unifying all access to variable creation through the factory we avoid avoiding all sorts of strange issues that are
 * very hard to track down.
 */
public interface DroolsVariableFactory {

    /**
     * Declare a new {@link Variable} with a given name and no declared source.
     * Delegates to {@link DSL#declarationOf(Class, String)}.
     *
     * @param clz type of the variable. Using {@link Object} will work in all cases, but Drools will spend unnecessary
     *        amount of time looking up applicable instances of that variable, as it has to traverse instances of all
     *        types in the working memory. Therefore, it is desirable to be as specific as possible.
     * @param baseName name of the variable, mostly useful for debugging purposes. Will be decorated by a numeric
     *        identifier to prevent multiple variables of the same name to exist within left-hand side of a single rule.
     * @param <U> generic type of the variable
     * @return new variable declaration, not yet bound to anything
     */
    <U> Variable<? extends U> createVariable(Class<U> clz, String baseName);

    /**
     * As defined by {@link #createVariable(String, Variable, boolean)} with no flattening.
     */
    default <U> Variable<U> createVariable(String baseName, Variable<U> source) {
        return createVariable(baseName, source, false);
    }

    /**
     * As defined by {@link #createVariable(String, Variable, Function, boolean)} with no flattening.
     */
    default <U, Result_> Variable<Result_> createVariable(String baseName, Variable<U> source, Function<U, Result_> mapping) {
        return createVariable(baseName, source, mapping, false);
    }

    /**
     * As defined by {@link #createVariable(String, Variable, Variable, BiFunction, boolean)} with no flattening.
     */
    default <U, V, Result_> Variable<Result_> createVariable(String baseName, Variable<U> source1, Variable<V> source2,
            BiFunction<U, V, Result_> mapping) {
        return createVariable(baseName, source1, source2, mapping, false);
    }

    /**
     * As defined by {@link #createVariable(String, Variable, Variable, Variable, TriFunction, boolean)}
     * with no flattening.
     */
    default <U, V, W, Result_> Variable<Result_> createVariable(String baseName, Variable<U> source1, Variable<V> source2,
            Variable<W> source3, TriFunction<U, V, W, Result_> mapping) {
        return createVariable(baseName, source1, source2, source3, mapping, false);
    }

    /**
     * As defined by {@link #createVariable(String, Variable, Variable, Variable, Variable, QuadFunction, boolean)}
     * with no flattening.
     */
    default <U, V, W, Y, Result_> Variable<Result_> createVariable(String baseName, Variable<U> source1, Variable<V> source2,
            Variable<W> source3, Variable<Y> source4, QuadFunction<U, V, W, Y, Result_> mapping) {
        return createVariable(baseName, source1, source2, source3, source4, mapping, false);
    }

    /**
     * Declares a new {@link Object}-typed variable, see {@link #createVariable(Class, String)} for details.
     */
    default <U> Variable<U> createVariable(String baseName) {
        return (Variable<U>) createVariable(Object.class, baseName);
    }

    /**
     * Declare a new {@link Variable} with a given name,
     * where the value of the variable will be read from the provided source variable.
     * If the value is {@link Iterable},
     * the final argument to this method controls whether the elements will be treated individually or not.
     *
     * @param baseName name of the variable, mostly useful for debugging purposes. Will be decorated by a numeric
     *        identifier to prevent multiple variables of the same name to exist within left-hand side of a single rule.
     * @param source the variable the value of which will be set to the new variable
     * @param flatten if true, we will flatten the collection and return one element after another
     * @param <U> generic type of the variable
     * @return never null
     */
    <U> Variable<U> createVariable(String baseName, Variable<U> source, boolean flatten);

    /**
     * Declare a new {@link Variable} with a given name,
     * where the value of the variable will be computed from the provided source variable.
     * If the computed value is {@link Iterable},
     * the final argument to this method controls whether the elements will be treated individually or not.
     *
     * @param baseName name of the variable, mostly useful for debugging purposes. Will be decorated by a numeric
     *        identifier to prevent multiple variables of the same name to exist within left-hand side of a single rule.
     * @param source the variable the value of which will be set to the new variable
     * @param mapping the function to apply on the source variable
     * @param flatten if true, we will flatten the collection and return one element after another
     * @param <U> generic type of the variable
     * @return never null
     */
    <U, Result_> Variable<Result_> createVariable(String baseName, Variable<U> source, Function<U, Result_> mapping,
            boolean flatten);

    /**
     * As defined by {@link #createVariable(String, Variable, Function, boolean)}.
     */
    <U, V, Result_> Variable<Result_> createVariable(String baseName, Variable<U> source1, Variable<V> source2,
            BiFunction<U, V, Result_> mapping, boolean flatten);

    /**
     * As defined by {@link #createVariable(String, Variable, Function, boolean)}.
     */
    <U, V, W, Result_> Variable<Result_> createVariable(String baseName, Variable<U> source1, Variable<V> source2,
            Variable<W> source3, TriFunction<U, V, W, Result_> mapping, boolean flatten);

    /**
     * As defined by {@link #createVariable(String, Variable, Function, boolean)}.
     */
    <U, V, W, Y, Result_> Variable<Result_> createVariable(String baseName, Variable<U> source1, Variable<V> source2,
            Variable<W> source3, Variable<Y> source4, QuadFunction<U, V, W, Y, Result_> mapping, boolean flatten);

}

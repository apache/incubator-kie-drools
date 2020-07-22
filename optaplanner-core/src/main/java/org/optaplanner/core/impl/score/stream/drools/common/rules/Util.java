/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools.common.rules;

import org.drools.model.DSL;
import org.drools.model.DeclarationSource;
import org.drools.model.Variable;

final class Util {

    private Util() {
        // No external instances.
    }

    /**
     * Declare a new {@link Variable} in this rule, with a given name and no declared source. Delegates to
     * {@link DSL#declarationOf(Class, String)}.
     *
     * @param clz type of the variable. Using {@link Object} will work in all cases, but Drools will spend unnecessary
     *        amount of time looking up applicable instances of that variable, as it has to traverse instances of all types in
     *        the working memory. Therefore, it is desirable to be as specific as possible.
     * @param name name of the variable, mostly useful for debugging purposes. Will be decorated by a pseudo-random
     *        numeric identifier to prevent multiple variables of the same name to exist within left-hand side of a single
     *        rule.
     * @param <X> Generic type of the variable.
     * @return new variable declaration, not yet bound to anything
     */
    public static <X> Variable<? extends X> createVariable(Class<X> clz, String name) {
        return DSL.declarationOf(clz, name);
    }

    /**
     * Declare a new {@link Variable} in this rule, with a given name and a declaration source.
     * Delegates to {@link DSL#declarationOf(Class, String, DeclarationSource)}.
     *
     * @param name name of the variable, mostly useful for debugging purposes. Will be decorated by a pseudo-random
     *        numeric identifier to prevent multiple variables of the same name to exist within left-hand side of a single
     *        rule.
     * @param source declaration source of the variable
     * @param <X> Generic type of the variable.
     * @return new variable declaration, not yet bound to anything
     */
    public static <X> Variable<? extends X> createVariable(String name, DeclarationSource source) {
        return (Variable<X>) DSL.declarationOf(Object.class, name, source);
    }

    /**
     * Declare a new {@link Variable} in this rule, with a given name and a declaration source.
     * Delegates to {@link DSL#declarationOf(Class, String, DeclarationSource)}.
     *
     * @param clz type of the variable. Using {@link Object} will work in all cases, but Drools will spend unnecessary
     *        amount of time looking up applicable instances of that variable, as it has to traverse instances of all types in
     *        the working memory. Therefore, it is desirable to be as specific as possible.
     * @param name name of the variable, mostly useful for debugging purposes. Will be decorated by a pseudo-random
     *        numeric identifier to prevent multiple variables of the same name to exist within left-hand side of a single
     *        rule.
     * @param source declaration source of the variable
     * @param <X> Generic type of the variable.
     * @return new variable declaration, not yet bound to anything
     */
    public static <X> Variable<? extends X> createVariable(Class<X> clz, String name, DeclarationSource source) {
        return DSL.declarationOf(clz, name, source);
    }

    /**
     * Declares a new {@link Object}-typed variable, see {@link #createVariable(Class, String, DeclarationSource)} for
     * details.
     */
    public static <X> Variable<X> createVariable(String name) {
        return (Variable<X>) createVariable(Object.class, name);
    }

}

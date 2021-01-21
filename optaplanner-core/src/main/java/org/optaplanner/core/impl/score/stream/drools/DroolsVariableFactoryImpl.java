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

import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.from;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import org.drools.model.Variable;

final class DroolsVariableFactoryImpl implements DroolsVariableFactory {

    private final AtomicLong counter = new AtomicLong(0);

    DroolsVariableFactoryImpl() {
        // No external instances.
    }

    private String generateUniqueId(String baseName) {
        return baseName + "_" + counter.incrementAndGet();
    }

    @Override
    public <X> Variable<? extends X> createVariable(Class<X> clz, String baseName) {
        return declarationOf(clz, generateUniqueId(baseName));
    }

    @Override
    public <X> Variable<X> createVariable(String baseName, Variable<X> source) {
        return declarationOf(source.getType(), generateUniqueId(baseName),
                from(source, x -> sanitize(x)));
    }

    /**
     * When using {@link org.drools.model.DSL#from(Variable)}, Drools has two behaviors.
     * If the variable is a simple object, it returns that.
     * If the variable is a {@link Iterable}, it will iterate over its elements and return them individually.
     * The second behavior is not acceptable for CS-D, and therefore we apply this workaround.
     * 
     * @param value the value to protect against iterative from(...)
     * @return never null
     */
    private static Object sanitize(Object value) {
        if (value instanceof Iterable) {
            return Collections.singleton(value);
        }
        return value;
    }

    @Override
    public <In, Out> Variable<Out> createVariable(String baseName, Variable<In> source, Function<In, Out> extractor) {
        return (Variable<Out>) declarationOf(Object.class, generateUniqueId(baseName), from(source, x -> {
            Out extracted = extractor.apply(x);
            return sanitize(extracted);
        }));
    }

}

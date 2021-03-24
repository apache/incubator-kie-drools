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
import java.util.function.BiFunction;
import java.util.function.Function;

import org.drools.model.Variable;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.TriFunction;

final class DroolsVariableFactoryImpl implements DroolsVariableFactory {

    private final AtomicLong counter = new AtomicLong(0);

    DroolsVariableFactoryImpl() {
        // No external instances.
    }

    private String generateUniqueId(String baseName) {
        return baseName + "_" + counter.incrementAndGet();
    }

    @Override
    public <U> Variable<? extends U> createVariable(Class<U> clz, String baseName) {
        return declarationOf(clz, generateUniqueId(baseName));
    }

    @Override
    public <U> Variable<U> createVariable(String baseName, Variable<U> source) {
        return declarationOf(source.getType(), generateUniqueId(baseName), from(source));
    }

    @Override
    public <U> Variable<U> createVariable(String baseName, Variable<U> source, boolean flatten) {
        if (flatten) {
            return declarationOf(source.getType(), generateUniqueId(baseName), from(source));
        } else {
            return declarationOf(source.getType(), generateUniqueId(baseName),
                    from(source, value -> {
                        if (value instanceof Iterable) {
                            return Collections.singleton(value);
                        }
                        return value;
                    }));
        }
    }

    @Override
    public <U, Result_> Variable<Result_> createVariable(String baseName, Variable<U> source, Function<U, Result_> mapping,
            boolean flatten) {
        if (flatten) {
            return (Variable<Result_>) declarationOf(source.getType(), generateUniqueId(baseName),
                    from(source, mapping::apply));
        } else {
            return (Variable<Result_>) declarationOf(Object.class, generateUniqueId(baseName),
                    from(source, value -> {
                        Result_ result = mapping.apply(value);
                        if (result instanceof Iterable) {
                            return Collections.singleton(result);
                        }
                        return result;
                    }));
        }
    }

    @Override
    public <U, V, Result_> Variable<Result_> createVariable(String baseName, Variable<U> source1, Variable<V> source2,
            BiFunction<U, V, Result_> mapping, boolean flatten) {
        if (flatten) {
            return (Variable<Result_>) declarationOf(Object.class, generateUniqueId(baseName),
                    from(source1, source2, mapping::apply));
        } else {
            return (Variable<Result_>) declarationOf(Object.class, generateUniqueId(baseName),
                    from(source1, source2, (value1, value2) -> {
                        Result_ result = mapping.apply(value1, value2);
                        if (result instanceof Iterable) {
                            return Collections.singleton(result);
                        }
                        return result;
                    }));
        }
    }

    @Override
    public <U, V, W, Result_> Variable<Result_> createVariable(String baseName, Variable<U> source1, Variable<V> source2,
            Variable<W> source3, TriFunction<U, V, W, Result_> mapping, boolean flatten) {
        if (flatten) {
            return (Variable<Result_>) declarationOf(Object.class, generateUniqueId(baseName),
                    from(source1, source2, source3, mapping::apply));
        } else {
            return (Variable<Result_>) declarationOf(Object.class, generateUniqueId(baseName),
                    from(source1, source2, source3, (value1, value2, value3) -> {
                        Result_ result = mapping.apply(value1, value2, value3);
                        if (result instanceof Iterable) {
                            return Collections.singleton(result);
                        }
                        return result;
                    }));
        }
    }

    @Override
    public <U, V, W, Y, Result_> Variable<Result_> createVariable(String baseName, Variable<U> source1, Variable<V> source2,
            Variable<W> source3, Variable<Y> source4, QuadFunction<U, V, W, Y, Result_> mapping, boolean flatten) {
        if (flatten) {
            return (Variable<Result_>) declarationOf(Object.class, generateUniqueId(baseName),
                    from(source1, source2, source3, source4, mapping::apply));
        } else {
            return (Variable<Result_>) declarationOf(Object.class, generateUniqueId(baseName),
                    from(source1, source2, source3, source4, (value1, value2, value3, value4) -> {
                        Result_ result = mapping.apply(value1, value2, value3, value4);
                        if (result instanceof Iterable) {
                            return Collections.singleton(result);
                        }
                        return result;
                    }));
        }
    }

}

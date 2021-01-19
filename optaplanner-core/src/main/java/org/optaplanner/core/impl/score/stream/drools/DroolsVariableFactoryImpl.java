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
        return declarationOf(source.getType(), generateUniqueId(baseName), from(source));
    }

    @Override
    public <In, Out> Variable<Out> createVariable(String baseName, Variable<In> source, Function<In, Out> extractor) {
        return (Variable<Out>) declarationOf(Object.class, generateUniqueId(baseName), from(source, extractor::apply));
    }

}

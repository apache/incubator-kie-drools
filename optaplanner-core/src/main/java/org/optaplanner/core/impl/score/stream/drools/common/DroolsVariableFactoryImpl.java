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

package org.optaplanner.core.impl.score.stream.drools.common;

import java.util.concurrent.atomic.AtomicLong;

import org.drools.model.DSL;
import org.drools.model.DeclarationSource;
import org.drools.model.Variable;

final class DroolsVariableFactoryImpl implements DroolsVariableFactory {

    private final AtomicLong counter = new AtomicLong(0);

    DroolsVariableFactoryImpl() {
        // No external instances.
    }

    private String generateUniqueId(String baseName) {
        return baseName + "_" + counter.incrementAndGet();
    }

    public <X> Variable<? extends X> createVariable(Class<X> clz, String baseName) {
        return DSL.declarationOf(clz, generateUniqueId(baseName));
    }

    public <X> Variable<? extends X> createVariable(String baseName, DeclarationSource source) {
        return (Variable<X>) DSL.declarationOf(Object.class, generateUniqueId(baseName), source);
    }

    public <X> Variable<? extends X> createVariable(Class<X> clz, String baseName, DeclarationSource source) {
        return DSL.declarationOf(clz, generateUniqueId(baseName), source);
    }

}

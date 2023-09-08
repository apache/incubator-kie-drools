/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.modelcompiler.constraints;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.Accumulator;
import org.kie.api.runtime.rule.FactHandle;

import java.util.Collection;
import java.util.function.Supplier;

public class GenericCollectAccumulator implements Accumulator {

    private final Supplier<? extends Collection> collectTargetSupplier;

    public GenericCollectAccumulator(Supplier<? extends Collection> collectTargetSupplier) {
        this.collectTargetSupplier = collectTargetSupplier;
    }

    @Override
    public Object createContext() {
        return null;
    }


    @Override
    public Object init(Object workingMemoryContext,
                       Object context,
                       BaseTuple leftTuple,
                       Declaration[] declarations,
                       ValueResolver valueResolver) {
        return collectTargetSupplier.get();
    }

    @Override
    public Object accumulate(Object workingMemoryContext,
                             Object context,
                             BaseTuple leftTuple,
                             FactHandle handle,
                             Declaration[] declarations,
                             Declaration[] innerDeclarations,
                             ValueResolver valueResolver) {
        Object value = handle.getObject();
        ((Collection) context).add( value );
        return value;
    }

    @Override
    public boolean tryReverse(Object workingMemoryContext,
                              Object context,
                              BaseTuple leftTuple,
                              FactHandle handle,
                              Object value,
                              Declaration[] declarations,
                              Declaration[] innerDeclarations,
                              ValueResolver valueResolver) {
        ((Collection) context).remove( value );
        return true;
    }

    @Override
    public Object getResult(Object workingMemoryContext,
                            Object context,
                            BaseTuple leftTuple,
                            Declaration[] declarations,
                            ValueResolver valueResolver) {
        return context;
    }

    @Override
    public boolean supportsReverse() {
        return true;
    }

    @Override
    public Object createWorkingMemoryContext() {
        // no working memory context needed
        return null;
    }
}

/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools.uni;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.CompiledInvoker;
import org.drools.core.spi.Tuple;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;

public class DroolsGroupByInvoker<A, B, ResultContainer, NewB> implements Accumulator, CompiledInvoker {

    private final UniConstraintCollector<B, ResultContainer, NewB> collector;

    public DroolsGroupByInvoker(final UniConstraintCollector<B, ResultContainer, NewB> collector) {
        this.collector = collector;
    }

    @Override
    public Serializable createContext() {
        return new DroolsGroupBy<>(collector);
    }

    @Override
    public void init(Object workingMemoryContext, Object context, Tuple tuple, Declaration[] declarations,
            WorkingMemory workingMemory) {
        ((DroolsGroupBy<A, B, ResultContainer, NewB>) context).init();
    }

    @Override
    public void accumulate(Object workingMemoryContext, Object context, Tuple tuple, InternalFactHandle handle,
            Declaration[] declarations, Declaration[] innerDeclarations, final WorkingMemory workingMemory) {
        InternalWorkingMemory internalWorkingMemory = (InternalWorkingMemory) workingMemory;
        Object handleObject = handle.getObject();
        final B toCollect = (B) innerDeclarations[1].getValue(internalWorkingMemory, handleObject);
        final A groupKey = (A) innerDeclarations[2].getValue(internalWorkingMemory, handleObject);
        ((DroolsGroupBy<A, B, ResultContainer, NewB>) context).accumulate(handle, groupKey, toCollect);
    }

    @Override
    public void reverse(Object workingMemoryContext, Object context, Tuple tuple, InternalFactHandle handle,
            Declaration[] declarations, Declaration[] innerDeclarations, WorkingMemory workingMemory) {
        ((DroolsGroupBy<A, B, ResultContainer, NewB>) context).reverse(handle);
    }

    @Override
    public Object getResult(Object workingMemoryContext, Object context, Tuple tuple, Declaration[] declarations,
            WorkingMemory workingMemory) {
        return ((DroolsGroupBy<A, B, ResultContainer, NewB>) context).getResult();
    }

    @Override
    public boolean supportsReverse() {
        return true;
    }

    @Override
    public Object createWorkingMemoryContext() {
        return null;
    }

    @Override
    public String getMethodBytecode() {
        Class<?> accumulateClass = DroolsGroupBy.class;
        String classFileName = accumulateClass.getCanonicalName().replace('.', '/') + ".class";
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(classFileName)) {
            final byte[] data = new byte[1024];
            int byteCount;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((byteCount = is.read(data, 0, 1024)) > -1) {
                bos.write(data, 0, byteCount);
            }
            return bos.toString();
        } catch (final IOException e) {
            throw new RuntimeException("Unable to getResourceAsStream for " + accumulateClass);
        }
    }

}

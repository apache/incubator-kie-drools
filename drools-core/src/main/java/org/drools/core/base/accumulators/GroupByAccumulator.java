/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.base.accumulators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.Tuple;

public abstract class GroupByAccumulator implements Accumulator, Externalizable {

    private Accumulator innerAccumulator;

    public GroupByAccumulator() { }

    public GroupByAccumulator( Accumulator innerAccumulator ) {
        this.innerAccumulator = innerAccumulator;
    }

    protected abstract Object getKey(Tuple leftTuple, InternalFactHandle handle, WorkingMemory workingMemory);

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeObject( innerAccumulator );
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        innerAccumulator = (Accumulator) in.readObject();
    }

    @Override
    public Object createWorkingMemoryContext() {
        // no working memory context needed
        return null;
    }

    @Override
    public Serializable createContext() {
        return new GroupByContext();
    }

    @Override
    public void init( Object workingMemoryContext, Object context, Tuple leftTuple, Declaration[] declarations, WorkingMemory workingMemory ) throws Exception {
        (( GroupByContext ) context).init();
        innerAccumulator.init( workingMemoryContext, innerAccumulator.createContext(), leftTuple, declarations, workingMemory );
    }

    @Override
    public void accumulate( Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, Declaration[] declarations, Declaration[] innerDeclarations, WorkingMemory workingMemory ) throws Exception {
        Serializable groupContext = (( GroupByContext ) context).loadContext( innerAccumulator, getKey(leftTuple, handle, workingMemory) );
        innerAccumulator.accumulate( workingMemoryContext, groupContext, leftTuple, handle, declarations, innerDeclarations, workingMemory );
    }

    @Override
    public boolean supportsReverse() {
        return innerAccumulator.supportsReverse();
    }

    @Override
    public void reverse( Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, Declaration[] declarations, Declaration[] innerDeclarations, WorkingMemory workingMemory ) throws Exception {
        Serializable groupContext = (( GroupByContext ) context).loadContext( innerAccumulator, getKey(leftTuple, handle, workingMemory) );
        innerAccumulator.reverse( workingMemoryContext, groupContext, leftTuple, handle, declarations, innerDeclarations, workingMemory );
    }

    @Override
    public Object getResult( Object workingMemoryContext, Object context, Tuple leftTuple, Declaration[] declarations, WorkingMemory workingMemory ) throws Exception {
        return (( GroupByContext ) context).result( groupContext -> {
            try {
                return innerAccumulator.getResult( workingMemoryContext, groupContext, leftTuple, declarations, workingMemory );
            } catch (Exception e) {
                throw new RuntimeException( e );
            }
        } );
    }

    public static class GroupByContext implements Serializable {
        private final Set<Object> keys = new HashSet<>();
        private final Map<Object, Serializable> contextsByGroup = new HashMap<>();
        private final List<Object[]> results = new ArrayList<>();

        Serializable loadContext(Accumulator innerAccumulator, Object key) {
            keys.add( key );
            return contextsByGroup.computeIfAbsent( key, k -> innerAccumulator.createContext() );
        }

        public void init() {
            this.keys.clear();;
            this.contextsByGroup.clear();;
            this.results.clear();
        }

        public List<Object[]> result( Function<Serializable, Object> groupResultSupplier ) {
            results.clear();
            for (Object k : keys) {
                results.add( new Object[]{ k, groupResultSupplier.apply( contextsByGroup.get(k) ) } );
            }
            keys.clear();
            return results;
        }
    }
}

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

package org.drools.modelcompiler.constraints;

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
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.Tuple;
import org.drools.model.functions.FunctionN;

public class LambdaGroupByAccumulate extends Accumulate {

    private Accumulate innerAccumulate;
    private Declaration[] groupingDeclarations;
    private FunctionN groupingFunction;

    public LambdaGroupByAccumulate() { }

    public LambdaGroupByAccumulate( Accumulate innerAccumulate, Declaration[] groupingDeclarations, FunctionN groupingFunction ) {
        super(innerAccumulate.getSource(), innerAccumulate.getRequiredDeclarations());
        this.innerAccumulate = innerAccumulate;
        this.groupingDeclarations = groupingDeclarations;
        this.groupingFunction = groupingFunction;
    }

    private Object getKey( Tuple tuple, InternalFactHandle handle, WorkingMemory workingMemory ) {
        Object[] args = new Object[groupingDeclarations.length];
        for (int i = 0; i < groupingDeclarations.length; i++) {
            Declaration declaration = groupingDeclarations[i];
            Object object = tuple != null && declaration.getOffset() < tuple.size() ? tuple.getObject(declaration.getOffset()) : handle.getObject();
            args[i] = declaration.getValue( workingMemory.getInternalWorkingMemory(), object );
        }
        return groupingFunction.apply( args );
    }

    @Override
    public void readExternal( ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        this.innerAccumulate = (Accumulate) in.readObject();
        this.groupingDeclarations = (Declaration[]) in.readObject();
        this.groupingFunction = (FunctionN) in.readObject();
    }

    @Override
    public void writeExternal( ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(innerAccumulate);
        out.writeObject(groupingDeclarations);
        out.writeObject(groupingFunction);
    }

    @Override
    public Accumulator[] getAccumulators() {
        return innerAccumulate.getAccumulators();
    }

    @Override
    public Object createContext() {
        return new GroupByContext(supportsReverse());
    }

    @Override
    public void init( Object workingMemoryContext, Object context, Tuple leftTuple, WorkingMemory workingMemory ) {
        (( GroupByContext ) context).init();
        innerAccumulate.init( workingMemoryContext, innerAccumulate.createContext(), leftTuple, workingMemory );
    }

    @Override
    public void accumulate( Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, WorkingMemory workingMemory ) {
        Object key = getKey(leftTuple, handle, workingMemory);
        if (key != null) {
            Object groupContext = (( GroupByContext ) context).loadContext( innerAccumulate, handle, key );
            innerAccumulate.accumulate( workingMemoryContext, groupContext, leftTuple, handle, workingMemory );
        }
    }

    @Override
    public void reverse( Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, WorkingMemory workingMemory ) {
        Object groupContext = (( GroupByContext ) context).loadContextForReverse( handle );
        innerAccumulate.reverse( workingMemoryContext, groupContext, leftTuple, handle, workingMemory );
    }

    @Override
    public Object getResult( Object workingMemoryContext, Object context, Tuple leftTuple, WorkingMemory workingMemory ) {
        return (( GroupByContext ) context).result( groupContext -> {
            try {
                return innerAccumulate.getResult( workingMemoryContext, groupContext, leftTuple, workingMemory );
            } catch (Exception e) {
                throw new RuntimeException( e );
            }
        } );
    }

    @Override
    public boolean supportsReverse() {
        return innerAccumulate.supportsReverse();
    }

    @Override
    public Accumulate clone() {
        return new LambdaGroupByAccumulate( innerAccumulate.clone(), groupingDeclarations, groupingFunction );
    }

    @Override
    public Object createWorkingMemoryContext() {
        return innerAccumulate.createWorkingMemoryContext();
    }

    @Override
    public boolean isMultiFunction() {
        return innerAccumulate.isMultiFunction();
    }

    @Override
    public void replaceAccumulatorDeclaration( Declaration declaration, Declaration resolved ) {
        innerAccumulate.replaceAccumulatorDeclaration(declaration, resolved);
    }

    @Override
    public boolean isGroupBy() {
        return true;
    }

    private static class GroupByContext implements Serializable {
        private final Map<Object, Object> contextsByGroup = new HashMap<>();
        private final List<Object[]> results = new ArrayList<>();
        private final Map<Long, GroupInfo> reverseSupport;

        private final Set<Object> keys = new HashSet<>();

        private GroupByContext(boolean supportsReverse) {
            reverseSupport = supportsReverse ? new HashMap<>() : null;
        }

        Object loadContext(Accumulate innerAccumulate, InternalFactHandle handle, Object key) {
            keys.add( key );
            Object groupContext = contextsByGroup.computeIfAbsent( key, k -> innerAccumulate.createContext() );
            if (reverseSupport != null) {
                reverseSupport.put( handle.getId(), new GroupInfo( key, groupContext ) );
            }
            return groupContext;
        }

        Object loadContextForReverse(InternalFactHandle handle) {
            GroupInfo groupInfo = reverseSupport.remove( handle.getId() );
            keys.add( groupInfo.key );
            return groupInfo.context;
        }

        public void init() {
            keys.clear();
            contextsByGroup.clear();
            results.clear();
            if (reverseSupport != null) {
                reverseSupport.clear();
            }
        }

        public List<Object[]> result( Function<Object, Object> groupResultSupplier ) {
            results.clear();
            for (Object k : keys) {
                Object ctx = contextsByGroup.get(k);
                results.add( new Object[]{ k, isEmptyContext(ctx) ? null : groupResultSupplier.apply( ctx ) } );
            }
            keys.clear();
            return results;
        }

        private boolean isEmptyContext(Object ctx) {
            if (ctx instanceof LambdaAccumulator.LambdaAccContext) {
                return (( LambdaAccumulator.LambdaAccContext ) ctx).isEmpty();
            }
            if (ctx instanceof Object[]) {
                return isEmptyContext( (( Object[] ) ctx)[0] );
            }
            return false;
        }
    }

    private static class GroupInfo {
        private final Object key;
        private final Object context;

        private GroupInfo( Object key, Object context ) {
            this.key = key;
            this.context = context;
        }
    }


}

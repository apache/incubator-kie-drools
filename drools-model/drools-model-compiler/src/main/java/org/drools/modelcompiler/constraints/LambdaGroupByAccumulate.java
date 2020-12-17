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
import java.util.List;
import java.util.Map;

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
    private boolean propagateAll;

    public LambdaGroupByAccumulate() { }

    public LambdaGroupByAccumulate( Accumulate innerAccumulate, Declaration[] groupingDeclarations, FunctionN groupingFunction, boolean propagateAll ) {
        super(innerAccumulate.getSource(), innerAccumulate.getRequiredDeclarations());
        this.innerAccumulate = innerAccumulate;
        this.groupingDeclarations = groupingDeclarations;
        this.groupingFunction = groupingFunction;
        this.propagateAll = propagateAll;
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
        this.propagateAll = in.readBoolean();
    }

    @Override
    public void writeExternal( ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(innerAccumulate);
        out.writeObject(groupingDeclarations);
        out.writeObject(groupingFunction);
        out.writeBoolean(propagateAll);
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
    public Object accumulate( Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, WorkingMemory workingMemory ) {
        Object key = getKey(leftTuple, handle, workingMemory);
        Object value = null;
        if (key != null) {
            Object groupContext = (( GroupByContext ) context).loadContext( innerAccumulate, handle, key );
            value = innerAccumulate.accumulate( workingMemoryContext, groupContext, leftTuple, handle, workingMemory );
        }
        return value;
    }

    @Override
    public void reverse(Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, Object value, WorkingMemory workingMemory) {
        Object groupContext = (( GroupByContext ) context).loadContextForReverse( handle );
        innerAccumulate.reverse(workingMemoryContext, groupContext, leftTuple, handle, value, workingMemory);
    }

    @Override
    public Object getResult( Object workingMemoryContext, Object context, Tuple leftTuple, WorkingMemory workingMemory ) {
        return (( GroupByContext ) context).result( innerAccumulate, workingMemoryContext, leftTuple, workingMemory );
    }

    @Override
    public boolean supportsReverse() {
        return innerAccumulate.supportsReverse();
    }

    @Override
    public Accumulate clone() {
        return new LambdaGroupByAccumulate( innerAccumulate.clone(), groupingDeclarations, groupingFunction, propagateAll );
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
        private final Map<Object, Object> changedGroups = new HashMap<>();
        private final Map<Long, GroupInfo> reverseSupport;

        private GroupByContext(boolean supportsReverse) {
            reverseSupport = supportsReverse ? new HashMap<>() : null;
        }

        Object loadContext(Accumulate innerAccumulate, InternalFactHandle handle, Object key) {
            Object groupContext = contextsByGroup.computeIfAbsent( key, k -> innerAccumulate.createContext() );
            if (reverseSupport != null) {
                reverseSupport.put( handle.getId(), new GroupInfo( key, groupContext ) );
            }
            changedGroups.put( key, groupContext );
            return groupContext;
        }

        Object loadContextForReverse(InternalFactHandle handle) {
            GroupInfo groupInfo = reverseSupport.remove( handle.getId() );
            changedGroups.put( groupInfo.key, groupInfo.context );
            return groupInfo.context;
        }

        public void init() {
            contextsByGroup.clear();
            changedGroups.clear();
            if (reverseSupport != null) {
                reverseSupport.clear();
            }
        }

        public List<Object[]> result( Accumulate innerAccumulate, Object wmCtx, Tuple leftTuple, WorkingMemory wm ) {
            List<Object[]> results = new ArrayList<>( changedGroups.size() );
            for (Map.Entry<Object, Object> entry : changedGroups.entrySet()) {
                results.add( new Object[]{ entry.getKey(), isEmptyContext(entry.getValue()) ? null : innerAccumulate.getResult( wmCtx, entry.getValue(), leftTuple, wm ) } );
            }
            changedGroups.clear();
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

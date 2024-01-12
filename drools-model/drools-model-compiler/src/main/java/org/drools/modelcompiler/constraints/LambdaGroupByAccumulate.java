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
import org.drools.base.reteoo.AccumulateContextEntry;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Accumulate;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.Accumulator;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.AccumulateNode.GroupByContext;
import org.drools.core.reteoo.Tuple;
import org.drools.core.reteoo.TupleImpl;
import org.drools.core.util.index.TupleListWithContext;
import org.drools.model.functions.Function1;
import org.drools.model.functions.FunctionN;
import org.kie.api.runtime.rule.FactHandle;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class LambdaGroupByAccumulate extends Accumulate {

    private Accumulate innerAccumulate;
    private Declaration[] groupingDeclarations;
    private FunctionN groupingFunction;
    private Function1 groupingFunction1;

    public LambdaGroupByAccumulate() { }

    public LambdaGroupByAccumulate( Accumulate innerAccumulate, Declaration[] groupingDeclarations, FunctionN groupingFunction ) {
        super(innerAccumulate.getSource(), innerAccumulate.getRequiredDeclarations());
        this.innerAccumulate = innerAccumulate;
        this.groupingDeclarations = groupingDeclarations;
        this.groupingFunction = groupingFunction;
        this.groupingFunction1 = groupingDeclarations.length == 1 ? groupingFunction.asFunction1() : null;
    }

    private Object getKey( Tuple tuple, FactHandle handle, ReteEvaluator reteEvaluator ) {
        if (groupingFunction1 != null) {
            return groupingFunction1.apply( getValue( tuple, handle, reteEvaluator, groupingDeclarations[0] ) );
        }

        Object[] args = new Object[groupingDeclarations.length];
        for (int i = 0; i < groupingDeclarations.length; i++) {
            args[i] = getValue( tuple, handle, reteEvaluator, groupingDeclarations[i] );
        }
        return groupingFunction.apply( args );
    }

    private Object getValue( Tuple tuple, FactHandle handle, ReteEvaluator reteEvaluator, Declaration declaration ) {
        // we already have the handle, so avoid tuple iteration if not needed.
        // (is this really saving time, as get(int index) has pretty much the same check, at best saves some method call) (mdp)
        return declaration.getValue( reteEvaluator, declaration.getTupleIndex() < tuple.size() ? tuple.get( declaration ).getObject() : handle.getObject() );
    }

    @Override
    public void readExternal( ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        this.innerAccumulate = (Accumulate) in.readObject();
        this.groupingDeclarations = (Declaration[]) in.readObject();
        this.groupingFunction = (FunctionN) in.readObject();
        this.groupingFunction1 = groupingDeclarations.length == 1 ? groupingFunction.asFunction1() : null;
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
    public Object createFunctionContext() {
        return innerAccumulate.createFunctionContext();
    }

    @Override
    public Object init(Object workingMemoryContext, Object accContext,
                       Object funcContext, BaseTuple leftTuple, ValueResolver valueResolver) {
        // do nothing here, it's done when the group is first created
        return funcContext;
    }

    @Override
    public Object accumulate(Object workingMemoryContext, Object context,
                             BaseTuple match, FactHandle handle, ValueResolver valueResolver) {
        GroupByContext groupByContext = ( GroupByContext ) context;
        TupleImpl leftTupleMatch = (TupleImpl) match;
        TupleListWithContext<AccumulateContextEntry> tupleList = groupByContext.getGroup(workingMemoryContext, innerAccumulate,
                                                                                         leftTupleMatch, getKey(leftTupleMatch, handle, (ReteEvaluator) valueResolver), (ReteEvaluator) valueResolver);

        return accumulate(workingMemoryContext, match, handle, groupByContext, tupleList, valueResolver);
    }

    @Override
    public Object accumulate(Object workingMemoryContext, BaseTuple match, FactHandle handle,
                             Object groupByContext, Object tupleList, ValueResolver valueResolver) {
        TupleListWithContext<AccumulateContextEntry> list = (TupleListWithContext<AccumulateContextEntry>) tupleList;
        ((GroupByContext)groupByContext).moveToPropagateTupleList( list);
        return innerAccumulate.accumulate(workingMemoryContext, list.getContext(), match, handle, valueResolver);
    }

    @Override
    public boolean tryReverse(Object workingMemoryContext, Object context, BaseTuple leftTuple, FactHandle handle,
                              BaseTuple match, ValueResolver valueResolver) {
        TupleImpl                                    tupleMatch = (TupleImpl) match;
        TupleListWithContext<AccumulateContextEntry> memory     = (TupleListWithContext<AccumulateContextEntry>) tupleMatch.getMemory();
        AccumulateContextEntry entry = memory.getContext();
        boolean reversed = innerAccumulate.tryReverse(workingMemoryContext, entry, leftTuple, handle, match, valueResolver);

        if (reversed) {
            GroupByContext groupByContext = ( GroupByContext ) context;
            groupByContext.moveToPropagateTupleList( memory );

            memory.remove( tupleMatch );
            if ( memory.isEmpty() ) {
                groupByContext.removeGroup( entry.getKey() );
                memory.getContext().setEmpty( true );
            }
        }

        return reversed;
    }

    @Override
    public Object getResult( Object workingMemoryContext, Object context, BaseTuple leftTuple, ValueResolver valueResolver ) {
        AccumulateContextEntry entry = (AccumulateContextEntry) context;
        return entry.isEmpty() ? null : innerAccumulate.getResult(workingMemoryContext, context, leftTuple, valueResolver);
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
}

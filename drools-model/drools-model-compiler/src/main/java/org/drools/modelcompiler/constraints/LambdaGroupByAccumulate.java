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

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.AccumulateNode.AccumulateContextEntry;
import org.drools.core.reteoo.AccumulateNode.GroupByContext;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.Tuple;
import org.drools.core.util.index.TupleList;
import org.drools.model.functions.Function1;
import org.drools.model.functions.FunctionN;


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

    private Object getKey( Tuple tuple, InternalFactHandle handle, WorkingMemory workingMemory ) {
        if (groupingFunction1 != null) {
            return groupingFunction1.apply( getValue( tuple, handle, workingMemory, groupingDeclarations[0] ) );
        }

        Object[] args = new Object[groupingDeclarations.length];
        for (int i = 0; i < groupingDeclarations.length; i++) {
            args[i] = getValue( tuple, handle, workingMemory, groupingDeclarations[i] );
        }
        return groupingFunction.apply( args );
    }

    private Object getValue( Tuple tuple, InternalFactHandle handle, WorkingMemory workingMemory, Declaration declaration ) {
        // we already have the handle, so avoid tuple iteration if not needed.
        // (is this really saving time, as get(int index) has pretty much the same check, at best saves some method call) (mdp)
        return declaration.getValue( ( InternalWorkingMemory )workingMemory, declaration.getTupleIndex() < tuple.size() ? tuple.get( declaration ).getObject() : handle.getObject() );
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
                       Object funcContext, Tuple leftTuple, WorkingMemory workingMemory) {
        // do nothing here, it's done when the group is first created
        return funcContext;
    }

    @Override
    public Object accumulate( Object workingMemoryContext, Object context,
                              Tuple match, InternalFactHandle handle, WorkingMemory wm ) {
        GroupByContext groupByContext = ( GroupByContext ) context;
        TupleList<AccumulateContextEntry> tupleList = groupByContext.getGroup(workingMemoryContext, innerAccumulate,
                                                                              match, getKey(match, handle, wm), wm);

        return accumulate(workingMemoryContext, match, handle, groupByContext, tupleList, wm);
    }

    @Override
    public Object accumulate(Object workingMemoryContext, Tuple match, InternalFactHandle handle,
                             GroupByContext groupByContext, TupleList<AccumulateContextEntry> tupleList, WorkingMemory wm) {
        groupByContext.moveToPropagateTupleList(tupleList);
        return innerAccumulate.accumulate(workingMemoryContext, tupleList.getContext(), match, handle, wm);
    }

    @Override
    public boolean tryReverse(Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle,
                              RightTuple rightParent, LeftTuple match, WorkingMemory workingMemory) {
        TupleList<AccumulateContextEntry> memory = match.getMemory();
        AccumulateContextEntry entry = memory.getContext();
        boolean reversed = innerAccumulate.tryReverse(workingMemoryContext, entry, leftTuple, handle, rightParent, match, workingMemory);

        if (reversed) {
            GroupByContext groupByContext = ( GroupByContext ) context;
            groupByContext.moveToPropagateTupleList( match.getMemory() );

            memory.remove( match );
            if ( memory.isEmpty() ) {
                groupByContext.removeGroup( entry.getKey() );
                memory.getContext().setEmpty( true );
            }
        }

        return reversed;
    }

    @Override
    public Object getResult( Object workingMemoryContext, Object context, Tuple leftTuple, WorkingMemory workingMemory ) {
        AccumulateContextEntry entry = (AccumulateContextEntry) context;
        return entry.isEmpty() ? null : innerAccumulate.getResult(workingMemoryContext, context, leftTuple, workingMemory);
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

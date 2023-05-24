/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.rule;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.AccumulateNode.AccumulateContextEntry;
import org.drools.core.reteoo.AccumulateNode.GroupByContext;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.Tuple;
import org.drools.core.rule.accessor.Accumulator;
import org.drools.core.rule.accessor.CompiledInvoker;
import org.drools.core.rule.accessor.ReturnValueExpression;
import org.drools.core.rule.accessor.Wireable;
import org.drools.core.util.index.TupleList;

public class MultiAccumulate extends Accumulate {
    private Accumulator[] accumulators;
    private ReturnValueExpression grouppingFunction;
    private int arraySize;

    public MultiAccumulate() { }

    public MultiAccumulate(final RuleConditionElement source,
                           final Declaration[] requiredDeclarations,
                           final Accumulator[] accumulators,
                           int arraySize) {
        super(source, requiredDeclarations);
        this.arraySize = arraySize;
        this.accumulators = accumulators;
    }

    public MultiAccumulate(final RuleConditionElement source,
            final Declaration[] requiredDeclarations,
            final ReturnValueExpression grouppingFunction,
            final Accumulator[] accumulators,
            int arraySize) {
        super(source, requiredDeclarations);
        this.arraySize = arraySize;
        this.grouppingFunction = grouppingFunction;
        this.accumulators = accumulators;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
        super.readExternal(in);
        arraySize = in.readInt();
        grouppingFunction = (ReturnValueExpression) in.readObject();
        this.accumulators = new Accumulator[in.readInt()];
        for ( int i = 0; i < this.accumulators.length; i++ ) {
            this.accumulators[i] = (Accumulator) in.readObject();
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(arraySize);
        if (CompiledInvoker.isCompiledInvoker(grouppingFunction)) {
            out.writeObject(null);
        } else {
            out.writeObject(grouppingFunction);
        }
        out.writeInt( accumulators.length );
        for (Accumulator acc : accumulators) {
            if (CompiledInvoker.isCompiledInvoker(acc)) {
                out.writeObject(null);
            } else {
                out.writeObject(acc);
            }
        }
    }

    private Object getKey( InternalFactHandle handle, Object context, Tuple tuple, ReteEvaluator reteEvaluator ) {
        try {
            return grouppingFunction.evaluate(handle, tuple, requiredDeclarations, requiredDeclarations, reteEvaluator, context);
        } catch (Exception e) {
            throw new RuntimeException("The grouping function threw an exception", e);
        }
    }

    public boolean isMultiFunction() {
        return true;
    }

    public Accumulator[] getAccumulators() {
        return this.accumulators;
    }

    public Object[] createFunctionContext() {
        Object[] ctxs = new Object[accumulators.length];
        for ( int i = 0; i < accumulators.length; i++ ) {
            // use the size of the accumulates not the arraySize, as the last element may be
            // left empty, for the groupby key
            ctxs[i] = this.accumulators[i].createContext();
        }
        return ctxs;
    }

    public Object init(final Object workingMemoryContext,
                       final Object accContext,
                       Object funcContext,
                       final Tuple leftTuple,
                       final ReteEvaluator reteEvaluator) {
        Object[] functionContext = (Object[]) funcContext;

        for ( int i = 0; i < this.accumulators.length; i++ ) {
            functionContext[i] = this.accumulators[i].init( ((Object[])workingMemoryContext)[i],
                                                            functionContext[i],
                                                            leftTuple,
                                                            this.requiredDeclarations,
                                                            reteEvaluator );
        }
        return funcContext;
    }

    public Object accumulate(final Object workingMemoryContext,
                             final Object context,
                             final Tuple match,
                             final InternalFactHandle handle,
                             final ReteEvaluator reteEvaluator) {
        if (context instanceof GroupByContext) {
            GroupByContext groupByContext = ( GroupByContext ) context;
            TupleList<AccumulateContextEntry> tupleList = groupByContext.getGroup(workingMemoryContext, this,
                    match, getKey(handle, context, match, reteEvaluator), reteEvaluator);

            return accumulate(workingMemoryContext, match, handle, groupByContext, tupleList, reteEvaluator);
        }
        Object[] values = new Object[accumulators.length];
        for ( int i = 0; i < this.accumulators.length; i++ ) {
            Object[] functionContext = (Object[]) ((AccumulateContextEntry)context).getFunctionContext();
            values[i] = this.accumulators[i].accumulate( ((Object[])workingMemoryContext)[i],
                                                         functionContext[i],
                                                         match,
                                                         handle,
                                                         this.requiredDeclarations,
                                                         getInnerDeclarationCache(),
                                                         reteEvaluator );
        }
        return values;
    }

    @Override
    public Object accumulate(Object workingMemoryContext, Tuple match, InternalFactHandle childHandle,
                             GroupByContext groupByContext, TupleList<AccumulateContextEntry> tupleList, ReteEvaluator reteEvaluator) {
        if (grouppingFunction == null) {
            throw new UnsupportedOperationException("This should never be called when grouppingFunction is null");
        }
        groupByContext.moveToPropagateTupleList(tupleList);
        return accumulate(workingMemoryContext, tupleList.getContext(), match, childHandle, reteEvaluator);
    }

    @Override
    public boolean tryReverse(final Object workingMemoryContext,
                              final Object context,
                              final Tuple leftTuple,
                              final InternalFactHandle handle,
                              final RightTuple rightParent,
                              final LeftTuple match,
                              final ReteEvaluator reteEvaluator) {
        Object[] values = (Object[]) match.getContextObject();
        for ( int i = 0; i < this.accumulators.length; i++ ) {
            Object[] functionContext = (Object[]) ((AccumulateContextEntry)context).getFunctionContext();
            boolean reversed = this.accumulators[i].tryReverse( ((Object[])workingMemoryContext)[i],
                                                                functionContext[i],
                                                                leftTuple,
                                                                handle,
                                                                values[i],
                                                                this.requiredDeclarations,
                                                                getInnerDeclarationCache(),
                                                                reteEvaluator );
            if (!reversed) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isGroupBy() {
        return grouppingFunction != null;
    }

    public boolean supportsReverse() {
        for ( Accumulator acc : this.accumulators ) {
            if ( ! acc.supportsReverse() ) {
                return false;
            }
        }
        return true;
    }

    public Object[] getResult(final Object workingMemoryContext,
                              final Object context,
                              final Tuple leftTuple,
                              final ReteEvaluator reteEvaluator) {
        Object[] results = new Object[arraySize];
        for ( int i = 0; i < this.accumulators.length; i++ ) {
            Object[] functionContext = (Object[]) ((AccumulateContextEntry)context).getFunctionContext();
            results[i] = this.accumulators[i].getResult( ((Object[])workingMemoryContext)[i],
                                                         functionContext[i],
                                                         leftTuple,
                                                         this.requiredDeclarations,
                                                         reteEvaluator );
        }
        return results;
    }

    public void replaceAccumulatorDeclaration(Declaration declaration, Declaration resolved) {
        for (Accumulator accumulator : accumulators) {
            accumulator.replaceDeclaration( declaration, resolved );
        }
    }

    public MultiAccumulate clone() {
        RuleConditionElement clonedSource = source instanceof GroupElement ? ((GroupElement) source).cloneOnlyGroup() : source.clone();
        MultiAccumulate clone = new MultiAccumulate( clonedSource,
                                                     this.requiredDeclarations,
                                                     this.accumulators,
                                                     this.arraySize);
        registerClone(clone);
        return clone;
    }

    public Object[] createWorkingMemoryContext() {
        Object[] ctx = new Object[ this.accumulators.length ];
        for( int i = 0; i < this.accumulators.length; i++ ) {
            ctx[i] = this.accumulators[i].createWorkingMemoryContext();
        }
        return ctx;
    }

    public final class GrouppingFunctionWirer implements Wireable.Immutable, Serializable {
        private static final long serialVersionUID = -9072646735174734614L;

        private transient boolean initialized;

        public GrouppingFunctionWirer( ) {
        }

        public void wire( Object object ) {
            ReturnValueExpression expression = (ReturnValueExpression) object;
            for ( Accumulate clone : cloned ) {
                ((MultiAccumulate)clone).grouppingFunction = expression;
            }
            initialized = true;
        }

        public boolean isInitialized() {
            return initialized;
        }
    }

    public final class Wirer implements Wireable.Immutable, Serializable {
        private static final long serialVersionUID = -9072646735174734614L;

        private transient boolean initialized;

        private final int index;

        public Wirer( int index ) {
            this.index = index;
        }

        public void wire( Object object ) {
            Accumulator accumulator = (Accumulator) object;
            accumulators[index] = accumulator;
            for ( Accumulate clone : cloned ) {
                ((MultiAccumulate)clone).accumulators[index] = accumulator;
            }
            initialized = true;
        }

        public boolean isInitialized() {
            return initialized;
        }
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(accumulators);
        result = prime * result + Objects.hashCode(grouppingFunction);
        result = prime * result + Arrays.hashCode( requiredDeclarations );
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        MultiAccumulate other = (MultiAccumulate) obj;
        if ( !Arrays.equals( accumulators, other.accumulators ) ) return false;
        if ( !Objects.equals( grouppingFunction, other.grouppingFunction ) ) return false;
        if ( !Arrays.equals( requiredDeclarations, other.requiredDeclarations ) ) return false;
        if ( source == null ) {
            return other.source == null;
        } else {
            return source.equals( other.source );
        }
    }
}

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
import org.drools.core.reteoo.BaseLeftTuple;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.Tuple;
import org.drools.core.rule.accessor.Accumulator;
import org.drools.core.rule.accessor.CompiledInvoker;
import org.drools.core.rule.accessor.ReturnValueExpression;
import org.drools.core.rule.accessor.Wireable;
import org.drools.core.util.index.TupleList;

public class SingleAccumulate extends Accumulate {
    private Accumulator accumulator;
    private ReturnValueExpression grouppingFunction;

    public SingleAccumulate() { }

    public SingleAccumulate(final RuleConditionElement source,
                            final Declaration[] requiredDeclarations) {
        super(source, requiredDeclarations);
    }

    public SingleAccumulate(final RuleConditionElement source,
                            final Declaration[] requiredDeclarations,
                            final Accumulator accumulator ) {
        super(source, requiredDeclarations);
        this.accumulator = accumulator;
    }

    public SingleAccumulate(final RuleConditionElement source,
            final Declaration[] requiredDeclarations,
            final ReturnValueExpression grouppingFunction,
            final Accumulator accumulator ) {
        super(source, requiredDeclarations);
        this.grouppingFunction = grouppingFunction;
        this.accumulator = accumulator;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
        super.readExternal(in);
        this.grouppingFunction = (ReturnValueExpression) in.readObject();
        this.accumulator = (Accumulator) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        if (CompiledInvoker.isCompiledInvoker(grouppingFunction)) {
            out.writeObject(null);
        } else {
            out.writeObject(grouppingFunction);
        }
        if (CompiledInvoker.isCompiledInvoker(accumulator)) {
            out.writeObject(null);
        } else {
            out.writeObject(accumulator);
        }
    }

    public boolean isMultiFunction() {
        return false;
    }

    public Accumulator[] getAccumulators() {
        return new Accumulator[] { this.accumulator };
    }

    public Object createFunctionContext() {
        return this.accumulator.createContext();
    }

    private Object getKey( final InternalFactHandle handle, final Object context, final Tuple tuple, final ReteEvaluator reteEvaluator ) {
        try {
            Tuple keyTuple = new BaseLeftTuple(handle, (LeftTuple) tuple, tuple.getTupleSink());
            Object out = grouppingFunction.evaluate(handle, keyTuple, requiredDeclarations, getInnerDeclarationCache(), reteEvaluator, grouppingFunction.createContext());
            return out;
        } catch (Exception e) {
            throw new RuntimeException("The grouping function threw an exception", e);
        }
    }

    public Object init(final Object workingMemoryContext,
                       final Object accContext,
                       final Object funcContext, final Tuple leftTuple,
                       final ReteEvaluator reteEvaluator) {
        Object returned = this.accumulator.init( workingMemoryContext,
                                                 funcContext, leftTuple,
                                                 this.requiredDeclarations, reteEvaluator );
        return returned;
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
        return this.accumulator.accumulate( workingMemoryContext,
                                            ((AccumulateContextEntry)context).getFunctionContext(),
                                            match,
                                            handle,
                                            this.requiredDeclarations,
                                            getInnerDeclarationCache(),
                                            reteEvaluator );
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
    public boolean isGroupBy() {
        return grouppingFunction != null;
    }

    @Override
    public boolean tryReverse(final Object workingMemoryContext,
                              final Object context,
                              final Tuple leftTuple,
                              final InternalFactHandle handle,
                              final RightTuple rightParent,
                              final LeftTuple match,
                              final ReteEvaluator reteEvaluator) {
        return this.accumulator.tryReverse( workingMemoryContext,
                                            ((AccumulateContextEntry)context).getFunctionContext(),
                                            leftTuple,
                                            handle,
                                            match.getContextObject(),
                                            this.requiredDeclarations,
                                            getInnerDeclarationCache(),
                                            reteEvaluator );
    }

    public boolean supportsReverse() {
        return this.accumulator.supportsReverse();
    }


    public Object getResult(final Object workingMemoryContext,
                            final Object context,
                            final Tuple leftTuple,
                            final ReteEvaluator reteEvaluator) {
        return this.accumulator.getResult( workingMemoryContext,
                                           ((AccumulateContextEntry)context).getFunctionContext(),
                                           leftTuple,
                                           this.requiredDeclarations,
                                           reteEvaluator );
    }

    public SingleAccumulate clone() {
        RuleConditionElement clonedSource = source instanceof GroupElement ? ((GroupElement) source).cloneOnlyGroup() : source.clone();
        SingleAccumulate clone = new SingleAccumulate( clonedSource,
                                                       this.requiredDeclarations,
                                                       this.grouppingFunction,
                                                       this.accumulator);
        registerClone(clone);
        return clone;
    }

    public void replaceAccumulatorDeclaration(Declaration declaration, Declaration resolved) {
        accumulator.replaceDeclaration( declaration, resolved );
        if (grouppingFunction != null) {
            grouppingFunction.replaceDeclaration(declaration, resolved);
        }
    }

    public Object createWorkingMemoryContext() {
        return this.accumulator.createWorkingMemoryContext();
    }

    public final class GrouppingFunctionWirer implements Wireable.Immutable, Serializable {
        private static final long serialVersionUID = -9072646735174734614L;

        private transient boolean initialized;

        public GrouppingFunctionWirer( ) {
        }

        public void wire( Object object ) {
            ReturnValueExpression expression = (ReturnValueExpression) object;
            grouppingFunction = expression;
            for ( Accumulate clone : cloned ) {
                ((SingleAccumulate)clone).grouppingFunction = expression;
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

        public void wire( Object object ) {
            Accumulator acc = (Accumulator) object;
            accumulator = acc;
            for ( Accumulate clone : cloned ) {
                ((SingleAccumulate)clone).accumulator = acc;
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
        result = prime * result + accumulator.hashCode();
        result = prime * result + Objects.hashCode(grouppingFunction);
        result = prime * result + Arrays.hashCode( requiredDeclarations );
        result = prime * result + Arrays.hashCode( innerDeclarationCache );
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        SingleAccumulate other = (SingleAccumulate) obj;
        if ( !accumulator.equals( other.accumulator ) ) return false;
        if ( !Objects.equals(grouppingFunction, other.grouppingFunction) ) return false;
        if ( !Arrays.equals( requiredDeclarations, other.requiredDeclarations ) ) return false;
        if ( !Arrays.equals( innerDeclarationCache, other.innerDeclarationCache ) ) return false;
        if ( source == null ) {
            return other.source == null;
        } else {
            return source.equals( other.source );
        }
    }
}

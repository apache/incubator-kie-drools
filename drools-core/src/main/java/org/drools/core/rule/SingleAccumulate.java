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

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.AccumulateNode.AccumulateContextEntry;
import org.drools.core.reteoo.AccumulateNode.GroupByContext;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.MvelAccumulator;
import org.drools.core.spi.Tuple;
import org.drools.core.spi.Wireable;
import org.drools.core.util.index.TupleList;
import org.kie.internal.security.KiePolicyHelper;

public class SingleAccumulate extends Accumulate {
    private Accumulator accumulator;

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

    public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
        super.readExternal(in);
        this.accumulator = (Accumulator) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        if (Accumulator.isCompiledInvoker(accumulator)) {
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

    public Object init(final Object workingMemoryContext,
                       final Object accContext,
                       final Object funcContext, final Tuple leftTuple,
                       final WorkingMemory workingMemory) {
        Object returned = this.accumulator.init( workingMemoryContext,
                                                 funcContext, leftTuple,
                                                 this.requiredDeclarations, workingMemory );
        return returned;
    }

    public Object accumulate(final Object workingMemoryContext,
                             final Object context,
                             final Tuple match,
                             final InternalFactHandle handle,
                             final WorkingMemory workingMemory) {
        return this.accumulator.accumulate( workingMemoryContext,
                                            ((AccumulateContextEntry)context).getFunctionContext(),
                                            match,
                                            handle,
                                            this.requiredDeclarations,
                                            getInnerDeclarationCache(),
                                            workingMemory );
    }

    @Override
    public Object accumulate(Object workingMemoryContext, Tuple match, InternalFactHandle childHandle,
                             GroupByContext groupByContext, TupleList<AccumulateContextEntry> tupleList, WorkingMemory wm) {
        throw new UnsupportedOperationException("This should never be called, it's for LambdaGroupByAccumulate only.");
    }

    @Override
    public boolean tryReverse(final Object workingMemoryContext,
                              final Object context,
                              final Tuple leftTuple,
                              final InternalFactHandle handle,
                              final RightTuple rightParent,
                              final LeftTuple match,
                              final WorkingMemory workingMemory) {
        return this.accumulator.tryReverse( workingMemoryContext,
                                            ((AccumulateContextEntry)context).getFunctionContext(),
                                            leftTuple,
                                            handle,
                                            match.getContextObject(),
                                            this.requiredDeclarations,
                                            getInnerDeclarationCache(),
                                            workingMemory );
    }

    public boolean supportsReverse() {
        return this.accumulator.supportsReverse();
    }


    public Object getResult(final Object workingMemoryContext,
                            final Object context,
                            final Tuple leftTuple,
                            final WorkingMemory workingMemory) {
        return this.accumulator.getResult( workingMemoryContext,
                                           ((AccumulateContextEntry)context).getFunctionContext(),
                                           leftTuple,
                                           this.requiredDeclarations,
                                           workingMemory );
    }

    public SingleAccumulate clone() {
        RuleConditionElement clonedSource = source instanceof GroupElement ? ((GroupElement) source).cloneOnlyGroup() : source.clone();
        SingleAccumulate clone = new SingleAccumulate( clonedSource,
                                                       this.requiredDeclarations,
                                                       this.accumulator );
        registerClone(clone);
        return clone;
    }

    public void replaceAccumulatorDeclaration(Declaration declaration, Declaration resolved) {
        if (accumulator instanceof MvelAccumulator ) {
            ( (MvelAccumulator) accumulator ).replaceDeclaration( declaration, resolved );
        }
    }

    public Object createWorkingMemoryContext() {
        return this.accumulator.createWorkingMemoryContext();
    }

    public final class Wirer implements Wireable.Immutable, Serializable {
        private static final long serialVersionUID = -9072646735174734614L;

        private transient boolean initialized;

        public void wire( Object object ) {
            Accumulator acc = KiePolicyHelper.isPolicyEnabled() ? new Accumulator.SafeAccumulator((Accumulator) object) : (Accumulator) object;
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
        if ( !Arrays.equals( requiredDeclarations, other.requiredDeclarations ) ) return false;
        if ( !Arrays.equals( innerDeclarationCache, other.innerDeclarationCache ) ) return false;
        if ( source == null ) {
            return other.source == null;
        } else {
            return source.equals( other.source );
        }
    }
}

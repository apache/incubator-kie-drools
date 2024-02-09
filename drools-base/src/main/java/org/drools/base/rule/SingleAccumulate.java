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
package org.drools.base.rule;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Arrays;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.AccumulateContextEntry;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.accessor.Accumulator;
import org.drools.base.rule.accessor.CompiledInvoker;
import org.drools.base.rule.accessor.Wireable;
import org.kie.api.runtime.rule.FactHandle;

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

    public Object init(final Object workingMemoryContext,
                       final Object accContext,
                       final Object funcContext, final BaseTuple leftTuple,
                       final ValueResolver valueResolver) {
        Object returned = this.accumulator.init( workingMemoryContext,
                                                 funcContext, leftTuple,
                                                 this.requiredDeclarations, valueResolver );
        return returned;
    }

    public Object accumulate(final Object workingMemoryContext,
                             final Object context,
                             final BaseTuple match,
                             final FactHandle handle,
                             final ValueResolver valueResolver) {
        return this.accumulator.accumulate( workingMemoryContext,
                                            ((AccumulateContextEntry)context).getFunctionContext(),
                                            match,
                                            handle,
                                            this.requiredDeclarations,
                                            getInnerDeclarationCache(),
                                            valueResolver );
    }

    @Override
    public Object accumulate(Object workingMemoryContext, BaseTuple match, FactHandle childHandle,
                             Object groupByContext, Object tupleList, ValueResolver valueResolver) {
        throw new UnsupportedOperationException("This should never be called, it's for LambdaGroupByAccumulate only.");
    }

    @Override
    public boolean tryReverse(final Object workingMemoryContext,
                              final Object context,
                              final BaseTuple leftTuple,
                              final FactHandle handle,
                              final BaseTuple match,
                              final ValueResolver valueResolver) {
        return this.accumulator.tryReverse( workingMemoryContext,
                                            ((AccumulateContextEntry)context).getFunctionContext(),
                                            leftTuple,
                                            handle,
                                            match.getContextObject(),
                                            this.requiredDeclarations,
                                            getInnerDeclarationCache(),
                                            valueResolver );
    }

    public boolean supportsReverse() {
        return this.accumulator.supportsReverse();
    }


    public Object getResult(final Object workingMemoryContext,
                            final Object context,
                            final BaseTuple leftTuple,
                            final ValueResolver valueResolver) {
        return this.accumulator.getResult( workingMemoryContext,
                                           ((AccumulateContextEntry)context).getFunctionContext(),
                                           leftTuple,
                                           this.requiredDeclarations,
                                           valueResolver );
    }

    public SingleAccumulate clone() {
        RuleConditionElement clonedSource = source instanceof GroupElement ge ? ge.cloneOnlyGroup() : source.clone();
        SingleAccumulate clone = new SingleAccumulate( clonedSource, this.requiredDeclarations, this.accumulator );
        registerClone(clone);
        return clone;
    }

    public void replaceAccumulatorDeclaration(Declaration declaration, Declaration resolved) {
        accumulator.replaceDeclaration( declaration, resolved );
    }

    public Object createWorkingMemoryContext() {
        return this.accumulator.createWorkingMemoryContext();
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
        result = prime * result + Arrays.hashCode( requiredDeclarations );
        result = prime * result + Arrays.hashCode( innerDeclarationCache );
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        SingleAccumulate other = (SingleAccumulate) obj;
        if ( !accumulator.equals( other.accumulator ) ) {
            return false;
        }
        if ( !Arrays.equals( requiredDeclarations, other.requiredDeclarations ) ) {
            return false;
        }
        if ( !Arrays.equals( innerDeclarationCache, other.innerDeclarationCache ) ) {
            return false;
        }
        if ( source == null ) {
            return other.source == null;
        } else {
            return source.equals( other.source );
        }
    }
}

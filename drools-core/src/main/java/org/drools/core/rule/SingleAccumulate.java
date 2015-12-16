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

import org.drools.core.WorkingMemory;
import org.drools.core.base.accumulators.MVELAccumulatorFunctionExecutor;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.CompiledInvoker;
import org.drools.core.spi.Tuple;
import org.drools.core.spi.Wireable;
import org.kie.internal.security.KiePolicyHelper;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Arrays;

public class SingleAccumulate extends Accumulate {
    private Accumulator accumulator;

    public SingleAccumulate() { }

    public SingleAccumulate(final RuleConditionElement source,
                            final Declaration[] requiredDeclarations ) {
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
        if ( accumulator instanceof CompiledInvoker) {
            out.writeObject( null );
        } else {
            out.writeObject( accumulator );
        }
    }

    public boolean isMultiFunction() {
        return false;
    }

    public Accumulator[] getAccumulators() {
        return new Accumulator[] { this.accumulator };
    }

    public Serializable createContext() {
        return this.accumulator.createContext();
    }

    public void init(final Object workingMemoryContext,
                     final Object context,
                     final Tuple leftTuple,
                     final WorkingMemory workingMemory) {
        try {
            this.accumulator.init( workingMemoryContext,
                                   context,
                                   leftTuple,
                                   this.requiredDeclarations,
                                   workingMemory );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public void accumulate(final Object workingMemoryContext,
                           final Object context,
                           final Tuple leftTuple,
                           final InternalFactHandle handle,
                           final WorkingMemory workingMemory) {
        try {
            this.accumulator.accumulate( workingMemoryContext,
                                         context,
                                         leftTuple,
                                         handle,
                                         this.requiredDeclarations,
                                         getInnerDeclarationCache(),
                                         workingMemory );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public void reverse(final Object workingMemoryContext,
                        final Object context,
                        final Tuple leftTuple,
                        final InternalFactHandle handle,
                        final WorkingMemory workingMemory) {
        try {
            this.accumulator.reverse( workingMemoryContext,
                                      context,
                                      leftTuple,
                                      handle,
                                      this.requiredDeclarations,
                                      getInnerDeclarationCache(),
                                      workingMemory );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public boolean supportsReverse() {
        return this.accumulator.supportsReverse();
    }


    public Object getResult(final Object workingMemoryContext,
                            final Object context,
                            final Tuple leftTuple,
                            final WorkingMemory workingMemory) {
        try {
            return this.accumulator.getResult( workingMemoryContext,
                                               context,
                                               leftTuple,
                                               this.requiredDeclarations,
                                               workingMemory );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public SingleAccumulate clone() {
        RuleConditionElement clonedSource = source instanceof GroupElement ? ((GroupElement) source).cloneOnlyGroup() : source.clone();
        SingleAccumulate clone = new SingleAccumulate( clonedSource,
                                                       this.requiredDeclarations,
                                                       this.accumulator );
        registerClone(clone);
        return clone;
    }

    protected void replaceAccumulatorDeclaration(Declaration declaration, Declaration resolved) {
        if (accumulator instanceof MVELAccumulatorFunctionExecutor) {
            ( (MVELAccumulatorFunctionExecutor) accumulator ).replaceDeclaration( declaration, resolved );
        }
    }

    public Object createWorkingMemoryContext() {
        return this.accumulator.createWorkingMemoryContext();
    }

    public final class Wirer implements Wireable, Serializable {
        private static final long serialVersionUID = -9072646735174734614L;

        public void wire( Object object ) {
            Accumulator acc = KiePolicyHelper.isPolicyEnabled() ? new Accumulator.SafeAccumulator((Accumulator) object) : (Accumulator) object;
            accumulator = acc;
            for ( Accumulate clone : cloned ) {
                ((SingleAccumulate)clone).accumulator = acc;
            }
        }
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + accumulator.hashCode();
        result = prime * result + Arrays.hashCode( requiredDeclarations );
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
        if ( source == null ) {
            if ( other.source != null ) return false;
        } else if ( !source.equals( other.source ) ) return false;
        return true;
    }
}

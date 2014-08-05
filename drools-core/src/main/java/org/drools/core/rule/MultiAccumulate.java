package org.drools.core.rule;

import org.drools.core.WorkingMemory;
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

public class MultiAccumulate extends Accumulate {
    private Accumulator[] accumulators;

    public MultiAccumulate() { }

    public MultiAccumulate(final RuleConditionElement source,
                           final Declaration[] requiredDeclarations,
                           final Accumulator[] accumulators ) {
        super(source, requiredDeclarations);
        this.accumulators = accumulators;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
        super.readExternal(in);
        this.accumulators = new Accumulator[in.readInt()];
        for ( int i = 0; i < this.accumulators.length; i++ ) {
            this.accumulators[i] = (Accumulator) in.readObject();
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt( accumulators.length );
        for ( Accumulator acc : accumulators ) {
            if ( acc instanceof CompiledInvoker) {
                out.writeObject( null );
            } else {
                out.writeObject( acc );
            }
        }
    }

    public boolean isMultiFunction() {
        return true;
    }

    public Accumulator[] getAccumulators() {
        return this.accumulators;
    }

    public Serializable[] createContext() {
        Serializable[] ctxs = new Serializable[this.accumulators.length];
        for ( int i = 0; i < ctxs.length; i++ ) {
            ctxs[i] = this.accumulators[i].createContext();
        }
        return ctxs;
    }

    public void init(final Object workingMemoryContext,
                     final Object context,
                     final Tuple leftTuple,
                     final WorkingMemory workingMemory) {
        try {
            for ( int i = 0; i < this.accumulators.length; i++ ) {
                this.accumulators[i].init( ((Object[])workingMemoryContext)[i],
                                           ((Object[])context)[i],
                                           leftTuple,
                                           this.requiredDeclarations,
                                           workingMemory );
            }
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
            for ( int i = 0; i < this.accumulators.length; i++ ) {
                this.accumulators[i].accumulate( ((Object[])workingMemoryContext)[i],
                                                 ((Object[])context)[i],
                                                 leftTuple,
                                                 handle,
                                                 this.requiredDeclarations,
                                                 getInnerDeclarationCache(),
                                                 workingMemory );
            }
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
            for ( int i = 0; i < this.accumulators.length; i++ ) {
                this.accumulators[i].reverse( ((Object[])workingMemoryContext)[i],
                                              ((Object[])context)[i],
                                              leftTuple,
                                              handle,
                                              this.requiredDeclarations,
                                              getInnerDeclarationCache(),
                                              workingMemory );
            }
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public boolean supportsReverse() {
        boolean supports = true;
        for( Accumulator acc : this.accumulators ) {
            if( ! acc.supportsReverse() ) {
                supports = false;
                break;
            }
        }
        return supports;
    }

    public Object[] getResult(final Object workingMemoryContext,
                              final Object context,
                              final Tuple leftTuple,
                              final WorkingMemory workingMemory) {
        try {
            Object[] results = new Object[this.accumulators.length];
            for ( int i = 0; i < this.accumulators.length; i++ ) {
                results[i] = this.accumulators[i].getResult( ((Object[])workingMemoryContext)[i],
                                                             ((Object[])context)[i],
                                                             leftTuple,
                                                             this.requiredDeclarations,
                                                             workingMemory );
            }
            return results;
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public MultiAccumulate clone() {
        RuleConditionElement clonedSource = source instanceof GroupElement ? ((GroupElement) source).cloneOnlyGroup() : source.clone();
        MultiAccumulate clone = new MultiAccumulate( clonedSource,
                                                     this.requiredDeclarations,
                                                     this.accumulators );
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

    public final class Wirer implements Wireable, Serializable {
        private static final long serialVersionUID = -9072646735174734614L;

        private final int index;

        public Wirer( int index ) {
            this.index = index;
        }

        public void wire( Object object ) {
            Accumulator accumulator = KiePolicyHelper.isPolicyEnabled() ? new Accumulator.SafeAccumulator((Accumulator) object) : (Accumulator) object;
            accumulators[index] = accumulator;
            for ( Accumulate clone : cloned ) {
                ((MultiAccumulate)clone).accumulators[index] = accumulator;
            }
        }
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(accumulators);
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
        if ( !Arrays.equals( requiredDeclarations, other.requiredDeclarations ) ) return false;
        if ( source == null ) {
            if ( other.source != null ) return false;
        } else if ( !source.equals( other.source ) ) return false;
        return true;
    }
}

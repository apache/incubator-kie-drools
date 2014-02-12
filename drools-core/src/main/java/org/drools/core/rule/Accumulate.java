/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.Accumulator.SafeAccumulator;
import org.drools.core.spi.CompiledInvoker;
import org.drools.core.spi.Tuple;
import org.drools.core.spi.Wireable;
import org.kie.internal.security.KiePolicyHelper;

/**
 * A class to represent the Accumulate CE
 */
public class Accumulate extends ConditionalElement
    implements
    PatternSource {

    private static final long    serialVersionUID = 510l;

    private Accumulator[]        accumulators;
    private RuleConditionElement source;
    private Declaration[]        requiredDeclarations;
    private Declaration[]        innerDeclarationCache;
    private boolean              multiFunction;

    private List<Accumulate>     cloned           = Collections.<Accumulate> emptyList();

    public Accumulate() {

    }

    public Accumulate(final RuleConditionElement source) {

        this( source,
              new Declaration[0],
              new Accumulator[1], // default is 1 accumulator
              false );
    }

    public Accumulate(final RuleConditionElement source,
                      final Declaration[] requiredDeclarations ) {

        this( source,
              requiredDeclarations,
              new Accumulator[1], // default is 1 accumulator
              false );
    }

    public Accumulate(final RuleConditionElement source,
                      final Declaration[] requiredDeclarations,
                      final Accumulator[] accumulators,
                      final boolean multiFunction ) {

        this.source = source;
        this.requiredDeclarations = requiredDeclarations;
        this.accumulators = accumulators;
        this.multiFunction = multiFunction;
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.accumulators = new Accumulator[in.readInt()];
        for ( int i = 0; i < this.accumulators.length; i++ ) {
            this.accumulators[i] = (Accumulator) in.readObject();
        }
        this.multiFunction = in.readBoolean();
        source = (RuleConditionElement) in.readObject();
        requiredDeclarations = (Declaration[]) in.readObject();
        this.cloned = (List<Accumulate>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt( accumulators.length );
        for ( Accumulator acc : accumulators ) {
            if ( acc instanceof CompiledInvoker ) {
                out.writeObject( null );
            } else {
                out.writeObject( acc );
            }
        }
        out.writeBoolean( multiFunction );
        out.writeObject( this.source );
        out.writeObject( this.requiredDeclarations );
        out.writeObject( this.cloned );
    }

    public Accumulator[] getAccumulators() {
        return this.accumulators;
    }

    public void setAccumulators(final Accumulator[] accumulators) {
        this.accumulators = accumulators;
    }

    public Serializable[] createContext() {
        Serializable[] ctxs = new Serializable[this.accumulators.length];
        for ( int i = 0; i < ctxs.length; i++ ) {
            ctxs[i] = this.accumulators[i].createContext();
        }
        return ctxs;
    }

    /**
     * Executes the initialization block of code
     */
    public void init(final Object[] workingMemoryContext,
                     final Object[] context,
                     final Tuple leftTuple,
                     final WorkingMemory workingMemory) {
        try {
            for ( int i = 0; i < this.accumulators.length; i++ ) {
                this.accumulators[i].init( workingMemoryContext[i],
                                           context[i],
                                           leftTuple,
                                           this.requiredDeclarations,
                                           workingMemory );
            }
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Executes the accumulate (action) code for the given fact handle
     */
    public void accumulate(final Object[] workingMemoryContext,
                           final Object[] context,
                           final Tuple leftTuple,
                           final InternalFactHandle handle,
                           final WorkingMemory workingMemory) {
        try {
            for ( int i = 0; i < this.accumulators.length; i++ ) {
                this.accumulators[i].accumulate( workingMemoryContext[i],
                                                 context[i],
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

    /**
     * Executes the reverse (action) code for the given fact handle
     */
    public void reverse(final Object[] workingMemoryContext,
                        final Object[] context,
                        final Tuple leftTuple,
                        final InternalFactHandle handle,
                        final WorkingMemory workingMemory) {
        try {
            for ( int i = 0; i < this.accumulators.length; i++ ) {
                this.accumulators[i].reverse( workingMemoryContext[i],
                                              context[i],
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

    /**
     * Gets the result of the accumulation
     */
    public Object[] getResult(final Object[] workingMemoryContext,
                            final Object[] context,
                            final Tuple leftTuple,
                            final WorkingMemory workingMemory) {
        try {
            Object[] results = new Object[this.accumulators.length];
            for ( int i = 0; i < this.accumulators.length; i++ ) {
                results[i] = this.accumulators[i].getResult( workingMemoryContext[i],
                                                             context[i],
                                                             leftTuple,
                                                             this.requiredDeclarations,
                                                             workingMemory );
            }
            return results;
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Returns true if this accumulate supports reverse
     * @return
     */
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

    public Accumulate clone() {
        RuleConditionElement clonedSource = source instanceof GroupElement ? ((GroupElement) source).cloneOnlyGroup() : source.clone();
        Accumulate clone = new Accumulate( clonedSource,
                                           this.requiredDeclarations,
                                           this.accumulators,
                                           this.multiFunction );

        if ( this.cloned == Collections.EMPTY_LIST ) {
            this.cloned = new ArrayList<Accumulate>( 1 );
        }

        this.cloned.add( clone );

        return clone;
    }

    public RuleConditionElement getSource() {
        return this.source;
    }

    public Map<String, Declaration> getInnerDeclarations() {
        return this.source.getInnerDeclarations();
    }

    public Map<String, Declaration> getOuterDeclarations() {
        return Collections.emptyMap();
    }
    
    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return (Declaration) this.source.getInnerDeclarations().get( identifier );
    }

    public Object[] createWorkingMemoryContext() {
        Object[] ctx = new Object[ this.accumulators.length ];
        for( int i = 0; i < this.accumulators.length; i++ ) {
            ctx[i] = this.accumulators[i].createWorkingMemoryContext();
        }
        return ctx;
    }

    public List<RuleConditionElement> getNestedElements() {
        return Collections.singletonList( this.source );
    }

    public boolean isPatternScopeDelimiter() {
        return true;
    }

    /**
     * @return the multiFunction
     */
    public boolean isMultiFunction() {
        return multiFunction;
    }

    /**
     * @param multiFunction the multiFunction to set
     */
    public void setMultiFunction( boolean multiFunction ) {
        this.multiFunction = multiFunction;
    }
    
    public void replaceDeclaration(Declaration declaration,
                                   Declaration resolved) {
        for ( int i = 0; i < this.requiredDeclarations.length; i++ ) {
            if ( this.requiredDeclarations[i].equals( declaration ) ) {
                this.requiredDeclarations[i] = resolved;
            }
        }
    }
    
    public void resetInnerDeclarationCache() {
        this.innerDeclarationCache = null;
    }
    
    private Declaration[] getInnerDeclarationCache() {
        if( this.innerDeclarationCache == null ) {
            Map<String, Declaration> innerDeclarations = this.source.getInnerDeclarations();
            this.innerDeclarationCache = innerDeclarations.values().toArray( new Declaration[innerDeclarations.size()] );
            Arrays.sort( this.innerDeclarationCache, RuleTerminalNode.SortDeclarations.instance );
        }
        return this.innerDeclarationCache;
    }
    
    public final class Wirer implements Wireable, Serializable {
        private static final long serialVersionUID = -9072646735174734614L;
        
        private final int index;
        
        public Wirer( int index ) {
            this.index = index;
        }

        public void wire( Object object ) {
            Accumulator accumulator = KiePolicyHelper.isPolicyEnabled() ? new SafeAccumulator((Accumulator) object) : (Accumulator) object;
            accumulators[index] = accumulator;
            for ( Accumulate clone : cloned ) {
                clone.accumulators[index] = accumulator;
            }
        }
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode( accumulators );
        //result = prime * result + Arrays.hashCode( localDeclarations );
        result = prime * result + (multiFunction ? 1231 : 1237);
        result = prime * result + Arrays.hashCode( requiredDeclarations );
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Accumulate other = (Accumulate) obj;
        if ( !Arrays.equals( accumulators, other.accumulators ) ) return false;
        //if ( !Arrays.equals( localDeclarations, other.localDeclarations ) ) return false;
        if ( multiFunction != other.multiFunction ) return false;
        if ( !Arrays.equals( requiredDeclarations, other.requiredDeclarations ) ) return false;
        if ( source == null ) {
            if ( other.source != null ) return false;
        } else if ( !source.equals( other.source ) ) return false;
        return true;
    }

}

/**
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

package org.drools.rule;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.RuntimeDroolsException;
import org.drools.common.InternalFactHandle;
import org.drools.WorkingMemory;
import org.drools.spi.Accumulator;
import org.drools.spi.CompiledInvoker;
import org.drools.spi.Tuple;
import org.drools.spi.Wireable;

/**
 * A class to represent the Accumulate CE
 */
public class Accumulate extends ConditionalElement
    implements
    Wireable,
    PatternSource {

    private static final long    serialVersionUID = 400L;

    private Accumulator          accumulator;
    private RuleConditionElement source;
    private Declaration[]        requiredDeclarations;
    private Declaration[]        innerDeclarations;
    
    private List<Accumulate> cloned = Collections.<Accumulate>emptyList();

    public Accumulate() {

    }

    public Accumulate(final RuleConditionElement source) {

        this( source,
              new Declaration[0],
              new Declaration[0],
              null );
    }

    public Accumulate(final RuleConditionElement source,
                      final Declaration[] requiredDeclarations,
                      final Declaration[] innerDeclarations) {

        this( source,
              requiredDeclarations,
              innerDeclarations,
              null );
    }

    public Accumulate(final RuleConditionElement source,
                      final Declaration[] requiredDeclarations,
                      final Declaration[] innerDeclarations,
                      final Accumulator accumulator) {

        this.source = source;
        this.requiredDeclarations = requiredDeclarations;
        this.innerDeclarations = innerDeclarations;
        this.accumulator = accumulator;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        accumulator = (Accumulator)in.readObject();
        source = (RuleConditionElement)in.readObject();
        requiredDeclarations = (Declaration[])in.readObject();
        innerDeclarations = (Declaration[])in.readObject();
        this.cloned = (List<Accumulate>) in.readObject();        
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        if ( this.accumulator instanceof CompiledInvoker ) {
            out.writeObject(  null );
        } else {
            out.writeObject(this.accumulator);   
        }
        out.writeObject(this.source);
        out.writeObject(this.requiredDeclarations);
        out.writeObject(this.innerDeclarations);
        out.writeObject( this.cloned );
    }

    public Accumulator getAccumulator() {
        return this.accumulator;
    }
    
    public void wire(Object object) {
        setAccumulator( (Accumulator) object );
        for ( Accumulate clone : this.cloned ) {
            clone.wire( object );
        }        
    }

    public void setAccumulator(final Accumulator accumulator) {
        this.accumulator = accumulator;
    }

    public Serializable createContext() {
        return this.accumulator.createContext();
    }

    /**
     * Executes the initialization block of code
     *
     * @param leftTuple tuple causing the rule fire
     * @param declarations previous declarations
     * @param workingMemory
     * @throws Exception
     */
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
            throw new RuntimeDroolsException( e );
        }
    }

    /**
     * Executes the accumulate (action) code for the given fact handle
     *
     * @param leftTuple
     * @param handle
     * @param declarations
     * @param innerDeclarations
     * @param workingMemory
     * @throws Exception
     */
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
                                         this.innerDeclarations,
                                         workingMemory );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    /**
     * Executes the reverse (action) code for the given fact handle
     *
     * @param leftTuple
     * @param handle
     * @param declarations
     * @param innerDeclarations
     * @param workingMemory
     * @throws Exception
     */
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
                                      this.innerDeclarations,
                                      workingMemory );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    /**
     * Gets the result of the accummulation
     *
     * @param leftTuple
     * @param declarations
     * @param workingMemory
     * @return
     * @throws Exception
     */
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
            throw new RuntimeDroolsException( e );
        }
    }

    /**
     * Returns true if this accumulate supports reverse
     * @return
     */
    public boolean supportsReverse() {
        return this.accumulator.supportsReverse();
    }

    public Object clone() {
        Accumulate clone = new Accumulate( this.source,
                               this.requiredDeclarations,
                               this.innerDeclarations,
                               this.accumulator );
        
        if ( this.cloned == Collections.EMPTY_LIST ) {
            this.cloned = new ArrayList<Accumulate>(1);
        }
        
        this.cloned.add( clone );
        
        return clone;
    }

    public RuleConditionElement getSource() {
        return this.source;
    }

    public Map getInnerDeclarations() {
        return this.source.getInnerDeclarations();
    }

    public Map getOuterDeclarations() {
        return Collections.EMPTY_MAP;
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return (Declaration) this.source.getInnerDeclarations().get( identifier );
    }

    public Object createWorkingMemoryContext() {
        return this.accumulator.createWorkingMemoryContext();
    }

    public List getNestedElements() {
        return Collections.singletonList( this.source );
    }

    public boolean isPatternScopeDelimiter() {
        return true;
    }

}

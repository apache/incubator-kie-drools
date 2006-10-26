/*
 * Copyright 2005 JBoss Inc
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

package org.drools.reteoo;

import java.util.ArrayList;
import java.util.List;

import org.drools.common.BetaConstraints;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.Accumulate;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.PropagationContext;
import org.drools.util.Iterator;
import org.drools.util.AbstractHashTable.FactEntry;
import org.drools.util.ObjectHashMap.ObjectEntry;

/**
 * AccumulateNode
 * A beta node capable of doing accumulate logic.
 *
 * Created: 04/06/2006
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a> 
 *
 * @version $Id$
 */
public class AccumulateNode extends BetaNode {

    private static final long                serialVersionUID = -4081578178269297948L;

    private final Accumulate                 accumulate;
    private final AlphaNodeFieldConstraint[] resultConstraints;
    private final BetaConstraints            resultBinder;

    /**
     * Construct.
     * 
     * @param id
     *            The id for the node
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>ObjectSource</code>.
     * @param accumulate
     *            The accumulate conditional element
     */
    AccumulateNode(final int id,
                   final TupleSource leftInput,
                   final ObjectSource rightInput,
                   final Accumulate accumulate) {
        this( id,
              leftInput,
              rightInput,
              new AlphaNodeFieldConstraint[0],
              EmptyBetaConstraints.getInstance(),
              EmptyBetaConstraints.getInstance(),
              accumulate );
    }

    public AccumulateNode(final int id,
                          final TupleSource leftInput,
                          final ObjectSource rightInput,
                          final AlphaNodeFieldConstraint[] resultConstraints,
                          final BetaConstraints sourceBinder,
                          final BetaConstraints resultBinder,
                          final Accumulate accumulate) {
        super( id,
               leftInput,
               rightInput,
               sourceBinder );
        this.resultBinder = resultBinder;
        this.resultConstraints = resultConstraints;
        this.accumulate = accumulate;
    }

    /**
     * @inheritDoc
     * 
     *  When a new tuple is asserted into an AccumulateNode, do this:
     *  
     *  1. Select all matching objects from right memory
     *  2. Execute the initialization code using the tuple + matching objects
     *  3. Execute the accumulation code for each combination of tuple+object
     *  4. Execute the return code
     *  5. Create a new CalculatedObjectHandle for the resulting object and add it to the tuple
     *  6. Propagate the tuple
     *  
     *  The initialization, accumulation and return codes, in JBRules, are assembled
     *  into a generated method code and called once for the whole match, as you can see
     *  bellow:
     *  
     *   Object result = this.accumulator.accumulate( ... );
     *  
     */
    public void assertTuple(final ReteTuple leftTuple,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        memory.getTupleMemory().add( leftTuple );

        final List matchingObjects = new ArrayList();
        
        final Iterator it = memory.getFactHandleMemory().iterator( leftTuple );
        this.constraints.updateFromTuple( workingMemory,
                                          leftTuple );

        for ( FactEntry entry = (FactEntry) it.next(); entry != null; entry = (FactEntry) it.next() ) {
            final InternalFactHandle handle = entry.getFactHandle();
            if ( this.constraints.isAllowedCachedLeft( handle.getObject() ) ) {
                matchingObjects.add( handle.getObject() );
            }
        }

        final Object result = this.accumulate.accumulate( leftTuple,
                                                          matchingObjects,
                                                          workingMemory );
        // First alpha node filters
        boolean isAllowed = true;
        for ( int i = 0, length = this.resultConstraints.length; i < length; i++ ) {
            if ( !this.resultConstraints[i].isAllowed( result,
                                                       workingMemory ) ) {
                isAllowed = false;
                break;
            }
        }
        if ( isAllowed ) {
            this.resultBinder.updateFromTuple( workingMemory, leftTuple );
            if ( this.resultBinder.isAllowedCachedLeft( result ) ) {
                final InternalFactHandle handle = workingMemory.getFactHandleFactory().newFactHandle( result );
                memory.getCreatedHandles().put( leftTuple,
                                                handle,
                                                false );

                sink.propagateAssertTuple( leftTuple,
                                           handle,
                                           context,
                                           workingMemory );
            }
        }
        
    }

    /**
     * @inheritDoc
     * 
     * As the accumulate node will always propagate the tuple,
     * it must always also retreat it.
     * 
     */
    public void retractTuple(final ReteTuple leftTuple,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        memory.getTupleMemory().remove( leftTuple );
        final InternalFactHandle handle = (InternalFactHandle) memory.getCreatedHandles().remove( leftTuple );

        // if tuple was propagated
        if ( handle != null ) {

            this.sink.propagateRetractTuple( leftTuple,
                                             handle,
                                             context,
                                             workingMemory );

            // Destroying the acumulate result object 
            workingMemory.getFactHandleFactory().destroyFactHandle( handle );
        }
    }

    /**
     * @inheritDoc
     * 
     *  When a new object is asserted into an AccumulateNode, do this:
     *  
     *  1. Select all matching tuples from left memory
     *  2. For each matching tuple, call a modify tuple
     *  
     */
    public void assertObject(final InternalFactHandle handle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        memory.getFactHandleMemory().add( handle );

        final Iterator it = memory.getTupleMemory().iterator();
        this.constraints.updateFromFactHandle( workingMemory,
                                               handle );
        for ( ReteTuple tuple = (ReteTuple) it.next(); tuple != null; tuple = (ReteTuple) it.next() ) {
            if ( this.constraints.isAllowedCachedRight( tuple ) ) {
                this.retractTuple( tuple,
                                   context,
                                   workingMemory );
                this.assertTuple( tuple,
                                  context,
                                  workingMemory );
            }
        }
    }

    /**
     *  @inheritDoc
     *  
     *  If an object is retract, call modify tuple for each
     *  tuple match.
     */
    public void retractObject(final InternalFactHandle handle,
                              final PropagationContext context,
                              final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        if ( !memory.getFactHandleMemory().remove( handle ) ) {
            return;
        }

        final Iterator it = memory.getTupleMemory().iterator();
        this.constraints.updateFromFactHandle( workingMemory,
                                               handle );
        for ( ReteTuple tuple = (ReteTuple) it.next(); tuple != null; tuple = (ReteTuple) it.next() ) {
            if ( this.constraints.isAllowedCachedRight( tuple ) ) {
                this.retractTuple( tuple,
                                   context,
                                   workingMemory );
                this.assertTuple( tuple,
                                  context,
                                  workingMemory );
            }
        }
    }

    public void updateSink(TupleSink sink,
                           PropagationContext context,
                           InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        
        final Iterator it = memory.getCreatedHandles().iterator();

        for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next()) {
            sink.assertTuple( new ReteTuple( (ReteTuple)entry.getKey(),
                                             (InternalFactHandle) entry.getValue()),
                              context,
                              workingMemory );
        }
    }
    
    public String toString() {
        return "[ " + this.getClass().getName() + "(" + this.id + ") ]";
    }


}

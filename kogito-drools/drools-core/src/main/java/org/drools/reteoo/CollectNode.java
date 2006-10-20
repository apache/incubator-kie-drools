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

import java.util.Collection;

import org.drools.common.BetaConstraints;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.Collect;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.PropagationContext;
import org.drools.util.Iterator;
import org.drools.util.AbstractHashTable.FactEntry;
import org.drools.util.ObjectHashMap.ObjectEntry;

/**
 * @author etirelli
 *
 */
public class CollectNode extends BetaNode
    implements
    TupleSink,
    ObjectSink {

    private static final long                serialVersionUID = -8321568626178187047L;

    private final Collect                    collect;
    private final AlphaNodeFieldConstraint[] resultConstraints;
    private final BetaConstraints            resultsBinder;

    /**
     * Constructor.
     * 
     * @param id
     *            The id for the node
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>ObjectSource</code>.
     * @param collect
     *            The collect conditional element
     */
    CollectNode(final int id,
                final TupleSource leftInput,
                final ObjectSource rightInput,
                final Collect collect) {
        this( id,
              leftInput,
              rightInput,
              new AlphaNodeFieldConstraint[0],
              EmptyBetaConstraints.getInstance(),
              EmptyBetaConstraints.getInstance(),
              collect );
    }

    /**
     * Constructor.
     * 
     * @param id
     *            The id for the node
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>ObjectSource</code>.
     * @param resultConstraints
     *            The alpha constraints to be applied to the resulting collection
     * @param sourceBinder
     *            The beta binder to be applied to the source facts
     * @param resultsBinder
     *            The beta binder to be applied to the resulting collection
     * @param collect
     *            The collect conditional element
     */
    public CollectNode(final int id,
                       final TupleSource leftInput,
                       final ObjectSource rightInput,
                       final AlphaNodeFieldConstraint[] resultConstraints,
                       final BetaConstraints sourceBinder,
                       final BetaConstraints resultsBinder,
                       final Collect collect) {
        super( id,
               leftInput,
               rightInput,
               sourceBinder );
        this.resultsBinder = resultsBinder;
        this.resultConstraints = resultConstraints;
        this.collect = collect;
    }

    /**
     * @inheritDoc
     * 
     *  When a new tuple is asserted into a CollectNode, do this:
     *  
     *  1. Select all matching objects from right memory
     *  2. Add them to the resulting collection object
     *  3. Apply resultConstraints and resultsBinder to the resulting collection
     *  4. In case all of them evaluates to true do the following:
     *  4.1. Create a new InternalFactHandle for the resulting collection and add it to the tuple
     *  4.2. Propagate the tuple
     *  
     */
    public void assertTuple(final ReteTuple leftTuple,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        memory.getTupleMemory().add( leftTuple );

        final Collection result = this.collect.instantiateResultObject();
        final Iterator it = memory.getObjectMemory().iterator( leftTuple );
        this.constraints.updateFromTuple( workingMemory,
                                          leftTuple );

        for ( FactEntry entry = (FactEntry) it.next(); entry != null; entry = (FactEntry) it.next() ) {
            final InternalFactHandle handle = entry.getFactHandle();
            if ( this.constraints.isAllowedCachedLeft( handle.getObject() ) ) {
                result.add( handle.getObject() );
            }
        }

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
            if ( this.resultsBinder.isAllowedCachedLeft( result ) ) {
                final InternalFactHandle handle = workingMemory.getFactHandleFactory().newFactHandle( result );
                memory.getCreatedHandles().put( leftTuple,
                                                handle );

                sink.propagateAssertTuple( leftTuple,
                                           handle,
                                           context,
                                           workingMemory );
            }
        }
    }

    /**
     * @inheritDoc
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
     *  When a new object is asserted into a CollectNode, do this:
     *  
     *  1. Select all matching tuples from left memory
     *  2. For each matching tuple, call a modify tuple
     *  
     */
    public void assertObject(final InternalFactHandle handle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        memory.getObjectMemory().add( handle );

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
        if ( !memory.getObjectMemory().remove( handle ) ) {
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

    public String toString() {
        return "[ " + this.getClass().getName() + "(" + this.id + ") ]";
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

}

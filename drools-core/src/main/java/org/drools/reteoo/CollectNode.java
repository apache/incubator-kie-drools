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

import java.util.Arrays;
import java.util.Collection;

import org.drools.common.BetaConstraints;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.Collect;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.PropagationContext;
import org.drools.util.ArrayUtils;
import org.drools.util.Entry;
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
    private final boolean                    unwrapRightObject;

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
              collect,
              false );
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
                       final Collect collect,
                       final boolean unwrapRight ) {
        super( id,
               leftInput,
               rightInput,
               sourceBinder );
        this.resultsBinder = resultsBinder;
        this.resultConstraints = resultConstraints;
        this.collect = collect;
        this.unwrapRightObject = unwrapRight;
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
        final InternalFactHandle resultHandle = workingMemory.getFactHandleFactory().newFactHandle( result );
        CollectResult colresult = new CollectResult();
        colresult.handle = resultHandle;
        colresult.propagated = false;
        memory.getCreatedHandles().put( leftTuple,
                                        colresult,
                                        false );

        final Iterator it = memory.getFactHandleMemory().iterator( leftTuple );
        this.constraints.updateFromTuple( workingMemory,
                                          leftTuple );

        for ( FactEntry entry = (FactEntry) it.next(); entry != null; entry = (FactEntry) it.next() ) {
            InternalFactHandle handle = entry.getFactHandle();
            if ( this.constraints.isAllowedCachedLeft( handle.getObject() ) ) {
                if( this.unwrapRightObject ) {
                    handle = ((ReteTuple) handle.getObject()).getLastHandle(); 
                }
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
            this.resultsBinder.updateFromTuple( workingMemory,
                                                leftTuple );
            if ( this.resultsBinder.isAllowedCachedLeft( result ) ) {
                colresult.propagated = true;
                this.sink.propagateAssertTuple( leftTuple,
                                                resultHandle,
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
        CollectResult result = (CollectResult) memory.getCreatedHandles().remove( leftTuple );
        final InternalFactHandle handle = result.handle;

        // if tuple was propagated
        if ( result.propagated ) {

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
        memory.getFactHandleMemory().add( handle );

        this.constraints.updateFromFactHandle( workingMemory,
                                               handle );

        // need to clone the tuples to avoid concurrent modification exceptions
        Entry[] tuples = memory.getTupleMemory().toArray();
        for ( int i = 0; i < tuples.length; i++ ) {
            ReteTuple tuple = (ReteTuple) tuples[i];
            if ( this.constraints.isAllowedCachedRight( tuple ) ) {
                this.modifyTuple( tuple,
                                  handle,
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

        this.constraints.updateFromFactHandle( workingMemory,
                                               handle );

        // need to clone the tuples to avoid concurrent modification exceptions
        Entry[] tuples = memory.getTupleMemory().toArray();
        for ( int i = 0; i < tuples.length; i++ ) {
            ReteTuple tuple = (ReteTuple) tuples[i];
            if ( this.constraints.isAllowedCachedRight( tuple ) ) {
                
                this.modifyTuple( tuple,
                                  handle,
                                  context,
                                  workingMemory );
            }
        }
    }

    /**
     * Modifies the results match for a tuple, retracting it and repropagating
     * if constraints allow it
     * 
     * @param leftTuple
     * @param handle
     * @param context
     * @param workingMemory
     */
    public void modifyTuple(final ReteTuple leftTuple,
                            InternalFactHandle handle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        CollectResult result = (CollectResult) memory.getCreatedHandles().get( leftTuple );

        // if tuple was propagated
        if ( result.propagated ) {
            this.sink.propagateRetractTuple( leftTuple,
                                             result.handle,
                                             context,
                                             workingMemory );
            result.propagated = false;
        }
        
        // if there is a subnetwork, we need to unwrapp the object from inside the tuple
        if( this.unwrapRightObject ) {
            handle = ((ReteTuple) handle.getObject()).getLastHandle(); 
        }

        if ( context.getType() == PropagationContext.ASSERTION ) {
            ((Collection) result.handle.getObject()).add( handle.getObject() );
        } else if ( context.getType() == PropagationContext.RETRACTION ) {
            ((Collection) result.handle.getObject()).remove( handle.getObject() );
        } else if ( context.getType() == PropagationContext.MODIFICATION ) {
            // nothing to do
        }

        // First alpha node filters
        boolean isAllowed = true;
        for ( int i = 0, length = this.resultConstraints.length; i < length; i++ ) {
            if ( !this.resultConstraints[i].isAllowed( result.handle.getObject(),
                                                       workingMemory ) ) {
                isAllowed = false;
                break;
            }
        }
        if ( isAllowed ) {
            this.resultsBinder.updateFromTuple( workingMemory,
                                                leftTuple );
            if ( this.resultsBinder.isAllowedCachedLeft( result.handle.getObject() ) ) {
                result.propagated = true;
                this.sink.propagateAssertTuple( leftTuple,
                                                result.handle,
                                                context,
                                                workingMemory );
            }
        }
    }

    public void updateSink(final TupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        final Iterator it = memory.getCreatedHandles().iterator();

        for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
            CollectResult result = (CollectResult) entry.getValue();
            sink.assertTuple( new ReteTuple( (ReteTuple) entry.getKey(),
                                             result.handle ),
                              context,
                              workingMemory );
        }
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#hashCode()
     */
    public int hashCode() {
        return this.leftInput.hashCode() ^ this.rightInput.hashCode() ^ this.collect.hashCode() ^ this.resultsBinder.hashCode() ^ ArrayUtils.hashCode( this.resultConstraints );
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof CollectNode) ) {
            return false;
        }

        final CollectNode other = (CollectNode) object;

        if ( this.getClass() != other.getClass() || (!this.leftInput.equals( other.leftInput )) || (!this.rightInput.equals( other.rightInput )) || (!this.constraints.equals( other.constraints )) ) {
            return false;
        }

        return this.collect.equals( other.collect ) && resultsBinder.equals( other.resultsBinder ) && Arrays.equals( this.resultConstraints,
                                                                                                                     other.resultConstraints );
    }

    public String toString() {
        return "[ " + this.getClass().getName() + "(" + this.id + ") ]";
    }

    private static class CollectResult {
        // keeping attributes public just for performance
        public InternalFactHandle handle;
        public boolean            propagated;
    }
}

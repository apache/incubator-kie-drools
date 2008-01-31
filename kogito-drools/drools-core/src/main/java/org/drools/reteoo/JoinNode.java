package org.drools.reteoo;

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

import org.drools.common.BetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.spi.PropagationContext;
import org.drools.util.FactEntry;
import org.drools.util.Iterator;

/**
 * <code>JoinNode</code> extends <code>BetaNode</code> to perform
 * <code>ReteTuple</code> and <code>FactHandle</code> joins. Tuples are
 * considered to be asserted from the left input and facts from the right input.
 * The <code>BetaNode</code> provides the BetaMemory to store assserted
 * ReteTuples and
 * <code>FactHandleImpl<code>s. Each fact handle is stored in the right memory as a key in a <code>HashMap</code>, the value is an <code>ObjectMatches</code> 
 * instance which maintains a <code>LinkedList of <code>TuplesMatches - The tuples that are matched with the handle. the left memory is a <code>LinkedList</code> 
 * of <code>ReteTuples</code> which maintains a <code>HashMa</code>, where the keys are the matching <code>FactHandleImpl</code>s and the value is 
 * populated <code>TupleMatche</code>es, the keys are matched fact handles. <code>TupleMatch</code> maintains a <code>List</code> of resulting joins, 
 * where there is joined <code>ReteTuple</code> per <code>TupleSink</code>.
 *  
 * 
 * The BetaNode provides
 * the BetaMemory which stores the 
 * 
 * @see BetaNode
 * @see ObjectMatches
 * @see TupleMatch
 * @see TupleSink
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
public class JoinNode extends BetaNode {
    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
    
    /**
     * 
     */
    private static final long serialVersionUID = 400L;

    public JoinNode(final int id,
                    final TupleSource leftInput,
                    final ObjectSource rightInput,
                    final BetaConstraints binder,
                    final BuildContext context) {
        super( id,
               leftInput,
               rightInput,
               binder );
        tupleMemoryEnabled = context.isTupleMemoryEnabled();
    }

    /**
     * Assert a new <code>ReteTuple</code>. The right input of
     * <code>FactHandleInput</code>'s is iterated and joins attemped, via the
     * binder, any successful bindings results in joined tuples being created
     * and propaged. there is a joined tuple per TupleSink.
     * 
     * @see ReteTuple
     * @see ObjectMatches
     * @see TupleSink
     * @see TupleMatch
     * 
     * @param tuple
     *            The <code>Tuple</code> being asserted.
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory seesion.
     */
    public void assertTuple(final ReteTuple leftTuple,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        
        if ( this.tupleMemoryEnabled ) {
            memory.getTupleMemory().add( leftTuple );
        }

        final Iterator it = memory.getFactHandleMemory().iterator( leftTuple );
        this.constraints.updateFromTuple( memory.getContext(),
                                          workingMemory,
                                          leftTuple );
        for ( FactEntry entry = (FactEntry) it.next(); entry != null; entry = (FactEntry) it.next() ) {
            final InternalFactHandle handle = entry.getFactHandle();
            if ( this.constraints.isAllowedCachedLeft( memory.getContext(),
                                                       handle ) ) {
                this.sink.propagateAssertTuple( leftTuple,
                                                handle,
                                                context,
                                                workingMemory );
            }
        }
        
        this.constraints.resetTuple( memory.getContext() );
    }

    /**
     * Assert a new <code>FactHandleImpl</code>. The left input of
     * <code>ReteTuple</code>s is iterated and joins attemped, via the
     * binder, any successful bindings results in joined tuples being created
     * and propaged. there is a joined tuple per TupleSink.
     * 
     * @see ReteTuple
     * @see ObjectMatches
     * @see TupleSink
     * @see TupleMatch
     * 
     * @param handle
     *            The <code>FactHandleImpl</code> being asserted.
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory seesion.
     */
    public void assertObject(final InternalFactHandle handle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        memory.getFactHandleMemory().add( handle );
        if ( ! this.tupleMemoryEnabled ) {
            // do nothing here, as we know there are no left tuples at this stage in sequential mode.
            return;
        }

        final Iterator it = memory.getTupleMemory().iterator( handle );
        this.constraints.updateFromFactHandle( memory.getContext(),
                                               workingMemory,
                                               handle );
        for ( ReteTuple tuple = (ReteTuple) it.next(); tuple != null; tuple = (ReteTuple) it.next() ) {
            if ( this.constraints.isAllowedCachedRight( memory.getContext(),
                                                        tuple ) ) {
                this.sink.propagateAssertTuple( tuple,
                                                handle,
                                                context,
                                                workingMemory );
            }
        }
        this.constraints.resetFactHandle( memory.getContext() );
    }

    /**
     * Retract a FactHandleImpl. Iterates the referenced TupleMatches stored in
     * the handle's ObjectMatches retracting joined tuples.
     * 
     * @param handle
     *            the <codeFactHandleImpl</code> being retracted
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory seesion.
     */
    public void retractObject(final InternalFactHandle handle,
                              final PropagationContext context,
                              final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        if ( !memory.getFactHandleMemory().remove( handle ) ) {
            return;
        }

        final Iterator it = memory.getTupleMemory().iterator( handle );
        this.constraints.updateFromFactHandle( memory.getContext(),
                                               workingMemory,
                                               handle );
        for ( ReteTuple tuple = (ReteTuple) it.next(); tuple != null; tuple = (ReteTuple) it.next() ) {
            if ( this.constraints.isAllowedCachedRight( memory.getContext(),
                                                        tuple ) ) {
                this.sink.propagateRetractTuple( tuple,
                                                 handle,
                                                 context,
                                                 workingMemory );
            }
        }
        
        this.constraints.resetFactHandle( memory.getContext() );
    }

    /**
     * Retract a <code>ReteTuple</code>. Iterates the referenced
     * <code>TupleMatche</code>'s stored in the tuples <code>Map</code>
     * retracting all joined tuples.
     * 
     * @param key
     *            The tuple key.
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory seesion.
     */
    public void retractTuple(final ReteTuple leftTuple,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        final ReteTuple tuple = memory.getTupleMemory().remove( leftTuple );
        if ( tuple == null ) {
            return;
        }

        final Iterator it = memory.getFactHandleMemory().iterator( leftTuple );
        this.constraints.updateFromTuple( memory.getContext(),
                                          workingMemory,
                                          leftTuple );
        for ( FactEntry entry = (FactEntry) it.next(); entry != null; entry = (FactEntry) it.next() ) {
            final InternalFactHandle handle = entry.getFactHandle();
            if ( this.constraints.isAllowedCachedLeft( memory.getContext(),
                                                       handle ) ) {
                this.sink.propagateRetractTuple( leftTuple,
                                                 handle,
                                                 context,
                                                 workingMemory );
            }
        }
        
        this.constraints.resetTuple( memory.getContext() );
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#updateNewNode(org.drools.reteoo.WorkingMemoryImpl, org.drools.spi.PropagationContext)
     */
    public void updateSink(final TupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        final Iterator tupleIter = memory.getTupleMemory().iterator();
        for ( ReteTuple tuple = (ReteTuple) tupleIter.next(); tuple != null; tuple = (ReteTuple) tupleIter.next() ) {
            final Iterator objectIter = memory.getFactHandleMemory().iterator( tuple );
            this.constraints.updateFromTuple( memory.getContext(),
                                              workingMemory,
                                              tuple );
            for ( FactEntry entry = (FactEntry) objectIter.next(); entry != null; entry = (FactEntry) objectIter.next() ) {
                final InternalFactHandle handle = entry.getFactHandle();
                if ( this.constraints.isAllowedCachedLeft( memory.getContext(),
                                                           handle ) ) {
                    sink.assertTuple( new ReteTuple( tuple,
                                                     handle ),
                                      context,
                                      workingMemory );
                }
            }
            
            this.constraints.resetTuple( memory.getContext() );
        }
    }

    public String toString() {
        ObjectSource source = this.rightInput;
        while ( !(source instanceof ObjectTypeNode) ) {
            source = source.objectSource;
        }

        return "[JoinNode("+this.getId()+") - " + ((ObjectTypeNode) source).getObjectType() + "]";
    }
}

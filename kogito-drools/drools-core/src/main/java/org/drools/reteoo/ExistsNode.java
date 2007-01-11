/*
 * Copyright 2006 JBoss Inc
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

import org.drools.common.BetaConstraints;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;
import org.drools.util.Iterator;
import org.drools.util.AbstractHashTable.FactEntry;

/**
 * <code>ExistsNode</code> extends <code>BetaNode</code> to perform tests for
 * the existence of a Fact plus one or more conditions. Where existence
 * is found the left ReteTuple is copied and propagated. Further to this it
 * maintains the "truth" by cancelling any
 * <code>Activation<code>s that are no longer 
 * considered true by the retraction of ReteTuple's or FactHandleImpl.  
 * Tuples are considered to be asserted from the left input and facts from the right input.
 * The <code>BetaNode</code> provides the BetaMemory to store assserted ReteTuples and 
 * <code>FactHandleImpl<code>s. Each fact handle is stored in the right 
 * memory.
 * 
 * @author <a href="mailto:etirelli@redhat.com">Edson Tirelli</a>
 *
 */
public class ExistsNode extends BetaNode {

    private static final long serialVersionUID = 2597133625232012026L;

    static int                notAssertObject  = 0;
    static int                notAssertTuple   = 0;

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Constructs a new Exists node with EmptyBetaConstraints.
     * 
     * @paran id
     *            The unique id for this node.
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>ObjectSource</code>.
     */
    public ExistsNode(final int id,
                      final TupleSource leftInput,
                      final ObjectSource rightInput) {
        super( id,
               leftInput,
               rightInput,
               EmptyBetaConstraints.getInstance() );
    }

    /**
     * Construct.
     * 
     * @paran id
     *            The unique id for this node.
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>ObjectSource</code>.
     * @param joinNodeBinder
     *            The constraints to be aplied to the right objects
     */
    public ExistsNode(final int id,
                      final TupleSource leftInput,
                      final ObjectSource rightInput,
                      final BetaConstraints joinNodeBinder) {
        super( id,
               leftInput,
               rightInput,
               joinNodeBinder );
    }

    /**
     * Assert a new <code>ReteTuple</code> from the left input. It iterates
     * over the right <code>FactHandleImpl</code>'s and if any match is found,
     * a copy of the <code>ReteTuple</code> is made and propagated.
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
        memory.getTupleMemory().add( leftTuple );

        final Iterator it = memory.getFactHandleMemory().iterator( leftTuple );
        this.constraints.updateFromTuple( workingMemory,
                                          leftTuple );
        int matches = 0;
        for ( FactEntry entry = (FactEntry) it.next(); entry != null; entry = (FactEntry) it.next() ) {
            final InternalFactHandle handle = entry.getFactHandle();
            if ( this.constraints.isAllowedCachedLeft( handle.getObject() ) ) {
                matches++;
            }
        }

        leftTuple.setMatches( matches );

        if ( matches > 0 ) {
            this.sink.propagateAssertTuple( leftTuple,
                                            context,
                                            workingMemory );
        }
    }

    /**
     * Assert a new <code>FactHandleImpl</code> from the right input. If it
     * matches any left ReteTuple's that had no matches before, propagate 
     * tuple as an assertion.
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

        final Iterator it = memory.getTupleMemory().iterator( handle );
        this.constraints.updateFromFactHandle( workingMemory,
                                               handle );
        for ( ReteTuple tuple = (ReteTuple) it.next(); tuple != null; tuple = (ReteTuple) it.next() ) {
            if ( this.constraints.isAllowedCachedRight( tuple ) ) {
                final int matches = tuple.getMatches();
                tuple.setMatches( matches + 1 );

                // if this is the first match, propagate tuple
                if ( tuple.getMatches() == 1 ) {
                    this.sink.propagateAssertTuple( tuple,
                                                    context,
                                                    workingMemory );
                }
            }
        }
    }

    /**
     * Retract the <code>FactHandleImpl</code>. If the handle has any
     * <code>ReteTuple</code> matches and those tuples now have no
     * other match, retract tuple
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
        this.constraints.updateFromFactHandle( workingMemory,
                                               handle );
        for ( ReteTuple tuple = (ReteTuple) it.next(); tuple != null; tuple = (ReteTuple) it.next() ) {
            if ( this.constraints.isAllowedCachedRight( tuple ) ) {
                tuple.setMatches( tuple.getMatches() - 1 );
                if ( tuple.getMatches() == 0 ) {
                    this.sink.propagateRetractTuple( tuple,
                                                     context,
                                                     workingMemory );
                }
            }
        }
    }

    /**
     * Retract the
     * <code>ReteTuple<code>, any resulting propagated joins are also retracted. 
     * 
     * @param leftTuple
     *            The tuple being retracted
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory seesion.
     */
    public void retractTuple(final ReteTuple leftTuple,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        // Must use the tuple in memory as it has the tuple matches count
        final ReteTuple tuple = (ReteTuple) memory.getTupleMemory().remove( leftTuple );
        if ( tuple == null ) {
            return;
        }

        if ( tuple.getMatches() > 0 ) {
            this.sink.propagateRetractTuple( tuple,
                                             context,
                                             workingMemory );
        }
    }

    /**
     * Updates the given sink propagating all previously propagated tuples to it
     * 
     */
    public void updateSink(final TupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        final Iterator tupleIter = memory.getTupleMemory().iterator();
        for ( ReteTuple tuple = (ReteTuple) tupleIter.next(); tuple != null; tuple = (ReteTuple) tupleIter.next() ) {
            if ( tuple.getMatches() > 0 ) {
                sink.assertTuple( new ReteTuple( tuple ),
                                  context,
                                  workingMemory );
            }
        }
    }

    public String toString() {
        ObjectSource source = this.rightInput;
        while ( source.getClass() != ObjectTypeNode.class ) {
            source = source.objectSource;
        }

        return "[ExistsNode - " + ((ObjectTypeNode) source).getObjectType() + "]";
    }

}

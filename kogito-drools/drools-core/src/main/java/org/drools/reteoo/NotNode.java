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
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;
import org.drools.util.Iterator;
import org.drools.util.AbstractHashTable.FactEntry;

/**
 * <code>NotNode</code> extends <code>BetaNode</code> to perform tests for
 * the non existence of a Fact plus one or more conditions. Where none existence
 * is found the left ReteTuple is copied and propgated. Further to this it
 * maintains the "truth" by cancelling any
 * <code>Activation<code>s that are nolonger 
 * considered true by the assertion of ReteTuple's or FactHandleImpl.  Tuples are considered to be asserted from the left input and facts from the right input.
 * The <code>BetaNode</code> provides the BetaMemory to store assserted ReteTuples and <code>FactHandleImpl<code>s. Each fact handle is stored in the right 
 * memory as a key in a <code>HashMap</code>, the value is an <code>ObjectMatches</code> instance which maintains a <code>LinkedList of <code>TuplesMatches - 
 * The tuples that are matched with the handle. the left memory is a <code>LinkedList</code> of <code>ReteTuples</code> which maintains a <code>HashMa</code>, 
 * where the keys are the matching <code>FactHandleImpl</code>s and the value is populated <code>TupleMatche</code>es, the keys are matched fact handles. 
 * <code>TupleMatch</code> maintains a <code>List</code> of resulting joins, where there is joined <code>ReteTuple</code> per <code>TupleSink</code>.
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
public class NotNode extends BetaNode {

    private static final long serialVersionUID = 320L;
    static int                notAssertObject  = 0;
    static int                notAssertTuple   = 0;

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>TupleSource</code>.
     */
    public NotNode(final int id,
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
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>TupleSource</code>.
     */
    public NotNode(final int id,
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
     * over the right <code>FactHandleImpl</code>'s if no matches are found
     * the a copy of the <code>ReteTuple</code> is made and propagated.
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

        if ( matches == 0 ) {
            this.sink.propagateAssertTuple( leftTuple,
                                            context,
                                            workingMemory );
        }
    }

    /**
     * Assert a new <code>FactHandleImpl</code> from the right input. If it
     * matches any left ReteTuple's that already has propagations then those
     * propagations are retracted.
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
                this.sink.propagateRetractTuple( tuple,
                                                 context,
                                                 workingMemory );
                //                if ( matches == 0 ) {
                //                    this.sink.propagateRetractTuple( tuple, context, workingMemory );
                //                }
            }
        }
    }

    /**
     * Retract the <code>FactHandleImpl</code>. If the handle has any
     * <code>ReteTuple</code> matches then those matches copied are propagated
     * as new joins.
     * 
     * @param handle
     *            the <codeFactHandleImpl</code> being retracted
     * @param context
     *            The <code>PropagationContext</code>
     * @param workingMemory
     *            The working memory seesion.
     * @throws AssertionException
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
                    this.sink.propagateAssertTuple( tuple,
                                                    context,
                                                    workingMemory );
                }
            }
        }
    }

    /**
     * Retract the
     * <code>ReteTuple<code>, any resulting proppagated joins are also retracted. 
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

        // Must use the tuple in memory as it has the tuple matches count
        final ReteTuple tuple = (ReteTuple) memory.getTupleMemory().remove( leftTuple );
        if ( tuple == null ) {
            return;
        }

        if ( tuple.getMatches() == 0 ) {
            this.sink.propagateRetractTuple( tuple,
                                             context,
                                             workingMemory );
        }
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
            if ( tuple.getMatches() == 0 ) {
                sink.assertTuple( new ReteTuple( tuple ),
                                  context,
                                  workingMemory );
            }
        }
    }

    public String toString() {
        ObjectSource source = this.rightInput;
        while ( !(source instanceof ObjectTypeNode ) ) {
            source = source.objectSource;
        }

        return "[NotNode - " + ((ObjectTypeNode) source).getObjectType() + "]";
    }
}

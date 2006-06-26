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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.drools.common.BetaNodeBinder;
import org.drools.common.DefaultFactHandle;
import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListNode;
import org.drools.util.LinkedListObjectWrapper;

/**
 * A source of <code>ReteTuple</code> s for a <code>TupleSink</code>.
 * 
 * <p>
 * Nodes that propagate <code>Tuples</code> extend this class.
 * </p>
 * 
 * @see TupleSource
 * @see ReteTuple
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
abstract class TupleSource extends BaseNode
    implements
    Serializable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The destination for <code>Tuples</code>. */
    protected List tupleSinks = new ArrayList( 1 );

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Single parameter constructor that specifies the unique id of the node.
     * 
     * @param id
     */
    TupleSource(final int id) {
        super( id );
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Adds the <code>TupleSink</code> so that it may receive
     * <code>Tuples</code> propagated from this <code>TupleSource</code>.
     * 
     * @param tupleSink
     *            The <code>TupleSink</code> to receive propagated
     *            <code>Tuples</code>.
     */
    protected void addTupleSink(final TupleSink tupleSink) {
        if ( !this.tupleSinks.contains( tupleSink ) ) {
            this.tupleSinks.add( tupleSink );
        }
    }

    /**
     * Removes the <code>TupleSink</code>
     * 
     * @param tupleSink
     *            The <code>TupleSink</code> to remove
     */
    protected void removeTupleSink(final TupleSink tupleSink) {
        this.tupleSinks.remove( tupleSink );
    }

    protected TupleMatch attemptJoin(final ReteTuple leftTuple,
                                     final DefaultFactHandle handle,
                                     final ObjectMatches objectMatches,
                                     final BetaNodeBinder binder,
                                     final ReteooWorkingMemory workingMemory) {
        if ( binder.isAllowed( handle,
                               leftTuple,
                               workingMemory ) ) {
            final TupleMatch tupleMatch = objectMatches.add( leftTuple );

            leftTuple.addTupleMatch( handle,
                                     tupleMatch );
            return tupleMatch;

        } else {
            return null;
        }
    }

    /**
     * Propagate the assertion of a <code>ReteTuple</code> to this node's
     * <code>TupleSink</code>.
     * 
     * @param tuple
     *            The <code>ReteTuple</code> to propagate.
     * @param context
     *            The <code>PropagationContext</code> of the
     *            <code>WorkingMemory<code> action            
     * @param workingMemory
     *            the <code>WorkingMemory</code> session.
     */

    protected void propagateAssertTuple(final ReteTuple tuple,
                                        final TupleMatch tupleMatch,
                                        final PropagationContext context,
                                        final ReteooWorkingMemory workingMemory) {

        // we do this one first to avoid an extra clone
        if ( !getTupleSinks().isEmpty() ) {
            ((TupleSink) getTupleSinks().get( 0 )).assertTuple( tuple,
                                                                context,
                                                                workingMemory );

            tupleMatch.addJoinedTuple( tuple );

            for ( int i = 1, size = getTupleSinks().size(); i < size; i++ ) {
                final ReteTuple clone = new ReteTuple( tuple );
                tupleMatch.addJoinedTuple( clone );
                ((TupleSink) getTupleSinks().get( i )).assertTuple( clone,
                                                                    context,
                                                                    workingMemory );
            }
        }
    }

    protected void propagateAssertTuple(final ReteTuple tuple,
                                        final PropagationContext context,
                                        final ReteooWorkingMemory workingMemory) {
        for ( int i = 0, size = getTupleSinks().size(); i < size; i++ ) {
            final ReteTuple child = new ReteTuple( tuple );
            // no TupleMatch so instead add as a linked tuple
            tuple.addLinkedTuple( new LinkedListObjectWrapper( child ) );
            ((TupleSink) getTupleSinks().get( i )).assertTuple( child,
                                                                context,
                                                                workingMemory );
        }
    }

    /**
     * Propagates a new tuple adding the given fact handle to the tuple 
     * before propagating.
     * 
     * @param tuple The base tuple for propagation
     * @param handle The handle to add to the tuple when propagating
     * @param context
     * @param workingMemory
     */
    protected void propagateAssertTuple(final ReteTuple tuple,
                                        final DefaultFactHandle handle,
                                        final PropagationContext context,
                                        final ReteooWorkingMemory workingMemory) {
        for ( int i = 0, size = getTupleSinks().size(); i < size; i++ ) {
            final ReteTuple child = new ReteTuple( tuple, handle );
            // no TupleMatch so instead add as a linked tuple
            tuple.addLinkedTuple( new LinkedListObjectWrapper( child ) );
            ((TupleSink) getTupleSinks().get( i )).assertTuple( child,
                                                                context,
                                                                workingMemory );
        }
    }

    protected void propagateRetractTuple(final TupleMatch tupleMatch,
                                         final PropagationContext context,
                                         final ReteooWorkingMemory workingMemory) {

        final List joined = tupleMatch.getJoinedTuples();
        for ( int i = 0, size = joined.size(); i < size; i++ ) {
            ((TupleSink) getTupleSinks().get( i )).retractTuple( (ReteTuple) joined.get( i ),
                                                                 context,
                                                                 workingMemory );
        }
    }

    protected void propagateRetractTuple(final ReteTuple tuple,
                                         final PropagationContext context,
                                         final ReteooWorkingMemory workingMemory) {
        final LinkedList list = tuple.getLinkedTuples();
        if ( list != null && !list.isEmpty() ) {
            int i = 0;
            for ( LinkedListNode node = list.removeFirst(); node != null; node = list.removeFirst() ) {
                ((TupleSink) getTupleSinks().get( i++ )).retractTuple( (ReteTuple) ((LinkedListObjectWrapper) node).getObject(),
                                                                       context,
                                                                       workingMemory );
            }
        }
    }

    protected void propagateModifyTuple(final TupleMatch tupleMatch,
                                        final PropagationContext context,
                                        final ReteooWorkingMemory workingMemory) {

        final List joined = tupleMatch.getJoinedTuples();
        for ( int i = 0, size = joined.size(); i < size; i++ ) {
            ((TupleSink) getTupleSinks().get( i )).modifyTuple( (ReteTuple) joined.get( i ),
                                                                context,
                                                                workingMemory );
        }
    }

    protected void propagateModifyTuple(final ReteTuple tuple,
                                        final PropagationContext context,
                                        final ReteooWorkingMemory workingMemory) {
        final LinkedList list = tuple.getLinkedTuples();
        if ( list != null && !list.isEmpty() ) {
            int i = 0;
            for ( LinkedListNode node = list.getFirst(); node != null; node = node.getNext() ) {
                ((TupleSink) getTupleSinks().get( i++ )).modifyTuple( (ReteTuple) ((LinkedListObjectWrapper) node).getObject(),
                                                                      context,
                                                                      workingMemory );
            }
        }
    }

    /**
     * Retrieve the <code>TupleSinks</code> that receive propagated
     * <code>Tuples</code>s.
     * 
     * @return The <code>TupleSinks</code> that receive propagated
     *         <code>Tuples</code>.
     */
    public List getTupleSinks() {
        return this.tupleSinks;
    }

    /**
     * Returns the list of propagated tuples
     * @return
     */
    public abstract List getPropagatedTuples(ReteooWorkingMemory workingMemory,
                                             TupleSink sink);

}

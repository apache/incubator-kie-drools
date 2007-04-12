package org.drools.reteoo;

import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.BetaConstraints;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.DataProvider;
import org.drools.spi.PropagationContext;
import org.drools.util.Iterator;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListEntry;
import org.drools.util.TupleHashTable;

public class FromNode extends TupleSource
    implements
    TupleSinkNode,
    NodeMemory {
    /**
     * 
     */
    private static final long          serialVersionUID = 320;

    private DataProvider               dataProvider;
    private TupleSource                tupleSource;
    private AlphaNodeFieldConstraint[] alphaConstraints;
    private BetaConstraints            betaConstraints;

    private TupleSinkNode              previousTupleSinkNode;
    private TupleSinkNode              nextTupleSinkNode;

    public FromNode(final int id,
                    final DataProvider dataProvider,
                    final TupleSource tupleSource,
                    final AlphaNodeFieldConstraint[] constraints,
                    final BetaConstraints binder) {
        super( id );
        this.dataProvider = dataProvider;
        this.tupleSource = tupleSource;
        this.alphaConstraints = constraints;
        this.betaConstraints = (binder == null) ? EmptyBetaConstraints.getInstance() : binder;
    }

    /**
     * @inheritDoc 
     */
    public void assertTuple(final ReteTuple leftTuple,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        memory.getTupleMemory().add( leftTuple );
        final LinkedList list = new LinkedList();
        this.betaConstraints.updateFromTuple( workingMemory,
                                              leftTuple );

        for ( final java.util.Iterator it = this.dataProvider.getResults( leftTuple,
                                                                          workingMemory,
                                                                          context ); it.hasNext(); ) {
            final Object object = it.next();

            if ( this.alphaConstraints != null ) {
                // First alpha node filters
                boolean isAllowed = true;
                for ( int i = 0, length = this.alphaConstraints.length; i < length; i++ ) {
                    if ( !this.alphaConstraints[i].isAllowed( object,
                                                              workingMemory ) ) {
                        // next iteration
                        isAllowed = false;
                        break;
                    }
                }
                if ( !isAllowed ) {
                    continue;
                }
            }

            if ( this.betaConstraints.isAllowedCachedLeft( object ) ) {
                final InternalFactHandle handle = workingMemory.getFactHandleFactory().newFactHandle( object );

                list.add( new LinkedListEntry( handle ) );

                this.sink.propagateAssertTuple( leftTuple,
                                                handle,
                                                context,
                                                workingMemory );
            }
        }
        if ( !list.isEmpty() ) {
            memory.getCreatedHandles().put( leftTuple,
                                            list );
        }

    }

    public void retractTuple(final ReteTuple leftTuple,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        final ReteTuple tuple = memory.getTupleMemory().remove( leftTuple );

        final LinkedList list = (LinkedList) memory.getCreatedHandles().remove( tuple );
        // if tuple was propagated
        if ( list != null ) {
            for ( LinkedListEntry entry = (LinkedListEntry) list.getFirst(); entry != null; entry = (LinkedListEntry) entry.getNext() ) {
                final InternalFactHandle handle = (InternalFactHandle) entry.getObject();
                this.sink.propagateRetractTuple( leftTuple,
                                                 handle,
                                                 context,
                                                 workingMemory );
                workingMemory.getFactHandleFactory().destroyFactHandle( handle );
            }
        }
    }

    public void attach() {
        this.tupleSource.addTupleSink( this );
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null );
            this.tupleSource.updateSink( this,
                                         propagationContext,
                                         workingMemory );
        }
    }

    public void remove(final BaseNode node,
                       final InternalWorkingMemory[] workingMemories) {

        if ( !node.isInUse() ) {
            removeTupleSink( (TupleSink) node );
        }
        removeShare();

        if ( !this.isInUse() ) {
            for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
                workingMemories[i].clearNodeMemory( this );
            }
        }
        this.tupleSource.remove( this,
                                 workingMemories );
    }

    public void updateSink(final TupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        final Iterator tupleIter = memory.getTupleMemory().iterator();
        for ( ReteTuple tuple = (ReteTuple) tupleIter.next(); tuple != null; tuple = (ReteTuple) tupleIter.next() ) {
            final LinkedList list = (LinkedList) memory.getCreatedHandles().remove( tuple );
            if ( list == null ) {
                continue;
            }
            for ( LinkedListEntry entry = (LinkedListEntry) list.getFirst(); entry != null; entry = (LinkedListEntry) entry.getNext() ) {
                final InternalFactHandle handle = (InternalFactHandle) entry.getObject();
                this.sink.propagateRetractTuple( tuple,
                                                 handle,
                                                 context,
                                                 workingMemory );
                workingMemory.getFactHandleFactory().destroyFactHandle( handle );
            }
        }
    }

    public Object createMemory(final RuleBaseConfiguration config) {
        return new BetaMemory( new TupleHashTable(),
                               null );
    }

    /**
     * Returns the next node
     * @return
     *      The next TupleSinkNode
     */
    public TupleSinkNode getNextTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node 
     * @param next
     *      The next TupleSinkNode
     */
    public void setNextTupleSinkNode(final TupleSinkNode next) {
        this.nextTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous TupleSinkNode
     */
    public TupleSinkNode getPreviousTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node 
     * @param previous
     *      The previous TupleSinkNode
     */
    public void setPreviousTupleSinkNode(final TupleSinkNode previous) {
        this.previousTupleSinkNode = previous;
    }

}

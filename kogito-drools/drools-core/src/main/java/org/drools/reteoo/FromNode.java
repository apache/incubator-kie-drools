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
    private static final long          serialVersionUID = 400L;

    private DataProvider               dataProvider;
    private TupleSource                tupleSource;
    private AlphaNodeFieldConstraint[] alphaConstraints;
    private BetaConstraints            betaConstraints;

    private TupleSinkNode              previousTupleSinkNode;
    private TupleSinkNode              nextTupleSinkNode;
    
    protected boolean                 tupleMemoryEnabled;      

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
        this.tupleMemoryEnabled = false;
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

            final InternalFactHandle handle = workingMemory.getFactHandleFactory().newFactHandle( object, false, workingMemory );

            boolean isAllowed = true;
            if ( this.alphaConstraints != null ) {
                // First alpha node filters
                for ( int i = 0, length = this.alphaConstraints.length; i < length; i++ ) {
                    if ( !this.alphaConstraints[i].isAllowed( handle,
                                                              workingMemory ) ) {
                        // next iteration
                        isAllowed = false;
                        break;
                    }
                }
            }

            if ( isAllowed && this.betaConstraints.isAllowedCachedLeft( handle ) ) {
                list.add( new LinkedListEntry( handle ) );

                this.sink.propagateAssertTuple( leftTuple,
                                                handle,
                                                context,
                                                workingMemory );
            } else {
                workingMemory.getFactHandleFactory().destroyFactHandle( handle );
            }
        }
        
        this.betaConstraints.resetTuple();
        
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

    public void remove(ReteooBuilder builder,
                       final BaseNode node, final InternalWorkingMemory[] workingMemories) {

        if ( !node.isInUse() ) {
            removeTupleSink( (TupleSink) node );
        }
        removeShare(builder);

        if ( !this.isInUse() ) {
            for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
                workingMemories[i].clearNodeMemory( this );
            }
        }
        this.tupleSource.remove( builder,
                                 this, workingMemories );
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
    
    public boolean isTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
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

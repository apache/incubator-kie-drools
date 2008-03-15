package org.drools.reteoo;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.BetaConstraints;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.ContextEntry;
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

    protected boolean                  tupleMemoryEnabled;

    public FromNode() {
    }

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

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        dataProvider            = (DataProvider)in.readObject();
        tupleSource             = (TupleSource)in.readObject();
        alphaConstraints        = (AlphaNodeFieldConstraint[])in.readObject();
        betaConstraints         = (BetaConstraints)in.readObject();
        previousTupleSinkNode   = (TupleSinkNode)in.readObject();
        nextTupleSinkNode       = (TupleSinkNode)in.readObject();
        tupleMemoryEnabled      = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(dataProvider);
        out.writeObject(tupleSource);
        out.writeObject(alphaConstraints);
        out.writeObject(betaConstraints);
        out.writeObject(previousTupleSinkNode);
        out.writeObject(nextTupleSinkNode);
        out.writeBoolean(tupleMemoryEnabled);
    }
    /**
     * @inheritDoc
     */
    public void assertTuple(final ReteTuple leftTuple,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );

        memory.betaMemory.getTupleMemory().add( leftTuple );
        final LinkedList list = new LinkedList();
        this.betaConstraints.updateFromTuple( memory.betaMemory.getContext(),
                                              workingMemory,
                                              leftTuple );

        for ( final java.util.Iterator it = this.dataProvider.getResults( leftTuple,
                                                                          workingMemory,
                                                                          context,
                                                                          memory.providerContext ); it.hasNext(); ) {
            final Object object = it.next();

            final InternalFactHandle handle = workingMemory.getFactHandleFactory().newFactHandle( object,
                                                                                                  false,
                                                                                                  workingMemory );

            boolean isAllowed = true;
            if ( this.alphaConstraints != null ) {
                // First alpha node filters
                for ( int i = 0, length = this.alphaConstraints.length; i < length; i++ ) {
                    if ( !this.alphaConstraints[i].isAllowed( handle,
                                                              workingMemory,
                                                              memory.alphaContexts[i] ) ) {
                        // next iteration
                        isAllowed = false;
                        break;
                    }
                }
            }

            if ( isAllowed && this.betaConstraints.isAllowedCachedLeft( memory.betaMemory.getContext(),
                                                                        handle ) ) {
                list.add( new LinkedListEntry( handle ) );

                this.sink.propagateAssertTuple( leftTuple,
                                                handle,
                                                context,
                                                workingMemory );
            } else {
                workingMemory.getFactHandleFactory().destroyFactHandle( handle );
            }
        }

        this.betaConstraints.resetTuple( memory.betaMemory.getContext() );

        if ( !list.isEmpty() ) {
            memory.betaMemory.getCreatedHandles().put( leftTuple,
                                                       list );
        }

    }

    public void retractTuple(final ReteTuple leftTuple,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {

        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );
        final ReteTuple tuple = memory.betaMemory.getTupleMemory().remove( leftTuple );

        if ( tuple == null ) {
            return;
        }

        final LinkedList list = (LinkedList) memory.betaMemory.getCreatedHandles().remove( tuple );
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

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {

        context.visitTupleSource( this );

        if ( !node.isInUse() ) {
            removeTupleSink( (TupleSink) node );
        }
        if ( !this.isInUse() ) {
            for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
                workingMemories[i].clearNodeMemory( this );
            }
        }
        if ( !context.alreadyVisited( this.tupleSource ) ) {
            this.tupleSource.remove( context,
                                     builder,
                                     this,
                                     workingMemories );
        }
    }

    public void updateSink(final TupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {

        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );

        final Iterator tupleIter = memory.betaMemory.getTupleMemory().iterator();
        for ( ReteTuple tuple = (ReteTuple) tupleIter.next(); tuple != null; tuple = (ReteTuple) tupleIter.next() ) {
            final LinkedList list = (LinkedList) memory.betaMemory.getCreatedHandles().remove( tuple );
            if ( list == null ) {
                continue;
            }
            for ( LinkedListEntry entry = (LinkedListEntry) list.getFirst(); entry != null; entry = (LinkedListEntry) entry.getNext() ) {
                final InternalFactHandle handle = (InternalFactHandle) entry.getObject();
                this.sink.propagateAssertTuple( tuple,
                                                handle,
                                                context,
                                                workingMemory );
            }
        }
    }

    public Object createMemory(final RuleBaseConfiguration config) {
        BetaMemory beta = new BetaMemory( new TupleHashTable(),
                                          null,
                                          this.betaConstraints.createContext() );
        return new FromMemory( beta,
                               this.dataProvider.createContext(),
                               this.alphaConstraints );
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

    public static class FromMemory
        implements
        Serializable {
        private static final long serialVersionUID = -5802345705144095216L;

        public BetaMemory         betaMemory;
        public Object             providerContext;
        public ContextEntry[]     alphaContexts;

        public FromMemory(BetaMemory betaMemory,
                          Object providerContext,
                          AlphaNodeFieldConstraint[] constraints) {
            this.betaMemory = betaMemory;
            this.providerContext = providerContext;
            this.alphaContexts = new ContextEntry[constraints.length];
            for( int i = 0; i < constraints.length; i++ ) {
                this.alphaContexts[i] = constraints[i].createContextEntry();
            }
        }
    }
}

package org.drools.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.BetaConstraints;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.core.util.Iterator;
import org.drools.core.util.LeftTupleList;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.ContextEntry;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.DataProvider;
import org.drools.spi.PropagationContext;

public class FromNode extends LeftTupleSource
    implements
    LeftTupleSinkNode,
    NodeMemory {
    /**
     *
     */
    private static final long          serialVersionUID = 400L;
       

    private DataProvider               dataProvider;
    private LeftTupleSource            tupleSource;
    private AlphaNodeFieldConstraint[] alphaConstraints;
    private BetaConstraints            betaConstraints;

    private LeftTupleSinkNode          previousTupleSinkNode;
    private LeftTupleSinkNode          nextTupleSinkNode;

    protected boolean                  tupleMemoryEnabled;

    public FromNode() {
    }

    public FromNode(final int id,
                    final DataProvider dataProvider,
                    final LeftTupleSource tupleSource,
                    final AlphaNodeFieldConstraint[] constraints,
                    final BetaConstraints binder,
                    final boolean tupleMemoryEnabled,
                    final BuildContext context ) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation() );
        this.dataProvider = dataProvider;
        this.tupleSource = tupleSource;
        this.alphaConstraints = constraints;
        this.betaConstraints = (binder == null) ? EmptyBetaConstraints.getInstance() : binder;
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        dataProvider = (DataProvider) in.readObject();
        tupleSource = (LeftTupleSource) in.readObject();
        alphaConstraints = (AlphaNodeFieldConstraint[]) in.readObject();
        betaConstraints = (BetaConstraints) in.readObject();
        previousTupleSinkNode = (LeftTupleSinkNode) in.readObject();
        nextTupleSinkNode = (LeftTupleSinkNode) in.readObject();
        tupleMemoryEnabled = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( dataProvider );
        out.writeObject( tupleSource );
        out.writeObject( alphaConstraints );
        out.writeObject( betaConstraints );
        out.writeObject( previousTupleSinkNode );
        out.writeObject( nextTupleSinkNode );
        out.writeBoolean( tupleMemoryEnabled );
    }

    /**
     * @inheritDoc
     */
    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );

        if ( this.tupleMemoryEnabled ) {
            memory.betaMemory.getLeftTupleMemory().add( leftTuple );
        }

        if ( this.sink.size() == 0 ) {
            // nothing to do
            return;
        }

        evaluateAndPropagate( leftTuple,
                              context,
                              workingMemory,
                              memory );
    }

    /**
     * @param leftTuple
     * @param context
     * @param workingMemory
     * @param memory
     */
    private void evaluateAndPropagate(final LeftTuple leftTuple,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory,
                                      final FromMemory memory) {
        this.betaConstraints.updateFromTuple( memory.betaMemory.getContext(),
                                              workingMemory,
                                              leftTuple );

        for ( final java.util.Iterator it = this.dataProvider.getResults( leftTuple,
                                                                          workingMemory,
                                                                          context,
                                                                          memory.providerContext ); it.hasNext(); ) {
            final Object object = it.next();

            final InternalFactHandle handle = workingMemory.getFactHandleFactory().newFactHandle( object,
                                                                                                  null, // set this to null, otherwise it uses the driver fact's entrypoint
                                                                                                  workingMemory );

            RightTuple rightTuple = new RightTuple( handle,
                                                    null );

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
                //list.add( new LinkedListEntry( handle ) );

                this.sink.propagateAssertLeftTuple( leftTuple,
                                                    rightTuple,
                                                    context,
                                                    workingMemory,
                                                    this.tupleMemoryEnabled );
            } else {
                workingMemory.getFactHandleFactory().destroyFactHandle( handle );
            }
        }

        this.betaConstraints.resetTuple( memory.betaMemory.getContext() );
    }

    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {

        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );
        memory.betaMemory.getLeftTupleMemory().remove( leftTuple );

        this.sink.propagateRetractLeftTupleDestroyRightTuple( leftTuple,
                                                              context,
                                                              workingMemory );
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
                                                                                      null,
                                                                                      null );
            this.tupleSource.updateSink( this,
                                         propagationContext,
                                         workingMemory );
        }
    }

    public void networkUpdated() {
        this.tupleSource.networkUpdated();
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {

        context.visitTupleSource( this );

        if ( !node.isInUse() ) {
            removeTupleSink( (LeftTupleSink) node );
        }
        
        if ( !this.isInUse() ) {
            for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
                FromMemory memory = ( FromMemory ) workingMemories[i].getNodeMemory( this );
                Iterator it = memory.betaMemory.getLeftTupleMemory().iterator();
                for ( LeftTuple leftTuple = (LeftTuple) it.next(); leftTuple != null; leftTuple = (LeftTuple) it.next() ) {
                    leftTuple.unlinkFromLeftParent();
                    leftTuple.unlinkFromRightParent();
                }                
                
                // RightTuple is already disconnected via the child LeftTuple in the sink nodes
                // special case exists in the BetaNode to handle destroying of the FactHandle
                
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

    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {

        final FromMemory memory = (FromMemory) workingMemory.getNodeMemory( this );

        final Iterator tupleIter = memory.betaMemory.getLeftTupleMemory().iterator();
        for ( LeftTuple leftTuple = (LeftTuple) tupleIter.next(); leftTuple != null; leftTuple = (LeftTuple) tupleIter.next() ) {
            evaluateAndPropagate( leftTuple,
                                  context,
                                  workingMemory,
                                  memory );
        }
    }

    public Object createMemory(final RuleBaseConfiguration config) {
        BetaMemory beta = new BetaMemory( new LeftTupleList(),
                                          null,
                                          this.betaConstraints.createContext() );
        return new FromMemory( beta,
                               this.dataProvider.createContext(),
                               this.alphaConstraints );
    }

    public boolean isLeftTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    /**
     * Returns the next node
     * @return
     *      The next TupleSinkNode
     */
    public LeftTupleSinkNode getNextLeftTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next TupleSinkNode
     */
    public void setNextLeftTupleSinkNode(final LeftTupleSinkNode next) {
        this.nextTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous TupleSinkNode
     */
    public LeftTupleSinkNode getPreviousLeftTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous TupleSinkNode
     */
    public void setPreviousLeftTupleSinkNode(final LeftTupleSinkNode previous) {
        this.previousTupleSinkNode = previous;
    }
    
    public short getType() {
        return NodeTypeEnums.FromNode;
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
            for ( int i = 0; i < constraints.length; i++ ) {
                this.alphaContexts[i] = constraints[i].createContextEntry();
            }
        }
    }
}

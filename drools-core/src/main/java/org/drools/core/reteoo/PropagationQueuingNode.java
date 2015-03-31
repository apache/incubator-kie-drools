/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.reteoo;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.bitmask.BitMask;
import org.drools.core.util.bitmask.EmptyBitMask;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A node that will add the propagation to the working memory actions queue,
 * in order to allow multiple threads to concurrently assert objects to multiple
 * entry points.
 */
public class PropagationQueuingNode extends ObjectSource
    implements
    ObjectSinkNode,
    MemoryFactory {

    private static final long serialVersionUID        = 510l;

    // should we make this one configurable?
    private static final int  PROPAGATION_SLICE_LIMIT = 1000;

    private ObjectSinkNode    previousObjectSinkNode;
    private ObjectSinkNode    nextObjectSinkNode;
    private PropagateAction   action; 

    public PropagationQueuingNode() {
    }

    /**
     * Construct a <code>PropagationQueuingNode</code> that will queue up
     * propagations until it the engine reaches a safe propagation point,
     * when all the queued facts are propagated.
     *
     * @param id           Node's ID
     * @param objectSource Node's object source
     * @param context
     */
    public PropagationQueuingNode(final int id,
                                  final ObjectSource objectSource,
                                  final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getKnowledgeBase().getConfiguration().isMultithreadEvaluation(),
               objectSource,
               context.getKnowledgeBase().getConfiguration().getAlphaNodeHashingThreshold() );
        this.action = new PropagateAction( this );
        initDeclaredMask(context);
    }
    
    @Override
    public BitMask calculateDeclaredMask(List<String> settableProperties) {
        return EmptyBitMask.get();
    }      

    public void readExternal( ObjectInput in ) throws IOException,
                                              ClassNotFoundException {
        super.readExternal( in );
        action = (PropagateAction) in.readObject();
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeObject( action );
    }
    
    public short getType() {
        return NodeTypeEnums.PropagationQueuingNode;
    }     

    public void updateSink( ObjectSink sink,
                            PropagationContext context,
                            InternalWorkingMemory workingMemory ) {

        final PropagationQueueingNodeMemory memory = (PropagationQueueingNodeMemory) workingMemory.getNodeMemory( this );

        // this is just sanity code. We may remove it in the future, but keeping it for now.
        if ( !memory.isEmpty() ) {
            throw new RuntimeException( "Error updating sink. Not safe to update sink as the PropagatingQueueingNode memory is not empty at node: " + this.toString() );
        }

        // as this node is simply a queue, ask object source to update the child sink directly
        this.source.updateSink( sink,
                                context,
                                workingMemory );
    }

    public void attach( BuildContext context ) {
        this.source.addObjectSink( this );
        // this node does not require update, so nothing else to do.
    }

    public ObjectSinkNode getNextObjectSinkNode() {
        return this.nextObjectSinkNode;
    }

    public ObjectSinkNode getPreviousObjectSinkNode() {
        return this.previousObjectSinkNode;
    }

    public void setNextObjectSinkNode( ObjectSinkNode next ) {
        this.nextObjectSinkNode = next;
    }

    public void setPreviousObjectSinkNode( ObjectSinkNode previous ) {
        this.previousObjectSinkNode = previous;
    }

    public boolean isObjectMemoryEnabled() {
        return true;
    }

    public void assertObject( InternalFactHandle factHandle,
                              PropagationContext context,
                              InternalWorkingMemory workingMemory ) {
        final PropagationQueueingNodeMemory memory = (PropagationQueueingNodeMemory) workingMemory.getNodeMemory( this );
        memory.addAction( new AssertAction( factHandle,
                                            context ) );

        // if not queued yet, we need to queue it up
        if ( memory.isQueued().compareAndSet( false,
                                              true ) ) {
            workingMemory.queueWorkingMemoryAction( this.action );
        }
    }

    public void retractObject( InternalFactHandle handle,
                               PropagationContext context,
                               InternalWorkingMemory workingMemory ) {
        final PropagationQueueingNodeMemory memory = (PropagationQueueingNodeMemory) workingMemory.getNodeMemory( this );
        memory.addAction( new RetractAction( handle,
                                             context ) );

        // if not queued yet, we need to queue it up
        if ( memory.isQueued().compareAndSet( false,
                                              true ) ) {
            workingMemory.queueWorkingMemoryAction( this.action );
        }
    }

    public void modifyObject(InternalFactHandle factHandle,
                             ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {
        final PropagationQueueingNodeMemory memory = (PropagationQueueingNodeMemory) workingMemory.getNodeMemory( this );

        //        for ( ObjectSink s : this.sink.getSinks() ) {
        //            RightTuple rightTuple = modifyPreviousTuples.removeRightTuple( (RightTupleSink) s );
        //            if ( rightTuple != null ) {
        //                rightTuple.reAdd();
        //                // RightTuple previously existed, so continue as modify
        //                memory.addAction( new ModifyToSinkAction( rightTuple,
        //                                                          context,
        //                                                          (RightTupleSink) s ) );
        //            } else {
        //                // RightTuple does not exist, so create and continue as assert
        //                memory.addAction( new AssertToSinkAction( factHandle,
        //                                                          context,
        //                                                          s ) );
        //            }
        //        }

        for ( ObjectSink s : this.sink.getSinks() ) {
            BetaNode betaNode = (BetaNode) s;
            RightTuple rightTuple = modifyPreviousTuples.peekRightTuple();
            while ( rightTuple != null &&
                    rightTuple.getRightTupleSink().getRightInputOtnId().before( betaNode.getRightInputOtnId() ) ) {
                modifyPreviousTuples.removeRightTuple();
                // we skipped this node, due to alpha hashing, so retract now
                rightTuple.getRightTupleSink().retractRightTuple( rightTuple,
                                                                  context,
                                                                  workingMemory );
                rightTuple = modifyPreviousTuples.peekRightTuple();
            }

            if ( rightTuple != null && rightTuple.getRightTupleSink().getRightInputOtnId().equals( betaNode.getRightInputOtnId() ) ) {
                modifyPreviousTuples.removeRightTuple();
                rightTuple.reAdd();
                if ( context.getModificationMask().intersects( betaNode.getRightInferredMask() ) ) {
                    // RightTuple previously existed, so continue as modify
                    memory.addAction( new ModifyToSinkAction( rightTuple,
                                                              context,
                                                              betaNode ) );
                }
            } else {
                if ( context.getModificationMask().intersects( betaNode.getRightInferredMask() ) ) {
                    // RightTuple does not exist for this node, so create and continue as assert
                    memory.addAction( new AssertToSinkAction( factHandle,
                                                              context,
                                                              betaNode ) );
                }
            }
        }

        // if not queued yet, we need to queue it up
        if ( memory.isQueued().compareAndSet( false,
                                              true ) ) {
            workingMemory.queueWorkingMemoryAction( this.action );
        }
    }

    public  void byPassModifyToBetaNode (final InternalFactHandle factHandle,
                                         final ModifyPreviousTuples modifyPreviousTuples,
                                         final PropagationContext context,
                                         final InternalWorkingMemory workingMemory) {
        modifyObject( factHandle, modifyPreviousTuples, context, workingMemory );
    }    
    
    /**
     * Propagate all queued actions (asserts and retracts).
     * <p/>
     * This method implementation is based on optimistic behavior to avoid the
     * use of locks. There may eventually be a minimum wasted effort, but overall
     * it will be better than paying for the lock's cost.
     *
     * @param workingMemory
     */
    public void propagateActions( InternalWorkingMemory workingMemory ) {
        final PropagationQueueingNodeMemory memory = (PropagationQueueingNodeMemory) workingMemory.getNodeMemory( this );

        // first we clear up the action queued flag
        memory.isQueued().compareAndSet( true,
                                         false );

        // we limit the propagation to avoid a hang when this queue is never empty
        Action next;
        for ( int counter = 0; counter < PROPAGATION_SLICE_LIMIT; counter++ ) {
            next = memory.getNextAction();
            if ( next != null ) {
                next.execute( this.sink,
                        workingMemory );
            } else {
                break;
            }
        }

        if ( memory.hasNextAction() && memory.isQueued().compareAndSet( false,
                                                                        true ) ) {
            // add action to the queue again.
            workingMemory.queueWorkingMemoryAction( this.action );
        }
    }

    public void setObjectMemoryEnabled( boolean objectMemoryOn ) {
        throw new UnsupportedOperationException( "PropagationQueueingNode must have its node memory enabled." );
    }

    public Memory createMemory(RuleBaseConfiguration config, InternalWorkingMemory wm) {
        return new PropagationQueueingNodeMemory();
    }
    
    public int hashCode() {
        return this.source.hashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof PropagationQueuingNode) ) {
            return false;
        }

        final PropagationQueuingNode other = (PropagationQueuingNode) object;

        return this.source.equals( other.source );
    }

    

    /**
     * Memory implementation for the node
     */
    public static class PropagationQueueingNodeMemory
        implements
        Memory {

        private static final long             serialVersionUID = 7372028632974484023L;

        private ConcurrentLinkedQueue<Action> queue;

        // "singleton" action - there is one of this for each node in each working memory
        private AtomicBoolean                 isQueued;

        public PropagationQueueingNodeMemory() {
            super();
            this.queue = new ConcurrentLinkedQueue<Action>();
            this.isQueued = new AtomicBoolean( false );
        }

        public boolean isEmpty() {
            return this.queue.isEmpty();
        }

        public void addAction( Action action ) {
            this.queue.add( action );
        }

        public Action getNextAction() {
            return this.queue.poll();
        }

        public boolean hasNextAction() {
            return this.queue.peek() != null;
        }

        public AtomicBoolean isQueued() {
            return isQueued;
        }

        public long getSize() {
            return this.queue.size();
        }
 
        public short getNodeType() {
            return NodeTypeEnums.PropagationQueueingNode;
        }

        public Memory getPrevious() {
            throw new UnsupportedOperationException();
        }

        public void setPrevious(Memory previous) {
            throw new UnsupportedOperationException();
        }

        public void setNext(Memory next) {
            throw new UnsupportedOperationException();
        }

        public Memory getNext() {
            throw new UnsupportedOperationException();
        }

        public SegmentMemory getSegmentMemory() {
            return null;
        }

        public void setSegmentMemory(SegmentMemory segmentMemory) {
            throw new UnsupportedOperationException();
        }

        public void nullPrevNext() {
            throw new UnsupportedOperationException();
        }

        public void reset() {
            queue.clear();
            isQueued.set(false);
        }
    }

    private static abstract class Action
        implements
        Externalizable {

        protected InternalFactHandle handle;
        protected PropagationContext context;

        public Action(InternalFactHandle handle,
                      PropagationContext context) {
            super();
            this.handle = handle;
            this.context = context;
        }

        public void readExternal( ObjectInput in ) throws IOException,
                                                  ClassNotFoundException {
            handle = (InternalFactHandle) in.readObject();
            context = (PropagationContext) in.readObject();
        }

        public void writeExternal( ObjectOutput out ) throws IOException {
            out.writeObject( handle );
            out.writeObject( context );
        }

        public abstract void execute( final ObjectSinkPropagator sink,
                                      final InternalWorkingMemory workingMemory );
    }

    private static class AssertAction extends Action {
        private static final long serialVersionUID = -8478488926430845209L;

        public AssertAction(final InternalFactHandle handle,
                            final PropagationContext context) {
            super( handle,
                   context );
        }

        public void execute( final ObjectSinkPropagator sink,
                             final InternalWorkingMemory workingMemory ) {
            sink.propagateAssertObject( this.handle,
                                        this.context,
                                        workingMemory );
            context.evaluateActionQueue( workingMemory );
        }
    }

    private static class AssertToSinkAction extends Action {
        private static final long serialVersionUID = -8478488926430845209L;

        private ObjectSink        nodeSink;

        public AssertToSinkAction(final InternalFactHandle handle,
                                  final PropagationContext context,
                                  final ObjectSink sink) {
            super( handle,
                   context );
            nodeSink = sink;
        }

        public void execute( final ObjectSinkPropagator sink,
                             final InternalWorkingMemory workingMemory ) {
            nodeSink.assertObject( this.handle,
                                   this.context,
                                   workingMemory );
            context.evaluateActionQueue( workingMemory );
        }

        @Override
        public void readExternal( ObjectInput in ) throws IOException,
                                                  ClassNotFoundException {
            super.readExternal( in );
            nodeSink = (ObjectSink) in.readObject();
        }

        @Override
        public void writeExternal( ObjectOutput out ) throws IOException {
            super.writeExternal( out );
            out.writeObject( nodeSink );
        }
    }

    private static class RetractAction extends Action {
        private static final long serialVersionUID = -84784886430845209L;

        public RetractAction(final InternalFactHandle handle,
                             final PropagationContext context) {
            super( handle,
                   context );
        }

        public void execute( final ObjectSinkPropagator sink,
                             final InternalWorkingMemory workingMemory ) {

            for ( RightTuple rightTuple = this.handle.getFirstRightTuple(); rightTuple != null; rightTuple = rightTuple.getHandleNext() ) {
                rightTuple.getRightTupleSink().retractRightTuple( rightTuple,
                                                                  context,
                                                                  workingMemory );
            }
            this.handle.clearRightTuples();

            for ( LeftTuple leftTuple = this.handle.getLastLeftTuple(); leftTuple != null; leftTuple = leftTuple.getLeftParentNext() ) {
                leftTuple.getLeftTupleSink().retractLeftTuple( leftTuple,
                                                               context,
                                                               workingMemory );
            }
            this.handle.clearLeftTuples();
            context.evaluateActionQueue( workingMemory );
        }
    }

    private static class ModifyToSinkAction extends Action {
        private static final long serialVersionUID = -8478488926430845209L;
        private RightTupleSink    nodeSink;
        private RightTuple        rightTuple;

        public ModifyToSinkAction(final RightTuple rightTuple,
                                  final PropagationContext context,
                                  final RightTupleSink nodeSink) {
            super( rightTuple.getFactHandle(),
                   context );
            this.nodeSink = nodeSink;
            this.rightTuple = rightTuple;
        }

        public void execute( final ObjectSinkPropagator sink,
                             final InternalWorkingMemory workingMemory ) {
            nodeSink.modifyRightTuple( rightTuple,
                                       context,
                                       workingMemory );
            context.evaluateActionQueue( workingMemory );
        }
        
        @Override
        public void readExternal( ObjectInput in ) throws IOException,
                                                  ClassNotFoundException {
            super.readExternal( in );
            nodeSink = (RightTupleSink) in.readObject();
            rightTuple = (RightTuple) in.readObject();
        }

        @Override
        public void writeExternal( ObjectOutput out ) throws IOException {
            super.writeExternal( out );
            out.writeObject( nodeSink );
            out.writeObject( rightTuple );
        }
    }

    /**
     * This is the action that is added to the working memory actions queue, so that
     * this node propagation can be triggered at a safe point
     */
    public static class PropagateAction
            extends PropagationEntry.AbstractPropagationEntry
            implements
        WorkingMemoryAction {

        private static final long      serialVersionUID = 6765029029501617115L;

        private PropagationQueuingNode node;

        public PropagateAction() {

        }

        public PropagateAction(PropagationQueuingNode node) {
            this.node = node;
        }

        public PropagateAction(MarshallerReaderContext context) throws IOException {
            this.node = (PropagationQueuingNode) context.sinks.get( context.readInt() );
        }

        public PropagateAction(MarshallerReaderContext context,
                               ProtobufMessages.ActionQueue.Action _action) {
            this.node = (PropagationQueuingNode) context.sinks.get( _action.getPropagate().getNodeId() );
        }

        public ProtobufMessages.ActionQueue.Action serialize( MarshallerWriteContext context ) {
            return ProtobufMessages.ActionQueue.Action.newBuilder()
                    .setType( ProtobufMessages.ActionQueue.ActionType.PROPAGATE )
                    .setPropagate( ProtobufMessages.ActionQueue.Propagate.newBuilder()
                                   .setNodeId( node.getId() )
                                   .build() )
                    .build();
        }

        public void execute( InternalWorkingMemory workingMemory ) {
            this.node.propagateActions( workingMemory );
        }
    }

}

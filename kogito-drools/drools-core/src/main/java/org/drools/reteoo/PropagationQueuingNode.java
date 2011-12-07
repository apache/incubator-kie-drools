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

package org.drools.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.RuleBaseConfiguration;
import org.drools.RuntimeDroolsException;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalKnowledgeRuntime;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.WorkingMemoryAction;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.reteoo.builder.BuildContext;
import org.drools.spi.PropagationContext;

/**
 * A node that will add the propagation to the working memory actions queue,
 * in order to allow multiple threads to concurrently assert objects to multiple
 * entry points.
 */
public class PropagationQueuingNode extends ObjectSource
    implements
    ObjectSinkNode,
    NodeMemory {

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
               context.getRuleBase().getConfiguration().isMultithreadEvaluation(),
               objectSource,
               context.getRuleBase().getConfiguration().getAlphaNodeHashingThreshold() );
        this.action = new PropagateAction( this );
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

    /**
     * @see org.drools.reteoo.ObjectSource#updateSink(org.drools.reteoo.ObjectSink, org.drools.spi.PropagationContext, org.drools.common.InternalWorkingMemory)
     */
    public void updateSink( ObjectSink sink,
                            PropagationContext context,
                            InternalWorkingMemory workingMemory ) {

        final PropagationQueueingNodeMemory memory = (PropagationQueueingNodeMemory) workingMemory.getNodeMemory( this );

        // this is just sanity code. We may remove it in the future, but keeping it for now.
        if ( !memory.isEmpty() ) {
            throw new RuntimeDroolsException( "Error updating sink. Not safe to update sink as the PropagatingQueueingNode memory is not empty at node: " + this.toString() );
        }

        // as this node is simply a queue, ask object source to update the child sink directly
        this.source.updateSink( sink,
                                context,
                                workingMemory );
    }

    /**
     * @see org.drools.common.BaseNode#attach()
     */
    public void attach() {
        this.source.addObjectSink( this );
    }

    /**
     * @see org.drools.common.BaseNode#attach(org.drools.common.InternalWorkingMemory[])
     */
    public void attach( InternalWorkingMemory[] workingMemories ) {
        attach();
        // this node does not require update, so nothing else to do.
    }

    /**
     * @see org.drools.reteoo.ObjectSinkNode#getNextObjectSinkNode()
     */
    public ObjectSinkNode getNextObjectSinkNode() {
        return this.nextObjectSinkNode;
    }

    /**
     * @see org.drools.reteoo.ObjectSinkNode#getPreviousObjectSinkNode()
     */
    public ObjectSinkNode getPreviousObjectSinkNode() {
        return this.previousObjectSinkNode;
    }

    /**
     * @see org.drools.reteoo.ObjectSinkNode#setNextObjectSinkNode(org.drools.reteoo.ObjectSinkNode)
     */
    public void setNextObjectSinkNode( ObjectSinkNode next ) {
        this.nextObjectSinkNode = next;
    }

    /**
     * @see org.drools.reteoo.ObjectSinkNode#setPreviousObjectSinkNode(org.drools.reteoo.ObjectSinkNode)
     */
    public void setPreviousObjectSinkNode( ObjectSinkNode previous ) {
        this.previousObjectSinkNode = previous;
    }

    public boolean isObjectMemoryEnabled() {
        return true;
    }

    /**
     * @see org.drools.reteoo.ObjectSink#assertObject(InternalFactHandle, org.drools.spi.PropagationContext, org.drools.common.InternalWorkingMemory)
     */
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

    public void modifyObject( InternalFactHandle factHandle,
                              ModifyPreviousTuples modifyPreviousTuples,
                              PropagationContext context,
                              InternalWorkingMemory workingMemory ) {
        final PropagationQueueingNodeMemory memory = (PropagationQueueingNodeMemory) workingMemory.getNodeMemory( this );

        for ( ObjectSink s : this.sink.getSinks() ) {
            RightTuple rightTuple = modifyPreviousTuples.removeRightTuple( (RightTupleSink) s );
            if ( rightTuple != null ) {
                rightTuple.reAdd();
                // RightTuple previously existed, so continue as modify
                memory.addAction( new ModifyToSinkAction( rightTuple,
                                                          context,
                                                          (RightTupleSink) s ) );
            } else {
                // RightTuple does not exist, so create and continue as assert
                memory.addAction( new AssertToSinkAction( factHandle,
                                                          context,
                                                          s ) );
            }
        }

        // if not queued yet, we need to queue it up
        if ( memory.isQueued().compareAndSet( false,
                                              true ) ) {
            workingMemory.queueWorkingMemoryAction( this.action );
        }
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
        Action next = memory.getNext();
        for ( int counter = 0; next != null && counter < PROPAGATION_SLICE_LIMIT; next = memory.getNext(), counter++ ) {
            next.execute( this.sink,
                          workingMemory );
        }

        if ( memory.hasNext() && memory.isQueued().compareAndSet( false,
                                                                  true ) ) {
            // add action to the queue again.
            workingMemory.queueWorkingMemoryAction( this.action );
        }
    }

    public void setObjectMemoryEnabled( boolean objectMemoryOn ) {
        throw new UnsupportedOperationException( "PropagationQueueingNode must have its node memory enabled." );
    }

    public Object createMemory( RuleBaseConfiguration config ) {
        return new PropagationQueueingNodeMemory();
    }

    /**
     * Memory implementation for the node
     */
    public static class PropagationQueueingNodeMemory
        implements
        Externalizable {

        private static final long             serialVersionUID = 7372028632974484023L;

        private ConcurrentLinkedQueue<Action> queue;

        // "singleton" action - there is one of this for each node in each working memory
        private AtomicBoolean                 isQueued;

        public PropagationQueueingNodeMemory() {
            super();
            this.queue = new ConcurrentLinkedQueue<Action>();
            this.isQueued = new AtomicBoolean( false );
        }

        @SuppressWarnings("unchecked")
        public void readExternal( ObjectInput in ) throws IOException,
                                                  ClassNotFoundException {
            queue = (ConcurrentLinkedQueue<Action>) in.readObject();
            isQueued = (AtomicBoolean) in.readObject();
        }

        public void writeExternal( ObjectOutput out ) throws IOException {
            out.writeObject( queue );
            out.writeObject( isQueued );
        }

        public boolean isEmpty() {
            return this.queue.isEmpty();
        }

        public void addAction( Action action ) {
            this.queue.add( action );
        }

        public Action getNext() {
            return this.queue.poll();
        }

        public boolean hasNext() {
            return this.queue.peek() != null;
        }

        public AtomicBoolean isQueued() {
            return isQueued;
        }

        public long getSize() {
            return this.queue.size();
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

            for ( RightTuple rightTuple = this.handle.getFirstRightTuple(); rightTuple != null; rightTuple = (RightTuple) rightTuple.getHandleNext() ) {
                rightTuple.getRightTupleSink().retractRightTuple( rightTuple,
                                                                  context,
                                                                  workingMemory );
            }
            this.handle.clearRightTuples();

            for ( LeftTuple leftTuple = this.handle.getLastLeftTuple(); leftTuple != null; leftTuple = (LeftTuple) leftTuple.getLeftParentNext() ) {
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

        public void write( MarshallerWriteContext context ) throws IOException {
            context.writeShort( WorkingMemoryAction.PropagateAction );
            context.write( node.getId() );
        }

        public void readExternal( ObjectInput in ) throws IOException,
                                                  ClassNotFoundException {
            node = (PropagationQueuingNode) in.readObject();
        }

        public void writeExternal( ObjectOutput out ) throws IOException {
            out.writeObject( node );
        }

        public void execute( InternalWorkingMemory workingMemory ) {
            this.node.propagateActions( workingMemory );
        }

        public void execute( InternalKnowledgeRuntime kruntime ) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }
    }

}

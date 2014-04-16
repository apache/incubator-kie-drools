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

package org.drools.core.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.PriorityQueue;

import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.PersisterEnums;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.marshalling.impl.ProtobufMessages.ActionQueue.Action;
import org.drools.core.marshalling.impl.ProtobufMessages.Timers.Timer;
import org.drools.core.marshalling.impl.TimersInputMarshaller;
import org.drools.core.marshalling.impl.TimersOutputMarshaller;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.WindowNode;
import org.drools.core.reteoo.WindowNode.WindowMemory;
import org.drools.core.spi.PropagationContext;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.TimerService;
import org.drools.core.time.impl.PointInTimeTrigger;

public class SlidingTimeWindow
    implements
    Externalizable,
    Behavior {

    private long              size;
    // stateless job
    public static final BehaviorJob job = new BehaviorJob();

    private int        nodeId;

    public SlidingTimeWindow() {
        this( 0 );
    }

    /**
     * @param size
     */
    public SlidingTimeWindow(final long size) {
        super();
        this.size = size;
    }

    /**
     * @inheritDoc
     *
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(final ObjectInput in) throws IOException,
                                                  ClassNotFoundException {
        this.size = in.readLong();
        this.nodeId = in.readInt();
    }

    /**
     * @inheritDoc
     *
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeLong( this.size );
        out.writeInt( this.nodeId );
    }

    public BehaviorType getType() {
        return BehaviorType.TIME_WINDOW;
    }

    public void setWindowNode(WindowNode windowNode) {
        this.nodeId = windowNode.getId();
    }

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(final long size) {
        this.size = size;
    }

    public Object createContext() {
        return new SlidingTimeWindowContext();
    }

    public boolean assertFact(final WindowMemory memory,
                              final Object context,
                              final InternalFactHandle fact,
                              final PropagationContext pctx,
                              final InternalWorkingMemory workingMemory) {
        final SlidingTimeWindowContext queue = (SlidingTimeWindowContext) context;
        final EventFactHandle handle = (EventFactHandle) fact;
        long currentTime = workingMemory.getTimerService().getCurrentTime();
        if ( isExpired( currentTime, handle ) ) {
            return false;
        }
        synchronized (queue.queue) {
            queue.queue.add( handle );
            if ( queue.queue.peek() == handle ) {
                // update next expiration time
                updateNextExpiration( handle,
                                      pctx,
                                      workingMemory,
                                      memory,
                                      this,
                                      queue,
                                      nodeId );
            }
        }
        return true;
    }

    public void retractFact(final WindowMemory memory,
                            final Object context,
                            final InternalFactHandle fact,
                            final PropagationContext pctx,
                            final InternalWorkingMemory workingMemory) {
        final SlidingTimeWindowContext queue = (SlidingTimeWindowContext) context;
        final EventFactHandle handle = (EventFactHandle) fact;
        // it may be a call back to expire the tuple that is already being expired
        synchronized (queue.queue) {
            if ( queue.expiringHandle != handle ) {
                if ( queue.queue.peek() == handle ) {
                    // it was the head of the queue
                    queue.queue.poll();
                    // update next expiration time
                    updateNextExpiration( queue.queue.peek(),
                                          pctx,
                                          workingMemory,
                                          memory,
                                          this,
                                          queue,
                                          nodeId);
                } else {
                    queue.queue.remove( handle );
                }
            }
        }
    }

    public void expireFacts(final WindowMemory memory,
                            final Object context,
                            final PropagationContext pctx,
                            final InternalWorkingMemory workingMemory) {
        TimerService clock = workingMemory.getTimerService();
        long currentTime = clock.getCurrentTime();
        SlidingTimeWindowContext queue = (SlidingTimeWindowContext) context;
        synchronized (queue.queue) {
            EventFactHandle handle = queue.queue.peek();
            while ( handle != null && isExpired( currentTime,
                                                 handle ) ) {
                queue.expiringHandle = handle;
                queue.queue.remove();
                if( handle.isValid()) {
                    // if not expired yet, expire it
                    PropagationContextFactory pctxFactory = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();
                    final PropagationContext expiresPctx = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), PropagationContext.EXPIRATION,
                                                                                                null, null, handle);
                    ObjectTypeNode.doRetractObject(handle, expiresPctx, workingMemory);
                    expiresPctx.evaluateActionQueue( workingMemory );
                }
                queue.expiringHandle = null;
                handle = queue.queue.peek();
            }
            // update next expiration time 
            updateNextExpiration( handle,
                                  pctx,
                                  workingMemory,
                                  memory,
                                  this,
                                  queue,
                                  nodeId );
        }

    }

    private boolean isExpired(final long currentTime,
                              final EventFactHandle handle) {
        return handle.getStartTimestamp() + this.size <= currentTime;
    }

    private static void updateNextExpiration(final InternalFactHandle fact,
                                             final PropagationContext pctx,
                                             final InternalWorkingMemory workingMemory,
                                             final WindowMemory memory,
                                             final SlidingTimeWindow stw,
                                             final Object context,
                                             final int nodeId) {
        TimerService clock = workingMemory.getTimerService();
        if ( fact != null ) {
            long nextTimestamp = ((EventFactHandle) fact).getStartTimestamp() + stw.getSize();
            if ( nextTimestamp < clock.getCurrentTime() ) {
                // Past and out-of-order events should not be insert,
                // but the engine silently accepts them anyway, resulting in possibly undesirable behaviors
                workingMemory.queueWorkingMemoryAction( new BehaviorExpireWMAction( nodeId, stw, memory, context, pctx ) );
            } else {
                JobContext jobctx = new BehaviorJobContext( nodeId, workingMemory, stw, memory,
                                                            context, pctx);
                JobHandle handle = clock.scheduleJob( job,
                                                      jobctx,
                                                      new PointInTimeTrigger( nextTimestamp, null, null ) );
                jobctx.setJobHandle( handle );
            }
        }
    }

    public long getExpirationOffset() {
        return this.size;
    }

    public String toString() {
        return "SlidingTimeWindow( size=" + size + " )";
    }

    public static class SlidingTimeWindowContext
        implements
        Externalizable {

        public PriorityQueue<EventFactHandle> queue;
        public EventFactHandle                expiringHandle;

        public SlidingTimeWindowContext() {
            this.queue = new PriorityQueue<EventFactHandle>( 16 ); // arbitrary size... can we improve it?
        }

        @SuppressWarnings("unchecked")
        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            this.queue = (PriorityQueue<EventFactHandle>) in.readObject();
            this.expiringHandle = (EventFactHandle) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( this.queue );
            out.writeObject( this.expiringHandle );
        }

        public PriorityQueue<EventFactHandle> getQueue() {
            return queue;
        }

        public void setQueue(PriorityQueue<EventFactHandle> queue) {
            this.queue = queue;
        }

        public EventFactHandle getExpiringHandle() {
            return expiringHandle;
        }

        public void setExpiringTuple(EventFactHandle expiringHandle) {
            this.expiringHandle = expiringHandle;
        }

    }
    
    public static class BehaviorJobContextTimerOutputMarshaller implements TimersOutputMarshaller {
        public void write(JobContext jobCtx,
                          MarshallerWriteContext outputCtx) throws IOException {   
            outputCtx.writeShort( PersisterEnums.BEHAVIOR_TIMER );
            // BehaviorJob, no state            
            BehaviorJobContext bjobCtx = ( BehaviorJobContext ) jobCtx;
            
            // write out SlidingTimeWindowContext
            SlidingTimeWindowContext slCtx = ( SlidingTimeWindowContext ) bjobCtx.behaviorContext;
  
            EventFactHandle handle = slCtx.getQueue().peek();
            outputCtx.writeInt( handle.getId() );

//            BetaNode node = (BetaNode) handle.getRightTupleSink();
//            outputCtx.writeInt( node.getId() );
//
//            Behavior[] behaviors = node.getBehaviors();
//            int i = 0;
//            for ( ; i < behaviors.length; i++ ) {
//                if ( behaviors[i] == bjobCtx.behavior ) {
//                    break;
//                }
//            }
//            outputCtx.writeInt( i );
        }

        public Timer serialize(JobContext jobCtx,
                               MarshallerWriteContext outputCtx) {
            // BehaviorJob, no state            
            BehaviorJobContext bjobCtx = ( BehaviorJobContext ) jobCtx;
            // write out SlidingTimeWindowContext
            SlidingTimeWindowContext slCtx = ( SlidingTimeWindowContext ) bjobCtx.behaviorContext;
  
            EventFactHandle handle = slCtx.getQueue().peek();
            
            return ProtobufMessages.Timers.Timer.newBuilder()
                    .setType( ProtobufMessages.Timers.TimerType.BEHAVIOR )
                    .setBehavior( ProtobufMessages.Timers.BehaviorTimer.newBuilder()
                                  .setHandleId( handle.getId() )
                                  .build() )
                    .build();
        }
    }
    
    public static class BehaviorJobContextTimerInputMarshaller implements TimersInputMarshaller {
        public void read(MarshallerReaderContext inCtx) throws IOException, ClassNotFoundException {
            int sinkId = inCtx.readInt();
            WindowNode windowNode = (WindowNode) inCtx.sinks.get( sinkId );
            
            WindowMemory memory = (WindowMemory) inCtx.wm.getNodeMemory( windowNode );       
            
            Object[] behaviorContext = ( Object[]  ) memory.behaviorContext;
            
            int i = inCtx.readInt();
//            SlidingTimeWindowContext stwCtx = ( SlidingTimeWindowContext ) behaviorContext[i];
//
//            updateNextExpiration( stwCtx.queue.peek(),
//                                  inCtx.wm,
//                                  memory,
//                                  (SlidingTimeWindow) windowNode.getBehaviors()[i],
//                                  stwCtx );
        }

        public void deserialize(MarshallerReaderContext inCtx,
                                Timer _timer) throws ClassNotFoundException {
            int i = _timer.getBehavior().getHandleId();
            // this should probably be doing something...
                       
//            updateNextExpiration( ( RightTuple) stwCtx.queue.peek(),
//                                  inCtx.wm,
//                                  (SlidingTimeWindow) betaNode.getBehaviors()[i],
//                                  stwCtx );            
        }
    }    
    

    public static class BehaviorJobContext
        implements
        JobContext,
        Externalizable {
        public InternalWorkingMemory workingMemory;
        public int                   nodeId;
        public Behavior              behavior;
        public Object                behaviorContext;
        public WindowMemory          memory;
        public JobHandle             handle;
        public final PropagationContext pctx;

        /**
         * @param workingMemory
         * @param behavior
         * @param behaviorContext
         */
        public BehaviorJobContext(int                   nodeId,
                                  InternalWorkingMemory workingMemory,
                                  Behavior behavior,
                                  WindowMemory memory,
                                  Object behaviorContext,
                                  final PropagationContext pctx) {
            super();
            this.nodeId = nodeId;
            this.workingMemory = workingMemory;
            this.behavior = behavior;
            this.memory = memory;
            this.behaviorContext = behaviorContext;
            this.pctx = pctx;
        }

        public JobHandle getJobHandle() {
            return this.handle;
        }

        public void setJobHandle(JobHandle jobHandle) {
            this.handle = jobHandle;
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            //this.behavior = (O)
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // TODO Auto-generated method stub

        }

    }

    public static class BehaviorJob
        implements
        Job {

        public void execute(JobContext ctx) {
            BehaviorJobContext context = (BehaviorJobContext) ctx;
            context.workingMemory.queueWorkingMemoryAction( new BehaviorExpireWMAction( context.nodeId,
                                                                                        context.behavior,
                                                                                        context.memory,
                                                                                        context.behaviorContext,
                                                                                        context.pctx ) );
        }

    }

    public static class BehaviorExpireWMAction
        implements
        WorkingMemoryAction {
        private final Behavior behavior;
        private final Object   context;
        private final WindowMemory memory;
        private final int nodeId;
        private final PropagationContext pctx;

        /**
         * @param behavior
         * @param context
         */
        public BehaviorExpireWMAction(final int nodeId,
                                      Behavior behavior,
                                      WindowMemory memory,
                                      Object context,
                                      PropagationContext pctx) {
            super();
            this.nodeId = nodeId;
            this.behavior = behavior;
            this.memory = memory;
            this.context = context;
            this.pctx = pctx;
        }

        public BehaviorExpireWMAction(MarshallerReaderContext inCtx) throws IOException {
            nodeId = inCtx.readInt();
            WindowNode windowNode = (WindowNode) inCtx.sinks.get( nodeId );
            
            memory = (WindowMemory) inCtx.wm.getNodeMemory( windowNode );
            
            Object[] behaviorContext = ( Object[]  ) memory.behaviorContext;
            
            int i = inCtx.readInt();
            
            this.behavior = (SlidingTimeWindow) windowNode.getBehaviors()[i];
            this.context =  ( SlidingTimeWindowContext ) behaviorContext[i];
            pctx = null;
        }
        
        public BehaviorExpireWMAction(MarshallerReaderContext context,
                                      Action _action) {
            nodeId =_action.getBehaviorExpire().getNodeId();
            WindowNode windowNode = (WindowNode) context.sinks.get( nodeId );
            
            memory = (WindowMemory) context.wm.getNodeMemory( windowNode );       
            
            Object[] behaviorContext = ( Object[]  ) memory.behaviorContext;
            
            int i = 0; //  <==== this needs fixing
            
            this.behavior = (SlidingTimeWindow) windowNode.getBehaviors()[i];
            this.context =  ( SlidingTimeWindowContext ) behaviorContext[i];
            pctx = null;
        }

        public void execute(InternalWorkingMemory workingMemory) {
            this.behavior.expireFacts( memory,
                                       context,
                                       null,
                                       workingMemory );
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute(((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory());
        }
        
        public void write(MarshallerWriteContext outputCtx) throws IOException {
            outputCtx.writeShort( WorkingMemoryAction.WorkingMemoryBehahviourRetract );

            // write out SlidingTimeWindowContext
            SlidingTimeWindowContext slCtx = ( SlidingTimeWindowContext ) context;

            EventFactHandle handle = slCtx.getQueue().peek();
            outputCtx.writeInt( handle.getId() );
        }
            
        public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext outputCtx) {
            SlidingTimeWindowContext slCtx = ( SlidingTimeWindowContext ) context;
            
            ProtobufMessages.ActionQueue.BehaviorExpire _be = ProtobufMessages.ActionQueue.BehaviorExpire.newBuilder()
                    .setNodeId( nodeId )
                    .build();
            
            return ProtobufMessages.ActionQueue.Action.newBuilder()
                    .setType( ProtobufMessages.ActionQueue.ActionType.BEHAVIOR_EXPIRE )
                    .setBehaviorExpire( _be )
                    .build();
                    
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }
    }
}

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

package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Comparator;
import java.util.PriorityQueue;

import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalKnowledgeRuntime;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.WorkingMemoryAction;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.PersisterEnums;
import org.drools.marshalling.impl.ProtobufMessages;
import org.drools.marshalling.impl.ProtobufMessages.ActionQueue.Action;
import org.drools.marshalling.impl.ProtobufMessages.Timers.Timer;
import org.drools.marshalling.impl.TimersInputMarshaller;
import org.drools.marshalling.impl.TimersOutputMarshaller;
import org.drools.reteoo.AccumulateNode;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.RightTuple;
import org.drools.spi.PropagationContext;
import org.drools.time.Job;
import org.drools.time.JobContext;
import org.drools.time.JobHandle;
import org.drools.time.TimerService;
import org.drools.time.impl.PointInTimeTrigger;

public class SlidingTimeWindow
    implements
    Externalizable,
    Behavior {

    private long              size;
    // stateless job
    public static final BehaviorJob job = new BehaviorJob();

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
    }

    /**
     * @inheritDoc
     *
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeLong( this.size );
    }

    public BehaviorType getType() {
        return BehaviorType.TIME_WINDOW;
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

    /**
     * @inheritDoc
     *
     * @see org.drools.rule.Behavior#assertRightTuple(java.lang.Object, org.drools.reteoo.RightTuple, org.drools.common.InternalWorkingMemory)
     */
    public boolean assertRightTuple(final PropagationContext pctx,
                                    final Object context,
                                    final RightTuple rightTuple,
                                    final InternalWorkingMemory workingMemory) {
        SlidingTimeWindowContext queue = (SlidingTimeWindowContext) context;
        if( pctx.getReaderContext() != null || !isExpired( workingMemory.getTimerService().getCurrentTime(), rightTuple ) ) {
            queue.queue.add( rightTuple );
            if ( queue.queue.peek() == rightTuple ) {
                // update next expiration time 
                updateNextExpiration( rightTuple,
                                      workingMemory,
                                      this,
                                      queue );
            }
            return true;
        }
        return false;
    }

    /**
     * @inheritDoc
     *
     * @see org.drools.rule.Behavior#retractRightTuple(java.lang.Object, org.drools.reteoo.RightTuple, org.drools.common.InternalWorkingMemory)
     */
    public void retractRightTuple(final Object context,
                                  final RightTuple rightTuple,
                                  final InternalWorkingMemory workingMemory) {
        SlidingTimeWindowContext queue = (SlidingTimeWindowContext) context;
        // it may be a call back to expire the tuple that is already being expired
        if ( queue.expiringTuple != rightTuple ) {
            if ( queue.queue.peek() == rightTuple ) {
                // it was the head of the queue
                queue.queue.poll();
                // update next expiration time 
                updateNextExpiration( queue.queue.peek(),
                                      workingMemory,
                                      this,
                                      queue );
            } else {
                queue.queue.remove( rightTuple );
            }
        }
    }

    public void expireTuples(final Object context,
                             final InternalWorkingMemory workingMemory) {
        TimerService clock = workingMemory.getTimerService();
        long currentTime = clock.getCurrentTime();
        SlidingTimeWindowContext queue = (SlidingTimeWindowContext) context;
        RightTuple tuple = queue.queue.peek();
        while ( tuple != null && isExpired( currentTime,
                                            tuple ) ) {
            queue.expiringTuple = tuple;
            queue.queue.remove();
            final InternalFactHandle handle = tuple.getFactHandle();
            if( handle != null ) {
                // when a beta node is removed, the handle might be null
                
                if( handle.isValid()) {
                    // if not expired yet, expire it
                    final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                              PropagationContext.EXPIRATION,
                                                                                              null,
                                                                                              null,
                                                                                              handle );
                    tuple.getRightTupleSink().retractRightTuple( tuple,
                                                                 propagationContext,
                                                                 workingMemory );
                    propagationContext.evaluateActionQueue( workingMemory );
                }
                tuple.unlinkFromRightParent();
            }
            queue.expiringTuple = null;
            tuple = queue.queue.peek();
        }

        // update next expiration time 
        updateNextExpiration( tuple,
                              workingMemory,
                              this,
                              queue );
    }

    private boolean isExpired(final long currentTime,
                              final RightTuple rightTuple) {
        return rightTuple.getFactHandle() == null || ((EventFactHandle) rightTuple.getFactHandle()).getStartTimestamp() + this.size <= currentTime;
    }

    /**
     * @param rightTuple
     * @param workingMemory
     */
    private static void updateNextExpiration(final RightTuple rightTuple,
                                      final InternalWorkingMemory workingMemory,
                                      final SlidingTimeWindow stw,
                                      final Object context) {
        TimerService clock = workingMemory.getTimerService();
        if ( rightTuple != null ) {
            long nextTimestamp = ((EventFactHandle) rightTuple.getFactHandle()).getStartTimestamp() + stw.getSize();
            JobContext jobctx = new BehaviorJobContext( workingMemory,
                                                        stw,
                                                        context );
            JobHandle handle = clock.scheduleJob( job,
                                                  jobctx,
                                                  new PointInTimeTrigger( nextTimestamp, null, null ) );
            jobctx.setJobHandle( handle );
        }
    }

    public long getExpirationOffset() {
        return this.size;
    }

    public String toString() {
        return "SlidingTimeWindow( size=" + size + " )";
    }

    /**
     * A Comparator<RightTuple> implementation for the fact queue
     */
    private static class SlidingTimeWindowComparator
        implements
        Comparator<RightTuple> {
        public int compare(RightTuple t1,
                           RightTuple t2) {
            final EventFactHandle e1 = (EventFactHandle) t1.getFactHandle();
            final EventFactHandle e2 = (EventFactHandle) t2.getFactHandle();
            return (e1.getStartTimestamp() < e2.getStartTimestamp()) ? -1 : (e1.getStartTimestamp() == e2.getStartTimestamp() ? 0 : 1);
        }
    }

    public static class SlidingTimeWindowContext
        implements
        Externalizable {

        public PriorityQueue<RightTuple> queue;
        public RightTuple                expiringTuple;

        public SlidingTimeWindowContext() {
            this.queue = new PriorityQueue<RightTuple>( 16, // arbitrary size... can we improve it?
                                                        new SlidingTimeWindowComparator() );
        }

        @SuppressWarnings("unchecked")
        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            this.queue = (PriorityQueue<RightTuple>) in.readObject();
            this.expiringTuple = (RightTuple) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( this.queue );
            out.writeObject( this.expiringTuple );
        }

        public PriorityQueue<RightTuple> getQueue() {
            return queue;
        }

        public void setQueue(PriorityQueue<RightTuple> queue) {
            this.queue = queue;
        }

        public RightTuple getExpiringTuple() {
            return expiringTuple;
        }

        public void setExpiringTuple(RightTuple expiringTuple) {
            this.expiringTuple = expiringTuple;
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
  
            RightTuple rightTuple = slCtx.getQueue().peek();
            //outputCtx.writeInt( rightTuple.getFactHandle().getId() );
            
            BetaNode node = (BetaNode) rightTuple.getRightTupleSink();
            outputCtx.writeInt( node.getId() );
            
            Behavior[] behaviors = node.getBehaviors();
            int i = 0;
            for ( ; i < behaviors.length; i++ ) {     
                if ( behaviors[i] == bjobCtx.behavior ) {
                    break;
                }
            }
            outputCtx.writeInt( i );           
        }

        public Timer serialize(JobContext jobCtx,
                               MarshallerWriteContext outputCtx) {
            // BehaviorJob, no state            
            BehaviorJobContext bjobCtx = ( BehaviorJobContext ) jobCtx;
            // write out SlidingTimeWindowContext
            SlidingTimeWindowContext slCtx = ( SlidingTimeWindowContext ) bjobCtx.behaviorContext;
  
            RightTuple rightTuple = slCtx.getQueue().peek();
            BetaNode node = (BetaNode) rightTuple.getRightTupleSink();
            
            Behavior[] behaviors = node.getBehaviors();
            int i = 0;
            for ( ; i < behaviors.length; i++ ) {     
                if ( behaviors[i] == bjobCtx.behavior ) {
                    break;
                }
            }
            return ProtobufMessages.Timers.Timer.newBuilder()
                    .setType( ProtobufMessages.Timers.TimerType.BEHAVIOR )
                    .setBehavior( ProtobufMessages.Timers.BehaviorTimer.newBuilder()
                                  .setNodeId( node.getId() )
                                  .setBehaviorIndex( i )
                                  .build() )
                    .build();
        }
    }
    
    public static class BehaviorJobContextTimerInputMarshaller implements TimersInputMarshaller {
        public void read(MarshallerReaderContext inCtx) throws IOException, ClassNotFoundException {
            int sinkId = inCtx.readInt();
            BetaNode betaNode = (BetaNode) inCtx.sinks.get( sinkId );
            
            BetaMemory betaMemory = null;       
            if ( betaNode instanceof AccumulateNode ) {
                betaMemory = (( AccumulateMemory ) inCtx.wm.getNodeMemory( betaNode )).betaMemory;
            } else {
                betaMemory = ( BetaMemory ) inCtx.wm.getNodeMemory( betaNode );
            }
            
            Object[] behaviorContext = ( Object[]  ) betaMemory.getBehaviorContext();
            
            int i = inCtx.readInt();
            SlidingTimeWindowContext stwCtx = ( SlidingTimeWindowContext ) behaviorContext[i];
                       
            updateNextExpiration( ( RightTuple) stwCtx.queue.peek(),
                                  inCtx.wm,
                                  (SlidingTimeWindow) betaNode.getBehaviors()[i],
                                  stwCtx );
        }

        public void deserialize(MarshallerReaderContext inCtx,
                                Timer _timer) throws ClassNotFoundException {
            int sinkId = _timer.getBehavior().getNodeId();
            BetaNode betaNode = (BetaNode) inCtx.sinks.get( sinkId );
            
            BetaMemory betaMemory = null;       
            if ( betaNode instanceof AccumulateNode ) {
                betaMemory = (( AccumulateMemory ) inCtx.wm.getNodeMemory( betaNode )).betaMemory;
            } else {
                betaMemory = ( BetaMemory ) inCtx.wm.getNodeMemory( betaNode );
            }
            
            Object[] behaviorContext = ( Object[]  ) betaMemory.getBehaviorContext();
            
            int i = _timer.getBehavior().getBehaviorIndex();
            SlidingTimeWindowContext stwCtx = ( SlidingTimeWindowContext ) behaviorContext[i];
                       
            updateNextExpiration( ( RightTuple) stwCtx.queue.peek(),
                                  inCtx.wm,
                                  (SlidingTimeWindow) betaNode.getBehaviors()[i],
                                  stwCtx );
        }
    }    
    

    public static class BehaviorJobContext
        implements
        JobContext,
        Externalizable {
        public InternalWorkingMemory workingMemory;
        public Behavior              behavior;
        public Object                behaviorContext;
        public JobHandle             handle;

        /**
         * @param workingMemory
         * @param behavior
         * @param behaviorContext
         */
        public BehaviorJobContext(InternalWorkingMemory workingMemory,
                                  Behavior behavior,
                                  Object behaviorContext) {
            super();
            this.workingMemory = workingMemory;
            this.behavior = behavior;
            this.behaviorContext = behaviorContext;
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
            context.workingMemory.queueWorkingMemoryAction( new BehaviorExpireWMAction( context.behavior,
                                                                                        context.behaviorContext ) );
        }

    }

    public static class BehaviorExpireWMAction
        implements
        WorkingMemoryAction {
        private final Behavior behavior;
        private final Object   context;

        /**
         * @param behavior
         * @param context
         */
        public BehaviorExpireWMAction(Behavior behavior,
                                      Object context) {
            super();
            this.behavior = behavior;
            this.context = context;
        }

        public BehaviorExpireWMAction(MarshallerReaderContext inCtx) throws IOException {
            int sinkId = inCtx.readInt();
            BetaNode betaNode = (BetaNode) inCtx.sinks.get( sinkId );
            
            BetaMemory betaMemory = null;       
            if ( betaNode instanceof AccumulateNode ) {
                betaMemory = (( AccumulateMemory ) inCtx.wm.getNodeMemory( betaNode )).betaMemory;
            } else {
                betaMemory = ( BetaMemory ) inCtx.wm.getNodeMemory( betaNode );
            }
            
            Object[] behaviorContext = ( Object[]  ) betaMemory.getBehaviorContext();
            
            int i = inCtx.readInt();
            
            this.behavior = (SlidingTimeWindow) betaNode.getBehaviors()[i];
            this.context =  ( SlidingTimeWindowContext ) behaviorContext[i];           
        }
        
        public BehaviorExpireWMAction(MarshallerReaderContext context,
                                      Action _action) {
            int sinkId =_action.getBehaviorExpire().getNodeId();
            BetaNode betaNode = (BetaNode) context.sinks.get( sinkId );
            
            BetaMemory betaMemory = null;       
            if ( betaNode instanceof AccumulateNode ) {
                betaMemory = (( AccumulateMemory ) context.wm.getNodeMemory( betaNode )).betaMemory;
            } else {
                betaMemory = ( BetaMemory ) context.wm.getNodeMemory( betaNode );
            }
            
            Object[] behaviorContext = ( Object[]  ) betaMemory.getBehaviorContext();
            
            int i = 0; //  <==== this needs fixing
            
            this.behavior = (SlidingTimeWindow) betaNode.getBehaviors()[i];
            this.context =  ( SlidingTimeWindowContext ) behaviorContext[i];           
        }

        public void execute(InternalWorkingMemory workingMemory) {
            this.behavior.expireTuples( context,
                                        workingMemory );
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute(((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory());
        }
        
        public void write(MarshallerWriteContext outputCtx) throws IOException {
            outputCtx.writeShort( WorkingMemoryAction.WorkingMemoryBehahviourRetract );

            // write out SlidingTimeWindowContext
            SlidingTimeWindowContext slCtx = ( SlidingTimeWindowContext ) context;
  
            RightTuple rightTuple = slCtx.getQueue().peek();
            //outputCtx.writeInt( rightTuple.getFactHandle().getId() );
            
            BetaNode node = (BetaNode) rightTuple.getRightTupleSink();
            outputCtx.writeInt( node.getId() );
            
            Behavior[] behaviors = node.getBehaviors();
            int i = 0;
            for ( ; i < behaviors.length; i++ ) {     
                if ( behaviors[i] == behavior ) {
                    break;
                }
            }
            outputCtx.writeInt( i );   
        }
            
        public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext outputCtx) {
            SlidingTimeWindowContext slCtx = ( SlidingTimeWindowContext ) context;
            
            ProtobufMessages.ActionQueue.BehaviorExpire _be = ProtobufMessages.ActionQueue.BehaviorExpire.newBuilder()
                    .setNodeId( slCtx.getQueue().peek().getRightTupleSink().getId() )
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

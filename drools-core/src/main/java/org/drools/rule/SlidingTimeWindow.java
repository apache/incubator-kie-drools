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
    public boolean assertFact(final Object context,
                              final InternalFactHandle fact,
                              final InternalWorkingMemory workingMemory) {
        final SlidingTimeWindowContext queue = (SlidingTimeWindowContext) context;
        final EventFactHandle handle = (EventFactHandle) fact;
        queue.queue.add( handle );
        if ( queue.queue.peek() == handle ) {
            // update next expiration time 
            updateNextExpiration( handle,
                                  workingMemory,
                                  this,
                                  queue );
        }
        return true;
    }

    /**
     * @inheritDoc
     *
     * @see org.drools.rule.Behavior#retractRightTuple(java.lang.Object, org.drools.reteoo.RightTuple, org.drools.common.InternalWorkingMemory)
     */
    public void retractFact(final Object context,
                            final InternalFactHandle fact,
                            final InternalWorkingMemory workingMemory) {
        final SlidingTimeWindowContext queue = (SlidingTimeWindowContext) context;
        final EventFactHandle handle = (EventFactHandle) fact;
        // it may be a call back to expire the tuple that is already being expired
        if ( queue.expiringHandle != handle ) {
            if ( queue.queue.peek() == handle ) {
                // it was the head of the queue
                queue.queue.poll();
                // update next expiration time 
                updateNextExpiration( queue.queue.peek(),
                                      workingMemory,
                                      this,
                                      queue );
            } else {
                queue.queue.remove( handle );
            }
        }
    }

    public void expireFacts(final Object context,
                             final InternalWorkingMemory workingMemory) {
        TimerService clock = workingMemory.getTimerService();
        long currentTime = clock.getCurrentTime();
        SlidingTimeWindowContext queue = (SlidingTimeWindowContext) context;
        EventFactHandle handle = queue.queue.peek();
        while ( handle != null && isExpired( currentTime,
                                             handle ) ) {
            queue.expiringHandle = handle;
            queue.queue.remove();
            if( handle.isValid()) {
                // if not expired yet, expire it
                final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                          PropagationContext.EXPIRATION,
                                                                                          null,
                                                                                          null,
                                                                                          handle );
                // TODO: expire previous handle
//                tuple.getRightTupleSink().retractRightTuple( tuple,
//                                                             propagationContext,
//                                                             workingMemory );
                propagationContext.evaluateActionQueue( workingMemory );
            }
//            tuple.unlinkFromRightParent();
            queue.expiringHandle = null;
            handle = queue.queue.peek();
        }

        // update next expiration time 
        updateNextExpiration( handle,
                              workingMemory,
                              this,
                              queue );
    }

    private boolean isExpired(final long currentTime,
                              final EventFactHandle handle) {
        return handle.getStartTimestamp() + this.size <= currentTime;
    }

    /**
     * @param rightTuple
     * @param workingMemory
     */
    private static void updateNextExpiration(final InternalFactHandle fact,
                                      final InternalWorkingMemory workingMemory,
                                      final SlidingTimeWindow stw,
                                      final Object context) {
        TimerService clock = workingMemory.getTimerService();
        if ( fact != null ) {
            long nextTimestamp = ((EventFactHandle) fact).getStartTimestamp() + stw.getSize();
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
        Comparator<EventFactHandle> {
        public int compare(EventFactHandle e1,
                           EventFactHandle e2) {
            return (e1.getStartTimestamp() < e2.getStartTimestamp()) ? -1 : (e1.getStartTimestamp() == e2.getStartTimestamp() ? 0 : 1);
        }
    }

    public static class SlidingTimeWindowContext
        implements
        Externalizable {

        public PriorityQueue<EventFactHandle> queue;
        public EventFactHandle                expiringHandle;

        public SlidingTimeWindowContext() {
            this.queue = new PriorityQueue<EventFactHandle>( 16, // arbitrary size... can we improve it?
                                                             new SlidingTimeWindowComparator() );
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
            //outputCtx.writeInt( rightTuple.getFactHandle().getId() );
            
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
                       
            updateNextExpiration( stwCtx.queue.peek(),
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
        
        public void execute(InternalWorkingMemory workingMemory) {
            this.behavior.expireFacts( context,
                                       workingMemory );
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute(((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory());
        }
        
        public void write(MarshallerWriteContext outputCtx) throws IOException {
            outputCtx.writeShort( WorkingMemoryAction.WorkingMemoryBehahviourRetract );
            
            // write out SlidingTimeWindowContext
            SlidingTimeWindowContext slCtx = ( SlidingTimeWindowContext ) context;
            //TODO
  
            //RightTuple rightTuple = slCtx.getQueue().peek();
            //outputCtx.writeInt( rightTuple.getFactHandle().getId() );
            
            //BetaNode node = (BetaNode) rightTuple.getRightTupleSink();
            //outputCtx.writeInt( node.getId() );
            
            //Behavior[] behaviors = node.getBehaviors();
//            int i = 0;
//            for ( ; i < behaviors.length; i++ ) {     
//                if ( behaviors[i] == behavior ) {
//                    break;
//                }
//            }
//            outputCtx.writeInt( i );   
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }
    }
}

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

package org.drools.reteoo.nodes;

import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.rule.Behavior;
import org.drools.core.rule.SlidingTimeWindow;
import org.drools.core.spi.PropagationContext;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.TimerService;
import org.drools.core.time.impl.PointInTimeTrigger;

import java.io.Externalizable;

public class ReteSlidingTimeWindow extends SlidingTimeWindow
        implements
        Externalizable,
        Behavior {

    // stateless job
    private static final ReteBehaviorJob reteJob = new ReteBehaviorJob();

    public ReteSlidingTimeWindow() {
        this( 0 );
    }

    public ReteSlidingTimeWindow(final long size) {
        super(size);
    }

    @Override
    public boolean assertFact(final Object context,
                              final InternalFactHandle fact,
                              final PropagationContext pctx,
                              final InternalWorkingMemory workingMemory) {
        final SlidingTimeWindowContext queue = (SlidingTimeWindowContext) context;
        final EventFactHandle handle = (EventFactHandle) fact;
        long currentTime = workingMemory.getTimerService().getCurrentTime();
        if ( isExpired( currentTime, handle ) ) {
            return false;
        }

        synchronized (queue) {
            queue.add( handle );
            if ( queue.peek() == handle ) {
                // update next expiration time
                updateNextExpiration( handle,
                                      workingMemory,
                                      queue,
                                      nodeId );
            }
        }
        return true;
    }

    @Override
    public void retractFact(final Object context,
                            final InternalFactHandle fact,
                            final PropagationContext pctx,
                            final InternalWorkingMemory workingMemory) {
        final SlidingTimeWindowContext queue = (SlidingTimeWindowContext) context;
        final EventFactHandle handle = (EventFactHandle) fact;

        synchronized (queue) {
            // it may be a call back to expire the tuple that is already being expired
            if ( queue.getExpiringHandle() != handle ) {
                if ( queue.peek() == handle ) {
                    // it was the head of the queue
                    queue.poll();
                    // update next expiration time
                    updateNextExpiration( queue.peek(),
                                          workingMemory,
                                          queue,
                                          nodeId );
                } else {
                    queue.remove( handle );
                }
            }
        }
    }

    @Override
    public void expireFacts(final Object context,
                            final PropagationContext pctx,
                            final InternalWorkingMemory workingMemory) {
        TimerService clock = workingMemory.getTimerService();
        long currentTime = clock.getCurrentTime();
        SlidingTimeWindowContext queue = (SlidingTimeWindowContext) context;

        synchronized (queue) {
            EventFactHandle handle = queue.peek();
            while ( handle != null && isExpired( currentTime,
                                                 handle ) ) {
                queue.setExpiringHandle( handle );
                queue.remove();
                if ( handle.isValid() ) {
                    // if not expired yet, expire it
                    PropagationContextFactory pctxFactory = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();
                    final PropagationContext expiresPctx = pctxFactory.createPropagationContext( workingMemory.getNextPropagationIdCounter(), PropagationContext.EXPIRATION,
                                                                                                 null, null, handle );
                    ObjectTypeNode.doRetractObject( handle, expiresPctx, workingMemory );
                    expiresPctx.evaluateActionQueue( workingMemory );
                }
                queue.setExpiringHandle( null );
                handle = queue.peek();
            }
            // update next expiration time
            updateNextExpiration( handle,
                                  workingMemory,
                                  queue,
                                  nodeId );
        }
    }

    @Override
    protected void updateNextExpiration(final InternalFactHandle fact,
                                        final InternalWorkingMemory workingMemory,
                                        final Object context,
                                        final int nodeId) {
        TimerService clock = workingMemory.getTimerService();
        if ( fact != null ) {
            long nextTimestamp = ((EventFactHandle) fact).getStartTimestamp() + getSize();
            if ( nextTimestamp < clock.getCurrentTime() ) {
                // Past and out-of-order events should not be insert,
                // but the engine silently accepts them anyway, resulting in possibly undesirable behaviors
                workingMemory.queueWorkingMemoryAction( new BehaviorExpireWMAction( nodeId, this, context ) );
            } else {
                JobContext jobctx = new BehaviorJobContext( nodeId, workingMemory, this, context);
                JobHandle handle = clock.scheduleJob( reteJob,
                                                      jobctx,
                                                      new PointInTimeTrigger( nextTimestamp, null, null ) );
                jobctx.setJobHandle( handle );
            }
        }
    }

    public static class ReteBehaviorJob
            implements
            Job {

        public void execute(JobContext ctx) {
            BehaviorJobContext context = (BehaviorJobContext) ctx;
            context.workingMemory.queueWorkingMemoryAction( new BehaviorExpireWMAction( context.nodeId,
                                                                                        context.behavior,
                                                                                        context.behaviorContext ) );
        }

    }
}

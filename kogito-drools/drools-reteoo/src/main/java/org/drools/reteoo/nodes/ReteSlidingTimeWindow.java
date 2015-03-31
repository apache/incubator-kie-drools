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

    /**
     * @param size
     */
    public ReteSlidingTimeWindow(final long size) {
        super(size);
    }

    @Override
    protected void updateNextExpiration(final InternalFactHandle fact,
                                        final PropagationContext pctx,
                                        final InternalWorkingMemory workingMemory,
                                        final Object context,
                                        final int nodeId) {
        TimerService clock = workingMemory.getTimerService();
        if ( fact != null ) {
            long nextTimestamp = ((EventFactHandle) fact).getStartTimestamp() + getSize();
            if ( nextTimestamp < clock.getCurrentTime() ) {
                // Past and out-of-order events should not be insert,
                // but the engine silently accepts them anyway, resulting in possibly undesirable behaviors
                workingMemory.queueWorkingMemoryAction( new BehaviorExpireWMAction( nodeId, this, context, pctx ) );
            } else {
                JobContext jobctx = new BehaviorJobContext( nodeId, workingMemory, this, context, pctx);
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
                                                                                        context.behaviorContext,
                                                                                        context.pctx ) );
        }

    }
}

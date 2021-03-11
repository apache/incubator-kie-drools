/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.serialization.protobuf.timers;

import java.io.IOException;

import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.StatefulKnowledgeSessionImpl.WorkingMemoryReteExpireAction;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.reteoo.ObjectTypeNode.ExpireJob;
import org.drools.core.reteoo.ObjectTypeNode.ExpireJobContext;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.TimerService;
import org.drools.core.time.impl.PointInTimeTrigger;
import org.drools.serialization.protobuf.ProtobufMarshallerReaderContext;
import org.drools.serialization.protobuf.ProtobufMessages;
import org.drools.serialization.protobuf.TimersInputMarshaller;

public class ExpireJobContextTimerInputMarshaller implements TimersInputMarshaller {
    private static final transient ExpireJob job = new ExpireJob();

    public void read( ProtobufMarshallerReaderContext inCtx) throws IOException,
            ClassNotFoundException {

        InternalFactHandle factHandle = inCtx.getHandles().get( inCtx.readLong() );

        long nextTimeStamp = inCtx.readLong();

        TimerService clock = inCtx.getWorkingMemory().getTimerService();

        JobContext jobctx = new ExpireJobContext( new WorkingMemoryReteExpireAction( ( EventFactHandle ) factHandle ), inCtx.getWorkingMemory() );
        JobHandle handle = clock.scheduleJob( job,
                jobctx,
                PointInTimeTrigger.createPointInTimeTrigger( nextTimeStamp, null ) );
        jobctx.setJobHandle( handle );

    }

    @Override
    public void deserialize( MarshallerReaderContext inCtx, ProtobufMessages.Timers.Timer timer) {
        ProtobufMessages.Timers.ExpireTimer expire = timer.getExpire();
        InternalFactHandle factHandle = inCtx.getHandles().get( expire.getHandleId() );

        TimerService clock = inCtx.getWorkingMemory().getTimerService();

        JobContext jobctx = new ExpireJobContext( new WorkingMemoryReteExpireAction((EventFactHandle)factHandle),
                inCtx.getWorkingMemory() );
        JobHandle jobHandle = clock.scheduleJob( job,
                jobctx,
                PointInTimeTrigger.createPointInTimeTrigger( expire.getNextFireTimestamp(), null ) );
        jobctx.setJobHandle( jobHandle );
        ((EventFactHandle) factHandle).addJob(jobHandle);
    }
}
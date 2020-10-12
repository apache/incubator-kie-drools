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
import java.util.Date;

import org.drools.core.impl.StatefulKnowledgeSessionImpl.WorkingMemoryReteExpireAction;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.PersisterEnums;
import org.drools.core.reteoo.ObjectTypeNode.ExpireJobContext;
import org.drools.core.time.JobContext;
import org.drools.core.time.impl.DefaultJobHandle;
import org.drools.core.time.impl.PointInTimeTrigger;
import org.drools.serialization.protobuf.ProtobufMessages;
import org.drools.serialization.protobuf.TimersOutputMarshaller;

public class ExpireJobContextTimerOutputMarshaller
        implements
        TimersOutputMarshaller {

    public void write( JobContext jobCtx, MarshallerWriteContext outputCtx) throws IOException {
        // ExpireJob, no state
        ExpireJobContext ejobCtx = ( ExpireJobContext ) jobCtx;
        DefaultJobHandle jobHandle = (DefaultJobHandle) ejobCtx.getJobHandle();
        PointInTimeTrigger trigger = (PointInTimeTrigger) jobHandle.getTimerJobInstance().getTrigger();
        // There is no reason to serialize a timer when it has no future execution time.
        Date nextFireTime = trigger.hasNextFireTime();
        if (nextFireTime != null) {
            outputCtx.writeShort( PersisterEnums.EXPIRE_TIMER);
            outputCtx.writeLong(ejobCtx.getExpireAction().getFactHandle().getId());
            outputCtx.writeLong(nextFireTime.getTime());
        }
    }

    @Override
    public ProtobufMessages.Timers.Timer serialize( JobContext jobCtx, MarshallerWriteContext outputCtx) {
        // ExpireJob, no state
        ExpireJobContext ejobCtx = (ExpireJobContext) jobCtx;
        WorkingMemoryReteExpireAction expireAction = ejobCtx.getExpireAction();
        DefaultJobHandle jobHandle = (DefaultJobHandle) ejobCtx.getJobHandle();
        PointInTimeTrigger trigger = (PointInTimeTrigger) jobHandle.getTimerJobInstance().getTrigger();
        Date nextFireTime = trigger.hasNextFireTime();
        if (nextFireTime != null) {
            return ProtobufMessages.Timers.Timer.newBuilder()
                    .setType(ProtobufMessages.Timers.TimerType.EXPIRE)
                    .setExpire(ProtobufMessages.Timers.ExpireTimer.newBuilder()
                            .setHandleId(expireAction.getFactHandle().getId())
                            .setNextFireTimestamp(nextFireTime.getTime())
                            .build())
                    .build();
        } else {
            // There is no reason to serialize a timer when it has no future execution time.
            return null;
        }
    }
}
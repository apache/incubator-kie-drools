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
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.PersisterEnums;
import org.drools.core.rule.SlidingTimeWindow.BehaviorJobContext;
import org.drools.core.rule.SlidingTimeWindow.SlidingTimeWindowContext;
import org.drools.core.time.JobContext;
import org.drools.serialization.protobuf.ProtobufMessages;
import org.drools.serialization.protobuf.TimersOutputMarshaller;

public class BehaviorJobContextTimerOutputMarshaller implements TimersOutputMarshaller {

    public void write( JobContext jobCtx, MarshallerWriteContext outputCtx) throws IOException {
        outputCtx.writeShort( PersisterEnums.BEHAVIOR_TIMER );
        // BehaviorJob, no state
        BehaviorJobContext bjobCtx = ( BehaviorJobContext ) jobCtx;

        // write out SlidingTimeWindowContext
        SlidingTimeWindowContext slCtx = ( SlidingTimeWindowContext ) bjobCtx.behaviorContext;

        EventFactHandle handle = slCtx.peek();
        outputCtx.writeLong( handle.getId() );
    }

    @Override
    public ProtobufMessages.Timers.Timer serialize(JobContext jobCtx, MarshallerWriteContext outputCtx) {
        // BehaviorJob, no state
        BehaviorJobContext bjobCtx = ( BehaviorJobContext ) jobCtx;
        // write out SlidingTimeWindowContext
        SlidingTimeWindowContext slCtx = ( SlidingTimeWindowContext ) bjobCtx.behaviorContext;

        EventFactHandle handle = slCtx.peek();

        return ProtobufMessages.Timers.Timer.newBuilder()
                .setType( ProtobufMessages.Timers.TimerType.BEHAVIOR )
                .setBehavior( ProtobufMessages.Timers.BehaviorTimer.newBuilder()
                        .setHandleId( handle.getId() )
                        .build() )
                .build();
    }
}
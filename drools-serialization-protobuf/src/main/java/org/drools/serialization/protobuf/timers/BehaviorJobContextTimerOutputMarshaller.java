package org.drools.serialization.protobuf.timers;

import java.io.IOException;

import org.drools.core.common.DefaultEventHandle;
import org.drools.core.marshalling.MarshallerWriteContext;
import org.drools.serialization.protobuf.marshalling.PersisterEnums;
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

        DefaultEventHandle handle = slCtx.peek();
        outputCtx.writeLong( handle.getId() );
    }

    @Override
    public ProtobufMessages.Timers.Timer serialize(JobContext jobCtx, MarshallerWriteContext outputCtx) {
        // BehaviorJob, no state
        BehaviorJobContext bjobCtx = ( BehaviorJobContext ) jobCtx;
        // write out SlidingTimeWindowContext
        SlidingTimeWindowContext slCtx = ( SlidingTimeWindowContext ) bjobCtx.behaviorContext;

        DefaultEventHandle handle = slCtx.peek();

        return ProtobufMessages.Timers.Timer.newBuilder()
                .setType( ProtobufMessages.Timers.TimerType.BEHAVIOR )
                .setBehavior( ProtobufMessages.Timers.BehaviorTimer.newBuilder()
                        .setHandleId( handle.getId() )
                        .build() )
                .build();
    }
}
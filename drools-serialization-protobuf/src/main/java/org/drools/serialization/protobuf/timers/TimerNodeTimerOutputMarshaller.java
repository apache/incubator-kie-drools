package org.drools.serialization.protobuf.timers;

import org.drools.core.marshalling.MarshallerWriteContext;
import org.drools.core.phreak.PhreakTimerNode.TimerNodeJobContext;
import org.drools.core.time.JobContext;
import org.drools.serialization.protobuf.PersisterHelper;
import org.drools.serialization.protobuf.ProtobufMessages;
import org.drools.serialization.protobuf.ProtobufOutputMarshaller;
import org.drools.serialization.protobuf.TimersOutputMarshaller;

public class TimerNodeTimerOutputMarshaller implements TimersOutputMarshaller {

    @Override
    public ProtobufMessages.Timers.Timer serialize( JobContext jobCtx, MarshallerWriteContext outputCtx) {
        // TimerNodeJobContext
        TimerNodeJobContext tnJobCtx = ( TimerNodeJobContext ) jobCtx;
        ProtobufMessages.Trigger trigger = ProtobufOutputMarshaller.writeTrigger( tnJobCtx.getTrigger(), outputCtx );
        if (trigger != null) {
            return ProtobufMessages.Timers.Timer.newBuilder()
                    .setType( ProtobufMessages.Timers.TimerType.TIMER_NODE )
                    .setTimerNode( ProtobufMessages.Timers.TimerNodeTimer.newBuilder()
                            .setNodeId( tnJobCtx.getTimerNodeId() )
                            .setTuple( PersisterHelper.createTuple( tnJobCtx.getTuple() ) )
                            .setTrigger( trigger )
                            .build() )
                    .build();
        } else {
            return null;
        }
    }
}
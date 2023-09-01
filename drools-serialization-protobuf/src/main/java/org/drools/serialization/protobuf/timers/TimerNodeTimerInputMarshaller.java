package org.drools.serialization.protobuf.timers;

import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.marshalling.TupleKey;
import org.drools.core.phreak.PhreakTimerNode;
import org.drools.base.time.Trigger;
import org.drools.serialization.protobuf.PersisterHelper;
import org.drools.serialization.protobuf.ProtobufInputMarshaller;
import org.drools.serialization.protobuf.ProtobufMessages;
import org.drools.serialization.protobuf.TimersInputMarshaller;

public class TimerNodeTimerInputMarshaller implements TimersInputMarshaller {

    @Override
    public void deserialize( MarshallerReaderContext inCtx, ProtobufMessages.Timers.Timer _timer) {
        ProtobufMessages.Timers.TimerNodeTimer _tn = _timer.getTimerNode();

        int timerNodeId = _tn.getNodeId();
        TupleKey tuple = PersisterHelper.createTupleKey( _tn.getTuple() );
        Trigger trigger = ProtobufInputMarshaller.readTrigger( inCtx, _tn.getTrigger() );

        PhreakTimerNode.Scheduler scheduler = inCtx.removeTimerNodeScheduler( timerNodeId, tuple );
        if( scheduler != null ) {
            scheduler.schedule( trigger );
        }
    }
}

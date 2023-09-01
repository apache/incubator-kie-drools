package org.drools.serialization.protobuf.timers;

import java.io.IOException;

import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.reteoo.WindowNode;
import org.drools.core.reteoo.WindowNode.WindowMemory;
import org.drools.serialization.protobuf.ProtobufMarshallerReaderContext;
import org.drools.serialization.protobuf.ProtobufMessages;
import org.drools.serialization.protobuf.TimersInputMarshaller;

public class BehaviorJobContextTimerInputMarshaller implements TimersInputMarshaller {

    public void read( ProtobufMarshallerReaderContext inCtx) throws IOException {
        int sinkId = inCtx.readInt();
        WindowNode windowNode = (WindowNode) inCtx.getSinks().get( sinkId );

        WindowMemory memory = inCtx.getWorkingMemory().getNodeMemory( windowNode );

        Object[] behaviorContext = memory.behaviorContext;

        int i = inCtx.readInt();
    }

    @Override
    public void deserialize( MarshallerReaderContext inCtx, ProtobufMessages.Timers.Timer timer) {
        long i = timer.getBehavior().getHandleId();
    }
}
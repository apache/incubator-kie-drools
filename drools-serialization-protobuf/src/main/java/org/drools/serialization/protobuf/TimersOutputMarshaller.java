package org.drools.serialization.protobuf;

import org.drools.core.marshalling.MarshallerWriteContext;
import org.drools.core.time.JobContext;

public interface TimersOutputMarshaller {
    
    ProtobufMessages.Timers.Timer serialize( JobContext jobCtx, MarshallerWriteContext outputCtx );
}

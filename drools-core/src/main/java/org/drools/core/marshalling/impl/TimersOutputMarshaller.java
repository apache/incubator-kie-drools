package org.drools.core.marshalling.impl;

import java.io.IOException;

import org.drools.core.time.JobContext;

public interface TimersOutputMarshaller {
    
    public ProtobufMessages.Timers.Timer serialize( JobContext jobCtx,
                                                    MarshallerWriteContext outputCtx );
}

package org.drools.core.marshalling.impl;

import java.io.IOException;

import org.drools.core.time.JobContext;

public interface TimersOutputMarshaller {

    public void write(JobContext jobCtx,
                      MarshallerWriteContext outputCtx) throws IOException;
    
    public ProtobufMessages.Timers.Timer serialize( JobContext jobCtx,
                                                    MarshallerWriteContext outputCtx );
}

package org.drools.marshalling.impl;

import java.io.IOException;

import org.drools.time.JobContext;

public interface TimersOutputMarshaller {

    public void write(JobContext jobCtx,
                      MarshallerWriteContext outputCtx) throws IOException;
}

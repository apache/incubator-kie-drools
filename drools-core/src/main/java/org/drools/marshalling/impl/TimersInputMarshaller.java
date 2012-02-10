package org.drools.marshalling.impl;

import java.io.IOException;

public interface TimersInputMarshaller {
    public void read(MarshallerReaderContext inCtx) throws IOException, ClassNotFoundException;
    
    public void deserialize(MarshallerReaderContext inCtx,
                            ProtobufMessages.Timers.Timer _timer) throws ClassNotFoundException;
}

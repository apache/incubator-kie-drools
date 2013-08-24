package org.drools.core.marshalling.impl;

import java.io.IOException;

public interface TimersInputMarshaller {
    public void deserialize(MarshallerReaderContext inCtx,
                            ProtobufMessages.Timers.Timer _timer) throws ClassNotFoundException;
}

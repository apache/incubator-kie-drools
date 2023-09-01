package org.drools.serialization.protobuf;

import org.drools.core.marshalling.MarshallerReaderContext;

public interface TimersInputMarshaller {

    void deserialize( MarshallerReaderContext inCtx, ProtobufMessages.Timers.Timer _timer);
}

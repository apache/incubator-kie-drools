package org.drools.marshalling.impl;

import java.io.IOException;

public interface TimersInputMarshaller {
    public void read(MarshallerReaderContext inCtx) throws IOException, ClassNotFoundException;
}

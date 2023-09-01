package org.drools.serialization.protobuf;

import java.io.IOException;

import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.marshalling.MarshallerWriteContext;

public interface ProtobufWorkingMemoryAction extends WorkingMemoryAction {
    ProtobufMessages.ActionQueue.Action serialize( MarshallerWriteContext context) throws IOException;
}

package org.drools.serialization.protobuf.marshalling;

import java.io.IOException;
import java.util.List;

import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.marshalling.MarshallerWriteContext;
import org.kie.api.runtime.process.ProcessInstance;
import org.drools.core.process.WorkItem;

public interface ProcessMarshaller {

    void writeProcessInstances( MarshallerWriteContext context ) throws IOException;

    void writeProcessTimers( MarshallerWriteContext context ) throws IOException;

    void writeWorkItems( MarshallerWriteContext context ) throws IOException;

    List<ProcessInstance> readProcessInstances( MarshallerReaderContext context ) throws IOException;

    void readProcessTimers( MarshallerReaderContext context ) throws IOException, ClassNotFoundException;

    void readWorkItems( MarshallerReaderContext context ) throws IOException;

    void init(MarshallerReaderContext context);

    void writeWorkItem( MarshallerWriteContext context, WorkItem workItem);

    WorkItem readWorkItem(MarshallerReaderContext context);

}

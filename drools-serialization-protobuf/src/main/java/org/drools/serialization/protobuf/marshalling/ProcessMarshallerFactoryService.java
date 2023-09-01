package org.drools.serialization.protobuf.marshalling;

import org.kie.api.internal.utils.KieService;

/**
 * ProcessRuntimeFactoryService is used by the AbstractWorkingMemory to "provide" it's concrete implementation.
 */
public interface ProcessMarshallerFactoryService extends KieService {

    ProcessMarshaller newProcessMarshaller();
    
}

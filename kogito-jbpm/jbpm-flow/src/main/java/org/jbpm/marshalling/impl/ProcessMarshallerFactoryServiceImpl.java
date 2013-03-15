package org.jbpm.marshalling.impl;

import org.drools.core.marshalling.impl.ProcessMarshaller;
import org.drools.core.marshalling.impl.ProcessMarshallerFactoryService;

public class ProcessMarshallerFactoryServiceImpl implements ProcessMarshallerFactoryService {

	public ProcessMarshaller newProcessMarshaller() {
		return new ProtobufProcessMarshaller();
	}
	
}

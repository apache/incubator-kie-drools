package org.jbpm.marshalling.impl;

import org.drools.marshalling.impl.ProcessMarshaller;
import org.drools.marshalling.impl.ProcessMarshallerFactoryService;

public class ProcessMarshallerFactoryServiceImpl implements ProcessMarshallerFactoryService {

	public ProcessMarshaller newProcessMarshaller() {
		return new ProtobufProcessMarshaller();
	}
	
}

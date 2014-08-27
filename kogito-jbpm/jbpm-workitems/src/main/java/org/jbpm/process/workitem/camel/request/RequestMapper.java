package org.jbpm.process.workitem.camel.request;

import java.util.Map;

import org.apache.camel.Processor;

public interface RequestMapper {
	
	Processor mapToRequest(Map<String, Object> params);
	
}
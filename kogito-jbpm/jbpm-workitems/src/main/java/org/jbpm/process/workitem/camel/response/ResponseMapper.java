package org.jbpm.process.workitem.camel.response;

import java.util.Map;

import org.apache.camel.Exchange;

public interface ResponseMapper {
	
	Map<String, Object> mapFromResponse(Exchange exchange);
	
}

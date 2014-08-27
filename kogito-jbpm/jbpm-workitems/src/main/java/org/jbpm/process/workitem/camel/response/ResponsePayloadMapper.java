package org.jbpm.process.workitem.camel.response;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

public class ResponsePayloadMapper implements ResponseMapper {

	private final String responseLocation;
	protected final Set<String> headerLocations;
	
	public ResponsePayloadMapper() {
		this("response");
	}
	
	public ResponsePayloadMapper(String responseLocation) {
		this(responseLocation, new HashSet<String>());
	}
	
	public ResponsePayloadMapper(String responseLocation, Set<String> headerLocations) {
		this.responseLocation = responseLocation;
		this.headerLocations = headerLocations;
	}
	
	@Override
	public Map<String, Object> mapFromResponse(Exchange exchange) {
		Map<String, Object> results = new HashMap<String, Object>();
		if (exchange.hasOut()) {
			Message out = exchange.getOut();
			Object response = out.getBody();
			results.put(responseLocation, response);
			Map<String, Object> headerValues = out.getHeaders();
			for (String headerLocation : this.headerLocations) {
				results.put(headerLocation, headerValues.get(headerLocation));
			}
		}
		return results;
	}
}

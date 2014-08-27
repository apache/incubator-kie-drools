package org.jbpm.process.workitem.camel.request;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class RequestPayloadMapper implements RequestMapper {
 
	protected final String requestLocation;
	protected final Set<String> headerLocations;
	
	public RequestPayloadMapper() {
		this("request");
	}
	
	public RequestPayloadMapper(String requestLocation) {
		this(requestLocation, new HashSet<String>());
	}
	
	public RequestPayloadMapper(String requestLocation, Set<String> headerLocations) {
		this.requestLocation = requestLocation;
		this.headerLocations = headerLocations;
	}
	
	public Processor mapToRequest(Map<String, Object> params) {
		Object request = (Object) params.remove(requestLocation);
		
		Map<String, Object> headers = new HashMap<String, Object>();
		
		for (String headerLocation : this.headerLocations) {
			//remove from the request params, move to header locations.
			if (params.containsKey(headerLocation)) {
				headers.put(headerLocation, params.remove(headerLocation));
			}
		}
		
		return new RequestProcessor(request, headers);
	}

	protected class RequestProcessor implements Processor {
		private Object payload;
		private Map<String, Object> headers;
		
		public RequestProcessor(Object payload, Map<String, Object> headers) {
			this.payload = payload;
			this.headers = headers;
		}
		
		@Override
		public void process(Exchange exchange) throws Exception {
			exchange.getIn().setBody(payload);
			exchange.getIn().setHeaders(headers);
		}
	}
}

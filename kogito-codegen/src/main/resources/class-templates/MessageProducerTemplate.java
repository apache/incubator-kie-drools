package com.myspace.demo;


import java.util.Optional;
import java.util.TimeZone;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.event.DataEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.github.javaparser.ast.body.MethodDeclaration;

public class MessageProducer {
    
    Object emitter;
    
    Optional<Boolean> useCloudEvents = Optional.of(true);
    private ObjectMapper json = new ObjectMapper();
    
    
    public void configure() {
        json.setDateFormat(new StdDateFormat().withColonInTimeZone(true).withTimeZone(TimeZone.getDefault()));
    }
    
	public void produce(ProcessInstance pi, $Type$ eventData) {
               
    }
	    
	private String marshall(ProcessInstance pi, $Type$ eventData) {
	    try {
	        
	        if (useCloudEvents.orElse(true)) {
        	    $DataEventType$ event = new $DataEventType$("",
        	                                                    eventData,
        	                                                    pi.getId(),
        	                                                    pi.getParentProcessInstanceId(),
        	                                                    pi.getRootProcessInstanceId(),
        	                                                    pi.getProcessId(),
        	                                                    pi.getRootProcessId(),
        	                                                    String.valueOf(pi.getState()));
        	    if (pi.getReferenceId() != null && !pi.getReferenceId().isEmpty()) {
        	        event.setKogitoReferenceId(pi.getReferenceId());
        	    }
        	    return json.writeValueAsString(event);
	        } else {
	            return json.writeValueAsString(eventData);
	        }
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
}
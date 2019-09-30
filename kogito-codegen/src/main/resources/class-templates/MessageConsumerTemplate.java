package com.myspace.demo;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.kie.kogito.Application;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.Sig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;

public class $Type$MessageConsumer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("MessageConsumer");

    Process<$Type$> process;

    Application application;
    
    private ObjectMapper json = new ObjectMapper();
        
    public void configure() {
        json.setDateFormat(new StdDateFormat().withColonInTimeZone(true).withTimeZone(TimeZone.getDefault()));
    }
    
	public void consume(String payload) {
	    final String trigger = "$Trigger$";
        try {
            final $DataEventType$ eventData = json.readValue(payload, $DataEventType$.class);
    	    final $Type$ model = new $Type$();
            model.set$ModelRef$(eventData.getData());
            org.kie.kogito.services.uow.UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
                
                if (eventData.getKogitoReferenceId() != null) {
                    LOGGER.debug("Received message with reference id '{}' going to use it to send signal '{}'", eventData.getKogitoReferenceId(), trigger);
                    process.instances().findById(eventData.getKogitoReferenceId()).ifPresent(pi -> pi.send(Sig.of("Message-"+trigger, eventData.getData(), eventData.getKogitoProcessinstanceId())));
                } else {  
                    LOGGER.debug("Received message without reference id, staring new process instance with trigger '{}'", trigger);
                    ProcessInstance<$Type$> pi = process.createInstance(model);
                    pi.start(trigger, eventData.getKogitoProcessinstanceId());  
                }
                return null;
            });
        } catch (Exception e) {
            LOGGER.error("Error when consuming message for process {}", process.id(), e);
        }
    }
	    
}